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
 * An exception thrown on operations within the Agent Station environment.
 *
 * @author Dr Christos Bohoris
 */
public class OperationException extends Exception {

    /**
     * Initiates a new exception with the specified detail message.
     * 
     * @param message the detail message
     */
    public OperationException(String message) {
        super(message);
    }

    /**
     * Initiates a new exception with the specified cause.
     * 
     * @param cause the cause
     */
    public OperationException(Throwable cause) {
        super(cause);
    }

    /**
     * Initiates a new exception with the specified detail message and cause.
     * 
     * @param message the detail message
     * @param cause the cause
     */
    public OperationException(String message, Throwable cause) {
        super(message, cause);
    }

}
