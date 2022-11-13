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
import java.util.Date;
import javax.swing.DefaultComboBoxModel;
import uk.co.connectina.agentstation.local.Schedule;

/**
 * A panel that allows a user to schedule the starting of an agent.
 *
 * @author Dr Christos Bohoris
 */
public class SchedulePanel extends javax.swing.JPanel {

    private String[] unitLabelText = {"Hours", "Days"};

    /**
     * Creates new form SchedulePanel
     */
    public SchedulePanel() {
        initComponents();
        int longerWidth = 120;
        int height = dateFormattedTextField.getPreferredSize().height;
        dateFormattedTextField.setMinimumSize(new Dimension(longerWidth, height));
        dateFormattedTextField.setPreferredSize(new Dimension(longerWidth, height));
        timeFormattedTextField.setMinimumSize(new Dimension(longerWidth, height));
        timeFormattedTextField.setPreferredSize(new Dimension(longerWidth, height));
        height = repeatComboBox.getPreferredSize().height;
        repeatComboBox.setMinimumSize(new Dimension(longerWidth, height));
        repeatComboBox.setPreferredSize(new Dimension(longerWidth, height));

        int shorterWidth = 100;
        height = intervalSpinner.getPreferredSize().height;
        intervalSpinner.setMinimumSize(new Dimension(shorterWidth, height));
        intervalSpinner.setPreferredSize(new Dimension(shorterWidth, height));
        occurSpinner.setMinimumSize(new Dimension(shorterWidth, height));
        occurSpinner.setPreferredSize(new Dimension(shorterWidth, height));
        scheduleCheckBox.addActionListener(new ScheduleListener());
        onceRadioButton.addActionListener(new OnceRadioListener());
        repeatRadioButton.addActionListener(new RepeatRadioListener());

        Date nowDate = new Date();
        dateFormattedTextField.setValue(nowDate);
        timeFormattedTextField.setValue(nowDate);

        repeatComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"Hourly", "Daily"}));
        repeatComboBox.addActionListener(new RepeatListener());
        repeatComboBox.setSelectedIndex(0);

        unitLabel.setText(unitLabelText[repeatComboBox.getSelectedIndex()]);

        scheduleSelected();
    }

    void setSchedule(Schedule schedule) {
        if (schedule != null) {
            scheduleCheckBox.setSelected(true);
        } else {
            
            return;
        }
        
        if (schedule.getRepeat() != null) {
            repeatRadioButton.setSelected(true);
            repeatComboBox.setSelectedIndex(schedule.getRepeat().ordinal());
            intervalSpinner.setValue(schedule.getInterval());
            occurSpinner.setValue(schedule.getOccur());
        } else {
            onceRadioButton.setSelected(true);
        }
        
        dateFormattedTextField.setText(schedule.getStartDate());
        timeFormattedTextField.setText(schedule.getStartTime());
        
        scheduleSelected();
    }
    
    Schedule getSchedule() {
        if (!scheduleCheckBox.isSelected()) {

            return null;
        }

        Schedule schedule = new Schedule();
        schedule.setStartDate(dateFormattedTextField.getText());
        schedule.setStartTime(timeFormattedTextField.getText());
        if (repeatRadioButton.isSelected()) {
            schedule.setInterval((int) intervalSpinner.getValue());
            schedule.setOccur((int) occurSpinner.getValue());
            String value = (String) repeatComboBox.getSelectedItem();
            if (value != null) {
                schedule.setRepeat(Schedule.RepeatType.valueOf(value.toUpperCase()));
            }
        }
        return schedule;
    }

    private void scheduleSelected() {
        boolean state = scheduleCheckBox.isSelected();
        onceRadioButton.setEnabled(state);
        repeatRadioButton.setEnabled(state);
        dateFormattedTextField.setEditable(state);
        timeFormattedTextField.setEditable(state);
        repeatComboBox.setEnabled(state);
        intervalSpinner.setEnabled(state);
        occurSpinner.setEnabled(state);
        onceSelected();
        repeatSelected();
    }

    private void onceSelected() {
        boolean state = onceRadioButton.isSelected();
        boolean schedState = scheduleCheckBox.isSelected();
        if (scheduleCheckBox.isSelected()) {
            dateFormattedTextField.setEditable(schedState);
            timeFormattedTextField.setEditable(schedState);
            repeatComboBox.setEnabled(!state);
            intervalSpinner.setEnabled(!state);
            occurSpinner.setEnabled(!state);
        }
    }

    private void repeatSelected() {
        boolean state = repeatRadioButton.isSelected();
        boolean schedState = scheduleCheckBox.isSelected();
        if (scheduleCheckBox.isSelected()) {
            dateFormattedTextField.setEditable(schedState);
            timeFormattedTextField.setEditable(schedState);
            repeatComboBox.setEnabled(state);
            intervalSpinner.setEnabled(state);
            occurSpinner.setEnabled(state);
        }
    }

    public class ScheduleListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            scheduleSelected();
        }

    }

    public class RepeatListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            unitLabel.setText(unitLabelText[repeatComboBox.getSelectedIndex()]);
        }

    }

    public class OnceRadioListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            onceSelected();
        }

    }

    public class RepeatRadioListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            repeatSelected();
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

        javax.swing.ButtonGroup radioButtonGroup = new javax.swing.ButtonGroup();
        javax.swing.JPanel mainPanel = new javax.swing.JPanel();
        javax.swing.JPanel contentPanel = new javax.swing.JPanel();
        scheduleCheckBox = new javax.swing.JCheckBox();
        onceRadioButton = new javax.swing.JRadioButton();
        repeatRadioButton = new javax.swing.JRadioButton();
        javax.swing.JLabel startLabel = new javax.swing.JLabel();
        dateFormattedTextField = new javax.swing.JFormattedTextField();
        timeFormattedTextField = new javax.swing.JFormattedTextField();
        javax.swing.JPanel repeatOuterPanel = new javax.swing.JPanel();
        javax.swing.JPanel repeatInnerPanel = new javax.swing.JPanel();
        javax.swing.JLabel repeatLabel = new javax.swing.JLabel();
        repeatComboBox = new javax.swing.JComboBox<>();
        javax.swing.JLabel intervalLabel = new javax.swing.JLabel();
        intervalSpinner = new javax.swing.JSpinner();
        javax.swing.JLabel occurLabel = new javax.swing.JLabel();
        occurSpinner = new javax.swing.JSpinner();
        javax.swing.JLabel positionLabel = new javax.swing.JLabel();
        unitLabel = new javax.swing.JLabel();
        javax.swing.JSeparator repeatSeparator = new javax.swing.JSeparator();

        setLayout(new java.awt.BorderLayout());

        mainPanel.setLayout(new java.awt.BorderLayout());

        contentPanel.setLayout(new java.awt.GridBagLayout());

        scheduleCheckBox.setText("Enable");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 11, 11);
        contentPanel.add(scheduleCheckBox, gridBagConstraints);

        radioButtonGroup.add(onceRadioButton);
        onceRadioButton.setSelected(true);
        onceRadioButton.setText("Once");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 11, 11);
        contentPanel.add(onceRadioButton, gridBagConstraints);

        radioButtonGroup.add(repeatRadioButton);
        repeatRadioButton.setText("Recurring");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 11, 11);
        contentPanel.add(repeatRadioButton, gridBagConstraints);

        startLabel.setText("Start:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 11, 5);
        contentPanel.add(startLabel, gridBagConstraints);

        dateFormattedTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(new java.text.SimpleDateFormat("yyyy-MM-dd"))));
        dateFormattedTextField.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 11);
        contentPanel.add(dateFormattedTextField, gridBagConstraints);

        timeFormattedTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT))));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 11);
        contentPanel.add(timeFormattedTextField, gridBagConstraints);

        repeatOuterPanel.setLayout(new java.awt.BorderLayout());

        repeatInnerPanel.setLayout(new java.awt.GridBagLayout());

        repeatLabel.setText("Repeat:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 11, 5);
        repeatInnerPanel.add(repeatLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 11, 11);
        repeatInnerPanel.add(repeatComboBox, gridBagConstraints);

        intervalLabel.setText("Interval:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 11, 5);
        repeatInnerPanel.add(intervalLabel, gridBagConstraints);

        intervalSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, 30, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 11);
        repeatInnerPanel.add(intervalSpinner, gridBagConstraints);

        occurLabel.setText("Repetitions:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 11, 5);
        repeatInnerPanel.add(occurLabel, gridBagConstraints);

        occurSpinner.setModel(new javax.swing.SpinnerNumberModel(3, 2, 30, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 11);
        repeatInnerPanel.add(occurSpinner, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        repeatInnerPanel.add(positionLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 11);
        repeatInnerPanel.add(unitLabel, gridBagConstraints);

        repeatOuterPanel.add(repeatInnerPanel, java.awt.BorderLayout.CENTER);
        repeatOuterPanel.add(repeatSeparator, java.awt.BorderLayout.NORTH);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        contentPanel.add(repeatOuterPanel, gridBagConstraints);

        mainPanel.add(contentPanel, java.awt.BorderLayout.CENTER);

        add(mainPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFormattedTextField dateFormattedTextField;
    private javax.swing.JSpinner intervalSpinner;
    private javax.swing.JSpinner occurSpinner;
    private javax.swing.JRadioButton onceRadioButton;
    private javax.swing.JComboBox<String> repeatComboBox;
    private javax.swing.JRadioButton repeatRadioButton;
    private javax.swing.JCheckBox scheduleCheckBox;
    private javax.swing.JFormattedTextField timeFormattedTextField;
    private javax.swing.JLabel unitLabel;
    // End of variables declaration//GEN-END:variables
}
