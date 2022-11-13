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
import javax.swing.JOptionPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.connectina.agentstation.api.Registry;
import uk.co.connectina.agentstation.api.Station;
import uk.co.connectina.agentstation.api.client.OperationException;
import static uk.co.connectina.agentstation.desktop.AgentStationFrame.ERROR_TITLE;
import uk.co.connectina.agentstation.local.IOAccess;

/**
 * Handles the required actions for adding a place. 
 *
 * @author Dr Christos Bohoris
 */
public class AddPlaceListener implements ActionListener {

    private final AgentStationFrame frame;
    private final Station agentStation;
    private final Registry agentRegistry;
    private final LogTableModel logTableModel;
    private static final Logger LOGGER = LogManager.getLogger(AddPlaceListener.class.toString());

    AddPlaceListener(AgentStationFrame frame, Station agentStation, Registry agentRegistry, LogTableModel logTableModel) {
        this.frame = frame;
        this.agentStation = agentStation;
        this.agentRegistry = agentRegistry;
        this.logTableModel = logTableModel;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        String placeName = JOptionPane.showInputDialog(frame, "Place name:", null);
        // Check that the place name is not blank
        if (placeName != null && placeName.isBlank()) {
            JOptionPane.showMessageDialog(frame, "The place name is mandatory.", ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
        } else if (placeName != null && !placeName.isBlank()) {
            try {
                // Check that the place name does not already exist
                List<String> places = agentRegistry.lookupPlaces();
                for (String place : places) {
                    if (place.equalsIgnoreCase(placeName)) {
                        JOptionPane.showMessageDialog(frame, "A place with this name already exists.", ERROR_TITLE, JOptionPane.ERROR_MESSAGE);

                        return;
                    }
                }

                agentStation.createPlace(placeName);
            } catch (OperationException e) {
                logTableModel.addError(IOAccess.getAboutPlace(placeName), e.getMessage());
                LOGGER.error("{}: {}",IOAccess.getAboutPlace(placeName), e.getMessage());
            }
        }
    }

}
