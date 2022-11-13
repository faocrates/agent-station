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
package uk.co.connectina.agentstation.rmi;

import uk.co.connectina.agentstation.api.rmi.RemoteRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Objects;
import uk.co.connectina.agentstation.api.Permission;
import uk.co.connectina.agentstation.api.PermissionIdentity;
import uk.co.connectina.agentstation.api.Registry;
import uk.co.connectina.agentstation.api.client.OperationException;

/**
 * Gives remote access (through Java-RMI/JRMP) to Agent Station-related information.
 *
 * @author Dr Christos Bohoris
 */
public class RemoteRegistryServer extends UnicastRemoteObject implements RemoteRegistry {
    
    private final Registry agentRegistry;

    /**
     * Initiates a new object instance.
     * 
     * @param agentRegistry the local agent registry
     */
    public RemoteRegistryServer(Registry agentRegistry) throws RemoteException {
        this.agentRegistry = agentRegistry;
    }
    
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
    @Override
    public Permission lookupPermission(String agentName, String hashCode, String placeName) throws OperationException, RemoteException {
        return agentRegistry.lookupPermission(new PermissionIdentity(agentName, hashCode, placeName));
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.agentRegistry);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RemoteRegistryServer other = (RemoteRegistryServer) obj;
        return Objects.equals(this.agentRegistry, other.agentRegistry);
    }

 }
