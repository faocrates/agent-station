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
package uk.co.connectina.agentstation.exampleagents.diskinfo;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import uk.co.connectina.agentstation.api.client.OperationException;
import uk.co.connectina.agentstation.api.client.Assistant;
import uk.co.connectina.agentstation.api.client.LogType;
import uk.co.connectina.agentstation.api.client.MobileAgent;

/**
 * A mobile agent that migrates to a remote Agent Station, gets disk space-related information from that node and finally returns
 * to the origin Agent Station to report the information gathered. This example demonstrates how you can implement a mobile agent
 * that migrates and operates in a remote Agent Station.
 *
 * @author Dr Christos Bohoris
 */
public class DiskInfoAgent extends MobileAgent {

    private List<DiskInfo> data;

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

        data = new ArrayList<>();
        for (Path root : FileSystems.getDefault().getRootDirectories()) {
            try {
                FileStore store = Files.getFileStore(root);
                DiskInfo info = new DiskInfo(root.toString(), store.getTotalSpace(), store.getUsableSpace());
                data.add(info);
            } catch (IOException e) {
                stationAssistant.log(agentInstance, LogType.ERROR, e.getMessage());
            }
        }
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
        
        stationAssistant.log(agentInstance, LogType.INFO, MessageFormat.format("Successful migration from {0}", agentInstance.getHomeServer()));
        status = Status.AT_HOME;
     
        Object[] content = new Object[(data.size() * 2) + 1];
        int index = 0;
        content[0] = MessageFormat.format("<html><strong>{0}:</strong>\n", agentInstance.getLastRemoteServer());
        for (DiskInfo info : data) {
            
            StringBuilder builder = new StringBuilder();
            builder.append(info);
            builder.append("\n");
            content[++index] = builder.toString();
            
            JProgressBar progressBar = new JProgressBar(0, 100);
            progressBar.setValue((int)info.getUsedPercentage());
            content[++index] = progressBar;
        }
        
        JOptionPane.showMessageDialog(null, content);
    }
    
    private static class DiskInfo implements Serializable {
        
        private String root;
        private long totalSpace;
        private long availableSpace;

        private DiskInfo(String root, long totalSpace, long availableSpace) {
            this.root = root;
            this.totalSpace = totalSpace;
            this.availableSpace = availableSpace;
        }
        
        public double getUsedPercentage() {
            double usedSpaceGB = (totalSpace - availableSpace) / (1024.0 * 1024.0 * 1024.0);
            double totalSpaceGB = totalSpace / (1024.0 * 1024.0 * 1024.0);
            
            return (100.0 / totalSpaceGB) * usedSpaceGB;
        }

        @Override
        public String toString() {
            double usedSpaceGB = (totalSpace - availableSpace) / (1024.0 * 1024.0 * 1024.0);
            double availSpaceGB = availableSpace / (1024.0 * 1024.0 * 1024.0);
            double totalSpaceGB = totalSpace / (1024.0 * 1024.0 * 1024.0);
            
            return "Root=" + root + ", Total Space=" + String.format("%.1f", totalSpaceGB) + "GB, Used Space=" + String.format("%.1f", usedSpaceGB) +  "GB, Available Space=" + String.format("%.1f", availSpaceGB) + "GB, Used %=" + String.format("%.1f", getUsedPercentage());
        }
        
    }

}
