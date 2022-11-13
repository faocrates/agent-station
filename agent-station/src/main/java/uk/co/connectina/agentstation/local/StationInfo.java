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

/**
 * Provides Agent Station information.
 * 
 * @author Dr Christos Bohoris
 */
public final class StationInfo implements Serializable {
    
    private String server;
    private String ui;
    private RemoteSupport remote;
    private String name;
    private int port;

    public StationInfo(String server, String ui, RemoteSupport remote, String name, int port) {
        this.server = server;
        this.ui = ui;
        this.remote = remote;
        this.name = name;
        this.port = port;
    }
    
    public String getServer() {
        return server;
    }

    public String getUi() {
        return ui;
    }

    public RemoteSupport getRemote() {
        return remote;
    }

    public String getName() {
        return name;
    }

    public int getPort() {
        return port;
    }
    
}
