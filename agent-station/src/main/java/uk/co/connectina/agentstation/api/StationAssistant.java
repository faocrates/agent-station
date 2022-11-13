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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uk.co.connectina.agentstation.api.client.AgentInstance;
import uk.co.connectina.agentstation.api.client.Assistant;
import uk.co.connectina.agentstation.api.client.Collaboration;
import uk.co.connectina.agentstation.api.client.LogType;
import uk.co.connectina.agentstation.api.client.OperationException;

/**
 * A station assistant that an agent can use.
 * 
 * @author Dr Christos Bohoris
 */
public final class StationAssistant implements Assistant {

    private final Station station;
    private static final Logger LOGGER = LogManager.getLogger(StationAssistant.class.toString());

    /**
     * Initiates a new object instance.
     * 
     * @param station the station
     */
    public StationAssistant(Station station) {
        this.station = station;
    }

    /**
     * A request to migrate the agent to a remote Agent Station.
     * 
     * @param agentInstance the agent instance details
     * @param remoteServer  the name or IP address of the server hosting the remote
     *                      Agent Station
     * @param port          the port that the station listens to
     * @param placeName     the remote place name
     * @throws OperationException an error occurred
     */
    @Override
    public void migrate(AgentInstance agentInstance, String remoteServer, int port, String placeName)
            throws OperationException {
        station.migrateAgent((Instance) agentInstance, remoteServer, port, placeName);
    }

    /**
     * A request to collaborate with another agent located in the same place.
     * 
     * @param agentInstance the agent instance details
     * @param agentName     the name of the other agent
     * @param organisation  the organisation of the other agent
     * @param majorVersion  the major version of the other agent
     * @param minorVersion  the minor version of the other agent
     * @return a collaboration link to the other agent
     * @throws OperationException an error occurred
     */
    @Override
    public Collaboration collaborate(AgentInstance agentInstance, String agentName, String organisation,
            int majorVersion, int minorVersion) throws OperationException {
        return station.collaborate(agentInstance, agentName, organisation, majorVersion, minorVersion);
    }

    /**
     * Log a text message in the Agent Station environment.
     * 
     * @param agentInstance the agent instance details
     * @param type          the message type
     * @param message       the message
     */
    @Override
    public void log(AgentInstance agentInstance, LogType type, String message) {
        station.log(agentInstance, type, message);
    }

    /**
     * A request to remove the agent.
     * 
     * @param agentInstance the agent instance details
     */
    @Override
    public void remove(AgentInstance agentInstance) {
        new Thread("Remove-" + agentInstance.getAgentIdentity().getName()) {
            @Override
            public void run() {
                try {
                    station.removeAgent((Instance) agentInstance);
                } catch (OperationException e) {
                    LOGGER.error(e);
                }
            }
        }.start();
    }

    public Assistant getAssistantInstance() {

        return new StationAssistant(station);
    }

}
