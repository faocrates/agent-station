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
import java.util.Objects;
import uk.co.connectina.agentstation.api.client.AgentIdentity;

/**
 * Information that identifies an agent instance.
 *
 * @author Dr Christos Bohoris
 */
public class Identity implements AgentIdentity {

    private final String organisation;
    private final String className;
    private final String hashCode;
    private String packageFile;
    private final int majorVersion;
    private final int minorVersion;
    private final String description;

    /*
     * Initiates a new object instance.
     */
    private Identity(IdentityBuilder builder) {
        this.organisation = builder.organisation;
        this.className = builder.className;
        this.hashCode = builder.hashCode;
        this.packageFile = builder.packageFile;
        this.majorVersion = builder.majorVersion;
        this.minorVersion = builder.minorVersion;
        this.description = builder.description;
    }

    /**
     * Provides the name.
     * 
     * @return the name
     */
    @Override
    public final String getName() {
        return !className.contains(".") ? className : className.substring(className.lastIndexOf(".") + 1);
    }

    /**
     * Provides the organisation.
     * 
     * @return the organisation
     */
    @Override
    public final String getOrganisation() {
        return organisation;
    }

    /**
     * Provides the class name.
     * 
     * @return the class name
     */
    @Override
    public final String getClassName() {
        return className;
    }

    /**
     * Provides the hash code. This is the MD5 hash of the jar package
     * that contains this agent.
     * 
     * @return the hash code
     */
    @Override
    public final String getHashCode() {
        return hashCode;
    }

    /**
     * Provides the jar package file.
     * 
     * @return the package file
     */
    @Override
    public final String getPackageFile() {
        return packageFile;
    }

    /**
     * Sets the jar package file.
     * 
     * @param packageFile the package file
     */
    public final void setPackageFile(String packageFile) {
        this.packageFile = packageFile;
    }

    /**
     * Provides the agent's major version number.
     * 
     * @return the major version
     */
    public final int getMajorVersion() {
        return majorVersion;
    }

    /**
     * Provides the agent's minor version number.
     * 
     * @return the minor version
     */
    public final int getMinorVersion() {
        return minorVersion;
    }

    /**
     * Provides the agent's version number by combining the major and
     * minor version parts.
     * 
     * @return the agent version
     */
    @Override
    public final double getVersion() {
        return Double.parseDouble(MessageFormat.format("{0}.{1}", majorVersion, minorVersion));
    }

    /**
     * Provides an agent description.
     * 
     * @return the agent description 
     */
    public final String getDescription() {
        return description;
    }

    /**
     * Provides a hash code value for the object.
     * 
     * @return a hash code value for this object
     */
    @Override
    public final int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.organisation);
        hash = 59 * hash + Objects.hashCode(this.className);
        hash = 59 * hash + Objects.hashCode(this.hashCode);
        hash = 59 * hash + this.majorVersion;
        hash = 59 * hash + this.minorVersion;
        return hash;
    }

    /**
     * Indicates whether some other object is "equal to" this one. 
     * 
     * @param obj the reference object with which to compare
     * @return true if this object is the same as the obj argument; false otherwise
     */
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Identity other = (Identity) obj;
        if (this.majorVersion != other.majorVersion) {
            return false;
        }
        if (this.minorVersion != other.minorVersion) {
            return false;
        }
        if (!Objects.equals(this.organisation, other.organisation)) {
            return false;
        }
        if (!Objects.equals(this.className, other.className)) {
            return false;
        }
        
        return Objects.equals(this.hashCode, other.hashCode); 
    }

    /**
     * Provides a string representation of the object.
     *
     * @return a string representation of the object
     */
    @Override
    public final String toString() {
        return "Identity{" + "name=" + getName() + ", organisation=" + organisation + ", className=" + className + ", hashCode=" + hashCode + ", packageFile=" + packageFile + ", majorVersion=" + majorVersion + ", minorVersion=" + minorVersion + ", description=" + description + '}';
    }

    /**
     * Used to implement the builder pattern used to construct an Identity object.
     */
    public static final class IdentityBuilder {

        private final String organisation;
        private final String className;
        private String hashCode;
        private String packageFile;
        private int majorVersion;
        private int minorVersion;
        private String description;

        /**
         * Initiates a new object instance.
         * 
         * @param className the class name
         * @param organisation  the organisation
         */
        public IdentityBuilder(String className, String organisation) {
            this.className = className;
            this.organisation = organisation;
        }

        /**
         * Sets the hash code. This is the MD5 hash of the jar package
         * that contains this agent.
         * 
         * @param hashCode the hash code
         * @return this builder object
         */
        public IdentityBuilder hashCode(String hashCode) {
            this.hashCode = hashCode;

            return this;
        }

        /**
         * Sets the jar package file.
         * 
         * @param packageFile the package file
         * @return this builder object
         */
        public IdentityBuilder packageFile(String packageFile) {
            this.packageFile = packageFile;

            return this;
        }

        /**
         * Sets the agent's major and minor version parts.
         * 
         * @param majorVersion the major version number
         * @param minorVersion the minor version number
         * @return this builder object
         */
        public IdentityBuilder version(int majorVersion, int minorVersion) {
            this.majorVersion = majorVersion;
            this.minorVersion = minorVersion;

            return this;
        }

        /**
         * Sets an agent description.
         * 
         * @param description the description
         * @return this builder object
         */
        public IdentityBuilder description(String description) {
            this.description = description;

            return this;
        }

        /**
         * Builds an Identity object.
         * 
         * @return an Identity object 
         */
        public Identity build() {
            return new Identity(this);
        }

    }

}
