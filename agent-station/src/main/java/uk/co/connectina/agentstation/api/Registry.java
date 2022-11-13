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
package uk.co.connectina.agentstation.api;

import uk.co.connectina.agentstation.local.Schedule;
import uk.co.connectina.agentstation.api.client.OperationException;

import java.io.Serializable;
import java.util.List;

/**
 * Persists and gives access to Agent Station-related information.
 *
 * @author Dr Christos Bohoris
 */
public interface Registry extends Serializable {

    /**
     * Registers an Agent.
     * 
     * @param instance the agent instance details
     * @throws OperationException an error occurred
     */
    void registerAgent(Instance instance) throws OperationException;

    /**
     * Registers a Place.
     * 
     * @param name the place name
     * @throws OperationException an error occurred
     */
    void registerPlace(String name) throws OperationException;

    /**
     * Registers a Permission.
     * 
     * @param permission the permission
     * @throws OperationException an error occurred
     */
    void registerPermission(Permission permission) throws OperationException;

    /**
     * Provides a list of all registered agents.
     * 
     * @return the agents
     * @throws OperationException an error occurred
     */
    List<Instance> lookupAgents() throws OperationException;

    /**
     * Provides a list of all registered permissions.
     * 
     * @return the permissions
     * @throws OperationException an error occurred
     */
    List<Permission> lookupPermissions() throws OperationException;

    /**
     * Provides a list of all schedules.
     *
     * @return the schedules
     * @throws OperationException an error occurred
     */
    List<Schedule> lookupSchedules() throws OperationException;

    /**
     * Provides a list of all registered agents that reside in a Place.
     * 
     * @param placeName the place name
     * @return the agents
     * @throws OperationException an error occurred
     */
    List<Instance> lookupAgentsByPlaceName(String placeName) throws OperationException;

    /**
     * Provides a list of all registered agents associated with a package file.
     * 
     * @param packageFile the package file
     * @return the agents
     * @throws OperationException an error occurred
     */
    List<Instance> lookupAgentsByPackageFile(String packageFile) throws OperationException;

    /**
     * Provides whether the agent with these instance details exists.
     * 
     * @param instance the agent instance details
     * @return exists or not
     * @throws OperationException an error occurred
     */
    boolean agentExists(Instance instance) throws OperationException;

    /**
     * Updates the state of an agent.
     * 
     * @param instance the agent instance details
     * @throws OperationException an error occurred
     */
    void updateAgentState(Instance instance) throws OperationException;

    /**
     * Updates the parameters of an agent.
     *
     * @param instance the agent instance details
     * @throws OperationException an error occurred
     */
    void updateAgentParameters(Instance instance) throws OperationException;

    /**
     * Provides whether the place exists.
     * 
     * @return exists or not
     * @throws OperationException an error occurred
     */
    List<String> lookupPlaces() throws OperationException;

    /**
     * Provides the registered permission that matches the given details.
     *
     * @param permissionIdentity the permission id
     * @return the permission
     * @throws OperationException an error occurred
     */
    Permission lookupPermission(PermissionIdentity permissionIdentity) throws OperationException;

    /**
     * Provides a list of all registered permissions for a Place.
     * 
     * @param placeName the place name
     * @return the permissions
     * @throws OperationException an error occurred
     */
    List<Permission> lookupPermissionsByPlaceName(String placeName) throws OperationException;

    /**
     * De-registers an Agent.
     * 
     * @param instance the agent instance details
     * @throws OperationException an error occurred
     */
    void deregisterAgent(Instance instance) throws OperationException;

    /**
     * De-registers a Place.
     * 
     * @param name the place name
     * @throws OperationException an error occurred
     */
    void deregisterPlace(String name) throws OperationException;

    /**
     * De-registers a Permission.
     *
     * @param permissionIdentity the permission id
     * @throws OperationException an error occurred
     */
    void deregisterPermission(PermissionIdentity permissionIdentity) throws OperationException;

    /**
     * Registers a schedule to start an agent.
     *
     * @param schedule a schedule
     * @throws OperationException an error occurred
     */
    void registerSchedule(Schedule schedule) throws OperationException;

    /**
     * De-registers a schedule for an agent.
     *
     * @param agentSid the system id for the agent
     * @throws OperationException an error occurred
     */
    public void deregisterSchedule(long agentSid) throws OperationException;

    /**
     * Provides whether the schedule exists.
     * 
     * @param agentSid the system id for the agent
     * @return exists or not
     * @throws OperationException an error occurred
     */
    boolean scheduleExists(long agentSid) throws OperationException;

    /**
     * Provides a schedule if one is associated with the given system id of an
     * agent.
     * 
     * @param agentSid the system id of an agent
     * @return the schedule
     * @throws OperationException an error occurred
     */
    Schedule lookupScheduleByAgentSid(long agentSid) throws OperationException;

    /**
     * Provides the system id of an agent.
     * 
     * @param instance the agent instance details
     * @return the system id of an agent
     * @throws OperationException an error occurred
     */
    public long lookupAgentSid(Instance instance) throws OperationException;

}
