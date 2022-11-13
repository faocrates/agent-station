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
import java.util.ArrayList;
import java.util.List;
import javax.swing.JList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.connectina.agentstation.api.Permission;
import uk.co.connectina.agentstation.api.Registry;
import uk.co.connectina.agentstation.api.Station;
import uk.co.connectina.agentstation.api.client.OperationException;

/**
 * Handles the actions required for viwing or modifying the permissions of agents within a particular place.
 * 
 * @author Dr Christos Bohoris
 */
public class PlacePermissionsListener implements ActionListener {

    private final AgentStationFrame frame;
    private final JList<String> placeList;
    private final Station agentStation;
    private final Registry agentRegistry;
    private static final Logger LOGGER = LogManager.getLogger(PlacePermissionsListener.class.toString());

    PlacePermissionsListener(AgentStationFrame frame, JList<String> placeList, Station agentStation, Registry agentRegistry) {
        this.frame = frame;
        this.placeList = placeList;
        this.agentStation = agentStation;
        this.agentRegistry = agentRegistry;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        final int row = placeList.getSelectedIndex();
        if (row == -1) {

            return;
        }
        List<Permission> originalPermissions = new ArrayList<>();
        try {
            originalPermissions = agentRegistry.lookupPermissionsByPlaceName(placeList.getSelectedValue());
        } catch (OperationException e) {
            LOGGER.error(e);
        }

        List<Permission> permissions = new PermissionDialog(frame, true, originalPermissions, placeList.getSelectedValue()).display();
        if (permissions == null) {

            return;
        }

        for (Permission permission : originalPermissions) {
            try {
                agentRegistry.deregisterPermission(permission);
            } catch (OperationException e) {
                LOGGER.error(e);
            }
        }

        for (Permission permission : permissions) {
            try {
                agentRegistry.registerPermission(permission);
            } catch (OperationException e) {
                LOGGER.error(e);
            }
        }
        
        try {
            agentStation.permissionsChange(placeList.getSelectedValue());
        } catch (OperationException e) {
            LOGGER.error(e);
        }
    }

}
