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
import javax.swing.JList;
import javax.swing.JOptionPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.connectina.agentstation.api.Instance;
import uk.co.connectina.agentstation.api.Permission;
import uk.co.connectina.agentstation.api.PermissionIdentity;
import uk.co.connectina.agentstation.api.Registry;
import uk.co.connectina.agentstation.api.Station;
import uk.co.connectina.agentstation.api.client.OperationException;
import static uk.co.connectina.agentstation.desktop.AgentStationFrame.ERROR_TITLE;
import uk.co.connectina.agentstation.local.IOAccess;

/**
 *
 *
 * @author Dr Christos Bohoris
 */
public class AddAgentListener implements ActionListener {

    private final AgentStationFrame frame;
    private final JList<String> placeList;
    private final Station agentStation;
    private final Registry agentRegistry;
    private Instance instance;
    private final LogTableModel logTableModel;
    private static final Logger LOGGER = LogManager.getLogger(AddAgentListener.class.toString());

    AddAgentListener(AgentStationFrame frame, JList<String> placeList, Station agentStation, Registry agentRegistry, LogTableModel logTableModel) {
        this.frame = frame;
        this.placeList = placeList;
        this.agentStation = agentStation;
        this.agentRegistry = agentRegistry;
        this.logTableModel = logTableModel;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        List<Instance> instances;
        try {
            instance = new AddAgentDialog(frame, true, agentRegistry.lookupPlaces(), placeList.getSelectedIndex()).display();
            if (instance == null) {

                return;
            }

            instances = agentRegistry.lookupAgents();
            if (instances.contains(instance)) {
                JOptionPane.showMessageDialog(frame, "This agent already exists.", ERROR_TITLE, JOptionPane.ERROR_MESSAGE);

                return;
            }

            Permission permission = agentRegistry.lookupPermission(new PermissionIdentity(instance.getIdentity().getName(), instance.getShortId(), instance.getPlaceName()));
            if (permission == null) {
                permission = new AgentPermissionDialog(frame, true, instance.getIdentity(), instance.getPlaceName()).display();
                if (permission == null) {

                    return;
                } else {
                    permission.setAgentShortId(instance.getShortId());
                    agentRegistry.registerPermission(permission);
                }
            }

            if (permission.isAllowed()) {
                agentStation.createAgent(instance, null);
                if (permission.isAutoStart()) {
                    agentStation.startAgent(instance);
                }
            } else {
                logTableModel.addError(IOAccess.getAboutAgent(instance), "Not allowed in this place");
            }

        } catch (OperationException e) {
            logTableModel.addError(IOAccess.getAboutAgent(instance), e.getMessage());
            LOGGER.error("{}: {}",IOAccess.getPlainAboutAgent(instance), e.getMessage());
        }
    }
}
