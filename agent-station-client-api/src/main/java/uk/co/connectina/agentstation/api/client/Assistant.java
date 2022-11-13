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
package uk.co.connectina.agentstation.api.client;

import java.io.Serializable;

/**
 * A station assistant that an agent can use.
 *
 * @author Dr Christos Bohoris
 */
public interface Assistant extends Serializable {

    /** The Default place name. */
    String DEFAULT_PLACE = "Default";
    
    /**
     * A request to migrate the agent to a remote Agent Station.
     *
     * @param agentInstance the agent instance details
     * @param remoteServer the name or IP address of the server hosting the remote Agent Station
     * @param port the port that the station listens to
     * @param placeName the remote place name
     * @throws OperationException an error occurred
     */
    void migrate(AgentInstance agentInstance, String remoteServer, int port, String placeName) throws OperationException;

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
     * Log a text message in the Agent Station environment.
     * 
     * @param agentInstance the agent instance details
     * @param type the message type
     * @param message the message
     */
    void log(AgentInstance agentInstance, LogType type, String message);

    /**
     * A request to remove the agent.
     * 
     * @param agentInstance the agent instance details
     */
    void remove(AgentInstance agentInstance);

}
