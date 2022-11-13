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
package uk.co.connectina.agentstation.api;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;
import uk.co.connectina.agentstation.api.client.Assistant;
import uk.co.connectina.agentstation.api.client.Agent;

/**
 * A runnable for an agent.
 *
 * @author Dr Christos Bohoris
 */
public class AgentRunnable implements Runnable, Serializable {
    
    private final Agent agent;
    private transient AgentListener agentListener;
    private final Instance instance;
    private final AtomicBoolean active = new AtomicBoolean(false);
    private transient Assistant assistant;

    /**
     * Initiates a new object instance.
     *
     * @param assistant a station assistant that this agent can use
     * @param instance the agent instance details
     * @param agent the agent provided by the user
     * @param agentListener a listener for events from this agent
     */
    public AgentRunnable(Assistant assistant, Instance instance, Agent agent, AgentListener agentListener) {
        this.assistant = assistant;
        this.instance = instance;
        this.agent = agent;
        this.agentListener = agentListener;
    }

    /**
     * Makes this agent active.
     */
    public void start() {
        if (!isActive()) {
            Thread thread = new Thread(this, instance.getIdentity().getName() + "." + instance.getShortId());
            thread.start();
            active.set(true);
            instance.setState(Instance.State.ACTIVE);
        }
    }

    /**
     * Makes this agent inactive.
     */
    public void stop() {
        if (isActive()) {
            active.set(false);
            instance.setState(Instance.State.INACTIVE);
            if (agentListener != null) {
                agentListener.notify(instance, "stop");
            }
            agent.stop();
        }
    }

    /**
     * Makes this agent active.
     *
     * @return whether agent is active
     */
    public boolean isActive() {
        return active.get();
    }

    /**
     * Provides the agent provided by a user.
     *
     * @return the agent provided by a user
     */
    public Agent getAgent() {
        return agent;
    }

    /**
     * When an object implementing interface {@code Runnable} is used to create
     * a thread, starting the thread causes the object's {@code run} method to
     * be called in that separately executing thread.
     * <p>
     * The general contract of the method {@code run} is that it may take any
     * action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        if (agentListener != null) {
            agentListener.notify(instance, "start");
        }

        agent.start(assistant, instance);
        if (isActive()) {
            instance.setState(Instance.State.INACTIVE);
            if (agentListener != null) {
                agentListener.notify(instance, "stop");
            }
        }
        active.set(false);
    }

    /**
     * Provides the agent instance details.
     *
     * @return the agent instance details
     */
    public Instance getInstance() {
        return instance;
    }

    /**
     * Sets the station listening to this agent.
     *
     * @param agentListener the agent listener
     */
    public void setAgentListener(AgentListener agentListener) {
        this.agentListener = agentListener;
    }

    /**
     * Sets the station assistant that this agent can use.
     *
     * @param assistant the station assistant
     */
    public void setAssistant(Assistant assistant) {
        this.assistant = assistant;
    }

    /**
     * Provides a string representation of the object.
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return "Agent{" + "instance=" + instance + ", active=" + active + '}';
    }

}
