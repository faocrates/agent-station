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
import java.io.FileWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.connectina.agentstation.api.Identity;
import uk.co.connectina.agentstation.api.Instance;
import uk.co.connectina.agentstation.api.Permission;
import uk.co.connectina.agentstation.api.PermissionIdentity;
import uk.co.connectina.agentstation.api.Registry;
import uk.co.connectina.agentstation.api.client.OperationException;
import static uk.co.connectina.agentstation.desktop.AddAgentDialog.ERROR_TITLE;
import uk.co.connectina.agentstation.local.IOAccess;

/**
 * Handles the required actions for exporting the details of an agent into a properties file.
 *
 * @author Dr Christos Bohoris
 */
public class ExportAgentListener implements ActionListener {

    private final JTable agentTable;
    private final AgentTableModel agentTableModel;
    private final Registry agentRegistry;
    private final AgentStationFrame frame;
    private static final Logger LOGGER = LogManager.getLogger(ExportAgentListener.class.toString());

    ExportAgentListener(AgentStationFrame frame, JTable agentTable, AgentTableModel agentTableModel, Registry agentRegistry) {
        this.frame = frame;
        this.agentTable = agentTable;
        this.agentTableModel = agentTableModel;
        this.agentRegistry = agentRegistry;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        int index = agentTable.getSelectedRow();
        Instance instance = agentTableModel.get(index);
        Identity id = instance.getIdentity();

        String org = id.getOrganisation();
        String major = String.valueOf(id.getMajorVersion());
        String minor = String.valueOf(id.getMinorVersion());
        String hash = id.getHashCode();
        String className = id.getClassName();
        String descr = id.getDescription();
        String loc = "";
        try {
            loc = new URL(id.getPackageFile()).getFile();
        } catch (MalformedURLException e) {
            LOGGER.error("{}: {}", IOAccess.getAboutAgent(instance), e);
        }
        String commaParams = instance.getCommaSeparatedParameters();
        String param = commaParams != null ? commaParams : "";
        String placeName = instance.getPlaceName();
        String allowed = "";
        String autoStart = "";

        String builder = new StringBuilder().append("className={0}\n").append("organisation={1}\n").append("hashCode={2}\n").append("majorVersion={3}\n").append("minorVersion={4}\n").append("packageLocation={5}\n").append("Description={6}\n").append("parameters={7}\n").append("placeName={8}\n").append("allowed={9}\n").append("autoStart={10}\n").toString();

        Permission permission = null;
        try {
            permission = agentRegistry.lookupPermission(new PermissionIdentity(id.getName(), instance.getShortId(), placeName));
        } catch (OperationException e) {
            LOGGER.error("{}: {}", IOAccess.getAboutAgent(instance), e);
        }

        if (permission != null) {
            allowed = Boolean.toString(permission.isAllowed());
            autoStart = Boolean.toString(permission.isAutoStart());
        }

        String fileContents = MessageFormat.format(builder, className, org, hash, major, minor, loc, descr, param, placeName, allowed, autoStart);

        final JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(IOAccess.APP_PROPERTY_FOLDER);
        chooser.setSelectedFile(new File(id.getName() + ".properties"));

        FileNameExtensionFilter filter = new FileNameExtensionFilter("Property files", "properties");
        chooser.addChoosableFileFilter(filter);
        chooser.setFileFilter(filter);
        int returnVal = chooser.showSaveDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try ( FileWriter fw = new FileWriter(chooser.getSelectedFile())) {
                fw.write(fileContents);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Failed to write property file.", ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
