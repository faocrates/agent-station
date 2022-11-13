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
package uk.co.connectina.agentstation.api.client;

/**
 * Abstract class to be extended by all Mobile Agents.
 *
 * @author Dr Christos Bohoris
 */
public abstract class MobileAgent extends StaticAgent {

    /**
     * An enumeration for the status of an agent.
     */
    protected enum Status {
        /**
         * Agent is at the home station
         */
        AT_HOME,
        /**
         * Agent is at a remote station
         */
        AT_REMOTE,
        /**
         * Agent is back at the home station
         */
        BACK_AT_HOME;
    }

    /**
     * The status of this agent.
     */
    protected Status status = Status.AT_HOME;

    @Override
    void startTasks() {
        if (status == Status.AT_HOME) {
            atHomeStation();
        } else if (status == Status.AT_REMOTE) {
            atRemoteStation();
        } else if (status == Status.BACK_AT_HOME) {
            backAtHomeStation();
        }
    }

    /**
     * Implements the tasks carried out by the agent at the home station.
     */
    public void atHomeStation() {
        status = Status.AT_HOME;
    }

    /**
     * Implements the tasks carried out by the agent after it migrates to the remote station.
     */
    public void atRemoteStation() {
        status = Status.AT_REMOTE;
    }

    /**
     * Implements the tasks carried out by the agent after it returns at the home station.
     */
    public void backAtHomeStation() {
        status = Status.BACK_AT_HOME;
    }

}
