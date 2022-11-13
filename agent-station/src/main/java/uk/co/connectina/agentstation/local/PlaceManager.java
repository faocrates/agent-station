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
package uk.co.connectina.agentstation.local;

import java.io.Serializable;
import java.util.List;
import uk.co.connectina.agentstation.api.Registry;
import uk.co.connectina.agentstation.api.client.OperationException;

/**
 * Handles the required actions that allow place management.
 * 
 * @author Dr Christos Bohoris
 */
class PlaceManager implements Serializable {

    private StationMessenger stationMessenger;
    private Registry registry;

    PlaceManager(Registry registry, StationMessenger stationMessenger) {
        this.registry = registry;
        this.stationMessenger = stationMessenger;
    }

    void createPlace(String name) throws OperationException {
        registry.registerPlace(name);
        stationMessenger.notifyStationListeners("createPlace", name);
    }

    boolean placeExists(String name) throws OperationException {
        List<String> registeredPlaces = registry.lookupPlaces();
        for (String place : registeredPlaces) {
            if (place.equalsIgnoreCase(name)) {

                return true;
            }
        }

        return false;
    }

    void loadPlace(String name) {
        stationMessenger.notifyStationListeners("createPlace", name);
    }

    void removePlace(String name) throws OperationException {
        registry.deregisterPlace(name);
        stationMessenger.notifyStationListeners("removePlace", name);
    }

}
