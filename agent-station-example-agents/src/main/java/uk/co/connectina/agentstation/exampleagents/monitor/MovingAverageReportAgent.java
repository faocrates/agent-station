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
import java.util.ArrayList;
import java.util.List;
import uk.co.connectina.agentstation.api.client.Collaboration;
import uk.co.connectina.agentstation.api.client.JSONConverter;
import uk.co.connectina.agentstation.api.client.LogType;
import uk.co.connectina.agentstation.api.client.Message;
import uk.co.connectina.agentstation.api.client.OperationException;
import uk.co.connectina.agentstation.api.client.StaticAgent;

/**
 * A static agent that calculates the moving averages of used memory in the JVM and reports on the values gathered. This example
 * demonstrates how an agent can collaborate with another in order to carry out its task. 
 *
 * @author Dr Christos Bohoris
 */
public class MovingAverageReportAgent extends StaticAgent {

    private boolean stopRequest;
    private static final int SECONDS_DELAY = 10;
    private static final int SLIDING_WINDOW_SIZE = 30;
    private List<Double> slidingWindow = new ArrayList<>(SLIDING_WINDOW_SIZE);
    private List<Double> slidingAverageWindow = new ArrayList<>(SLIDING_WINDOW_SIZE);
    private MemoryGraphPanel memoryGraphPanel;

    @Override
    public void atHomeStation() {
        // Implement atHomeStation behaviour of StaticAgent class
        stopRequest = false;
        memoryGraphPanel = new MemoryGraphPanel();
        memoryGraphPanel.displayInDialog("Used Memory Moving Average in JVM");

        try {
            // Collect measurements until stopped
            do {
                Collaboration memoryUsageAgent = stationAssistant.collaborate(agentInstance, "MemoryMonitorAgent", "connectina.co.uk", 1, 0);
                if (memoryUsageAgent == null) {
                    stationAssistant.log(agentInstance, LogType.INFO, "MemoryMonitor agent not available");
                    break;
                }
                
                MonitoredResource usedMemoryResource = getUsedMemory(memoryUsageAgent);
                double result = addMeasurement(usedMemoryResource.getValue());
                stationAssistant.log(agentInstance, LogType.INFO, MessageFormat.format("Moving average: {0, number, #.#} MB", result));
               
                // Collect data every SECONDS_DELAY
                sleep(SECONDS_DELAY * 1000L);
            } while (!stopRequest);
        } catch (OperationException e) {
            stationAssistant.log(agentInstance, LogType.ERROR, e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void stop() {
        // Extend stop behaviour of StaticAgent class
        super.stop();
        
        // Stop collecting measurements
        stopRequest = true;
        memoryGraphPanel.disposeDialog();
    }

    private MonitoredResource getUsedMemory(Collaboration memoryUsageAgent) throws OperationException {
        Message response = memoryUsageAgent.communicate(new Message("used-memory", null));
        JSONConverter<MonitoredResource> converter = new JSONConverter<>(MonitoredResource.class);

        return converter.jsonToObject(response.getData());
    }

    private double addMeasurement(double value) {
        // Add the latest value to the sliding window
        slidingWindow.add(value);

        // Remove the oldest value if we have just crossed the size limit
        if (slidingWindow.size() > SLIDING_WINDOW_SIZE) {
            slidingWindow.remove(0);
        }
        
        // Additions and division to calculate moving average
        double result = 0;
        for (double measurement : slidingWindow) {
            result += measurement; 
        }
        result /= slidingWindow.size();
        
        slidingAverageWindow.add(result);
        
        // Remove the oldest value if we have just crossed the size limit
        if (slidingAverageWindow.size() > SLIDING_WINDOW_SIZE) {
            slidingAverageWindow.remove(0);
        }
        
        memoryGraphPanel.updateData(slidingAverageWindow.stream().mapToDouble(Double::doubleValue).toArray());
        
        return result;
    }

}

