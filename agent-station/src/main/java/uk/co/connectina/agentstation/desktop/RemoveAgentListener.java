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
import javax.swing.JTable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.connectina.agentstation.api.Instance;
import uk.co.connectina.agentstation.api.Station;
import uk.co.connectina.agentstation.api.client.OperationException;
import uk.co.connectina.agentstation.local.IOAccess;

/**
 * Handles the actions required for removing an agent.
 *
 * @author Dr Christos Bohoris
 */
public class RemoveAgentListener implements ActionListener {

    private final JTable agentTable;
    private final AgentTableModel agentTableModel;
    private final Station agentStation;
    private final LogTableModel logTableModel;
    private static final Logger LOGGER = LogManager.getLogger(RemoveAgentListener.class.toString());

    RemoveAgentListener(JTable agentTable, AgentTableModel agentTableModel, Station agentStation, LogTableModel logTableModel) {
        this.agentTable = agentTable;
        this.agentTableModel = agentTableModel;
        this.agentStation = agentStation;
        this.logTableModel = logTableModel;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        final int row = agentTable.getSelectedRow();
        if (row == -1) {

            return;
        }

        Instance instance = agentTableModel.get(row);
        try {
            agentStation.removeAgent(instance);
        } catch (OperationException e) {
            logTableModel.addError(IOAccess.getAboutAgent(instance), e.getMessage());
            LOGGER.error("{}: {}", IOAccess.getPlainAboutAgent(instance), e.getMessage());
        }
    }

}
