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

import java.io.File;

import uk.co.connectina.agentstation.api.rmi.RemoteStation;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Objects;
import uk.co.connectina.agentstation.api.Station;
import uk.co.connectina.agentstation.api.Instance;
import uk.co.connectina.agentstation.api.Permission;
import uk.co.connectina.agentstation.api.PermissionIdentity;
import uk.co.connectina.agentstation.api.Registry;
import uk.co.connectina.agentstation.api.client.OperationException;
import uk.co.connectina.agentstation.local.IOAccess;

/**
 * Gives remote access (through Java-RMI/JRMP) to a station environment for
 * mobile software agents.
 *
 * @author Dr Christos Bohoris
 */
public class RemoteStationServer extends UnicastRemoteObject implements RemoteStation {

    private final Station agentStation;
    private final Registry agentRegistry;

    /**
     * Initiates a new object instance.
     *
     * @param agentStation the local agent station
     * @param agentRegistry the local agent registry
     */
    public RemoteStationServer(Station agentStation, Registry agentRegistry) throws RemoteException {
        super();
        this.agentStation = agentStation;
        this.agentRegistry = agentRegistry;
    }

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
    @Override
    public void transferAgent(Instance instance, byte[] agentBytes, String packageName, String placeName, byte[] packageData) throws OperationException, RemoteException {
        String packageLocation = IOAccess.APP_PACKAGE_FOLDER + File.separator + packageName;
        IOAccess.writeToFile(packageLocation, packageData);
        instance.setPlaceName(placeName);

        Permission permission = agentRegistry.lookupPermission(new PermissionIdentity(instance.getIdentity().getName(), instance.getShortId(), instance.getPlaceName()));
        if (permission != null && permission.isAllowed()) {
            agentStation.createAgent(instance, agentBytes);
            if (permission.isAutoStart()) {
                agentStation.startAgent(instance);
            }
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.agentStation);
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
        final RemoteStationServer other = (RemoteStationServer) obj;
        return Objects.equals(this.agentStation, other.agentStation);
    }

}
