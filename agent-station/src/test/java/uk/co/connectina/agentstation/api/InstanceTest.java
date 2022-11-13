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

import java.time.LocalDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.co.connectina.agentstation.App;
import uk.co.connectina.agentstation.api.client.AgentIdentity;

/**
 * Tests for the Instance class.
 * 
 * @author Dr Christos Bohoris
 */
class InstanceTest {

    public static final String INCOMING = "Incoming";
    public static final String T_17_25_31 = "2022-05-21T17:25:31";
    public static final String TEST_AGENT_DESCRIPTION = "Test agent description";
    public static final String HASH_CODE = "9f64b865ed237ab9905a4ce2ec99146b";
    public static final String TEST_AGENT = "uk.co.connectina.test.TestAgent";
    public static final String ORG = "connectina.co.uk";
    public static final String PACKAGE_FILE = "file:/home/christos/test-agent.jar";
    private Instance instance;
    private LocalDateTime dateTime;
    
    @BeforeEach
    void setUp() {
        Identity identity = new Identity.IdentityBuilder(TEST_AGENT, ORG).description(TEST_AGENT_DESCRIPTION).hashCode(HASH_CODE).packageFile(PACKAGE_FILE).version(1, 3).build();
        dateTime = LocalDateTime.parse(T_17_25_31, App.DATETIME_FORMATTER);
        instance = new Instance(identity, dateTime, "Test", "Param1", "Param2", "Param3");
    }

    @Test
    void getAgentIdentity() {
        AgentIdentity ai = instance.getAgentIdentity();
        Assertions.assertEquals(TEST_AGENT, ai.getClassName());
        Assertions.assertEquals(ORG, ai.getOrganisation());
        Assertions.assertEquals(TEST_AGENT_DESCRIPTION, ai.getDescription());
        Assertions.assertEquals(HASH_CODE, ai.getHashCode());
        Assertions.assertEquals(1.3d, ai.getVersion());
    }

    @Test
    void getCreationDateTime() {
        Assertions.assertEquals(dateTime, instance.getCreationDateTime());
    }

    @Test
    void getPlaceName() {
        Assertions.assertEquals("Test", instance.getPlaceName());
    }

    @Test
    void setPlaceName() {
        instance.setPlaceName(INCOMING);
        Assertions.assertEquals(INCOMING, instance.getPlaceName());
    }

    @Test
    void getParameters() {
        Assertions.assertEquals(3, instance.getParameters().length);
        Assertions.assertEquals("Param1", instance.getParameters()[0]);
        Assertions.assertEquals("Param2", instance.getParameters()[1]);
        Assertions.assertEquals("Param3", instance.getParameters()[2]);
    }

    @Test
    void getIdentity() {
        Identity i = instance.getIdentity();
        Assertions.assertEquals(TEST_AGENT, i.getClassName());
        Assertions.assertEquals(ORG, i.getOrganisation());
        Assertions.assertEquals(TEST_AGENT_DESCRIPTION, i.getDescription());
        Assertions.assertEquals(HASH_CODE, i.getHashCode());
        Assertions.assertEquals(1.3d, i.getVersion());
    }

    @Test
    void getCreation() {
        Assertions.assertEquals(T_17_25_31, instance.getCreation());
    }

    @Test
    void getState() {
        Assertions.assertEquals(Instance.State.INACTIVE, instance.getState());
    }

    @Test
    void setState() {
        instance.setState(Instance.State.ACTIVE);
        Assertions.assertEquals(Instance.State.ACTIVE, instance.getState());
    }

    @Test
    void getCommaSeparatedParameters() {
        Assertions.assertEquals("Param1,Param2,Param3", instance.getCommaSeparatedParameters());
    }

    @Test
    void testHashCode() {
        Assertions.assertEquals(-1493800049, instance.hashCode());
    }

    @Test
    void testEquals() {
        Identity identity = new Identity.IdentityBuilder(TEST_AGENT, ORG).description(TEST_AGENT_DESCRIPTION).hashCode(HASH_CODE).packageFile(PACKAGE_FILE).version(1, 3).build();
        dateTime = LocalDateTime.parse(T_17_25_31, App.DATETIME_FORMATTER);
        Instance otherInstance = new Instance(identity, dateTime, "Test");
        Assertions.assertEquals(otherInstance, instance);
    }
    
    @Test
    void testNotEqualsIdentity() {
        Identity identity = new Identity.IdentityBuilder("uk.co.connectina.test.OtherTestAgent", ORG).description(TEST_AGENT_DESCRIPTION).hashCode(HASH_CODE).packageFile(PACKAGE_FILE).version(1, 3).build();
        dateTime = LocalDateTime.parse(T_17_25_31, App.DATETIME_FORMATTER);
        Instance otherInstance = new Instance(identity, dateTime, INCOMING);
        Assertions.assertNotEquals(otherInstance, instance);
    }

    @Test
    void testToString() {
        Assertions.assertEquals("Instance{identity=Identity{name=TestAgent, organisation=connectina.co.uk, className=uk.co.connectina.test.TestAgent, hashCode=9f64b865ed237ab9905a4ce2ec99146b, packageFile=file:/home/christos/test-agent.jar, majorVersion=1, minorVersion=3, description=Test agent description}, fullId=0575ed518768aeb0e8b341c0233679ed, shortId=3679ed, creation=2022-05-21T17:25:31, placeName=Test, state=INACTIVE}", instance.toString());
    }
    
}
