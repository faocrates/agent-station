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
package uk.co.connectina.agentstation.local;

import io.grpc.ManagedChannel;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.connectina.agentstation.api.AgentListener;
import uk.co.connectina.agentstation.api.AgentRunnable;
import uk.co.connectina.agentstation.api.Identity;
import uk.co.connectina.agentstation.api.Instance;
import uk.co.connectina.agentstation.api.Permission;
import uk.co.connectina.agentstation.api.Registry;
import uk.co.connectina.agentstation.api.Station;
import uk.co.connectina.agentstation.api.StationAssistant;
import uk.co.connectina.agentstation.api.client.Agent;
import uk.co.connectina.agentstation.api.client.AgentInstance;
import uk.co.connectina.agentstation.api.client.LogType;
import uk.co.connectina.agentstation.api.client.OperationException;
import uk.co.connectina.agentstation.api.grpc.RemoteRegistryGrpc;
import uk.co.connectina.agentstation.api.grpc.RemoteStationGrpc;
import uk.co.connectina.agentstation.api.rmi.RemoteRegistry;
import uk.co.connectina.agentstation.api.rmi.RemoteStation;

/**
 * Handles the required actions that allow agent management.
 *
 * @author Dr Christos Bohoris
 */
class AgentManager implements Serializable, ScheduleTaskListener {

    private static class ScheduleTask extends TimerTask {

        private static final Logger LOGGER = LogManager.getLogger(ScheduleTask.class.toString());
        private Instance instance;
        private Station agentStation;
        private ScheduleTaskListener listener;

        ScheduleTask(Instance instance, Station agentStation, ScheduleTaskListener listener) {
            this.instance = instance;
            this.agentStation = agentStation;
            this.listener = listener;
        }

        @Override
        public void run() {
            listener.notifyScheduledRun(instance);

            try {
                agentStation.startAgent(instance);
            } catch (OperationException e) {
                LOGGER.error(e);
            }
        }

    }
    private static final String MIGRATE_AGENT = "migrateAgent";
    private static final String REMOTE_PERMISSION_LOOKUP_FAILED = "Remote permission lookup failed";
    private static final String MESSAGE_ERROR_PATTERN = "{0}: {1}";
    private static final Logger LOGGER = LogManager.getLogger(AgentManager.class.toString());
    private StationMessenger stationMessenger;
    private Station station;
    private Registry registry;
    private StationInfo stationInfo;
    private PlaceManager placeManager;
    private StationAssistant assistant;
    private AgentListener agentListener;
    private final List<AgentRunnable> agents;

    private final transient List<List<LogEntry>> logs;

    AgentManager(StationInfo stationInfo, Station station, Registry registry, PlaceManager placeManager,
            StationMessenger stationMessenger, StationAssistant stationAssistant, AgentListener agentListener) {
        this.stationInfo = stationInfo;
        this.station = station;
        this.registry = registry;
        this.placeManager = placeManager;
        this.stationMessenger = stationMessenger;
        this.assistant = stationAssistant;
        this.agentListener = agentListener;
        agents = new ArrayList<>();
        logs = new ArrayList<>();
    }

    @Override
    public void notifyScheduledRun(Instance instance) {
        try {
            initiateAnySchedule(instance);
        } catch (OperationException e) {
            LOGGER.error(e);
        }
    }

    public void migrateAgent(Instance instance, String ipAddress, int port, String placeName)
            throws OperationException {
        RemoteRegistryGrpc.RemoteRegistryBlockingStub remoteGRPCRegistry;
        RemoteRegistry remoteRMIRegistry;
        Permission permission;

        // Get permission from remote registry
        if (stationInfo.getRemote() == RemoteSupport.GRPC) {
            remoteGRPCRegistry = GRPCUtility.getGRPCRegistry(ipAddress, port, instance, stationMessenger);
            permission = GRPCUtility.getPermissionWithGRPC(instance, remoteGRPCRegistry, placeName);
            ((ManagedChannel) remoteGRPCRegistry.getChannel()).shutdown();
        } else {
            remoteRMIRegistry = RMIUtility.getRMIRegistry(ipAddress, port, instance, stationMessenger);
            permission = RMIUtility.getPermissionWithRMI(instance, remoteRMIRegistry, placeName);
        }

        // Check permission
        try {
            if (permission == null || !permission.isAllowed()) {
                stationMessenger.notifyStationListeners(MIGRATE_AGENT, instance, ipAddress, ((Integer) port).toString(),
                        "noPermission");
                AgentRunnable agent = getAgent(instance);
                if (agent == null) {
                    throw new OperationException("The agent cannot be found.");
                }
                if (agent.isActive()) {
                    stopAgent(instance);
                }
                removeAgent(instance);

                return;
            }
        } catch (Exception e) {
            throw new OperationException(
                    MessageFormat.format(MESSAGE_ERROR_PATTERN, REMOTE_PERMISSION_LOOKUP_FAILED, e.getMessage()));
        }

        // Get agent
        String packageName = instance.getIdentity().getPackageFile()
                .substring(instance.getIdentity().getPackageFile().lastIndexOf("/") + 1);
        AgentRunnable agent = getAgent(instance);
        if (agent == null) {
            throw new OperationException("The agent cannot be found.");
        }

        stopAndRemoveAgent(instance, agent);

        // Transfer agent
        byte[] agentBytes = IOAccess.toByteArray(agent);
        try {
            if (stationInfo.getRemote() == RemoteSupport.GRPC) {
                RemoteStationGrpc.RemoteStationBlockingStub remoteGRPCStation = GRPCUtility.getGRPCStation(ipAddress,
                        port, instance, stationMessenger);
                GRPCUtility.transferAgentWithGRPC(instance, agentBytes, packageName, placeName, remoteGRPCStation);
                ((ManagedChannel) remoteGRPCStation.getChannel()).shutdown();
            } else {
                RemoteStation remoteRMIStation = RMIUtility.getRMIAgentStation(ipAddress, port);
                remoteRMIStation.transferAgent(instance, agentBytes, packageName, placeName,
                        IOAccess.readFromFile(new URL(instance.getIdentity().getPackageFile()).getFile()));
            }
            stationMessenger.notifyStationListeners(MIGRATE_AGENT, instance, ipAddress, ((Integer) port).toString(),
                    "success");
        } catch (Exception e) {
            stationMessenger.notifyStationListeners(MIGRATE_AGENT, instance, ipAddress, ((Integer) port).toString(),
                    "stationCommFailed");
            throw new OperationException(MessageFormat.format("Agent transfer failed. {0}", e.getMessage()));
        }
    }

    void createAgent(Instance instance, byte[] agentBytes) throws OperationException {
        Identity newIdentity = instance.getIdentity();
        String packageLocation = newIdentity.getPackageFile();
        try {
            IOAccess.copyPackage(packageLocation);
        } catch (IOException e) {
            LOGGER.error(e);
            throw new OperationException(e);
        }

        String localPackageLocation = IOAccess.checkMD5(packageLocation, newIdentity.getHashCode());
        String localPackageLocationURL = IOAccess.fromPathToURL(localPackageLocation);
        newIdentity.setPackageFile(localPackageLocationURL);

        // Check and create place if it doesn't exist
        String placeName = instance.getPlaceName();
        if (!placeManager.placeExists(placeName)) {
            placeManager.createPlace(instance.getPlaceName());
        }

        // Add a new agent
        if (agentBytes == null) {
            // Create agent instance
            Agent agent;
            try {
                agent = (Agent) createObject(newIdentity.getClassName(), packageLocation);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException
                    | ClassNotFoundException | IOException e) {
                throw new OperationException(e);
            }
            instance.setHomeStationLocation(stationInfo, placeName);

            agents.add(new AgentRunnable(assistant.getAssistantInstance(), instance, agent, agentListener));
            logs.add(new ArrayList<>());
        } else { // Add a migrated agent
            try {
                AgentRunnable agentRef = (AgentRunnable) IOAccess.toObject(agentBytes,
                        new URL(localPackageLocationURL));
                agentRef.setAgentListener(agentListener);
                agentRef.setAssistant(assistant.getAssistantInstance());
                instance = agentRef.getInstance();
                instance.setPlaceName(placeName);
                instance.setLastRemoteStationLocation(stationInfo, placeName);
                agents.add(agentRef);
                logs.add(new ArrayList<>());
            } catch (MalformedURLException | OperationException e) {
                throw new OperationException(e);
            }

        }

        String description = instance.getIdentity().getDescription();
        if (description != null && !description.isBlank()) {
            assistant.log(instance, LogType.INFO, MessageFormat.format("Description: {0}", description));
        }
        if (instance.getParameters() != null && instance.getParameters().length > 0) {
            assistant.log(instance, LogType.INFO,
                    MessageFormat.format("Parameters: {0}", instance.getCommaSeparatedParameters()));
        }

        // Register agent
        if (!registry.agentExists(instance)) {
            registry.registerAgent(instance);
        }

        initiateAnySchedule(instance);

        // Notify station listeners
        stationMessenger.notifyStationListeners("createAgent", instance);
    }

    void initiateAnySchedule(Instance instance) throws OperationException {
        if (instance.getSid() == 0) {
            instance.setSid(registry.lookupAgentSid(instance));
        }

        Schedule schedule = registry.lookupScheduleByAgentSid(instance.getSid());
        // If there is no schedule for this agent there is nothing more to do here
        if (schedule == null) {

            return;
        }
        
        LocalDateTime nextDateTime = schedule.getNextOccurence();
        if (nextDateTime != null) {
            Date nextDate = Date.from(nextDateTime.atZone(ZoneId.systemDefault()).toInstant());
            // If there is one in the future then schedule it
            Timer timer = new Timer();
            ScheduleTask scheduleTask = new ScheduleTask(instance, station, this);
            timer.schedule(scheduleTask, nextDate);
        } else {
            // If there is no schedule in the future then remove the schedule
            registry.deregisterSchedule(schedule.getAgentSid());
        }
    }

    void log(AgentInstance agentInstance, LogType type, String text) {
        final int LOG_LIMIT = 50;
        AgentRunnable agent = getAgent((Instance) agentInstance);
        if (agent != null) {
            int index = agents.indexOf(agent);
            List<LogEntry> log = logs.get(index);
            LogEntry logEntry = new LogEntry(LocalDateTime.now(), type,
                    IOAccess.getAboutAgent(((Instance) agentInstance)), text);

            log.add(0, logEntry);
            int size = log.size();
            if (size > LOG_LIMIT) {
                log.subList(LOG_LIMIT, size).clear();
            }

            stationMessenger.notifyStationListeners("agentLog", agentInstance, type, text);
        }
    }

    void startAgent(Instance instance) throws OperationException {
        AgentRunnable agent = getAgent(instance);
        if (agent != null) {
            agent.start();
            registry.updateAgentState(agent.getInstance());
        }
    }

    void stopAgent(Instance instance) throws OperationException {
        AgentRunnable agent = getAgent(instance);
        if (agent != null) {
            agent.stop();
            registry.updateAgentState(agent.getInstance());
        }
    }

    List<LogEntry> getLog(Instance instance) {
        AgentRunnable agent = getAgent(instance);
        if (agent != null) {
            int index = agents.indexOf(agent);

            return logs.get(index);
        }

        return new ArrayList<>();
    }

    void removeAgent(Instance instance) throws OperationException {
        AgentRunnable agent = getAgent(instance);
        if (agent != null && agent.isActive()) {
            agent.stop();
        }
        int index = agents.indexOf(agent);
        boolean removed = agents.remove(agent);
        if (!removed) {
            throw new OperationException("Agent removal failed.");
        } else {
            logs.remove(index);
        }

        registry.deregisterAgent(instance);
        stationMessenger.notifyStationListeners("removeAgent", instance);
    }

    boolean isAgentActive(Instance instance) {
        AgentRunnable agent = getAgent(instance);
        if (agent != null) {
            return agent.isActive();
        }

        return false;
    }

    AgentRunnable getAgent(Instance instance) {
        for (AgentRunnable agent : agents) {
            if (agent.getInstance().equals(instance)) {

                return agent;
            }
        }

        return null;
    }

    List<AgentRunnable> getAgentRunnables() {
        return agents;
    }

    List<Instance> getInstancesByPackageFile(File file) throws OperationException {
        List<Instance> instances;
        try {
            instances = registry.lookupAgentsByPackageFile(file.toURI().toURL().toString());
        } catch (MalformedURLException e) {
            LOGGER.error(e);

            return Collections.emptyList();
        }

        return instances;
    }

    private void stopAndRemoveAgent(Instance instance, AgentRunnable agent) throws OperationException {
        if (agent.isActive()) {
            agent.stop();
        }
        removeAgent(instance);
    }

    private Object createObject(String className, String packageLocation)
            throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException,
            ClassNotFoundException, IOException {
        URL url = IOAccess.copyPackage(packageLocation);

        URLClassLoader classLoader = new URLClassLoader(new URL[] { url }, this.getClass().getClassLoader());

        return Class.forName(className, true, classLoader).getDeclaredConstructor().newInstance();
    }

}
