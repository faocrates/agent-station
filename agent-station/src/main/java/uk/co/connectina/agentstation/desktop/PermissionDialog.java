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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import uk.co.connectina.agentstation.api.Identity;
import uk.co.connectina.agentstation.api.Instance;
import uk.co.connectina.agentstation.api.Permission;
import uk.co.connectina.agentstation.local.IOAccess;

/**
 * A dialog that allows a user to view and modify permissions for the agents within a particular place.
 *
 * @author Dr Christos Bohoris
 */
public class PermissionDialog extends javax.swing.JDialog {

    public static final String ERROR_TITLE = "Error";
    private final PermissionTableModel permissionTableModel;
    private List<Permission> permissions = null;
    private final String placeName;

    /**
     * Creates new form PlacePermissionsDialog
     */
    public PermissionDialog(java.awt.Frame parent, boolean modal, List<Permission> permissions, String placeName) {
        super(parent, modal);
        setTitle(MessageFormat.format("Permissions for {0} Place", placeName));
        List<Permission> copyPermissions = new ArrayList<>();
        for (Permission permission : permissions) {
            copyPermissions.add(new Permission(permission.getAgentName(), permission.getAgentShortId(), permission.getPlaceName(), permission.isAllowed(), permission.isAutoStart()));
        }
        this.placeName = placeName;
        initComponents();

        okButton.addActionListener(new OkListener());
        cancelButton.addActionListener(new CancelListener());
        addButton.addActionListener(new AddListener());
        removeButton.addActionListener(new RemoveListener());
        importButton.addActionListener(new ImportListener());

        permissionTableModel = new PermissionTableModel(copyPermissions);
        permissionTable.setModel(permissionTableModel);
        permissionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        permissionTable.getSelectionModel().addListSelectionListener((ListSelectionEvent e)
                -> removeButton.setEnabled(permissionTable.getSelectedRow() > -1)
        );
        removeButton.setEnabled(false);

        permissionTable.getColumnModel().getColumn(1).setCellRenderer(new HashCodeRenderer(SwingConstants.CENTER));

        permissionTable.getColumnModel().getColumn(0).setPreferredWidth(300);
        permissionTable.getColumnModel().getColumn(0).setMaxWidth(500);
        permissionTable.getColumnModel().getColumn(1).setPreferredWidth(260);
        permissionTable.getColumnModel().getColumn(1).setMaxWidth(340);
        permissionTable.getColumnModel().getColumn(2).setMaxWidth(200);
        permissionTable.getColumnModel().getColumn(3).setMaxWidth(150);
        permissionTable.getColumnModel().getColumn(4).setMaxWidth(150);
        okButton.requestFocus();
        getRootPane().setDefaultButton(okButton);
        okButton.revalidate();
        
        pack();
        setLocationRelativeTo(parent);
    }

    public List<Permission> display() {
        setVisible(true);

        return permissions;
    }

    public class ImportListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            final JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Property files", "properties");
            chooser.addChoosableFileFilter(filter);
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(PermissionDialog.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                try ( FileInputStream fis = new FileInputStream(file)) {
                    Properties properties = new Properties();
                    properties.load(fis);

                    String name = properties.getProperty("name", "");
                    String className = "";
                    if (name.isBlank()) {
                        className = properties.getProperty("className", "");
                        name = !className.contains(".") ? className : className.substring(className.lastIndexOf(".") + 1);
                    }
                    String org = properties.getProperty("organisation", "");
                    int majorVersion = Integer.parseInt(properties.getProperty("majorVersion", ""));
                    int minorVersion = Integer.parseInt(properties.getProperty("minorVersion", ""));

                    String loc = properties.getProperty("packageLocation", "");
                    Identity id = new Identity.IdentityBuilder(className, org)
                            .hashCode(IOAccess.getFileMD5(loc))
                            .version(majorVersion, minorVersion)
                            .build();
                    Instance instance = new Instance(id, LocalDateTime.now(), placeName);
                    permissionTableModel.addPermission(new Permission(name, instance.getShortId(), placeName, false, false));
                    permissionTableModel.fireTableDataChanged();
                } catch (FileNotFoundException e) {
                    JOptionPane.showMessageDialog(PermissionDialog.this, "Failed to access property file.", ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(PermissionDialog.this, "Failed to read property file.", ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                }
            }
        }

    }

    public class AddListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            permissionTableModel.addPermission(new Permission("", "", placeName, false, false));
            permissionTableModel.fireTableDataChanged();
        }

    }

    public class RemoveListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            int rowIndex = permissionTable.getSelectedRow();
            permissionTableModel.removePermission(rowIndex);
            permissionTableModel.fireTableDataChanged();
            if (permissionTableModel.getRowCount() > 0) {
                permissionTable.getSelectionModel().setSelectionInterval(permissionTableModel.getRowCount() - 1, permissionTableModel.getRowCount() - 1);
            }
        }

    }

    public class OkListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            String issues = inputValidation();
            if (issues.isEmpty()) {
                permissions = permissionTableModel.getPermissions();

                setVisible(false);
                dispose();
            } else {
                JOptionPane.showMessageDialog(PermissionDialog.this, issues, ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            }
        }

        private String inputValidation() {
            List<String> codes = new ArrayList<>();
            StringBuilder builder = new StringBuilder();
            int row = 1;
            for (Permission p : permissionTableModel.getPermissions()) {
                if (p.getAgentName().isBlank()) {
                    builder.append("Agent name cannot be blank in row ");
                    builder.append(row);
                    builder.append(".\n");
                }
                if (p.getAgentShortId().isBlank()) {
                    builder.append("Agent short id cannot be blank in row ");
                    builder.append(row);
                    builder.append(".\n");
                }

                codes.add(Integer.toString((p.getAgentName() + p.getAgentShortId()).hashCode()));
                row++;
            }

            row = 1;
            for (Permission p : permissionTableModel.getPermissions()) {
                int num = Collections.frequency(codes, Integer.toString((p.getAgentName() + p.getAgentShortId()).hashCode()));
                if (num > 1) {
                    builder.append("Duplicate permission in row ");
                    builder.append(row);
                    builder.append(".\n");

                    break;
                }
                row++;
            }

            return builder.toString();
        }

    }

    public class CancelListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {

            permissions = null;

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

        javax.swing.JPanel mainPanel = new javax.swing.JPanel();
        javax.swing.JToolBar mainToolBar = new javax.swing.JToolBar();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        importButton = new javax.swing.JButton();
        javax.swing.JScrollPane permissionScrollPane = new javax.swing.JScrollPane();
        permissionTable = new javax.swing.JTable();
        javax.swing.JPanel controlPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(900, 350));

        mainPanel.setLayout(new java.awt.BorderLayout());

        mainToolBar.setRollover(true);

        addButton.setText("Add");
        addButton.setBorderPainted(false);
        addButton.setFocusable(false);
        addButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolBar.add(addButton);

        removeButton.setText("Remove");
        removeButton.setBorderPainted(false);
        removeButton.setFocusable(false);
        removeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        removeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolBar.add(removeButton);

        importButton.setText("Import...");
        importButton.setBorderPainted(false);
        importButton.setFocusable(false);
        importButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        importButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolBar.add(importButton);

        mainPanel.add(mainToolBar, java.awt.BorderLayout.PAGE_END);

        permissionScrollPane.setViewportView(permissionTable);

        mainPanel.add(permissionScrollPane, java.awt.BorderLayout.CENTER);

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        controlPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 12, 11, 11));
        controlPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        okButton.setText("OK");
        controlPanel.add(okButton);

        cancelButton.setText("Cancel");
        controlPanel.add(cancelButton);

        getContentPane().add(controlPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton importButton;
    private javax.swing.JButton okButton;
    private javax.swing.JTable permissionTable;
    private javax.swing.JButton removeButton;
    // End of variables declaration//GEN-END:variables
}
