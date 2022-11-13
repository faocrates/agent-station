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
package uk.co.connectina.agentstation.desktop;

import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import uk.co.connectina.agentstation.api.StationListener;
import uk.co.connectina.agentstation.App;
import uk.co.connectina.agentstation.api.Instance;
import uk.co.connectina.agentstation.api.client.OperationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.github.weisj.darklaf.LafManager;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.SwingConstants;
import uk.co.connectina.agentstation.api.Registry;
import uk.co.connectina.agentstation.api.Station;
import uk.co.connectina.agentstation.api.client.LogType;
import uk.co.connectina.agentstation.local.RemoteSupport;
import uk.co.connectina.agentstation.local.AgentStation;
import uk.co.connectina.agentstation.local.IOAccess;
import uk.co.connectina.agentstation.local.ServerRunnable;
import uk.co.connectina.agentstation.local.StationInfo;

/**
 * The main GUI frame of an Agent Station.
 *
 * @author Dr Christos Bohoris
 */
public class AgentStationFrame extends javax.swing.JFrame implements StationListener {

    public static final String ERROR_TITLE = "Error";
    public static final String LOG_FORMAT = "{}: {}";
    public static final String AGENT_STATION_FORMAT = "{0} Agent Station";
    private final AgentTableModel agentTableModel = new AgentTableModel();
    private final LogTableModel agentLogTableModel = new LogTableModel(false);
    private final InstanceTableModel agentInstanceTableModel = new InstanceTableModel();
    private transient Station agentStation;
    private transient Registry agentRegistry;
    private final LocalDateTime startTime;
    private LogTableModel logTableModel;
    private final DefaultListModel<String> placeListModel = new DefaultListModel<>();
    private final StationInfo stationInfo;
    private transient ServerRunnable serverRunnable;
    private static final Logger LOGGER = LogManager.getLogger(AgentStationFrame.class.toString());

    /**
     * Creates new form AgentStationFrame
     */
    public AgentStationFrame(StationInfo stationInfo) {
        startTime = LocalDateTime.now();

        this.stationInfo = stationInfo;

        LafManager.install();

        initComponents();

        initiateAppIcons();
        
        pack();
        setLocationRelativeTo(null);

        setTitle(this.stationInfo.getName() + " - " + App.getAppProperty(App.APP_NAME));

        initiatePlaceList();

        initiateLogTable();

        initiateAgentTable();

        initiateAgentInfoTable();

        initiateAgentLogTable();

        initiateStationAndRegistry();

        initiateStationInfoTable();

        serverRunnable = new ServerRunnable(this.stationInfo, agentStation, agentRegistry, this);

        initiatePlaceListListener();

        initiateAgentTableListener();

        initiateStatesAndButtons();

        initiateShutdownSupport();
    }

    private void initiateAppIcons() {
        Class<AgentStationFrame> thisClass = AgentStationFrame.class;
        String[] sizes = {"16", "24", "32", "64", "128", "256", "512"};
        List<Image> images = new ArrayList<>();

        for (String size : sizes) {
            InputStream stream = thisClass.getResourceAsStream("/AppIcons/agent-station-" + size + ".png");
            if (stream == null) {

                continue;
            }

            try {
                images.add(ImageIO.read(stream));
            } catch (IOException e) {
                LOGGER.error(e);
            }
        }

        setIconImages(images);
    }

    private void initiateShutdownSupport() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                agentStation.shutdownStation();
                serverRunnable.shutdownServers();
                System.out.println("\nShutdown completed");
                LOGGER.info("Shutdown completed");
            }
        });
    }

    private void initiateStationAndRegistry() {
        try {
            agentStation = new AgentStation(stationInfo, AgentStationFrame.this);
        } catch (OperationException e) {
            LOGGER.error(e);
        }
        agentRegistry = agentStation.getRegistry();
    }

    private void initiateStatesAndButtons() {
        SwingUtilities.invokeLater(() -> placeList.setSelectedIndex(0));

        startButton.setEnabled(false);
        stopButton.setEnabled(false);
        changeButton.setEnabled(false);
        removeButton.setEnabled(false);
        removePlaceButton.setEnabled(false);
        exportButton.setEnabled(false);
        aboutButton.addActionListener(new AboutListener());
        addButton.addActionListener(new AddAgentListener(this, placeList, agentStation, agentRegistry, logTableModel));
        startButton.addActionListener(new StartAgentListener(agentTable, agentTableModel, agentStation, logTableModel));
        stopButton.addActionListener(new StopAgentListener(agentTable, agentTableModel, agentStation, logTableModel));
        changeButton.addActionListener(new ChangeAgentListener(this, agentTable, agentTableModel, agentStation, agentRegistry));
        removeButton.addActionListener(new RemoveAgentListener(agentTable, agentTableModel, agentStation, logTableModel));
        exportButton.addActionListener(new ExportAgentListener(this, agentTable, agentTableModel, agentRegistry));
        addPlaceButton.addActionListener(new AddPlaceListener(this, agentStation, agentRegistry, logTableModel));
        removePlaceButton.addActionListener(new RemovePlaceListener(this, placeList, agentStation, agentRegistry, placeListModel, logTableModel));
        permissionButton.addActionListener(new PlacePermissionsListener(this, placeList, agentStation, agentRegistry));
    }

    private void initiateAgentTableListener() {
        agentTable.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                updateAgentUIStates();
            }
        });
    }

    private void initiatePlaceListListener() {
        placeList.addListSelectionListener((ListSelectionEvent event) -> {
            if (!event.getValueIsAdjusting() && placeList.getSelectedValue() != null) {
                try {
                    List<Instance> instances = agentRegistry.lookupAgentsByPlaceName(placeList.getSelectedValue());
                    agentTableModel.clear();
                    for (Instance instance : instances) {
                        agentTableModel.add(instance);
                    }
                    agentTableModel.fireTableDataChanged();
                    removePlaceButton.setEnabled(placeList.getSelectedIndex() >= 0 && !placeListModel.get(placeList.getSelectedIndex()).equals("Default"));
                    agentLogTableModel.clear();
                    agentInstanceTableModel.clear();
                } catch (OperationException e) {
                    LOGGER.error(e);
                }
            }
        });
    }

    private void initiatePlaceList() {
        placeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        placeList.setModel(placeListModel);
    }

    private void initiateAgentInfoTable() {
        agentInfoTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        agentInfoTable.setModel(agentInstanceTableModel);
        agentInfoTable.getColumnModel().getColumn(0).setMaxWidth(160);
        agentInfoTable.getColumnModel().getColumn(0).setPreferredWidth(160);
    }

    private void initiateStationInfoTable() {
        String activeStation = IOAccess.getServerAndPortText(stationInfo.getServer(), stationInfo.getPort());
        String protocol = stationInfo.getRemote() == RemoteSupport.RMI ? "JRMP" : "HTTP/2, SSL, TLS";
        String rpc = stationInfo.getRemote() == RemoteSupport.RMI ? "RMI" : "GRPC";

        String[] properties = {"Name", "Version", "Started", "Protocol", "RPC", "Station At"};
        String[] values = {stationInfo.getName(), App.getAppProperty(App.APP_VERSION), App.DATETIME_FORMATTER.format(startTime), protocol, rpc, activeStation};

        PropertyTableModel stationInfoTableModel = new PropertyTableModel(properties, values);
        infoStationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        infoStationTable.setModel(stationInfoTableModel);
        infoStationTable.getColumnModel().getColumn(0).setMaxWidth(160);
        infoStationTable.getColumnModel().getColumn(0).setPreferredWidth(160);
    }

    private void initiateAgentTable() {
        agentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        agentTable.setModel(agentTableModel);
        agentTable.getColumnModel().getColumn(0).setMinWidth(160);
        agentTable.getColumnModel().getColumn(0).setPreferredWidth(160);
        agentTable.getColumnModel().getColumn(1).setMinWidth(160);
        agentTable.getColumnModel().getColumn(1).setPreferredWidth(160);
        agentTable.getColumnModel().getColumn(2).setMaxWidth(100);
        agentTable.getColumnModel().getColumn(3).setMaxWidth(100);
        agentTable.getColumnModel().getColumn(4).setMaxWidth(100);
        agentTable.getColumnModel().getColumn(2).setCellRenderer(new CenterRenderer());
        agentTable.getColumnModel().getColumn(3).setCellRenderer(new HashCodeRenderer(SwingConstants.CENTER));
        agentTable.getColumnModel().getColumn(4).setCellRenderer(new AgentStateCellRenderer());
    }

    private void initiateAgentLogTable() {
        agentLogTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        agentLogTable.setModel(agentLogTableModel);
        agentLogTable.getColumnModel().getColumn(0).setPreferredWidth(160);
        agentLogTable.getColumnModel().getColumn(0).setMaxWidth(160);
        agentLogTable.getColumnModel().getColumn(1).setMaxWidth(80);
        agentLogTable.getColumnModel().getColumn(1).setCellRenderer(new LogTypeCellRenderer());
    }

    private void initiateLogTable() {
        logTableModel = new LogTableModel(true);
        logTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        logTable.setModel(logTableModel);
        logTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        logTable.getColumnModel().getColumn(0).setPreferredWidth(160);
        logTable.getColumnModel().getColumn(0).setMaxWidth(160);
        logTable.getColumnModel().getColumn(1).setMaxWidth(80);
        logTable.getColumnModel().getColumn(1).setCellRenderer(new LogTypeCellRenderer());
        logTable.getColumnModel().getColumn(2).setPreferredWidth(360);
        logTable.getColumnModel().getColumn(2).setMaxWidth(500);
    }

    private void updateAgentUIStates() {
        int selected = agentTable.getSelectedRow();
        agentInstanceTableModel.update(selected > -1 ? agentTableModel.get(agentTable.getSelectedRow()) : null);
        agentInstanceTableModel.fireTableDataChanged();

        agentLogTableModel.clear();

        startButton.setEnabled(selected >= 0);
        stopButton.setEnabled(selected >= 0);
        changeButton.setEnabled(selected >= 0);
        removeButton.setEnabled(selected >= 0);
        exportButton.setEnabled(selected >= 0);

        if (selected > -1) {
            Instance instance = agentTableModel.get(agentTable.getSelectedRow());
            agentLogTableModel.update(agentStation.getLog(instance));
            startButton.setEnabled(instance.getState() == Instance.State.INACTIVE);
            stopButton.setEnabled(instance.getState() == Instance.State.ACTIVE);
        }
    }

    @Override
    public void notify(String operationName, Object... info) {
        switch (operationName) {
            case "agentLog" ->
                incomingAgentLog(info);
            case "serverStart" ->
                incomingServerStart(info);
            case "createPlace" ->
                incomingCreatePlace(info);
            case "createAgent" ->
                incomingCreateAgent(info);
            case "removeAgent" ->
                incomingRemoveAgent(info);
            case "removePlace" ->
                incomingRemovePlace(info);
            case "migrateAgent" ->
                incomingMigrateAgent(info);
            case "start", "stop" ->
                incomingStartStop(info, operationName);  
            default -> {
                String activeServer = MessageFormat.format(AGENT_STATION_FORMAT, stationInfo.getName());
                logTableModel.addError(activeServer, "Unknown operation name");
                LOGGER.error("{}: {} {}", "Agent Station", "Unknown operation name:", operationName);
            }
        }
    }

    private void incomingStartStop(Object[] info, String operationName) {
        SwingUtilities.invokeLater(() -> {
            Instance inst = ((Instance) info[0]);
            agentTableModel.updateState(inst);
            final int index = agentTableModel.indexOf(inst);
            agentTable.getSelectionModel().setSelectionInterval(index, index);
            agentTableModel.fireTableDataChanged();

            if (operationName.equals("start")) {
                logInfo(inst, "Agent successfully started");
            } else if (operationName.equals("stop")) {
                logInfo(inst, "Agent successfully stopped");
            }

            agentTable.getSelectionModel().setSelectionInterval(index, index);
        });
    }

    private void incomingMigrateAgent(Object[] info) {
        SwingUtilities.invokeLater(() -> {
            Instance inst = (Instance) info[0];
            String remoteServer = (String) info[1];
            String remotePort = (String) info[2];
            String remoteName = IOAccess.getServerAndPortText(remoteServer, Integer.valueOf(remotePort));
            String msg = (String) info[3];
            String dest = IOAccess.getServerAndPortText(remoteServer, Integer.valueOf(remotePort));

            String about = IOAccess.getAboutAgent(inst);
            String plainAbout = IOAccess.getPlainAboutAgent(inst);
            String message;
            switch (msg) {
                case "noPermission" -> {
                    message = MessageFormat.format("No permission to migrate to {0}", dest);
                    logTableModel.addError(about, message);
                    LOGGER.error(LOG_FORMAT, plainAbout, message);
                }
                case "registryCommFailed" -> {
                    message = MessageFormat.format("Cannot migrate to {0} as communication with remote registry failed", dest);
                    logTableModel.addError(about, message);
                    LOGGER.error(LOG_FORMAT, plainAbout, message);
                }
                case "stationCommFailed" -> {
                    message = MessageFormat.format("Cannot migrate to {0} as communication with remote station failed", remoteName);
                    logTableModel.addError(about, message);
                    LOGGER.error(LOG_FORMAT, plainAbout, message);
                }
                case "success" ->
                    logInfo(inst, MessageFormat.format("Successfully migrated to {0}", dest));
                default -> {
                    String activeServer = MessageFormat.format(AGENT_STATION_FORMAT, stationInfo.getName());
                    logTableModel.addError(activeServer, "Unknown notification message");
                    LOGGER.error("{}: {} {}", activeServer, "Unknown notification message:", msg);
                }
            }
        });
    }

    private void incomingRemovePlace(Object[] info) {
        SwingUtilities.invokeLater(() -> {
            String selectedPlaceName = (String) info[0];
            placeListModel.removeElement(selectedPlaceName);
            placeList.setSelectedIndex(placeListModel.getSize() - 1);
            logPlainInfo(IOAccess.getAboutPlace(selectedPlaceName), "Successfully created");
        });
    }

    private void incomingRemoveAgent(Object[] info) {
        SwingUtilities.invokeLater(() -> {
            Instance inst = (Instance) info[0];

            agentTableModel.remove(inst);
            agentTableModel.fireTableDataChanged();
            agentTable.getSelectionModel().setSelectionInterval(agentTableModel.getRowCount() - 1, agentTableModel.getRowCount() - 1);
            logInfo(inst, "Successfully removed");
        });
    }

    private void incomingCreateAgent(Object[] info) {
        SwingUtilities.invokeLater(() -> {
            Instance inst = (Instance) info[0];
            if (placeList.getSelectedValue().equals(inst.getPlaceName())) {
                agentTableModel.add(inst);
                agentTable.getSelectionModel().setSelectionInterval(agentTableModel.getRowCount() - 1, agentTableModel.getRowCount() - 1);
                agentTableModel.fireTableDataChanged();
                logInfo(inst, "Agent successfully created");
            }
        });
    }

    private void incomingCreatePlace(Object[] info) {
        SwingUtilities.invokeLater(() -> {
            placeListModel.addElement((String) info[0]);
            placeList.setSelectedIndex(placeListModel.getSize() - 1);
            agentTableModel.fireTableDataChanged();
            logPlainInfo(IOAccess.getAboutPlace((String) info[0]), "Place successfully created");
        });
    }

    private void incomingServerStart(Object[] info) {
        String activeServer = MessageFormat.format(AGENT_STATION_FORMAT, stationInfo.getName());
        if (info[0] != null) {
            logTableModel.addError(activeServer, (String) info[0]);
            JOptionPane.showMessageDialog(this, info[0], ERROR_TITLE, JOptionPane.ERROR_MESSAGE);

            System.exit(1);
        } else {
            logTableModel.add(LocalDateTime.now(), LogType.INFO, activeServer, MessageFormat.format("Initialised {0}", stationInfo.getRemote()));
            String activeStation = IOAccess.getServerAndPortText(stationInfo.getServer(), stationInfo.getPort());
            logPlainInfo(activeServer, MessageFormat.format("Agent Station running at {0}", activeStation));
        }
    }

    private void incomingAgentLog(Object[] info) {
        Instance instance = (Instance) info[0];
        if (agentTable.getSelectedRow() > -1 && agentTableModel.get(agentTable.getSelectedRow()).equals(instance)) {
            agentLogTableModel.update(agentStation.getLog(instance));
        }
    }

    private void logPlainInfo(String about, String info) {
        logTableModel.addInfo(about, info);
        LOGGER.info(LOG_FORMAT, about, info);
    }

    private void logInfo(Instance instance, String info) {
        String about = IOAccess.getAboutAgent(instance);
        String plainAbout = IOAccess.getPlainAboutAgent(instance);
        logTableModel.addInfo(about, info);
        LOGGER.info(LOG_FORMAT, plainAbout, info);
    }
    
    public class AboutListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            new AboutDialog(AgentStationFrame.this, true).setVisible(true);
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JPanel mainPanel = new javax.swing.JPanel();
        javax.swing.JSplitPane outterSplitPane = new javax.swing.JSplitPane();
        javax.swing.JSplitPane innerSplitPane = new javax.swing.JSplitPane();
        javax.swing.JPanel leftPanel = new javax.swing.JPanel();
        javax.swing.JScrollPane leftScrollPane = new javax.swing.JScrollPane();
        placeList = new javax.swing.JList<>();
        javax.swing.JToolBar placeToolBar = new javax.swing.JToolBar();
        javax.swing.JLabel placesLabel = new javax.swing.JLabel();
        javax.swing.JToolBar.Separator placesSeparator = new javax.swing.JToolBar.Separator();
        addPlaceButton = new javax.swing.JButton();
        removePlaceButton = new javax.swing.JButton();
        permissionButton = new javax.swing.JButton();
        javax.swing.JPanel rightPanel = new javax.swing.JPanel();
        javax.swing.JToolBar agentToolBar = new javax.swing.JToolBar();
        javax.swing.JLabel agentsLabel = new javax.swing.JLabel();
        javax.swing.JToolBar.Separator agentsSeparator = new javax.swing.JToolBar.Separator();
        addButton = new javax.swing.JButton();
        startButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();
        changeButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        exportButton = new javax.swing.JButton();
        javax.swing.JSplitPane rightSplitPane = new javax.swing.JSplitPane();
        javax.swing.JScrollPane rightScrollPane = new javax.swing.JScrollPane();
        agentTable = new javax.swing.JTable();
        javax.swing.JTabbedPane agentTabbedPane = new javax.swing.JTabbedPane();
        javax.swing.JPanel agentLogPanel = new javax.swing.JPanel();
        javax.swing.JScrollPane agentLogScrollPane = new javax.swing.JScrollPane();
        agentLogTable = new javax.swing.JTable();
        javax.swing.JPanel agentInfoPanel = new javax.swing.JPanel();
        javax.swing.JScrollPane agentInfoScrollPane = new javax.swing.JScrollPane();
        agentInfoTable = new javax.swing.JTable();
        javax.swing.JPanel logPanel = new javax.swing.JPanel();
        javax.swing.JTabbedPane agentStationTabbedPane = new javax.swing.JTabbedPane();
        javax.swing.JPanel logStationPanel = new javax.swing.JPanel();
        javax.swing.JScrollPane logScrollPane = new javax.swing.JScrollPane();
        logTable = new javax.swing.JTable();
        javax.swing.JPanel infoStationPanel = new javax.swing.JPanel();
        javax.swing.JScrollPane infoStationScrollPane = new javax.swing.JScrollPane();
        infoStationTable = new javax.swing.JTable();
        javax.swing.JToolBar agentStationToolBar = new javax.swing.JToolBar();
        javax.swing.JLabel agentStationLabel = new javax.swing.JLabel();
        javax.swing.JToolBar.Separator agentStationSeparator = new javax.swing.JToolBar.Separator();
        aboutButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        mainPanel.setPreferredSize(new java.awt.Dimension(1280, 720));
        mainPanel.setLayout(new java.awt.BorderLayout());

        outterSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        outterSplitPane.setResizeWeight(0.7);

        innerSplitPane.setResizeWeight(0.2);

        leftPanel.setLayout(new java.awt.BorderLayout());

        leftScrollPane.setViewportView(placeList);

        leftPanel.add(leftScrollPane, java.awt.BorderLayout.CENTER);

        placeToolBar.setRollover(true);

        placesLabel.setText("<html><strong>Places");
        placesLabel.setMaximumSize(new java.awt.Dimension(60, 16));
        placeToolBar.add(placesLabel);
        placeToolBar.add(placesSeparator);

        addPlaceButton.setText("Add...");
        addPlaceButton.setToolTipText("Add place");
        addPlaceButton.setBorderPainted(false);
        addPlaceButton.setFocusable(false);
        addPlaceButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addPlaceButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        placeToolBar.add(addPlaceButton);

        removePlaceButton.setText("Remove");
        removePlaceButton.setToolTipText("Remove place");
        removePlaceButton.setBorderPainted(false);
        removePlaceButton.setFocusable(false);
        removePlaceButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        removePlaceButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        placeToolBar.add(removePlaceButton);

        permissionButton.setText("Permissions");
        permissionButton.setBorderPainted(false);
        permissionButton.setFocusable(false);
        permissionButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        permissionButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        placeToolBar.add(permissionButton);

        leftPanel.add(placeToolBar, java.awt.BorderLayout.NORTH);

        innerSplitPane.setLeftComponent(leftPanel);

        rightPanel.setLayout(new java.awt.BorderLayout());

        agentToolBar.setRollover(true);

        agentsLabel.setText("<html><strong>Agents");
        agentsLabel.setMaximumSize(new java.awt.Dimension(60, 16));
        agentToolBar.add(agentsLabel);
        agentToolBar.add(agentsSeparator);

        addButton.setText("Add...");
        addButton.setToolTipText("Add agent");
        addButton.setBorderPainted(false);
        addButton.setFocusable(false);
        addButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        agentToolBar.add(addButton);

        startButton.setText("Start");
        startButton.setToolTipText("Start agent");
        startButton.setBorderPainted(false);
        startButton.setFocusable(false);
        startButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        startButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        agentToolBar.add(startButton);

        stopButton.setText("Stop");
        stopButton.setToolTipText("Stop agent");
        stopButton.setBorderPainted(false);
        stopButton.setFocusable(false);
        stopButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        stopButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        agentToolBar.add(stopButton);

        changeButton.setText("Change...");
        changeButton.setBorderPainted(false);
        changeButton.setFocusable(false);
        changeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        changeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        agentToolBar.add(changeButton);

        removeButton.setText("Remove");
        removeButton.setToolTipText("Remove agent");
        removeButton.setBorderPainted(false);
        removeButton.setFocusable(false);
        removeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        removeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        agentToolBar.add(removeButton);

        exportButton.setText("Export");
        exportButton.setBorderPainted(false);
        exportButton.setFocusable(false);
        exportButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        exportButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        agentToolBar.add(exportButton);

        rightPanel.add(agentToolBar, java.awt.BorderLayout.NORTH);

        rightSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        rightSplitPane.setResizeWeight(0.5);

        agentTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        rightScrollPane.setViewportView(agentTable);

        rightSplitPane.setLeftComponent(rightScrollPane);

        agentLogPanel.setLayout(new java.awt.BorderLayout());

        agentLogTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        agentLogScrollPane.setViewportView(agentLogTable);

        agentLogPanel.add(agentLogScrollPane, java.awt.BorderLayout.CENTER);

        agentTabbedPane.addTab("Log", agentLogPanel);

        agentInfoPanel.setLayout(new java.awt.BorderLayout());

        agentInfoScrollPane.setViewportView(agentInfoTable);

        agentInfoPanel.add(agentInfoScrollPane, java.awt.BorderLayout.CENTER);

        agentTabbedPane.addTab("Information", agentInfoPanel);

        rightSplitPane.setRightComponent(agentTabbedPane);

        rightPanel.add(rightSplitPane, java.awt.BorderLayout.CENTER);

        innerSplitPane.setRightComponent(rightPanel);

        outterSplitPane.setTopComponent(innerSplitPane);

        logPanel.setLayout(new java.awt.BorderLayout());

        logStationPanel.setLayout(new java.awt.BorderLayout());

        logScrollPane.setViewportView(logTable);

        logStationPanel.add(logScrollPane, java.awt.BorderLayout.CENTER);

        agentStationTabbedPane.addTab("Log", logStationPanel);

        infoStationPanel.setLayout(new java.awt.BorderLayout());

        infoStationScrollPane.setViewportView(infoStationTable);

        infoStationPanel.add(infoStationScrollPane, java.awt.BorderLayout.CENTER);

        agentStationTabbedPane.addTab("Information", infoStationPanel);

        logPanel.add(agentStationTabbedPane, java.awt.BorderLayout.CENTER);

        agentStationToolBar.setRollover(true);

        agentStationLabel.setText("<html><strong>Agent Station");
        agentStationLabel.setMaximumSize(new java.awt.Dimension(110, 16));
        agentStationToolBar.add(agentStationLabel);
        agentStationToolBar.add(agentStationSeparator);

        aboutButton.setText("About...");
        aboutButton.setBorderPainted(false);
        aboutButton.setFocusable(false);
        aboutButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        aboutButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        agentStationToolBar.add(aboutButton);

        logPanel.add(agentStationToolBar, java.awt.BorderLayout.NORTH);

        outterSplitPane.setBottomComponent(logPanel);

        mainPanel.add(outterSplitPane, java.awt.BorderLayout.CENTER);

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton aboutButton;
    private javax.swing.JButton addButton;
    private javax.swing.JButton addPlaceButton;
    private javax.swing.JTable agentInfoTable;
    private javax.swing.JTable agentLogTable;
    private javax.swing.JTable agentTable;
    private javax.swing.JButton changeButton;
    private javax.swing.JButton exportButton;
    private javax.swing.JTable infoStationTable;
    private javax.swing.JTable logTable;
    private javax.swing.JButton permissionButton;
    private javax.swing.JList<String> placeList;
    private javax.swing.JButton removeButton;
    private javax.swing.JButton removePlaceButton;
    private javax.swing.JButton startButton;
    private javax.swing.JButton stopButton;
    // End of variables declaration//GEN-END:variables

}
