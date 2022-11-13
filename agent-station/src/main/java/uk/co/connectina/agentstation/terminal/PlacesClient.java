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
package uk.co.connectina.agentstation.terminal;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.connectina.agentstation.api.Registry;
import uk.co.connectina.agentstation.api.Station;
import uk.co.connectina.agentstation.api.client.OperationException;

/**
 * An interactive commands client for places management.
 * 
 * @author Dr Christos Bohoris
 */
final class PlacesClient {
    
    private final Registry registry;
    private final Station station;
    private static final Logger LOGGER = LogManager.getLogger(PlacesClient.class.toString());
    
    PlacesClient(Station station, Registry registry) {
        this.station = station;
        this.registry = registry;
    }
    
    void listPlaces() {
        try {
            List<String> names = registry.lookupPlaces();
            System.out.println("Places:");
            for (String n : names) {
                System.out.println(" -- " + n);
            }
        } catch (OperationException e) {
            LOGGER.error(e);
        }
    }
    
    void createPlace(String[] cmd) {
        if (cmd.length != 3) {
            return;
        }
        
        try {
             station.createPlace(cmd[2]);           
        } catch (OperationException e) {
            LOGGER.error(e);
        }
    }
    
    void removePlace(String[] cmd) {
        if (cmd.length != 3) {
            return;
        }
        
        try {
             station.removePlace(cmd[2]);           
        } catch (OperationException e) {
            LOGGER.error(e);
        }
    }
    
}
