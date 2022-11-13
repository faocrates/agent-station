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
package uk.co.connectina.agentstation.exampleagents.monitor;

import java.text.MessageFormat;
import uk.co.connectina.agentstation.api.client.JSONConverter;
import uk.co.connectina.agentstation.api.client.LogType;
import uk.co.connectina.agentstation.api.client.Message;
import uk.co.connectina.agentstation.api.client.OperationException;
import uk.co.connectina.agentstation.api.client.StaticAgent;

/**
 * A static agent that enables other agents to get memory information for the
 * running JVM. This example demonstrates an agent that waits for collaboration
 * requests from other agents.
 *
 * @author Dr Christos Bohoris
 */
public class MemoryMonitorAgent extends StaticAgent {

    @Override
    public void atHomeStation() {
        // Implement atHomeStation behaviour of StaticAgent class
        this.stationAssistant.log(this.agentInstance, LogType.INFO, "Waiting for requests ...");
        // Wait for collaboration requests from other agents
        startWaiting();

        this.stationAssistant.log(this.agentInstance, LogType.INFO, "Stopped");
    }

    @Override
    public Message communicate(Message msg) {
        // Override communicate behaviour of StaticAgent class
        Message response = null;
        double value;
        if (msg.getContext().equals("used-memory")) {
            value = getUsedMemory();
            stationAssistant.log(agentInstance, LogType.INFO, MessageFormat.format("Used memory: {0, number, #.#} MB", value));
        } else if (msg.getContext().equals("free-memory")) {
            value = getFreeMemory();
            stationAssistant.log(agentInstance, LogType.INFO, MessageFormat.format("Free memory: {0, number, #.#} MB", value));
        } else {

            return null;
        }

        MonitoredResource monitoredResource = new MonitoredResource(msg.getContext(), "MB", value);
        response = prepareJSONResponse(monitoredResource);

        return response;
    }

    private double getUsedMemory() {

        return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024.0 * 1024.0);
    }

    private double getFreeMemory() {

        return (Runtime.getRuntime().freeMemory()) / (1024.0 * 1024.0);
    }

    private Message prepareJSONResponse(MonitoredResource monitoredResource) {
        JSONConverter<MonitoredResource> converter = new JSONConverter<>(MonitoredResource.class);
        try {
            String data = converter.objectToPrettyFormatJson(monitoredResource);

            return new Message("used-memory", data);
        } catch (OperationException e) {
            stationAssistant.log(agentInstance, LogType.ERROR, e.getMessage());
        }
        
        return null;
    }

}
