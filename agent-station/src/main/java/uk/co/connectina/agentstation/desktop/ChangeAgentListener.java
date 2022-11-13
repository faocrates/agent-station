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
import java.util.Arrays;
import javax.swing.JTable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.connectina.agentstation.api.Instance;
import uk.co.connectina.agentstation.api.Registry;
import uk.co.connectina.agentstation.local.Schedule;
import uk.co.connectina.agentstation.api.Station;
import uk.co.connectina.agentstation.api.client.OperationException;

/**
 * Handles the required actions for changing an agent.
 *
 * @author Dr Christos Bohoris
 */
public class ChangeAgentListener implements ActionListener {

    private final AgentStationFrame frame;
    private final JTable agentTable;
    private final Station agentStation;
    private final Registry agentRegistry;
    private final AgentTableModel agentModel;
    private static final Logger LOGGER = LogManager.getLogger(ChangeAgentListener.class.toString());

    ChangeAgentListener(AgentStationFrame frame, JTable agentTable, AgentTableModel agentModel, Station agentStation, Registry agentRegistry) {
        this.frame = frame;
        this.agentTable = agentTable;
        this.agentModel = agentModel;
        this.agentStation = agentStation;
        this.agentRegistry = agentRegistry;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        int index = agentTable.getSelectionModel().getMinSelectionIndex();
        if (index < 0) {

            return;
        }
        Instance instance = agentModel.get(agentTable.getSelectionModel().getMinSelectionIndex());
        if (instance == null) {

            return;
        }

        Schedule originalSchedule;
        try {
            originalSchedule = agentRegistry.lookupScheduleByAgentSid(instance.getSid());
        } catch (OperationException e) {
            LOGGER.error(e);
            
            return;
        }

        ChangeAgentDialog changeAgentDialog = new ChangeAgentDialog(frame, true, instance, originalSchedule);
        changeAgentDialog.display();

        String[] parameters = changeAgentDialog.getParameters();

        // If parameters have changed then update them
        if (parameters != null && !Arrays.equals(instance.getParameters(), parameters)) {
            instance.setParameters(parameters);
            try {
                agentRegistry.updateAgentParameters(instance);
            } catch (OperationException e) {
                LOGGER.error(e);
            }
        }

        Schedule schedule = changeAgentDialog.getSchedule();
        try {
            if (schedule != null) {
                schedule.setAgentSid(instance.getSid());
                agentRegistry.registerSchedule(schedule);
                agentStation.initiateAnySchedule(instance);
            } else if (agentRegistry.scheduleExists(instance.getSid())) {
                agentRegistry.deregisterSchedule(instance.getSid());
            }
        } catch (OperationException e) {
            LOGGER.error(e);
        }
    }

}
