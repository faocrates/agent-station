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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.connectina.agentstation.App;
import uk.co.connectina.agentstation.local.IOAccess;

/**
 * A dialog that displays useful application-related information.
 *
 * @author Dr Christos Bohoris
 */
public class AboutDialog extends javax.swing.JDialog {

    private static final Logger LOGGER = LogManager.getLogger(AboutDialog.class.toString());
    public static final String CONNECTINA_CO_UK_AGENT_STATION = "https://www.connectina.co.uk/agent-station";

    /**
     * Creates new form AboutDialog
     */
    public AboutDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setTitle("About");
        initComponents();
        initiateSystemTable();
        initiateLicenseText();
        initiateMemoryInfo();
        initiateLatestInfo();

        versionLabel.setText(MessageFormat.format("Version {0}", App.getAppProperty("app_version")));
        closeButton.addActionListener(new CloseListener());
        
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void initiateLatestInfo() {
        String latest = IOAccess.checkNewerVersion();
        String text = latest == null ? "This is the latest available version" : MessageFormat.format("A newer version {0} is available", latest);
        latestLabel.setText(text);
        siteLabel.setForeground(Color.BLUE.darker());
        siteLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
 
        siteLabel.addMouseListener(new MouseAdapter() {
 
            @Override
            public void mouseClicked(MouseEvent event) {
                try {
                    Desktop.getDesktop().browse(new URI(CONNECTINA_CO_UK_AGENT_STATION));
                } catch (IOException | URISyntaxException e) {
                    LOGGER.error(e);
                }
            }
 
            @Override
            public void mouseExited(MouseEvent e) {
                siteLabel.setText("connectina.co.uk/agent-station");
            }
 
            @Override
            public void mouseEntered(MouseEvent e) {
                siteLabel.setText("<html><a href=''>connectina.co.uk/agent-station");
            }
 
        });
    }

    private void initiateMemoryInfo() {
        double used = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024.0 * 1024.0);
        double total = (Runtime.getRuntime().totalMemory()) / (1024.0 * 1024.0);
        String memoryText = MessageFormat.format("Used memory: {0, number, #.#} MB, Total memory: {1, number, #.#} MB", used, total);
        memLabel.setText(memoryText);
        
        double usedPercentage = (100.0 / total) * used;
        memProgressBar.setValue((int) usedPercentage);
    }
    
    private void initiateSystemTable() {
        List<String> keys = new ArrayList<>();
        List<String> values = new ArrayList<>();
        Properties props = System.getProperties();
        Enumeration<?> names = props.propertyNames();
        
        while (names.hasMoreElements()) {
            String key = (String) names.nextElement();
            keys.add(key);
            values.add(props.getProperty(key));
        }
        PropertyTableModel sysModel = new PropertyTableModel(keys.toArray(new String[]{}), values.toArray(new String[]{}));
        sysTable.setModel(sysModel);
    }

    private void initiateLicenseText() {
        try {
            licTextArea.setText(IOAccess.readFileFromJar("/gpl-3.0.txt"));
            licTextArea.setCaretPosition(0);
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    public class CloseListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            setVisible(false);
            dispose();
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
        java.awt.GridBagConstraints gridBagConstraints;

        javax.swing.JPanel mainPanel = new javax.swing.JPanel();
        javax.swing.JTabbedPane mainTabbedPane = new javax.swing.JTabbedPane();
        javax.swing.JPanel appPanel = new javax.swing.JPanel();
        javax.swing.JLabel nameLabel = new javax.swing.JLabel();
        versionLabel = new javax.swing.JLabel();
        javax.swing.JLabel subtextLabel = new javax.swing.JLabel();
        javax.swing.JLabel imageLabel = new javax.swing.JLabel();
        javax.swing.JLabel copyLabel = new javax.swing.JLabel();
        latestLabel = new javax.swing.JLabel();
        siteLabel = new javax.swing.JLabel();
        javax.swing.JSeparator versionSeparator = new javax.swing.JSeparator();
        javax.swing.JPanel memPanel = new javax.swing.JPanel();
        memLabel = new javax.swing.JLabel();
        memProgressBar = new javax.swing.JProgressBar();
        javax.swing.JLabel positionLabel = new javax.swing.JLabel();
        javax.swing.JPanel licPanel = new javax.swing.JPanel();
        javax.swing.JScrollPane licScrollPane = new javax.swing.JScrollPane();
        licTextArea = new javax.swing.JTextArea();
        javax.swing.JPanel systemPanel = new javax.swing.JPanel();
        javax.swing.JScrollPane sysScrollPane = new javax.swing.JScrollPane();
        sysTable = new javax.swing.JTable();
        javax.swing.JPanel controlPanel = new javax.swing.JPanel();
        closeButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        mainPanel.setLayout(new javax.swing.BoxLayout(mainPanel, javax.swing.BoxLayout.LINE_AXIS));

        appPanel.setLayout(new java.awt.GridBagLayout());

        nameLabel.setText("<html><strong>Agent Station");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(22, 0, 11, 0);
        appPanel.add(nameLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 0);
        appPanel.add(versionLabel, gridBagConstraints);

        subtextLabel.setText("Environment for static and mobile software agents");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 0);
        appPanel.add(subtextLabel, gridBagConstraints);

        imageLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/AppIcons/agent-station-64.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 0);
        appPanel.add(imageLabel, gridBagConstraints);

        copyLabel.setText("<html>Copyright &copy; 2022  Dr Christos Bohoris");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 22, 0);
        appPanel.add(copyLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 0);
        appPanel.add(latestLabel, gridBagConstraints);

        siteLabel.setText("connectina.co.uk/agent-station");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 0);
        appPanel.add(siteLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 11, 11);
        appPanel.add(versionSeparator, gridBagConstraints);

        memPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        memPanel.add(memLabel, gridBagConstraints);

        memProgressBar.setStringPainted(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        memPanel.add(memProgressBar, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        appPanel.add(memPanel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        appPanel.add(positionLabel, gridBagConstraints);

        mainTabbedPane.addTab("Application", appPanel);

        licPanel.setLayout(new java.awt.BorderLayout());

        licTextArea.setEditable(false);
        licTextArea.setColumns(20);
        licTextArea.setRows(5);
        licScrollPane.setViewportView(licTextArea);

        licPanel.add(licScrollPane, java.awt.BorderLayout.CENTER);

        mainTabbedPane.addTab("License", licPanel);

        systemPanel.setLayout(new java.awt.BorderLayout());

        sysScrollPane.setViewportView(sysTable);

        systemPanel.add(sysScrollPane, java.awt.BorderLayout.CENTER);

        mainTabbedPane.addTab("System", systemPanel);

        mainPanel.add(mainTabbedPane);

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        controlPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 12, 11, 11));
        controlPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        closeButton.setText("Close");
        controlPanel.add(closeButton);

        getContentPane().add(controlPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JLabel latestLabel;
    private javax.swing.JTextArea licTextArea;
    private javax.swing.JLabel memLabel;
    private javax.swing.JProgressBar memProgressBar;
    private javax.swing.JLabel siteLabel;
    private javax.swing.JTable sysTable;
    private javax.swing.JLabel versionLabel;
    // End of variables declaration//GEN-END:variables
}
