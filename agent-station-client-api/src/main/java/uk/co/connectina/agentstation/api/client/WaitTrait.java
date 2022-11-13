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

import java.io.Serializable;

/**
 * Allows an agent to stay active and waiting for collaboration requests.
 * 
 * @author Dr Christos Bohoris
 */
class WaitTrait implements Serializable {
    
    private final transient Object lock = new Object();
    
    WaitTrait() {
        
    }
    
    /**
     * Makes the agent stay active and waiting for collaboration requests.
     */
    public final void waiting() {
        synchronized (lock) {
            boolean waiting = true;
            
            while (waiting) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    waiting = false;
                }
            }
        }
    }
    
    /**
     * Stops the agent from waiting.
     */
    public void stopWaiting() {
        // Stop waiting
        synchronized (lock) {
            lock.notifyAll();
        }
    }
    
}
