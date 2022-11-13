package uk.co.connectina.agentstation.api;

import java.util.Objects;

/**
 * Information that identifies a permission.
 * 
 * @author Dr Christos Bohoris
 */
public class PermissionIdentity {

    protected String agentName;
    protected String agentShortId;
    protected String placeName;
    private long sid;

    public PermissionIdentity() {

    }

    public PermissionIdentity(String agentName, String agentTraceId, String placeName) {
        this.agentName = agentName;
        this.agentShortId = agentTraceId;
        this.placeName = placeName;
    }

    /**
     * Provides the agent name.
     *
     * @return the agent name
     */
    public String getAgentName() {
        return agentName;
    }

    /**
     * Sets the agent name.
     *
     * @param agentName the agent name
     */
    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    /**
     * Provides the agent short id.
     *
     * @return the agent short id
     */
    public String getAgentShortId() {
        return agentShortId;
    }

    /**
     * Sets agent short id.
     *
     * @param agentShortId the agent short id
     */
    public void setAgentShortId(String agentShortId) {
        this.agentShortId = agentShortId;
    }

    /**
     * Provides the place name.
     *
     * @return the place name
     */
    public String getPlaceName() {
        return placeName;
    }

    /**
     * Sets the place name.
     *
     * @param placeName the place name
     */
    public void setPlaceName(String placeName) {
        this.placeName = placeName;
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
     * Provides a hash code value for the object.
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.agentName);
        hash = 59 * hash + Objects.hashCode(this.agentShortId);
        hash = 59 * hash + Objects.hashCode(this.placeName);
        return hash;
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
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PermissionIdentity other = (PermissionIdentity) obj;
        if (!Objects.equals(this.agentName, other.agentName)) {
            return false;
        }
        if (!Objects.equals(this.agentShortId, other.agentShortId)) {
            return false;
        }
        return Objects.equals(this.placeName, other.placeName);
    }

    /**
     * Provides a string representation of the object.
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return "PermissionIdentity{" + "agentName=" + agentName + ", agentTraceId=" + agentShortId + ", placeName=" + placeName + '}';
    }

}
