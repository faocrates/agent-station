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

import io.grpc.Server;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyServerBuilder;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.connectina.agentstation.api.Registry;
import uk.co.connectina.agentstation.api.Station;
import uk.co.connectina.agentstation.api.StationListener;
import uk.co.connectina.agentstation.api.rmi.RemoteRegistry;
import uk.co.connectina.agentstation.api.rmi.RemoteStation;
import uk.co.connectina.agentstation.rmi.RemoteRegistryServer;
import uk.co.connectina.agentstation.rmi.RemoteStationServer;

/**
 * Responsible for the start and shutdown of gRPC or RMI servers.
 *
 * @author Dr Christos Bohoris
 */
public final class ServerRunnable implements Runnable {

    private final Thread thread;
    private final Station agentStation;
    private final Registry agentRegistry;
    private final StationListener listener;
    private static final Logger LOGGER = LogManager.getLogger(ServerRunnable.class.toString());
    private final StationInfo stationInfo;
    private Server grpcServerInstance;

    public ServerRunnable(StationInfo stationInfo, Station agentStation, Registry agentRegistry, StationListener listener) {
        this.stationInfo = stationInfo;
        this.agentStation = agentStation;
        this.agentRegistry = agentRegistry;
        this.listener = listener;
        thread = new Thread(this, this.stationInfo.getRemote().name());
        thread.start();
    }

    public void shutdownServers() {
        if (stationInfo.getRemote() == RemoteSupport.GRPC) {
            try {
                grpcServerInstance.shutdown().awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                LOGGER.error(e);
                Thread.currentThread().interrupt();
            }
        }
        LOGGER.info("Shutdown completed");
    }

    @Override
    public void run() {
        if (stationInfo.getRemote() == RemoteSupport.RMI) {
            listener.notify("serverStart", startRMIServers());
        } else {
            listener.notify("serverStart", startGRPCServers());
        }
    }

    public static SslContext loadTLSCredentials() throws SSLException {
        File serverCertFile = new File(IOAccess.APP_CERT_FOLDER.getAbsolutePath() + File.separator + "server-cert.pem");
        File serverKeyFile = new File(IOAccess.APP_CERT_FOLDER.getAbsolutePath() + File.separator + "server-key.pem");
        File clientCACertFile = new File(IOAccess.APP_CERT_FOLDER.getAbsolutePath() + File.separator + "ca-cert.pem");

        SslContextBuilder ctxBuilder = SslContextBuilder.forServer(serverCertFile, serverKeyFile)
                .clientAuth(ClientAuth.REQUIRE)
                .trustManager(clientCACertFile);

        return GrpcSslContexts.configure(ctxBuilder).build();
    }

    private String startGRPCServers() {
        String result = null;
        try {
            SslContext sslContext = loadTLSCredentials();
            grpcServerInstance = NettyServerBuilder
                    .forPort(stationInfo.getPort())
                    .sslContext(sslContext)
                    .addService(new uk.co.connectina.agentstation.grpc.RemoteStationServer(agentStation, agentRegistry))
                    .addService(new uk.co.connectina.agentstation.grpc.RemoteRegistryServer(agentRegistry))
                    .build();

            grpcServerInstance.start();
            LOGGER.info("Initialised GRPC");
            LOGGER.info("{} Agent Station running at {}", stationInfo.getName(), IOAccess.getServerAndPortText(stationInfo.getServer(), stationInfo.getPort()));
        } catch (IOException e) {
            LOGGER.error(e);

            result = e.getMessage();
        }

        return result;
    }

    private String startRMIServers() {
        String result = null;
        System.setProperty("java.rmi.server.hostname", stationInfo.getServer());
        String portText = MessageFormat.format("{0,number,#}", stationInfo.getPort());

        try {
            LocateRegistry.createRegistry(stationInfo.getPort());

            LOGGER.info("Initialised RMI");
            String stationIdentifier = MessageFormat.format("{0}://{1}:{2}/RemoteStation", stationInfo.getRemote().name().toLowerCase(), stationInfo.getServer(), portText);
            RemoteStation rmiAgentStation = new RemoteStationServer(agentStation, agentRegistry);
            Naming.rebind(stationIdentifier, rmiAgentStation);
            LOGGER.info("{} Agent Station running at {}", stationInfo.getName(), IOAccess.getServerAndPortText(stationInfo.getServer(), stationInfo.getPort()));

            String registryIdentifier = MessageFormat.format("{0}://{1}:{2}/RemoteRegistry", stationInfo.getRemote().name().toLowerCase(), stationInfo.getServer(), portText);
            RemoteRegistry rmiAgentRegistry = new RemoteRegistryServer(agentRegistry);
            Naming.rebind(registryIdentifier, rmiAgentRegistry);
        } catch (MalformedURLException | RemoteException e) {
            LOGGER.error(e);

            result = e.getMessage();
        }

        return result;
    }

}
