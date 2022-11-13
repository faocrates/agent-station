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
 * Abstract class to be extended by all StaticAgents.
 * 
 * @author Dr Christos Bohoris
 */
public abstract class StaticAgent implements Agent, Collaboration {

    /**
     * The station assistant helping the agent.
     */
    protected Assistant stationAssistant;
    
    /**
     * The instance information of this agent.
     */
    protected AgentInstance agentInstance;
    /**
     * The wait trait.
     */
    private WaitTrait waitTrait;
    /**
     * Whether the agent is waiting.
     */
    private boolean waiting;

    @Override
    public final void start(Assistant stationAssistant, AgentInstance agentInstance) {
        this.stationAssistant = stationAssistant;
        this.agentInstance = agentInstance;
        waitTrait = new WaitTrait();
        startTasks();
    }
    
    void startTasks() {
        atHomeStation();
    }

    /**
     * Implement the tasks carried out by the agent at the home station.
     */
    public abstract void atHomeStation();

    @Override
    public void stop() {
        stopWaiting();
    }

    @Override
    public Message communicate(Message message) {
        return message;
    }
    
    
    /**
     * The agent starts waiting.
     */
    public final void startWaiting() {
        if (!waiting) {
            waiting = true;
            waitTrait.waiting();
        }
    }
    
    /**
     * The agent stops waiting.
     */
    public final void stopWaiting() {
        if (isWaiting()) {
            waitTrait.stopWaiting();
        }
    }
    
    /**
     * Whether agent is waiting.
     * 
     * @return true if waiting
     */
    public final boolean isWaiting() {
        
        return waiting;
    }
    
    /**
     * Makes the agent sleep for the given duration.
     * 
     * @param millis the sleep duration in milliseconds
     * @throws OperationException an error occurred
     */
    public final void sleep(long millis) throws OperationException {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OperationException(e);
        }
    }
    
}
