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

/**
 * A permission indicates whether an Agent is allowed (and how) within a Place.
 * A permission is unique by agent name and hash code within the Place.
 *
 * @author Dr Christos Bohoris
 */
public final class Permission extends PermissionIdentity implements Serializable {

    private boolean allowed;
    private boolean autoStart;

    /**
     * Initiates a new object instance.
     *
     * @param agentName the agent name
     * @param agentShortId the agent short id
     * @param placeName the place name
     * @param allowed whether the agent is allowed in this place
     * @param autoStart whether the agent should be automatically started in
     * this place
     */
    public Permission(String agentName, String agentShortId, String placeName, boolean allowed, boolean autoStart) {
        this.agentName = agentName;
        this.agentShortId = agentShortId;
        this.placeName = placeName;
        this.allowed = allowed;
        this.autoStart = autoStart;
    }


    /**
     * Provides whether the agent is allowed.
     *
     * @return allowed or not
     */
    public boolean isAllowed() {
        return allowed;
    }

    /**
     * Sets whether the agent is allowed.
     *
     * @param allowed allowed or not
     */
    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }

    /**
     * Provides whether the agent is automatically started.
     *
     * @return automatically started or not
     */
    public boolean isAutoStart() {
        return autoStart;
    }

    /**
     * Sets whether the agent is automatically started.
     *
     * @param autoStart automatically started or not
     */
    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    /**
     * Provides a hash code value for the object.
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare
     * @return true if this object is the same as the obj argument; false
     * otherwise
     */
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    /**
     * Provides a string representation of the object.
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return "Permission{" + "agentName=" + agentName + ", agentTraceId=" + agentShortId + ", placeName=" + placeName + ", allowed=" + allowed + ", autoStart=" + autoStart + '}';
    }

}
