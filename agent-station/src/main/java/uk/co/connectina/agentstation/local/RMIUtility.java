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
package uk.co.connectina.agentstation.local;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.text.MessageFormat;
import uk.co.connectina.agentstation.api.Instance;
import uk.co.connectina.agentstation.api.Permission;
import uk.co.connectina.agentstation.api.client.OperationException;
import uk.co.connectina.agentstation.api.rmi.RemoteRegistry;
import uk.co.connectina.agentstation.api.rmi.RemoteStation;

/**
 * Utility that takes care of RMI communication-related tasks.
 * 
 * @author Dr Christos Bohoris
 */
class RMIUtility {

    private static final String MIGRATE_AGENT = "migrateAgent";
    private static final String REGISTRY_COMM_FAILED = "registryCommFailed";
    private static final String REMOTE_PERMISSION_LOOKUP_FAILED = "Remote permission lookup failed";
    private static final String MESSAGE_ERROR_PATTERN = "{0}: {1}";
    
    private RMIUtility() {
    
    }
    
    static RemoteStation getRMIAgentStation(String ipAddress, int port) throws OperationException {
        try {
            return (RemoteStation) Naming.lookup(MessageFormat.format("rmi://{0}:{1}/RemoteStation", ipAddress, Integer.toString(port)));
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            throw new OperationException(e);
        }
    }

    static RemoteRegistry getRMIAgentRegistry(String ipAddress, int port) throws OperationException {
        try {
            return (RemoteRegistry) Naming.lookup(MessageFormat.format("rmi://{0}:{1}/RemoteRegistry", ipAddress, Integer.toString(port)));
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            throw new OperationException(e);
        }
    }

    static RemoteRegistry getRMIRegistry(String ipAddress, int port, Instance instance, StationMessenger stationMessenger) throws OperationException {
        RemoteRegistry remoteRegistry;
        try {
            remoteRegistry = getRMIAgentRegistry(ipAddress, port);
        } catch (OperationException e) {
            stationMessenger.notifyStationListeners(MIGRATE_AGENT, instance, ipAddress, ((Integer) port).toString(), REGISTRY_COMM_FAILED);
            throw new OperationException(e);
        }
        return remoteRegistry;
    }

    static Permission getPermissionWithRMI(Instance instance, RemoteRegistry remoteRMIRegistry, String placeName) throws OperationException {
        Permission permission;
        try {
            permission = remoteRMIRegistry.lookupPermission(instance.getIdentity().getName(), instance.getShortId(), placeName);
        } catch (RemoteException e) {
            throw new OperationException(MessageFormat.format(MESSAGE_ERROR_PATTERN, REMOTE_PERMISSION_LOOKUP_FAILED, e.getMessage()));
        }
     
        return permission;
    }
    
    
}
