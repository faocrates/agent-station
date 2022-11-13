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
 * Information that identifies an agent instance.
 * 
 * @author Dr Christos Bohoris
 */
public interface AgentIdentity extends Serializable {

    /**
     * Provides the name.
     * 
     * @return the name
     */
    String getName();

    /**
     * Provides the organisation.
     * 
     * @return the organisation
     */
    String getOrganisation();

    /**
     * Provides the class name.
     * 
     * @return the class name
     */
    String getClassName();

    /**
     * Provides the hash code. This is the MD5 hash of the jar package
     * that contains this agent.
     * 
     * @return the hash code
     */
    String getHashCode();

    /**
     * Provides the jar package file.
     * 
     * @return the package file
     */
    String getPackageFile();

    /**
     * Provides the agent's version number by combining the major and
     * minor version parts.
     * 
     * @return the agent version
     */
    double getVersion();

    /**
     * Provides an agent description.
     * 
     * @return the agent description 
     */
    String getDescription();

}
