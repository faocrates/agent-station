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
package uk.co.connectina.agentstation.grpc;

import io.grpc.stub.StreamObserver;
import uk.co.connectina.agentstation.api.client.OperationException;
import uk.co.connectina.agentstation.api.grpc.PermissionInput;
import uk.co.connectina.agentstation.api.grpc.PermissionType;
import uk.co.connectina.agentstation.api.grpc.RemoteRegistryGrpc;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.connectina.agentstation.api.Permission;
import uk.co.connectina.agentstation.api.PermissionIdentity;
import uk.co.connectina.agentstation.api.Registry;

/**
 * Gives remote access (through gRPC/HTTP2) to Agent Station-related information.
 *
 * @author Dr Christos Bohoris
 */
public class RemoteRegistryServer extends RemoteRegistryGrpc.RemoteRegistryImplBase {
    
    private final Registry agentRegistry;
    private static final Logger LOGGER = LogManager.getLogger(RemoteRegistryServer.class.toString());
    
    /**
     * Initiates a new object instance.
     * 
     * @param agentRegistry the local agent registry
     */
    public RemoteRegistryServer(Registry agentRegistry) {
        this.agentRegistry = agentRegistry;
    }

    /**
     * Provides the registered permission that matches the given details.
     * 
     * @param request the request
     * @param responseObserver the response observer
     */
    @Override
    public void lookupPermission(PermissionInput request, StreamObserver<PermissionType> responseObserver) {
        PermissionType response = PermissionType.newBuilder().build();
        try {
            Permission permission = agentRegistry.lookupPermission(new PermissionIdentity(request.getAgentName(), request.getAgentTraceId(), request.getPlaceName()));
            if (permission != null) {
                response = MapUtility.toPermissionType(permission);
            }
        } catch (OperationException e) {
            LOGGER.error(e);
        } finally {
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
    
}
