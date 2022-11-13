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

import uk.co.connectina.agentstation.api.*;
import uk.co.connectina.agentstation.api.client.OperationException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.connectina.agentstation.api.client.Collaboration;
import uk.co.connectina.agentstation.api.client.AgentInstance;
import uk.co.connectina.agentstation.api.client.LogType;

/**
 * Provides access to Agent Station capabilities.
 *
 * @author Dr Christos Bohoris
 */
public class AgentStation implements Station, AgentListener {

    private static final Logger LOGGER = LogManager.getLogger(AgentStation.class.toString());

    private Registry registry;
    private StationAssistant assistant;
    private StationInfo stationInfo;
    private transient StationMessenger stationMessenger = new StationMessenger();
    private transient PlaceManager placeManager;
    private transient AgentManager agentManager;

    public AgentStation(StationInfo stationInfo) throws OperationException {
        this(stationInfo, null);
    }

    public AgentStation(StationInfo stationInfo, StationListener listener) throws OperationException {
        this.stationInfo = stationInfo;
        assistant = new StationAssistant(this);
        registry = new AgentRegistry(this.stationInfo.getName(), assistant.getAssistantInstance());
        placeManager = new PlaceManager(registry, stationMessenger);
        agentManager = new AgentManager(stationInfo, this, registry, placeManager, stationMessenger, assistant, this);

        if (listener != null) {
            stationMessenger.addListener(listener);
        }

        initiate();
    }

    AgentStation() {
        stationInfo = new StationInfo("", "", RemoteSupport.RMI, "", 0);
        assistant = new StationAssistant(this);
        registry = new AgentRegistry(this.stationInfo.getName(), assistant.getAssistantInstance());
        placeManager = new PlaceManager(registry, stationMessenger);
        agentManager = new AgentManager(stationInfo, this, registry, placeManager, stationMessenger, assistant, this);
    }

    /**
     * Provides the agent registry.
     *
     * @return the agent registry
     */
    @Override
    public Registry getRegistry() {
        return registry;
    }

    /**
     * Shutdown this station.
     */
    @Override
    public void shutdownStation() {
        for (AgentRunnable ac : agentManager.getAgentRunnables()) {
            if (ac.isActive()) {
                ac.stop();
                LOGGER.info("{}: {}", IOAccess.getPlainAboutAgent(ac.getInstance()), "Stop requested");
            }
        }
    }

    /**
     * Initiates any scheduled stars associated with the agent.
     *
     * @param instance the agent instance details
     * @throws OperationException an error occurred
     */
    @Override
    public void initiateAnySchedule(Instance instance) throws OperationException {
        agentManager.initiateAnySchedule(instance);
    }

    /**
     * Called whenever permissions may have changed for a place.
     *
     * @param placeName the place name
     */
    @Override
    public void permissionsChange(String placeName) throws OperationException {
        List<Instance> instances = registry.lookupAgentsByPlaceName(placeName);
        List<Permission> permissions = registry.lookupPermissionsByPlaceName(placeName);
        List<Instance> notFoundInstances = registry.lookupAgentsByPlaceName(placeName);

        // Remove any agents that are now not allowed to run
        for (Instance instance : instances) {
            for (Permission permission : permissions) {
                if (instance.getIdentity().getName().equals(permission.getAgentName())
                        && instance.getShortId().equals(permission.getAgentShortId())) {
                    // Remove, as we found it
                    notFoundInstances.remove(instance);

                    if (!permission.isAllowed()) {
                        agentManager.removeAgent(instance);
                    }

                    break;
                }
            }
        }

        // Remove any instances for which no permission exists
        for (Instance instance : notFoundInstances) {
            agentManager.removeAgent(instance);
        }
    }

    @Override
    public void log(AgentInstance agentInstance, LogType type, String text) {
        agentManager.log(agentInstance, type, text);
    }

    @Override
    public void createAgent(Instance instance, byte[] agentBytes) throws OperationException {
        instanceValidation(instance);
        agentManager.createAgent(instance, agentBytes);
    }

    @Override
    public void migrateAgent(Instance instance, String remoteServer, int port, String placeName)
            throws OperationException {
        instanceValidation(instance);
        agentManager.migrateAgent(instance, remoteServer, port, placeName);
        deleteUnusedJar(instance.getIdentity().getPackageFile());
    }

    @Override
    public Collaboration collaborate(AgentInstance agentInstance, String agentName, String organisation,
            int majorVersion, int minorVersion) throws OperationException {
        for (AgentRunnable agentRunnable : agentManager.getAgentRunnables()) {
            Identity currentIdentity = agentRunnable.getInstance().getIdentity();
            if (agentRunnable.getInstance().getPlaceName().equals(agentInstance.getPlaceName())
                    && agentRunnable.isActive()
                    && agentRunnable.getAgent() instanceof Collaboration collaboration
                    && currentIdentity.getName().equals(agentName)
                    && currentIdentity.getOrganisation().equals(organisation)
                    && currentIdentity.getMajorVersion() == majorVersion
                    && currentIdentity.getMinorVersion() == minorVersion) {

                return collaboration;
            }
        }

        return null;
    }

    @Override
    public void createPlace(String name) throws OperationException {
        createPlaceValidation(name);
        placeManager.createPlace(name);
    }

    @Override
    public void startAgent(Instance instance) throws OperationException {
        instanceValidation(instance);
        if (!agentManager.isAgentActive(instance)) {
            agentManager.startAgent(instance);
        }
    }

    @Override
    public void stopAgent(Instance instance) throws OperationException {
        instanceValidation(instance);
        if (agentManager.isAgentActive(instance)) {
            agentManager.stopAgent(instance);
        }
    }

    @Override
    public List<LogEntry> getLog(Instance instance) {
        return agentManager.getLog(instance);
    }

    @Override
    public void removeAgent(Instance instance) throws OperationException {
        instanceValidation(instance);
        agentManager.removeAgent(instance);
        deleteUnusedJar(instance.getIdentity().getPackageFile());
    }

    @Override
    public boolean isAgentActive(Instance instance) throws OperationException {
        instanceValidation(instance);
        return agentManager.isAgentActive(instance);
    }

    @Override
    public void removePlace(String name) throws OperationException {
        removePlaceValidation(name);
        placeManager.removePlace(name);
    }

    @Override
    public void notify(Instance agentInstance, String operationName, Object... info) {
        if (operationName.equals("start") || operationName.equals("stop")) {
            try {
                registry.updateAgentState(agentInstance);
            } catch (OperationException e) {
                LOGGER.error("{}: {}", IOAccess.getPlainAboutAgent(agentInstance), e.getMessage());
            }
        }
        stationMessenger.notifyStationListeners(operationName, agentInstance, info);
    }

    boolean placeExists(String name) throws OperationException {
        return placeManager.placeExists(name);
    }

    void notifyStationListeners(String operationName, Object... info) {
        stationMessenger.notifyStationListeners(operationName, info);
    }

    void setSupport(Registry registry, AgentManager agentManager, PlaceManager placeManager) {
        this.registry = registry;
        this.agentManager = agentManager;
        this.placeManager = placeManager;
    }

    void instanceValidation(Instance instance) throws OperationException {
        if (instance == null) {
            throw new OperationException("The instance is missing.");
        }
        Identity newIdentity = instance.getIdentity();
        if (newIdentity == null) {
            throw new OperationException("Missing identity.");
        }
        if (newIdentity.getName() == null || newIdentity.getName().isBlank()
                || newIdentity.getClassName() == null || newIdentity.getClassName().isBlank()
                || newIdentity.getPackageFile() == null || newIdentity.getPackageFile().isBlank()
                || newIdentity.getOrganisation() == null || newIdentity.getOrganisation().isBlank()
                || newIdentity.getHashCode() == null || newIdentity.getHashCode().isBlank()
                || newIdentity.getDescription() == null || newIdentity.getDescription().isBlank()) {
            throw new OperationException("Missing identity information.");
        }
    }

    private void deleteUnusedJar(String fileUrl) throws OperationException {
        File file = null;
        try {
            file = new File(new URL(fileUrl).toURI());
        } catch (URISyntaxException | IOException e) {
            
            throw new OperationException(e);
        }
        
        List<Instance> instances = agentManager.getInstancesByPackageFile(file);
        if (instances.isEmpty()) {
            try {
                Files.delete(file.toPath());
            } catch (IOException ex) {
                file.deleteOnExit();
            }
        }
    }

    private void initiate() throws OperationException {
        initiatePlaces();

        // Initiate agents
        File[] files = IOAccess.APP_PACKAGE_FOLDER.listFiles((File folder, String name) -> name.endsWith(".jar"));
        for (File file : Objects.requireNonNull(files)) {
            List<Instance> instances = agentManager.getInstancesByPackageFile(file);
            if (instances.isEmpty()) {
                try {
                    Files.delete(file.toPath());
                } catch (IOException ex) {
                    file.deleteOnExit();
                }

                continue;
            }
            for (Instance instance : instances) {
                if (instance.getState() == Instance.State.ACTIVE) {
                    instance.setState(Instance.State.INACTIVE);
                    registry.updateAgentState(instance);
                }

                initiateAgent(instance);
            }
        }
    }

    private void initiateAgent(Instance instance) throws OperationException {
        Permission permission = registry.lookupPermission(new PermissionIdentity(instance.getIdentity().getName(),
                instance.getShortId(), instance.getPlaceName()));
        if (permission != null && permission.isAllowed()) {
            createAgent(instance, null);
            if (permission.isAutoStart()) {
                startAgent(instance);
            }
        }
    }

    private void initiatePlaces() throws OperationException {
        // Initiate places
        List<String> registeredPlaces = registry.lookupPlaces();
        for (String place : registeredPlaces) {
            placeManager.loadPlace(place);
        }
        if (registeredPlaces.isEmpty()) {
            placeManager.createPlace("Default");
        }
    }

    private void createPlaceValidation(String name) throws OperationException {
        if (name == null || name.isBlank()) {

            throw new OperationException("The place name cannot be empty.");
        }
        if (placeExists(name)) {

            throw new OperationException("A place with this name already exists.");
        }
    }

    private void removePlaceValidation(String name) throws OperationException {
        if (name == null || name.isBlank()) {

            throw new OperationException("The place name cannot be empty.");
        }
        if (!placeExists(name)) {

            throw new OperationException("A place with this name does not exist.");
        }
        if (name.equalsIgnoreCase("default")) {

            throw new OperationException("The Default place cannot be removed.");
        }
    }

}
