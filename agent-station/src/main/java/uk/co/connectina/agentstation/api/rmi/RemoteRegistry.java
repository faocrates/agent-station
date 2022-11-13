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
package uk.co.connectina.agentstation.api.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import uk.co.connectina.agentstation.api.Permission;
import uk.co.connectina.agentstation.api.client.OperationException;

/**
 * Gives remote access (through Java-RMI/JRMP) to Agent Station-related information.
 *
 * @author Dr Christos Bohoris
 */
public interface RemoteRegistry extends Remote {

    /**
     * Provides the registered permission that matches the given details.
     * 
     * @param agentName the agent name
     * @param hashCode the hash code
     * @param placeName the place name
     * @return the permission
     * @throws OperationException an error occurred 
     * @throws RemoteException a remote communication error occurred 
     */
    Permission lookupPermission(String agentName, String hashCode, String placeName) throws OperationException, RemoteException;

}
