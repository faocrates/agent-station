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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;

/**
 * Tests for the Identity class.
 * 
 * @author Dr Christos Bohoris
 */
class IdentityTest {

    public static final String AGENT_DESCRIPTION = "Other test agent description";
    public static final String PACKAGE_FILE = "other-test-agent.jar";
    public static final String HASH_CODE = "4f64b865ed237ab9905a4ce2ec99146b";
    public static final String TEST_AGENT = "uk.co.connectina.test.TestAgent";
    public static final String ORG = "connectina.co.uk";
    public static final String HASH_CODE1 = "9f64b865ed237ab9905a4ce2ec99146b";
    private Identity identity;

    @BeforeEach
    void setUp() {
        identity = new Identity.IdentityBuilder(TEST_AGENT, ORG).description("Test agent description").hashCode(HASH_CODE1).packageFile("file:/home/christos/test-agent.jar").version(1, 3).build();
    }

    @Test
    void getName() {
        Assertions.assertEquals("TestAgent", identity.getName());
    }

    @Test
    void getOrganisation() {
        Assertions.assertEquals(ORG, identity.getOrganisation());
    }

    @Test
    void getClassName() {
        Assertions.assertEquals(TEST_AGENT, identity.getClassName());
    }

    @Test
    void getHashCode() {
        Assertions.assertEquals(HASH_CODE1, identity.getHashCode());
    }

    @Test
    void getPackageFile() {
        Assertions.assertEquals("file:/home/christos/test-agent.jar", identity.getPackageFile());
    }

    @Test
    void setPackageFile() {
        identity.setPackageFile("file:/home/christos/test-agent-1.3.jar");
        Assertions.assertEquals("file:/home/christos/test-agent-1.3.jar", identity.getPackageFile());
    }

    @Test
    void getMajorVersion() {
        Assertions.assertEquals(1, identity.getMajorVersion());
    }

    @Test
    void getMinorVersion() {
        Assertions.assertEquals(3, identity.getMinorVersion());
    }

    @Test
    void getVersion() {
        Assertions.assertEquals(1.3d, identity.getVersion());
    }

    @Test
    void getDescription() {
        Assertions.assertEquals("Test agent description", identity.getDescription());
    }

    @Test
    void testHashCode() {
        Assertions.assertEquals(951900977, identity.hashCode());
    }

    @Test
    void testEquals() {
        Identity otherIdentity = new Identity.IdentityBuilder(TEST_AGENT, ORG).description(AGENT_DESCRIPTION).hashCode(HASH_CODE1).packageFile(PACKAGE_FILE).version(1, 3).build();
        Assertions.assertEquals(otherIdentity, identity);
    }
    
    @Test
    void testNotEqualsVersion() {
        Identity otherIdentity = new Identity.IdentityBuilder(TEST_AGENT, ORG).description(AGENT_DESCRIPTION).hashCode(HASH_CODE1).packageFile(PACKAGE_FILE).version(2, 2).build();
        Assertions.assertNotEquals(otherIdentity,identity);
    }
    
    @Test
    void testNotEqualsHashCode() {
        Identity otherIdentity = new Identity.IdentityBuilder(TEST_AGENT, ORG).description(AGENT_DESCRIPTION).hashCode(HASH_CODE).packageFile(PACKAGE_FILE).version(1, 3).build();
        Assertions.assertNotEquals(otherIdentity,identity);
    }
    
    @Test
    void testNotEqualsClassName() {
        Identity otherIdentity = new Identity.IdentityBuilder("uk.co.connectina.test.OtherTestAgent", ORG).description(AGENT_DESCRIPTION).hashCode(HASH_CODE1).packageFile(PACKAGE_FILE).version(1, 3).build();
        Assertions.assertNotEquals(otherIdentity, identity);
    }
    
    @Test
    void testNotEqualsOrganisation() {
        Identity otherIdentity = new Identity.IdentityBuilder(TEST_AGENT, "connectina.com").description(AGENT_DESCRIPTION).hashCode(HASH_CODE1).packageFile(PACKAGE_FILE).version(1, 3).build();
        Assertions.assertNotEquals(otherIdentity,identity);
    }

    @Test
    void testToString() {
        Assertions.assertEquals("Identity{name=TestAgent, organisation=connectina.co.uk, className=uk.co.connectina.test.TestAgent, hashCode=9f64b865ed237ab9905a4ce2ec99146b, packageFile=file:/home/christos/test-agent.jar, majorVersion=1, minorVersion=3, description=Test agent description}", identity.toString());
    }

}
