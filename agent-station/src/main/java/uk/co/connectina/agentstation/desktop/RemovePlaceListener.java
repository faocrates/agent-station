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
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.connectina.agentstation.api.Instance;
import uk.co.connectina.agentstation.api.Registry;
import uk.co.connectina.agentstation.api.Station;
import uk.co.connectina.agentstation.api.client.OperationException;
import static uk.co.connectina.agentstation.desktop.AgentStationFrame.ERROR_TITLE;
import uk.co.connectina.agentstation.local.IOAccess;

/**
 * Handles the actions required for removing a place.
 *
 * @author Dr Christos Bohoris
 */
public class RemovePlaceListener implements ActionListener {

    private final AgentStationFrame frame;
    private final JList<String> placeList;
    private final Station agentStation;
    private final Registry agentRegistry;
    private final DefaultListModel<String> placeListModel;
    private final LogTableModel logTableModel;
    private static final Logger LOGGER = LogManager.getLogger(RemovePlaceListener.class.toString());

    RemovePlaceListener(AgentStationFrame frame, JList<String> placeList, Station agentStation, Registry agentRegistry, DefaultListModel<String> placeListModel, LogTableModel logTableModel) {
        this.frame = frame;
        this.placeList = placeList;
        this.agentStation = agentStation;
        this.agentRegistry = agentRegistry;
        this.placeListModel = placeListModel;
        this.logTableModel = logTableModel;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        String placeName = null;
        try {
            int index = placeList.getSelectedIndex();
            if (index == -1 || index > placeListModel.size() - 1) {

                return;
            }
            placeName = placeListModel.get(index);
            // Check that the place name is not the Default
            if (placeName.equals("Default")) {
                JOptionPane.showMessageDialog(frame, "The Default place cannot be deleted.", ERROR_TITLE, JOptionPane.ERROR_MESSAGE);

                return;
            }
            // Check that the place does not contain agents
            List<Instance> instances = agentRegistry.lookupAgentsByPlaceName(placeListModel.get(index));
            if (!instances.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "The place contains agents and cannot be deleted.", ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            } else {
                agentStation.removePlace(placeName);
            }
        } catch (OperationException e) {
            logTableModel.addError(IOAccess.getAboutPlace(placeName), e.getMessage());
            LOGGER.error("{}: {}", IOAccess.getAboutPlace(placeName), e.getMessage());
        }
    }

}
