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
 * An agent provided by a user.
 * 
 * @author Dr Christos Bohoris
 */
public interface Agent extends Serializable {

    /**
     * A request to make the agent active.
     * 
     * @param stationAssistant a station assistant that this agent can use
     * @param agentInstance the agent instance details
     */
    void start(Assistant stationAssistant, AgentInstance agentInstance);

    /**
     * A request to make the agent inactive.
     */
    void stop();
    
}
