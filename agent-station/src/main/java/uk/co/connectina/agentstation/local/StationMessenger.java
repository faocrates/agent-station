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
import java.util.ArrayList;
import java.util.List;
import uk.co.connectina.agentstation.api.StationListener;

/**
 * Allows agents to message their Agent Station.
 * 
 * @author Dr Christos Bohoris
 */
class StationMessenger implements Serializable {

    private List<StationListener> listeners;

    StationMessenger() {
        listeners = new ArrayList<>();
    }

    void addListener(StationListener listener) {
        listeners.add(listener);
    }

    void notifyStationListeners(String operationName, Object... info) {
        for (StationListener listener : listeners) {
            listener.notify(operationName, info);
        }
    }

}
