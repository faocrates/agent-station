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

import java.text.MessageFormat;
import java.util.List;

import uk.co.connectina.agentstation.api.*;
import uk.co.connectina.agentstation.api.client.OperationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.connectina.agentstation.api.client.Assistant;
import uk.co.connectina.agentstation.api.client.LogType;
import uk.co.connectina.agentstation.local.dao.AgentAccess;
import uk.co.connectina.agentstation.local.dao.PermissionAccess;
import uk.co.connectina.agentstation.local.dao.PlaceAccess;
import uk.co.connectina.agentstation.local.dao.ScheduleAccess;

/**
 * Persists and gives access to local Agent Station-related information.
 *
 * @author Dr Christos Bohoris
 */
public class AgentRegistry implements Registry {

    public static final String MISSING_AGENT_INFORMATION = "Missing agent information.";
    public static final String MISSING_PERMISSION_INFORMATION = "Missing permission information.";
    public static final String MISSING_PLACE_NAME_INFORMATION = "Missing place name information.";
    private static final Logger LOGGER = LogManager.getLogger(AgentRegistry.class.toString());
    private transient AgentAccess agentsAccess;
    private transient PlaceAccess placesAccess;
    private transient PermissionAccess permissionsAccess;
    private transient ScheduleAccess schedulesAccess;
    private transient Assistant stationAssistant;

    /**
     * Initiates a new object instance.
     *
     * @param stationName the local station name
     */
    public AgentRegistry(String stationName, Assistant stationAssistant) {
        this.stationAssistant = stationAssistant;
        agentsAccess = new AgentAccess(stationName);
        placesAccess = new PlaceAccess(stationName);
        permissionsAccess = new PermissionAccess(stationName);
        schedulesAccess = new ScheduleAccess();

        try {
            agentsAccess.createSchema();
            placesAccess.createSchema();
            permissionsAccess.createSchema();
            schedulesAccess.createSchema();
        } catch (OperationException e) {
            LOGGER.error(e);
        }
    }

    AgentRegistry() {

    }

    /**
     * Registers an Agent.
     *
     * @param instance the agent instance details
     * @throws OperationException an error occurred
     */
    @Override
    public void registerAgent(Instance instance) throws OperationException {
        registerAgentValidation(instance);
        agentsAccess.create(instance);
    }

    /**
     * Registers a schedule to start an agent.
     *
     * @param schedule a schedule
     * @throws OperationException an error occurred
     */
    @Override
    public void registerSchedule(Schedule schedule) throws OperationException {
        registerScheduleValidation(schedule);
        if (!schedulesAccess.exists(schedule.getAgentSid())) {
            schedulesAccess.create(schedule);
        } else {
            schedulesAccess.update(schedule);
        }
    }

    /**
     * De-registers a schedule for an agent.
     *
     * @param agentSid the system id for the agent
     * @throws OperationException an error occurred
     */
    @Override
    public void deregisterSchedule(long agentSid) throws OperationException {
        if (agentSid < 1) {

            throw new OperationException("Invalid agent system id.");
        }
        if (schedulesAccess.exists(agentSid)) {
            schedulesAccess.delete(agentSid);
        }
    }

    /**
     * Registers a Place.
     *
     * @param name the place name
     * @throws OperationException an error occurred
     */
    @Override
    public void registerPlace(String name) throws OperationException {
        registerPlaceValidation(name);
        placesAccess.create(name);
    }

    /**
     * Registers a Permission.
     *
     * @param permission the permission
     * @throws OperationException an error occurred
     */
    @Override
    public void registerPermission(Permission permission) throws OperationException {
        registerPermissionValidation(permission);
        permissionsAccess.create(permission);
    }

    /**
     * Provides a list of all registered agents.
     *
     * @return the agents
     * @throws OperationException an error occurred
     */
    @Override
    public List<Instance> lookupAgents() throws OperationException {

        return agentsAccess.readList();
    }

    /**
     * Provides a list of all schedules.
     *
     * @return the schedules
     * @throws OperationException an error occurred
     */
    @Override
    public List<Schedule> lookupSchedules() throws OperationException {

        return schedulesAccess.readList();
    }

    /**
     * Provides a list of all registered permissions.
     *
     * @return the permissions
     * @throws OperationException an error occurred
     */
    @Override
    public List<Permission> lookupPermissions() throws OperationException {

        return permissionsAccess.readList();
    }

    /**
     * Provides a schedule if one is associated with the given system id of an
     * agent.
     *
     * @param agentSid the system id of an agent
     * @return the schedule
     * @throws OperationException an error occurred
     */
    @Override
    public Schedule lookupScheduleByAgentSid(long agentSid) throws OperationException {

        return schedulesAccess.read(agentSid);
    }

    /**
     * Provides a list of all registered permissions for a Place.
     *
     * @param placeName the place name
     * @return the permissions
     * @throws OperationException an error occurred
     */
    @Override
    public List<Permission> lookupPermissionsByPlaceName(String placeName) throws OperationException {
        if (placeName == null || placeName.isBlank()) {
            throw new OperationException(MISSING_PLACE_NAME_INFORMATION);
        }

        return permissionsAccess.readByPlaceName(placeName);
    }

    /**
     * Provides a list of all registered agents that reside in a Place.
     *
     * @param placeName the place name
     * @return the agents
     * @throws OperationException an error occurred
     */
    @Override
    public List<Instance> lookupAgentsByPlaceName(String placeName) throws OperationException {
        if (placeName == null || placeName.isBlank()) {
            throw new OperationException(MISSING_PLACE_NAME_INFORMATION);
        }

        return agentsAccess.readByPlaceName(placeName);
    }

    /**
     * Provides a list of all registered agents associated with a package file.
     *
     * @param packageFile the package file
     * @return the agents
     * @throws OperationException an error occurred
     */
    @Override
    public List<Instance> lookupAgentsByPackageFile(String packageFile) throws OperationException {
        if (packageFile == null || packageFile.isBlank()) {
            throw new OperationException("Missing package file information.");
        }

        return agentsAccess.readByPackageFile(packageFile);
    }

    /**
     * Provides the system id of an agent.
     *
     * @param instance the agent instance details
     * @return the system id of an agent
     * @throws OperationException an error occurred
     */
    @Override
    public long lookupAgentSid(Instance instance) throws OperationException {

        return agentsAccess.read(instance).getSid();
    }

    /**
     * Provides the registered permission that matches the given identity.
     *
     * @param permissionIdentity the permission id
     * @return the permission
     * @throws OperationException an error occurred
     */
    @Override
    public Permission lookupPermission(PermissionIdentity permissionIdentity) throws OperationException {
        if (permissionIdentity == null
                || permissionIdentity.getAgentName() == null || permissionIdentity.getAgentName().isBlank()
                || permissionIdentity.getAgentShortId() == null || permissionIdentity.getAgentShortId().isBlank()
                || permissionIdentity.getPlaceName() == null || permissionIdentity.getPlaceName().isBlank()) {
            throw new OperationException(MISSING_PERMISSION_INFORMATION);
        }

        return permissionsAccess.read(permissionIdentity);
    }

    /**
     * De-registers a Permission.
     *
     * @param permissionIdentity the permission id
     * @throws OperationException an error occurred
     */
    @Override
    public void deregisterPermission(PermissionIdentity permissionIdentity) throws OperationException {
        if (permissionIdentity == null
                || permissionIdentity.getAgentName() == null || permissionIdentity.getAgentName().isBlank()
                || permissionIdentity.getAgentShortId() == null || permissionIdentity.getAgentShortId().isBlank()
                || permissionIdentity.getPlaceName() == null || permissionIdentity.getPlaceName().isBlank()) {
            throw new OperationException(MISSING_PERMISSION_INFORMATION);
        }

        permissionsAccess.delete(permissionIdentity);
    }

    /**
     * Provides whether the place exists.
     *
     * @return exists or not
     * @throws OperationException an error occurred
     */
    @Override
    public List<String> lookupPlaces() throws OperationException {

        return placesAccess.readList();
    }

    /**
     * Updates the state of an agent.
     *
     * @param instance the agent instance details
     * @throws OperationException an error occurred
     */
    @Override
    public void updateAgentState(Instance instance) throws OperationException {
        if (instance == null || instance.getIdentity() == null) {

            throw new OperationException(MISSING_AGENT_INFORMATION);
        }

        agentsAccess.update(instance);
    }

    /**
     * Updates the parameters of an agent.
     *
     * @param instance the agent instance details
     * @throws OperationException an error occurred
     */
    @Override
    public void updateAgentParameters(Instance instance) throws OperationException {
        if (instance == null || instance.getIdentity() == null) {

            throw new OperationException(MISSING_AGENT_INFORMATION);
        }

        agentsAccess.update(instance);

        stationAssistant.log(instance, LogType.INFO,
                MessageFormat.format("Parameters changed: {0}", instance.getCommaSeparatedParameters()));
    }

    /**
     * De-registers an Agent.
     *
     * @param instance the agent instance details
     * @throws OperationException an error occurred
     */
    @Override
    public void deregisterAgent(Instance instance) throws OperationException {
        if (instance == null || instance.getIdentity() == null) {

            throw new OperationException(MISSING_AGENT_INFORMATION);
        }

        agentsAccess.delete(instance);
    }

    /**
     * De-registers a Place.
     *
     * @param placeName the place name
     * @throws OperationException an error occurred
     */
    @Override
    public void deregisterPlace(String placeName) throws OperationException {
        if (placeName == null || placeName.isBlank()) {
            throw new OperationException(MISSING_PLACE_NAME_INFORMATION);
        }

        placesAccess.delete(placeName);
    }

    /**
     * Provides whether the agent with these instance details exists.
     *
     * @param instance the agent instance details
     * @return exists or not
     * @throws OperationException an error occurred
     */
    @Override
    public boolean agentExists(Instance instance) throws OperationException {
        if (instance == null || instance.getIdentity() == null) {

            throw new OperationException(MISSING_AGENT_INFORMATION);
        }

        return agentsAccess.exists(instance);
    }

    /**
     * Provides whether the schedule exists.
     *
     * @param agentSid the system id for the agent
     * @return exists or not
     * @throws OperationException an error occurred
     */
    @Override
    public boolean scheduleExists(long agentSid) throws OperationException {

        return schedulesAccess.exists(agentSid);
    }

    void setAccessSupport(AgentAccess agentsAccess, PlaceAccess placesAccess, PermissionAccess permissionsAccess) {
        this.agentsAccess = agentsAccess;
        this.placesAccess = placesAccess;
        this.permissionsAccess = permissionsAccess;
    }

    void registerScheduleValidation(Schedule schedule) throws OperationException {
        if (schedule == null || schedule.getStartDate() == null || schedule.getStartTime() == null) {

            throw new OperationException("Schedule information is missing.");
        }
    }

    void registerAgentValidation(Instance instance) throws OperationException {
        if (agentExists(instance)) {

            throw new OperationException("This agent already exists.");
        }
        if (instance == null || instance.getIdentity() == null) {

            throw new OperationException(MISSING_AGENT_INFORMATION);
        }
    }

    void registerPlaceValidation(String name) throws OperationException {
        if (name == null || name.isBlank()) {

            throw new OperationException("The place name cannot be empty.");
        }
        if (placeExists(name)) {

            throw new OperationException("A place with this name already exists.");
        }
    }

    void registerPermissionValidation(Permission permission) throws OperationException {
        if (permissionExists(permission)) {

            throw new OperationException("This permission already exists.");
        }
        if (permission == null) {

            throw new OperationException(MISSING_PERMISSION_INFORMATION);
        }
    }

    boolean placeExists(String placeName) throws OperationException {
        if (placeName == null || placeName.isBlank()) {
            throw new OperationException(MISSING_PLACE_NAME_INFORMATION);
        }

        return placesAccess.exists(placeName);
    }

    boolean permissionExists(PermissionIdentity permissionIdentity) throws OperationException {
        if (permissionIdentity == null
                || permissionIdentity.getAgentName() == null || permissionIdentity.getAgentName().isBlank()
                || permissionIdentity.getAgentShortId() == null || permissionIdentity.getAgentShortId().isBlank()
                || permissionIdentity.getPlaceName() == null || permissionIdentity.getPlaceName().isBlank()) {
            throw new OperationException("Missing permission identity information.");
        }

        return permissionsAccess.exists(permissionIdentity);
    }

}
