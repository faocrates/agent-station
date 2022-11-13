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

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Objects;

import uk.co.connectina.agentstation.App;
import uk.co.connectina.agentstation.api.client.AgentIdentity;
import uk.co.connectina.agentstation.api.client.AgentInstance;
import uk.co.connectina.agentstation.local.IOAccess;
import uk.co.connectina.agentstation.local.StationInfo;

/**
 * Provides agent instance details.
 *
 * @author Dr Christos Bohoris
 */
public class Instance implements AgentInstance {

    /**
     * The agent states, either active or inactive.
     */
    public enum State {
        ACTIVE, INACTIVE
    }

    private Identity identity;
    private String creation;
    private String placeName;
    private State state;
    private String homeServer;
    private int homePort;
    private String homePlace;
    private String lastRemoteServer;
    private int lastRemotePort;
    private String lastRemotePlace;
    private String[] parameters;
    private long sid;

    /**
     * Initiates a new object instance.
     * 
     * @param identity the agent identity
     * @param creation the creation date time
     * @param placeName the place where the agent resides
     * @param parameters the initial parameters
     */
    public Instance(Identity identity, LocalDateTime creation, String placeName, String... parameters) {
        this.identity = identity;
        this.creation = App.DATETIME_FORMATTER.format(creation);
        this.placeName = placeName;
        state = State.INACTIVE;
        this.parameters = parameters;
    }
    
    /**
     * Sets the home station information.
     * 
     * @param homeStationInfo the home station information
     * @param homePlace the home place name
     */
    public void setHomeStationLocation(StationInfo homeStationInfo, String homePlace) {
        if (homeServer == null) {
            homeServer = homeStationInfo.getServer();
        }
        if (homePort == 0) {
            homePort = homeStationInfo.getPort();
        }
        if (this.homePlace == null) {
            this.homePlace = homePlace;
        }
    }
    
    /**
     * Sets the last remote station information.
     * 
     * @param lastRemoteStationInfo the last remote station information
     * @param lastRemotePlace the last remote place name
     */
    public void setLastRemoteStationLocation(StationInfo lastRemoteStationInfo, String lastRemotePlace) {
        if (lastRemoteServer == null) {
            lastRemoteServer = lastRemoteStationInfo.getServer();
        }
        if (lastRemotePort == 0) {
            lastRemotePort = lastRemoteStationInfo.getPort();
        }
        if (this.lastRemotePlace == null) {
            this.lastRemotePlace = lastRemotePlace;
        }
    }

    /**
     * Provides the agent identity.
     * 
     * @return the agent identity
     */
    @Override
    public AgentIdentity getAgentIdentity() {
        return getIdentity();
    }

    /**
     * The creation date time.
     * 
     * @return the creation date time
     */
    @Override
    public LocalDateTime getCreationDateTime() {
        return LocalDateTime.parse(creation, App.DATETIME_FORMATTER);
    }
        
    /**
     * Provides the place where the agent resides.
     * 
     * @return the place name
     */
    @Override
    public String getPlaceName() {
        return placeName;
    }
    
    /**
     * Sets the place where the agent resides.
     * 
     * @param placeName the place name
     */
    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    /**
     * Provides the initial parameters.
     * 
     * @return the initial parameters
     */
    @Override
    public String[] getParameters() {
        return parameters;
    }

    public void setParameters(String[] parameters) {
        this.parameters = parameters;
    }

    /**
     * Provides the identity.
     * 
     * @return the identity
     */
    public Identity getIdentity() {
        return identity;
    }

    /**
     * Provides the creation date time.
     * 
     * @return the creation date time
     */
    public String getCreation() {
        return creation;
    }

    /**
     * Provides the current agent state.
     * 
     * @return the agent state
     */
    public State getState() {
        return state;
    }

    /**
     * Sets the current agent state.
     * 
     * @param state the agent state
     */
    public void setState(State state) {
        this.state = state;
    }

    /**
     * Provides the database system id.
     * 
     * @return the system id
     */
    public long getSid() {
        return sid;
    }

    /**
     * Sets the database system id. 
     * 
     * @param sid the system id
    */
    public void setSid(long sid) {
        this.sid = sid;
    }

    /**
     * Provides the initial parameters in a comma-separated form.
     * 
     * @return the initial parameters
     */
    public String getCommaSeparatedParameters() {
        if (getParameters() == null || getParameters().length == 0) {
            
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (String p : getParameters()) {
            if (!builder.isEmpty()) {
                builder.append(",");
            }
            builder.append(p);
        }

        return builder.toString();
    }
    
    /**
     * Provides a long unique id for this agent instance.
     * 
     * @return the long instance id
     */
    public String getLongId() {
        String label = MessageFormat.format("{0}:{1}:{2}", getIdentity().getClassName(), getIdentity().getHashCode(), Double.toString(getIdentity().getVersion()));

        return IOAccess.getStringMD5(label);
    }
    
    /**
     * Provides a short unique id for this agent instance.
     * 
     * @return the short instance id
     */
    public String getShortId() {
        String fullId = getLongId();
        
        return fullId.substring(fullId.length() - 6);
    }

    /**
     * Provides a hash code value for the object.
     * 
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + Objects.hashCode(getLongId());
        hash = 11 * hash + Objects.hashCode(getCreationDateTime());
        
        return hash;
    }
    
    /**
     * Provides the home server.
     * 
     * @return the home server
     */
    @Override
    public String getHomeServer() {
        return homeServer;
    }

    /**
     * Provides the home port.
     * 
     * @return the home port
     */
    @Override
    public int getHomePort() {
        return homePort;
    }

    /**
     * Provides the home place.
     * 
     * @return the home place name
     */
    @Override
    public String getHomePlace() {
        return homePlace;
    }

    /**
     * Provides the last remote server.
     * 
     * @return the last remote server
     */
    @Override
    public String getLastRemoteServer() {
        return lastRemoteServer;
    }

    /**
     * Provides the last remote port.
     * 
     * @return the last remote port
     */
    @Override
    public int getLastRemotePort() {
        return lastRemotePort;
    }

    /**
     * Provides the last remote place.
     * 
     * @return the last remote place name
     */
    @Override
    public String getLastRemotePlace() {
        return lastRemotePlace;
    }
    
    /**
     * Indicates whether some other object is "equal to" this one. 
     * 
     * @param obj the reference object with which to compare
     * @return true if this object is the same as the obj argument; false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Instance other = (Instance) obj;
        if (!Objects.equals(this.getCreationDateTime(), other.getCreationDateTime())) {
            return false;
        }
        return Objects.equals(this.getLongId(), other.getLongId());
    }

    /**
     * Provides a string representation of the object.
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return "Instance{" + "identity=" + identity + ", fullId=" + getLongId() +  ", shortId=" + getShortId() + ", creation=" + creation + ", placeName=" + placeName + ", state=" + state + '}';
    }

}
