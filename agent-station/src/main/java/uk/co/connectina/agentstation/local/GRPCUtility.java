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

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.SslContext;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import javax.net.ssl.SSLException;
import uk.co.connectina.agentstation.api.Instance;
import uk.co.connectina.agentstation.api.Permission;
import uk.co.connectina.agentstation.api.client.OperationException;
import uk.co.connectina.agentstation.api.grpc.AgentTransferInput;
import uk.co.connectina.agentstation.api.grpc.PermissionInput;
import uk.co.connectina.agentstation.api.grpc.PermissionType;
import uk.co.connectina.agentstation.api.grpc.RemoteRegistryGrpc;
import uk.co.connectina.agentstation.api.grpc.RemoteStationGrpc;
import uk.co.connectina.agentstation.grpc.MapUtility;

/**
 * UTility that takes care of gRPC communication-related tasks.
 *
 * @author Dr Christos Bohoris
 */
class GRPCUtility {

    private static final String MIGRATE_AGENT = "migrateAgent";
    private static final String REMOTE_PERMISSION_LOOKUP_FAILED = "Remote permission lookup failed";
    private static final String REGISTRY_COMM_FAILED = "registryCommFailed";
    private static final String MESSAGE_ERROR_PATTERN = "{0}: {1}";
    
    private GRPCUtility() {
    
    }
    
    static SslContext loadTLSCredentials() throws SSLException {
        File serverCACertFile = new File(IOAccess.APP_CERT_FOLDER.getAbsolutePath() + File.separator + "ca-cert.pem");
        File clientCertFile = new File(IOAccess.APP_CERT_FOLDER.getAbsolutePath() + File.separator + "client-cert.pem");
        File clientKeyFile = new File(IOAccess.APP_CERT_FOLDER.getAbsolutePath() + File.separator + "client-key.pem");

        return GrpcSslContexts.forClient()
                .keyManager(clientCertFile, clientKeyFile)
                .trustManager(serverCACertFile)
                .build();
    }

    static RemoteStationGrpc.RemoteStationBlockingStub getGRPCStation(String ipAddress, int port, Instance instance, StationMessenger stationMessenger) throws OperationException {
        ManagedChannel channel;
        try {
            SslContext sslContext = loadTLSCredentials();
            channel = NettyChannelBuilder.forAddress(ipAddress, port)
                    .sslContext(sslContext)
                    .keepAliveWithoutCalls(true)
                    .build();
        } catch (Exception e) {
            stationMessenger.notifyStationListeners(MIGRATE_AGENT, instance, ipAddress, ((Integer) port).toString(), REGISTRY_COMM_FAILED);
            throw new OperationException(e);
        }

        return RemoteStationGrpc.newBlockingStub(channel);
    }

    static Permission getPermissionWithGRPC(Instance instance, RemoteRegistryGrpc.RemoteRegistryBlockingStub remoteRegistry, String placeName) throws OperationException {
        PermissionType permissionType;
        try {
            permissionType = remoteRegistry.lookupPermission(PermissionInput.newBuilder().setAgentName(instance.getIdentity().getName())
                    .setAgentTraceId(instance.getShortId())
                    .setPlaceName(placeName).build());
        } catch (Exception e) {
            throw new OperationException(MessageFormat.format(MESSAGE_ERROR_PATTERN, REMOTE_PERMISSION_LOOKUP_FAILED, e.getMessage()));
        }

        return new Permission(permissionType.getAgentName(), permissionType.getAgentTraceId(), permissionType.getPlaceName(), permissionType.getAllowed(), permissionType.getAutoStart());
    }

    static void transferAgentWithGRPC(Instance instance, byte[] agentBytes, String packageName, String placeName, RemoteStationGrpc.RemoteStationBlockingStub remoteStation) throws OperationException, MalformedURLException {
        AgentTransferInput input = AgentTransferInput.newBuilder().setInstance(MapUtility.toInstanceType(instance))
                .setAgentBytes(ByteString.copyFrom(agentBytes))
                .setPackageName(packageName)
                .setPlaceName(placeName)
                .setPackageData(ByteString.copyFrom(IOAccess.readFromFile(new URL(instance.getIdentity().getPackageFile()).getFile()))).build();
        remoteStation.transferAgent(input);
    }
    
    static RemoteRegistryGrpc.RemoteRegistryBlockingStub getGRPCRegistry(String ipAddress, int port, Instance instance, StationMessenger stationMessenger) throws OperationException {
        ManagedChannel channel;
        try {
            SslContext sslContext = loadTLSCredentials();
            channel = NettyChannelBuilder.forAddress(ipAddress, port)
                    .sslContext(sslContext)
                    .keepAliveWithoutCalls(true)
                    .build();
        } catch (Exception e) {
            stationMessenger.notifyStationListeners(MIGRATE_AGENT, instance, ipAddress, ((Integer) port).toString(), REGISTRY_COMM_FAILED);
            throw new OperationException(e);
        }

        return RemoteRegistryGrpc.newBlockingStub(channel);
    }
    
}
