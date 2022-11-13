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
package uk.co.connectina.agentstation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.weisj.darklaf.LafManager;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import uk.co.connectina.agentstation.desktop.AgentStationFrame;
import uk.co.connectina.agentstation.local.IOAccess;
import uk.co.connectina.agentstation.local.RemoteSupport;
import uk.co.connectina.agentstation.local.StationInfo;
import uk.co.connectina.agentstation.local.UISupport;
import uk.co.connectina.agentstation.terminal.AgentStationCommand;

/**
 * Application starting point.
 *
 * @author Dr Christos Bohoris
 */
@Command(name = "agentstation", mixinStandardHelpOptions = true, version = "1", description = "Starts an Agent Station environment for mobile stoftware agents.")
public class App implements Runnable {

    @Option(names = { "-s", "--server" }, required = true, description = "Local server name or IP address")
    private String localServer;
    @Option(names = { "-i", "--ui" }, required = false, description = "The frontend type. Use gui or tui")
    private String ui;
    @Option(names = { "-r",
            "--remote" }, required = false, description = "The remote communication type. Use grpc or rmi")
    private String remote;
    @Option(names = { "-n", "--name" }, required = false, description = "Agent Station name")
    private String stationName;
    @Option(names = { "-p", "--port" }, required = false, description = "Remote communication port")
    private int port;
    @Option(names = { "-h", "--help", "-?", "-help" }, usageHelp = true, description = "Display this help and exit")
    private boolean help;
    private static final Logger LOGGER = LogManager.getLogger(App.class.toString());
    public static final String APP_FOLDER = System.getProperty("user.home") + File.separator + ".AgentStation";
    private static Properties appProperties;

    public static final String APP_NAME = "app_name";
    public static final String APP_VERSION = "app_version";
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private void applyRemote() {
        // Set default
        if (remote == null) {
            remote = RemoteSupport.RMI.name();
        } else {
            remote = remote.toUpperCase();
            if (!remote.equals(RemoteSupport.GRPC.name()) && !remote.equals(RemoteSupport.RMI.name())) {
                System.out.println("Valid --remote options are grpc or rmi.");

                System.exit(1);
            }
        }
    }

    private void applyName() {
        // Set default
        if (stationName == null) {
            stationName = "Main";
        }
    }

    private void applyPort() {
        // Set default
        if (port <= 0) {
            port = remote.equals(RemoteSupport.GRPC.name()) ? 50051 : 1099; 
        }
    }

    private void applyUI() {
        // Set default
        if (ui == null) {
            ui = "GUI";
        } else {
            ui = ui.toUpperCase();
            if (!ui.equals(UISupport.GUI.name()) && !ui.equals(UISupport.TUI.name())) {
                System.out.println("Valid --ui options are gui or tui.");
    
                System.exit(1);
            }
        }
    }

    @Override
    public void run() {
        // Handle optional input
        applyName();
        applyRemote();
        applyPort();
        applyUI();
        
        // Prepare app run
        try {
            IOAccess.prepareAppFolder();
            App.loadAppProperties("/app.properties");
        } catch (IOException e) {
            LOGGER.error(e);

            return;
        }

        // Handle process lock
        if (processLock()) {

            return;
        }

        StationInfo stationInfo = new StationInfo(localServer, ui, RemoteSupport.valueOf(remote), stationName, port);
        // Initiate UI
        if (ui.equals(UISupport.GUI.name())) {
            SwingUtilities.invokeLater(() -> new AgentStationFrame(stationInfo).setVisible(true));
        } else {
            new AgentStationCommand(stationInfo);
        }
    }

    public static String getAppProperty(String name) {

        return App.appProperties.getProperty(name, "");
    }

    private static void loadAppProperties(String fileName) throws IOException {
        try (InputStream fis = App.class.getResourceAsStream(fileName)) {
            App.appProperties = new Properties();
            App.appProperties.load(fis);
        }
    }

    private boolean processLock() {
        String lockFilePath = APP_FOLDER + File.separator + "processes" + File.separator + ".station-" + port;
        String dbLockFilePath = APP_FOLDER + File.separator + "db" + File.separator + "agentstation.lock.db";
        if (new File(lockFilePath).exists() && new File(dbLockFilePath).exists()) {
            if (ui.equals(UISupport.GUI.name())) {
                LafManager.install();
                JOptionPane.showMessageDialog(null, "An Agent Station is already running on this port.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                System.out.println("An Agent Station is already running on this port.");
            }

            return true;
        } else if (new File(lockFilePath).exists() && !new File(dbLockFilePath).exists()) {
            try {
                // Delete unused lock file
                Files.delete(Paths.get(lockFilePath));
            } catch (IOException e) {
                LOGGER.error("Failed to remove lock file {}. Error: {}", lockFilePath, e.getMessage());
            }
        }
        lockInstance(lockFilePath);

        return false;
    }

    public static void main(String[] args) {
        new CommandLine(new App()).execute(args);
    }

    boolean lockInstance(final String lockFile) {
        try {
            final File file = new File(lockFile);
            try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw")) {
                final FileLock fileLock = randomAccessFile.getChannel().tryLock();
                if (fileLock != null) {
                    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                        try {
                            Files.delete(file.toPath());
                        } catch (Exception e) {
                            LOGGER.error(e);
                        }
                    }));

                    return true;
                }
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }

        return false;
    }

}
