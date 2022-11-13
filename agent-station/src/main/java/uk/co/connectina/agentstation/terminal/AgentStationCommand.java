/* Agent Station environment for static and mobile software agents
 * Copyright (C) 2022  Dr Christos Bohoris
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * connectina.co.uk/agent-station
 */
package uk.co.connectina.agentstation.terminal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.connectina.agentstation.api.Registry;
import uk.co.connectina.agentstation.api.Station;
import uk.co.connectina.agentstation.api.StationListener;
import uk.co.connectina.agentstation.api.Identity;
import uk.co.connectina.agentstation.api.Instance;
import uk.co.connectina.agentstation.api.client.LogType;
import uk.co.connectina.agentstation.api.client.OperationException;
import uk.co.connectina.agentstation.local.AgentStation;
import uk.co.connectina.agentstation.local.IOAccess;
import uk.co.connectina.agentstation.local.ServerRunnable;
import uk.co.connectina.agentstation.local.StationInfo;

/**
 * An interactive TUI (i.e. command-line interface) for an Agent Station. 
 * 
 * @author Dr Christos Bohoris
 */
public final class AgentStationCommand implements StationListener {

    public static final String MESSAGE_ERROR_PATTERN = "{0}: {1}";
    private Station agentStation;
    private final Registry agentRegistry;
    private static final Logger LOGGER = LogManager.getLogger(AgentStationCommand.class.toString());
    private final transient PlacesClient placesClient;
    private final transient AgentsClient agentsClient;
    private final transient PermissionsClient permissionsClient;
    private final StationInfo stationInfo;
    private transient ServerRunnable serverRunnable;

    public AgentStationCommand(StationInfo stationInfo) {
        this.stationInfo = stationInfo;

        try {
            agentStation = new AgentStation(stationInfo, this);
        } catch (OperationException e) {
            LOGGER.error(e);
        }
        agentRegistry = agentStation.getRegistry();
        placesClient = new PlacesClient(agentStation, agentRegistry);
        agentsClient = new AgentsClient(agentStation, agentRegistry);
        permissionsClient = new PermissionsClient(agentRegistry);
        serverRunnable = new ServerRunnable(this.stationInfo, agentStation, agentRegistry, AgentStationCommand.this);

        initiateShutdownSupport();

        LOGGER.info("Type 'help<Enter>' to get a list of available commands.");
        displayReadLine();
    }

    private boolean processPlace(String processedName, String[] cmd) {
        if (processedName.startsWith("list places")) {
            placesClient.listPlaces();

            return true;
        } else if (processedName.startsWith("create place")) {
            placesClient.createPlace(cmd);

            return true;
        } else if (processedName.startsWith("remove place")) {
            placesClient.removePlace(cmd);

            return true;
        }

        return false;
    }

    private boolean processAgent(String processedName, String[] cmd) {
        if (processedName.startsWith("list agents")) {
            agentsClient.listAgents();

            return true;
        } else if (processedName.startsWith("create agent")) {
            agentsClient.createAgent(cmd);

            return true;
        } else if (processedName.startsWith("start agent")) {
            agentsClient.startAgent(cmd);

            return true;
        } else if (processedName.startsWith("stop agent")) {
            agentsClient.stopAgent(cmd);

            return true;
        } else if (processedName.startsWith("remove agent")) {
            agentsClient.removeAgent(cmd);

            return true;
        }

        return false;
    }

    private boolean processPermission(String processedName, String[] cmd) {
        if (processedName.startsWith("list permissions")) {
            permissionsClient.listPermissions();

            return true;
        } else if (processedName.startsWith("remove permission")) {
            permissionsClient.removePermission(cmd);

            return true;
        }

        return false;
    }

    private void displayReadLine() {
        Thread thread = new Thread(() -> {
            String name;
            do {
                System.out.println("->");
                name = System.console().readLine();
                String processedName = name.trim().toLowerCase();
                String[] cmd = name.split(" ");
                if (processEntities(processedName, cmd)) continue;

                if (processedName.equals("help")) {
                    System.out.println("Commands:");
                    System.out.println(" 1. list places");
                    System.out.println(" 2. create place [name]");
                    System.out.println(" 3. remove place [name]");
                    System.out.println(" 4. create agent [agent properties file path]");
                    System.out.println(" 5. remove agent [agent name] [hash code] [place name]");
                    System.out.println(" 6. list agents");
                    System.out.println(" 7. create agent [agent file name]");
                    System.out.println(" 8. start agent [agent list index]");
                    System.out.println(" 9. stop agent [agent list index]");
                    System.out.println(" 10. remove agent [agent list index]");
                    System.out.println(" 11. list permissions");
                    System.out.println(" 12. remove permission [permission list index]");
                    System.out.println(" 13. quit");
                } else if (!processedName.equals("quit")) {
                    LOGGER.error("Invalid command.");
                }
            } while (!name.equals("quit"));

            System.exit(0);
        });
        thread.start();
    }

    private boolean processEntities(String processedName, String[] cmd) {
        boolean found = processPlace(processedName, cmd);
        if (!found) {
            found = processAgent(processedName, cmd);
            if (!found) {
                found = processPermission(processedName, cmd);
            }
        }
        
        return found;
    }

    @Override
    public void notify(String operationName, Object... info) {
        switch (operationName) {
            case "agentLog" -> incomingAgentLog(info);
            case "serverStart" -> incomingServerStart(info);
            case "createPlace" -> incomingCreatePlace(info);
            case "createAgent" -> incomingCreateAgent(info);
            case "removeAgent" -> incomingRemoveAgent(info);
            case "removePlace" -> incomingRemovePlace(info);
            case "migrateAgent" -> incomingMigrateAgent(info);
            case "start", "stop" -> incomingStartStop(info, operationName);
            default -> LOGGER.error("{}: {} {}", "Agent Station", "Unknown operation name:", operationName);
        }
    }

    private void incomingStartStop(Object[] info, String operationName) {
        Instance inst = (Instance) info[0];
        if (operationName.equals("start")) {
            LOGGER.info("{}: Agent successfully started", IOAccess.getPlainAboutAgent(inst));
        } else if (operationName.equals("stop")) {
            LOGGER.info("{}: Agent successfully stopped", IOAccess.getPlainAboutAgent(inst));
        }
    }

    private void incomingMigrateAgent(Object[] info) {
        Instance inst = (Instance) info[0];
        String remoteServer = (String) info[1];
        String remotePort = (String) info[2];
        String remoteName = IOAccess.getServerAndPortText(remoteServer, Integer.valueOf(remotePort));
        String msg = (String) info[3];
        String dest = IOAccess.getServerAndPortText(remoteServer, Integer.valueOf(remotePort));

        String about = IOAccess.getPlainAboutAgent(inst);
        switch (msg) {
            case "noPermission" -> LOGGER.error("{}: No permission to migrate to {}", about, dest);
            case "registryCommFailed" ->
                    LOGGER.error("{}: Cannot migrate to {} as communication with remote registry failed", about, dest);
            case "stationCommFailed" ->
                    LOGGER.error("{}: Cannot migrate to {} as communication with remote station failed", about, remoteName);
            case "success" -> LOGGER.info("{}: Successfully migrated to {}", about, dest);
            default -> LOGGER.error("{}: {} {}", about, "Unknown notification message:", msg);
        }
    }

    private void initiateShutdownSupport() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                agentStation.shutdownStation();
                serverRunnable.shutdownServers();
                System.out.println("\nShutdown completed");
                LOGGER.info("Shutdown completed");
            }
        });
    }

    private void incomingRemovePlace(Object[] info) {
        String selectedPlaceName = (String) info[0];
        LOGGER.info("{}: Successfully created", IOAccess.getAboutPlace(selectedPlaceName));
    }

    private void incomingRemoveAgent(Object[] info) {
        Instance inst = (Instance) info[0];
        LOGGER.info("{}: Successfully removed", IOAccess.getPlainAboutAgent(inst));
    }

    private void incomingCreateAgent(Object[] info) {
        Instance inst = (Instance) info[0];
        LOGGER.info("{}: Agent successfully created", IOAccess.getPlainAboutAgent(inst));
    }

    private void incomingCreatePlace(Object[] info) {
        LOGGER.info("{}: Place successfully created", IOAccess.getAboutPlace((String) info[0]));
    }

    private void incomingServerStart(Object[] info) {
        if (info[0] != null) {

            System.exit(1);
        }
    }

    private void incomingAgentLog(Object[] info) {
        Instance instance = (Instance) info[0];
        Identity id = instance.getIdentity();
        LogType type = (LogType) info[1];
        String text = (String) info[2];
        LOGGER.info("{} Id={} - {}: {}", id.getName(), instance.getShortId(), type, text);
    }

}
