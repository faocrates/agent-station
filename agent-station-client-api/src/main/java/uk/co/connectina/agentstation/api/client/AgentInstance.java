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
import java.time.LocalDateTime;

/**
 * Provides agent instance details.
 *
 * @author Dr Christos Bohoris
 */
public interface AgentInstance extends Serializable {

    /**
     * Provides the agent identity.
     *
     * @return the agent identity
     */
    AgentIdentity getAgentIdentity();

    /**
     * The creation date time.
     *
     * @return the creation date time
     */
    LocalDateTime getCreationDateTime();

    /**
     * Provides the place where the agent resides.
     *
     * @return the place name
     */
    String getPlaceName();

    /**
     * Provides the initial parameters.
     *
     * @return the initial parameters
     */
    String[] getParameters();

    /**
     * Provides the home server.
     *
     * @return the home server
     */
    String getHomeServer();

    /**
     * Provides the home port.
     *
     * @return the home port
     */
    int getHomePort();

    /**
     * Provides the home place.
     *
     * @return the home place name
     */
    String getHomePlace();

    /**
     * Provides the last remote server.
     *
     * @return the last remote server
     */
    public String getLastRemoteServer();

    /**
     * Provides the last remote port.
     *
     * @return the last remote port
     */
    public int getLastRemotePort();

    /**
     * Provides the last remote place.
     *
     * @return the last remote place name
     */
    public String getLastRemotePlace();

}
