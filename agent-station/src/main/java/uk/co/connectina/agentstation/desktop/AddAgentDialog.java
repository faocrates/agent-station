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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import uk.co.connectina.agentstation.api.Identity;
import uk.co.connectina.agentstation.api.Instance;
import uk.co.connectina.agentstation.local.IOAccess;

/**
 * A dialog allowing a user to add an agent. The agent details are either provided manually or imported.
 *
 * @author Dr Christos Bohoris
 */
public class AddAgentDialog extends javax.swing.JDialog {

    public static final String ERROR_TITLE = "Error";
    public static final String USER_HOME = "user.home";
    private Instance instance = null;

    /**
     * Creates new form AddAgentDialog
     */
    public AddAgentDialog(java.awt.Frame parent, boolean modal, List<String> places, int selectedPlaceIndex) {
        super(parent, modal);
        setTitle("Add Agent");
        initComponents();
        okButton.addActionListener(new OkListener());
        cancelButton.addActionListener(new CancelListener());
        importButton.addActionListener(new ImportListener());
        browseButton.addActionListener(new BrowseListener());
        for (String p : places) {
            placeComboBox.addItem(p);
        }
        if (selectedPlaceIndex > -1) {
            placeComboBox.setSelectedIndex(selectedPlaceIndex);
        }

        getRootPane().setDefaultButton(okButton);
        okButton.revalidate();
        int height = new JTextField().getPreferredSize().height;
        Dimension longDimension = new Dimension(280, height);
        nameTextField.setPreferredSize(longDimension);
        orgTextField.setPreferredSize(longDimension);
        majorSpinner.setPreferredSize(new Dimension(100, height));
        minorSpinner.setPreferredSize(new Dimension(100, height));
        majorSpinner.setMaximumSize(new Dimension(100, height));
        minorSpinner.setMaximumSize(new Dimension(100, height));
        hashTextField.setPreferredSize(longDimension);
        classTextField.setPreferredSize(longDimension);
        descTextField.setPreferredSize(longDimension);
        placeComboBox.setPreferredSize(longDimension);
        locTextField.setPreferredSize(new Dimension(140, height));
        paramTextField.setPreferredSize(longDimension);

        classTextField.getDocument().addDocumentListener(getClassNameDocListener());
        orgTextField.requestFocus();

        pack();
        setLocationRelativeTo(parent);
    }

    public Instance display() {
        setVisible(true);

        return instance;
    }

    public DocumentListener getClassNameDocListener() {

        return new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateNameField();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateNameField();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateNameField();
            }

            private void updateNameField() {
                String className = classTextField.getText().trim();
                String text = !className.contains(".") ? className : className.substring(className.lastIndexOf(".") + 1);
                nameTextField.setText(text);
            }
        };
    }

    public class OkListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            String name = nameTextField.getText().trim();
            String org = orgTextField.getText().trim();
            String major = majorSpinner.getValue().toString();
            String minor = minorSpinner.getValue().toString();
            String hash = hashTextField.getText().trim();
            String className = classTextField.getText().trim();
            String descr = descTextField.getText().trim();
            String placeName = Objects.requireNonNull(placeComboBox.getSelectedItem()).toString();
            String loc = locTextField.getText().trim();
            String param = paramTextField.getText().trim();
            if (name.isBlank() || org.isBlank() || major.isBlank() || minor.isBlank() || hash.isBlank() || className.isBlank() || descr.isBlank() || placeName.isBlank() || loc.isBlank()) {
                JOptionPane.showMessageDialog(AddAgentDialog.this, "All information is mandatory.", ERROR_TITLE, JOptionPane.ERROR_MESSAGE);

                return;
            }

            String[] paramOutput = null;
            if (!param.isBlank()) {
                String[] params = param.split(",");
                paramOutput = new String[params.length];
                int index = 0;
                for (String p : params) {
                    paramOutput[index++] = p.trim();
                }
            }
            int majorVersion = Integer.parseInt(major);
            int minorVersion = Integer.parseInt(minor);
            Identity identity = new Identity.IdentityBuilder(className, org).hashCode(hash).packageFile(loc).version(majorVersion, minorVersion).description(descr).build();
            instance = new Instance(identity, LocalDateTime.now(), placeName, paramOutput);

            setVisible(false);
            dispose();
        }

    }

    public class CancelListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            setVisible(false);
            dispose();
        }

    }

    public class BrowseListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            final JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File(System.getProperty(USER_HOME)));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Jar files", "jar");
            chooser.addChoosableFileFilter(filter);
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(AddAgentDialog.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                locTextField.setText(file.getAbsolutePath());
                try {
                    hashTextField.setText(IOAccess.getFileMD5(file.getAbsolutePath()));
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(AddAgentDialog.this, "Failed to get hash code for package.\n" + e.getMessage(), ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                }
            }
        }

    }

    public class ImportListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            final JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(IOAccess.APP_PROPERTY_FOLDER);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Property files", "properties");
            chooser.addChoosableFileFilter(filter);
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(AddAgentDialog.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                try ( FileInputStream fis = new FileInputStream(file)) {
                    Properties properties = new Properties();
                    properties.load(fis);
                    orgTextField.setText(properties.getProperty("organisation", ""));
                    majorSpinner.setValue(Integer.parseInt(properties.getProperty("majorVersion", "")));
                    minorSpinner.setValue(Integer.parseInt(properties.getProperty("minorVersion", "")));
                    classTextField.setText(properties.getProperty("className", ""));
                    descTextField.setText(properties.getProperty("Description", ""));
                    locTextField.setText(properties.getProperty("packageLocation", ""));
                    paramTextField.setText(properties.getProperty("parameters", ""));

                    String hashCodeInProperties = properties.getProperty("hashCode", "");
                    String hashCodeOfPackage = IOAccess.getFileMD5(locTextField.getText());
                    if (!hashCodeInProperties.isBlank() && !hashCodeOfPackage.equals(hashCodeInProperties)) {
                        JOptionPane.showMessageDialog(AddAgentDialog.this, MessageFormat.format("Hash code mismatch.\nProperty value: {0}\nPackage value: {1}", hashCodeInProperties, hashCodeOfPackage), "Warning", JOptionPane.WARNING_MESSAGE);
                    }
                    hashTextField.setText(hashCodeOfPackage);
                } catch (FileNotFoundException e) {
                    JOptionPane.showMessageDialog(AddAgentDialog.this, "Failed to access property file.\n" + e.getMessage(), ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(AddAgentDialog.this, "Failed to read property file.\n" + e.getMessage(), ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                }
            }
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
        javax.swing.JPanel inputPanel = new javax.swing.JPanel();
        javax.swing.JLabel nameLabel = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        javax.swing.JLabel orgLabel = new javax.swing.JLabel();
        orgTextField = new javax.swing.JTextField();
        javax.swing.JLabel majorLabel = new javax.swing.JLabel();
        majorSpinner = new javax.swing.JSpinner();
        javax.swing.JLabel minorLabel = new javax.swing.JLabel();
        minorSpinner = new javax.swing.JSpinner();
        javax.swing.JLabel hashLabel = new javax.swing.JLabel();
        hashTextField = new javax.swing.JTextField();
        javax.swing.JLabel descLabel = new javax.swing.JLabel();
        descTextField = new javax.swing.JTextField();
        javax.swing.JLabel classLabel = new javax.swing.JLabel();
        classTextField = new javax.swing.JTextField();
        javax.swing.JLabel placeLabel = new javax.swing.JLabel();
        placeComboBox = new javax.swing.JComboBox<>();
        javax.swing.JLabel locLabel = new javax.swing.JLabel();
        javax.swing.JLabel paramLabel = new javax.swing.JLabel();
        paramTextField = new javax.swing.JTextField();
        javax.swing.JPanel locPanel = new javax.swing.JPanel();
        locTextField = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        javax.swing.JToolBar mainToolBar = new javax.swing.JToolBar();
        importButton = new javax.swing.JButton();
        javax.swing.JPanel controlPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        mainPanel.setLayout(new java.awt.BorderLayout());

        inputPanel.setLayout(new java.awt.GridBagLayout());

        nameLabel.setLabelFor(nameTextField);
        nameLabel.setText("Agent Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 11, 5);
        inputPanel.add(nameLabel, gridBagConstraints);

        nameTextField.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 11, 0);
        inputPanel.add(nameTextField, gridBagConstraints);

        orgLabel.setLabelFor(orgTextField);
        orgLabel.setText("Organisation:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 11, 11, 5);
        inputPanel.add(orgLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 11, 11);
        inputPanel.add(orgTextField, gridBagConstraints);

        majorLabel.setLabelFor(majorSpinner);
        majorLabel.setText("Major Version:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 11, 5);
        inputPanel.add(majorLabel, gridBagConstraints);

        majorSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 0, null, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 0);
        inputPanel.add(majorSpinner, gridBagConstraints);

        minorLabel.setLabelFor(minorSpinner);
        minorLabel.setText("Minor Version:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 11, 11, 5);
        inputPanel.add(minorLabel, gridBagConstraints);

        minorSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 0);
        inputPanel.add(minorSpinner, gridBagConstraints);

        hashLabel.setLabelFor(hashTextField);
        hashLabel.setText("Package Hash Code:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 11, 5);
        inputPanel.add(hashLabel, gridBagConstraints);

        hashTextField.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 0);
        inputPanel.add(hashTextField, gridBagConstraints);

        descLabel.setLabelFor(descTextField);
        descLabel.setText("Description:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 11, 11, 5);
        inputPanel.add(descLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 11);
        inputPanel.add(descTextField, gridBagConstraints);

        classLabel.setLabelFor(classTextField);
        classLabel.setText("Class Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 11, 5);
        inputPanel.add(classLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 0);
        inputPanel.add(classTextField, gridBagConstraints);

        placeLabel.setLabelFor(placeComboBox);
        placeLabel.setText("Place Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 11, 11, 5);
        inputPanel.add(placeLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 11);
        inputPanel.add(placeComboBox, gridBagConstraints);

        locLabel.setLabelFor(locTextField);
        locLabel.setText("Package Location:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 11, 11, 5);
        inputPanel.add(locLabel, gridBagConstraints);

        paramLabel.setLabelFor(paramTextField);
        paramLabel.setText("Parameters:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 11, 11, 5);
        inputPanel.add(paramLabel, gridBagConstraints);

        paramTextField.setToolTipText("Comma-separated list of parameters");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 11);
        inputPanel.add(paramTextField, gridBagConstraints);

        locPanel.setLayout(new java.awt.GridBagLayout());

        locTextField.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        locPanel.add(locTextField, gridBagConstraints);

        browseButton.setText("Browse...");
        browseButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        browseButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        locPanel.add(browseButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 0);
        inputPanel.add(locPanel, gridBagConstraints);

        mainPanel.add(inputPanel, java.awt.BorderLayout.CENTER);

        mainToolBar.setRollover(true);

        importButton.setText("Import...");
        importButton.setBorderPainted(false);
        importButton.setFocusable(false);
        importButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        importButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolBar.add(importButton);

        mainPanel.add(mainToolBar, java.awt.BorderLayout.SOUTH);

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        controlPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 12, 11, 11));
        controlPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        okButton.setText("OK");
        controlPanel.add(okButton);

        cancelButton.setText("Cancel");
        controlPanel.add(cancelButton);

        getContentPane().add(controlPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JTextField classTextField;
    private javax.swing.JTextField descTextField;
    private javax.swing.JTextField hashTextField;
    private javax.swing.JButton importButton;
    private javax.swing.JTextField locTextField;
    private javax.swing.JSpinner majorSpinner;
    private javax.swing.JSpinner minorSpinner;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JButton okButton;
    private javax.swing.JTextField orgTextField;
    private javax.swing.JTextField paramTextField;
    private javax.swing.JComboBox<String> placeComboBox;
    // End of variables declaration//GEN-END:variables

}
