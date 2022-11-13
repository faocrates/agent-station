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

import uk.co.connectina.agentstation.api.client.Collaboration;
import uk.co.connectina.agentstation.api.client.OperationException;

import java.io.Serializable;
import java.util.List;
import uk.co.connectina.agentstation.api.client.AgentInstance;
import uk.co.connectina.agentstation.api.client.LogType;
import uk.co.connectina.agentstation.local.LogEntry;

/**
 * A station environment for mobile software agents.
 *
 * @author Dr Christos Bohoris
 */
public interface Station extends Serializable {

    /**
     * Creates an Agent.
     * 
     * @param instance the agent instance details
     * @param agentBytes the agent object as a byte array
     * @throws OperationException an error occurred
     */
    void createAgent(Instance instance, byte[] agentBytes) throws OperationException;
    
    /**
     * A request to migrate the agent to a remote Agent Station.
     * 
     * @param instance the agent instance details
     * @param remoteServer the name or IP address of the server hosting the remote Agent Station
     * @param port the port that the station listens to
     * @param placeName the remote place name
     * @throws OperationException an error occurred
     */
    void migrateAgent(Instance instance, String remoteServer, int port, String placeName) throws OperationException;
    
    /**
     * Creates a Place.
     * 
     * @param name the place name
     * @throws OperationException an error occurred
     */
    void createPlace(String name) throws OperationException;
    
    /**
     * Makes an Agent active.
     * 
     * @param instance the agent instance details
     * @throws OperationException an error occurred
     */
    void startAgent(Instance instance) throws OperationException;
    
    /**
     * Makes an Agent inactive.
     * 
     * @param instance the agent instance details
     * @throws OperationException an error occurred
     */
    void stopAgent(Instance instance) throws OperationException;
            
    /**
     * Removes an Agent from the Agent Station.
     * 
     * @param instance the agent instance details
     * @throws OperationException an error occurred
     */
    void removeAgent(Instance instance) throws OperationException;
    
    /**
     * Provides whether an Agent active.
     * 
     * @param instance the agent instance details
     * @return active or not
     * @throws OperationException an error occurred
     */
    boolean isAgentActive(Instance instance) throws OperationException;
    
    /**
     * A request to collaborate with another agent located in the same place.
     * 
     * @param agentInstance the agent instance details
     * @param agentName the name of the other agent
     * @param organisation the organisation of the other agent
     * @param majorVersion the major version of the other agent
     * @param minorVersion the minor version of the other agent
     * @return a collaboration link to the other agent
     * @throws OperationException an error occurred
     */
    Collaboration collaborate(AgentInstance agentInstance, String agentName, String organisation, int majorVersion, int minorVersion) throws OperationException;
    
    /**
     * Removes a Place.
     * 
     * @param name the name
     * @throws OperationException an error occurred
     */
    void removePlace(String name) throws OperationException;

    /**
     * Log a text message in the Agent Station environment.
     * 
     * @param agentInstance the agent instance details
     * @param type the message type
     * @param message the message
     */
    void log(AgentInstance agentInstance, LogType type, String message);
    
    /**
     * Provides the log for an Agent.
     * 
     * @param instance the agent instance details
     * @return the log
     */
    List<LogEntry> getLog(Instance instance);
    
    /**
     * Shutdown this station. 
     */
    void shutdownStation();
    
    /**
     * Provides the agent registry.
     * 
     * @return the agent registry
     */
    Registry getRegistry();
    
    /**
     * Called whenever permissions may have changed for a place.
     * 
     * @param placeName the place name
     * @throws OperationException an error occurred
     */
    void permissionsChange(String placeName) throws OperationException;

    /**
     * Initiates any scheduled stars associated with the agent.
     * 
     * @param instance the agent instance details
     * @throws OperationException an error occurred
     */
    void initiateAnySchedule(Instance instance) throws OperationException;

}
