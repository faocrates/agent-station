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
import java.io.File;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.connectina.agentstation.api.Instance;
import uk.co.connectina.agentstation.api.Permission;
import uk.co.connectina.agentstation.api.PermissionIdentity;
import uk.co.connectina.agentstation.api.Registry;
import uk.co.connectina.agentstation.api.Station;
import uk.co.connectina.agentstation.api.client.OperationException;
import uk.co.connectina.agentstation.api.grpc.AgentTransferInput;
import uk.co.connectina.agentstation.api.grpc.RemoteStationGrpc;
import uk.co.connectina.agentstation.api.grpc.VoidType;
import uk.co.connectina.agentstation.local.IOAccess;

/**
 * Gives remote access (through gRPC/HTTP2) to a station environment for mobile
 * software agents.
 *
 * @author Dr Christos Bohoris
 */
public class RemoteStationServer extends RemoteStationGrpc.RemoteStationImplBase {

    private final Station agentStation;
    private final Registry agentRegistry;
    private static final Logger LOGGER = LogManager.getLogger(RemoteStationServer.class.toString());

    /**
     * Initiates a new object instance.
     *
     * @param agentStation the local agent station
     * @param agentRegistry the local agent registry
     */
    public RemoteStationServer(Station agentStation, Registry agentRegistry) {
        super();
        this.agentStation = agentStation;
        this.agentRegistry = agentRegistry;
    }

    /**
     * A request to transfer the agent to a remote Agent Station.
     *
     * @param request the request
     * @param responseObserver the response observer
     */
    @Override
    public void transferAgent(AgentTransferInput request, StreamObserver<VoidType> responseObserver) {
        try {
            String packageLocation = IOAccess.APP_PACKAGE_FOLDER + File.separator + request.getPackageName();
            IOAccess.writeToFile(packageLocation, request.getPackageData().toByteArray());
            Instance instance = MapUtility.toInstance(request.getInstance(), request.getPlaceName());
            instance.setPlaceName(request.getPlaceName());

            Permission permission = agentRegistry.lookupPermission(new PermissionIdentity(instance.getIdentity().getName(), instance.getShortId(), instance.getPlaceName()));
            if (permission != null && permission.isAllowed()) {
                agentStation.createAgent(instance, request.getAgentBytes().toByteArray());
                if (permission.isAutoStart()) {
                    agentStation.startAgent(instance);
                }
            }
        } catch (OperationException e) {
            LOGGER.error(e);
        } finally {
            responseObserver.onNext(VoidType.newBuilder().build());
            responseObserver.onCompleted();
        }
    }

}
