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
 * A message communicated between collaborating Agents.
 * 
 * @author Dr Christos Bohoris
 */
public class Message implements Serializable {

    /**
     * The context.
     */
    private String context;
    /**
     * The data.
     */
    private String data;

    /**
     * Initiates a new object instance.
     */
    public Message() {
    }
    
    /**
     * Initiates a new object instance.
     * 
     * @param context the context of this message e.g. status-update, start-request, etc 
     * @param data any text or JSON content that can be processed with {@link uk.co.connectina.agentstation.api.client.JSONConverter}
     */
    public Message(String context, String data) {
        this.context = context;
        this.data = data;
    }

    /**
     * Provides the context of this message.
     * 
     * @return the context of this message e.g. status-update, start-request, etc 
     */
    public String getContext() {
        return context;
    }

    /**
     * Sets the context of this message.
     * 
     * @param context the context of this message e.g. status-update, start-request, etc 
     */
    public void setContext(String context) {
        this.context = context;
    }

    /**
     * Provides the data of this message.
     * 
     * @return any text or JSON content that can be processed with {@link uk.co.connectina.agentstation.api.client.JSONConverter}
     */
    public String getData() {
        return data;
    }

    /**
     * Sets the context of this message.
     * 
     * @param data any text or JSON content that can be processed with {@link uk.co.connectina.agentstation.api.client.JSONConverter}  
     */
    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Message{" + "context=" + context + ", data=" + data + '}';
    }
    
}
