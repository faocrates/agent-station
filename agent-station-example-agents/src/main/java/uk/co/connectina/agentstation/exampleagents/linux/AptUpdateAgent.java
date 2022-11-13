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
package uk.co.connectina.agentstation.exampleagents.linux;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import uk.co.connectina.agentstation.api.client.Assistant;
import uk.co.connectina.agentstation.api.client.LogType;
import uk.co.connectina.agentstation.api.client.MobileAgent;
import uk.co.connectina.agentstation.api.client.OperationException;

/**
 * A mobile agent that migrates to a remote Agent Station running on a Linux system managed by apt. The agent uses apt to update the
 * remote OS and returns to the origin Agent Station to report on the update performed. This example demonstrates a mobile agent performing
 * a routine task on a remote node on behalf of a user. The agent can also be scheduled to carry out the update on a repeated, set day 
 * interval to regularly update in an automated manner.
 *
 * @author Dr Christos Bohoris
 */
public class AptUpdateAgent extends MobileAgent {

    private String password;
    private int exitCode = -1;
    private String info;
    private String error;

    @Override
    public void atHomeStation() {
        // Extend atHomeStation behaviour of MobileAgent class
        super.atHomeStation();
        
        if (agentInstance.getParameters().length != 2) {
            stationAssistant.log(agentInstance, LogType.ERROR, "Two parameters are expected. Remote Server,Remote Port");

            return;
        }
        String remoteServer = agentInstance.getParameters()[0];
        int remotePort = Integer.parseInt(agentInstance.getParameters()[1]);

        password = getPasswordInput();
        if (password == null || password.isBlank()) {

            return;
        }

        try {
            stationAssistant.log(agentInstance, LogType.INFO, MessageFormat.format("Migrating to {0}...", remoteServer));
            status = Status.AT_REMOTE;
            stationAssistant.migrate(agentInstance, remoteServer, remotePort, Assistant.DEFAULT_PLACE);
        } catch (OperationException e) {
            stationAssistant.log(agentInstance, LogType.ERROR, e.getMessage());
            status = Status.AT_HOME;
        }
    }

    @Override
    public void atRemoteStation() {
        // Extend atRemoteStation behaviour of MobileAgent class
        super.atRemoteStation();
        
        stationAssistant.log(agentInstance, LogType.INFO, MessageFormat.format("Successful migration from {0}", agentInstance.getHomeServer()));
        executeUpdate(password);

        try {
            stationAssistant.log(agentInstance, LogType.INFO, MessageFormat.format("Migrating to {0}...", agentInstance.getHomeServer()));
            status = Status.BACK_AT_HOME;
            stationAssistant.migrate(agentInstance, agentInstance.getHomeServer(), agentInstance.getHomePort(), agentInstance.getHomePlace());
        } catch (OperationException e) {
            stationAssistant.log(agentInstance, LogType.ERROR, e.getMessage());
            status = Status.AT_REMOTE;
        }
    }

    @Override
    public void backAtHomeStation() {
        // Extend backAtHomeStation behaviour of MobileAgent class
        super.backAtHomeStation();
        
        stationAssistant.log(agentInstance, LogType.INFO, MessageFormat.format("Successful migration from {0}", agentInstance.getLastRemoteServer()));
        status = Status.AT_HOME;
        String outcome = exitCode != 0 ? "Completed wih error" : "Completed successfully";
        stationAssistant.log(agentInstance, exitCode != 0 ? LogType.ERROR : LogType.INFO, outcome);
        new AptUpdateReportDialog(MessageFormat.format("<html><strong>Update of {0}: {1}.", agentInstance.getLastRemoteServer(), outcome), info, error).display();

        stationAssistant.remove(agentInstance);
    }

    private String getPasswordInput() {
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Remote sudo password:");
        JPasswordField passField = new JPasswordField();
        int height = passField.getPreferredSize().height;
        passField.setMinimumSize(new Dimension(200, height));
        passField.setPreferredSize(new Dimension(200, height));

        panel.add(label);
        panel.add(passField);
    
        String[] options = new String[]{"OK", "Cancel"};
        int option = JOptionPane.showOptionDialog(null, panel, "Input",
                JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, options[0]);
        
        if (option == JOptionPane.YES_OPTION) {
            char[] passChars = passField.getPassword();

            return new String(passChars);
        }

        return null;
    }

    private void executeUpdate(String password) {
        String[] command = {"/bin/bash", "-c", MessageFormat.format("echo {0} | sudo -S apt update && sudo -S apt upgrade -y", password)};

        stationAssistant.log(agentInstance, LogType.INFO, "Running: apt update && apt upgrade");
        try {
            Process process = Runtime.getRuntime().exec(command);

            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }
            info = builder.toString();

            builder = new StringBuilder();
            BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }
            error = builder.toString();

            exitCode = process.waitFor();

            process.destroy();
        } catch (IOException | InterruptedException e) {
            stationAssistant.log(agentInstance, LogType.ERROR, e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

}
