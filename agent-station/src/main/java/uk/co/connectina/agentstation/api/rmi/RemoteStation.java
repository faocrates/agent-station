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
import uk.co.connectina.agentstation.api.Instance;
import uk.co.connectina.agentstation.api.client.OperationException;

/**
 * Gives remote access (through Java-RMI/JRMP) to a station environment for mobile software agents.
 *
 * @author Dr Christos Bohoris
 */
public interface RemoteStation extends Remote {

    /**
     * A request to transfer the agent to a remote Agent Station.
     * 
     * @param instance the agent instance details
     * @param agentBytes the agent object as a byte array
     * @param packageName the package name
     * @param placeName the place name
     * @param packageData the package data as a byte array
     * @throws OperationException an error occurred 
     * @throws RemoteException a remote communication error occurred
     */
    void transferAgent(Instance instance, byte[] agentBytes, String packageName, String placeName, byte[] packageData) throws OperationException, RemoteException;

}
