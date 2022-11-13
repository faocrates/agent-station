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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.connectina.agentstation.api.Identity;
import uk.co.connectina.agentstation.api.Instance;
import uk.co.connectina.agentstation.api.Permission;
import uk.co.connectina.agentstation.api.PermissionIdentity;
import uk.co.connectina.agentstation.api.Registry;
import uk.co.connectina.agentstation.api.Station;
import uk.co.connectina.agentstation.api.client.OperationException;
import uk.co.connectina.agentstation.local.IOAccess;

/**
 * An interactive commands client for agent management.
 *
 * @author Dr Dr Christos Bohoris
 */
final class AgentsClient {

    private final Registry registry;
    private final Station station;
    private Properties properties;
    private static final Logger LOGGER = LogManager.getLogger(AgentsClient.class.toString());

    AgentsClient(Station station, Registry registry) {
        this.station = station;
        this.registry = registry;
    }

    void listAgents() {
        try {
            List<Instance> agentInstances = registry.lookupAgents();
            System.out.println("Agents:");
            int index = 1;
            for (Instance instance : agentInstances) {
                System.out.println(" " + index++ + ". " + IOAccess.getPlainAboutAgent(instance) + ", " + instance.getPlaceName() + ": " + instance.getState());
            }
        } catch (OperationException e) {
            LOGGER.error(e);
        }
    }

    void createAgent(String[] cmd) {
        Instance instance = getInstance(cmd[2]);
        if (instance == null) {

            return;
        }

        try {
            List<Instance> instances = registry.lookupAgents();
            if (instances.contains(instance)) {
                LOGGER.error("This agent already exists.");

                return;
            }

            Permission permission = registry.lookupPermission(new PermissionIdentity(instance.getIdentity().getName(), instance.getShortId(), instance.getPlaceName()));
            if (permission == null) {
                String allowed = properties.getProperty("allowed", "true");
                String autoStart = properties.getProperty("autoStart", "false");
                
                permission = new Permission(instance.getIdentity().getName(), instance.getShortId(), instance.getPlaceName(), Boolean.valueOf(allowed), Boolean.valueOf(autoStart));
                registry.registerPermission(permission);
            }

            if (permission.isAllowed()) {
                station.createAgent(instance, null);
                if (permission.isAutoStart()) {
                    station.startAgent(instance);
                }
            } else {
                LOGGER.error("{}: {}", IOAccess.getAboutAgent(instance), "Not allowed in this place");
            }
        } catch (OperationException e) {
            LOGGER.error(e);
        }
    }

    void startAgent(String[] cmd) {
        Instance instance = lookupAgent(cmd);
        if (instance == null) {

            return;
        }

        try {
            station.startAgent(instance);
        } catch (OperationException e) {
            LOGGER.error(e);
        }
    }

    void stopAgent(String[] cmd) {
        Instance instance = lookupAgent(cmd);
        if (instance == null) {

            return;
        }

        try {
            station.stopAgent(instance);
        } catch (OperationException e) {
            LOGGER.error(e);
        }
    }

    void removeAgent(String[] cmd) {
        Instance instance = lookupAgent(cmd);
        if (instance == null) {

            return;
        }

        try {
            station.removeAgent(instance);
        } catch (OperationException e) {
            LOGGER.error(e);
        }
    }

    private Instance lookupAgent(String[] cmd) {
        if (cmd.length != 3) {

            return null;
        }

        int index;
        try {
            index = Integer.valueOf(cmd[2]);
        } catch (NumberFormatException e) {
            LOGGER.error(e);

            return null;
        }

        List<Instance> agentInstances;
        try {
            agentInstances = registry.lookupAgents();
        } catch (OperationException e) {
            LOGGER.error(e);

            return null;
        }

        if (index < 1 || index > agentInstances.size()) {
            LOGGER.error("Invalid agent list index");

            return null;
        }

        return agentInstances.get(index - 1);
    }

    private Instance getInstance(String fileName) {
        Instance instance;
        properties = new Properties();

        File file = new File(IOAccess.APP_PROPERTY_FOLDER + File.separator + fileName + ".properties");
        try ( FileInputStream fis = new FileInputStream(file)) {

            properties.load(fis);

            String org = properties.getProperty("organisation", "");
            int majorVersion = Integer.parseInt(properties.getProperty("majorVersion", ""));
            int minorVersion = Integer.parseInt(properties.getProperty("minorVersion", ""));
            String hash = properties.getProperty("hashCode", "");
            String className = properties.getProperty("className", "");
            String descr = properties.getProperty("Description", "");
            String loc = properties.getProperty("packageLocation", "");
            String placeName = properties.getProperty("placeName", "");
            String[] params = properties.getProperty("parameters", "").split(",");
            String hashCodeOfPackage = IOAccess.getFileMD5(loc);
            if (!hash.isBlank() && !hash.equals(hashCodeOfPackage)) {
                LOGGER.error("Hash code mismatch. Property value: {}. Package value: {}.", hash, hashCodeOfPackage);

                return null;
            }
            Identity identity = new Identity.IdentityBuilder(className, org).hashCode(hash).packageFile(loc).version(majorVersion, minorVersion).description(descr).build();
            instance = new Instance(identity, LocalDateTime.now(), placeName, params);
        } catch (IOException e) {
            LOGGER.error(e);

            return null;
        }

        return instance;
    }

}
