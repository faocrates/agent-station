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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the Permission class.
 *
 * @author Dr Christos Bohoris
 */
class PermissionTest {

    private Permission permission;
    private static final String OTHER_AGENT_NAME = "OtherTestAgent";
    private static final String AGENT_NAME = "TestAgent";
    private static final String TRACE_ID = "99146b";
    private static final String INCOMING = "Incoming";

    @BeforeEach
    void setUp() {
        permission = new Permission(AGENT_NAME, TRACE_ID, "Main", true, true);
    }

    @Test
    void getAgentName() {
        Assertions.assertEquals(AGENT_NAME, permission.getAgentName());
    }

    @Test
    void setAgentName() {
        permission.setAgentName(OTHER_AGENT_NAME);
        Assertions.assertEquals(OTHER_AGENT_NAME, permission.getAgentName());
    }

    @Test
    void getAgentShortId() {
        Assertions.assertEquals(TRACE_ID, permission.getAgentShortId());
    }

    @Test
    void setHashCode() {
        permission.setAgentShortId(TRACE_ID);
        Assertions.assertEquals(TRACE_ID, permission.getAgentShortId());
    }

    @Test
    void getPlaceName() {
        Assertions.assertEquals("Main", permission.getPlaceName());
    }

    @Test
    void setPlaceName() {
        permission.setPlaceName(INCOMING);
        Assertions.assertEquals(INCOMING, permission.getPlaceName());
    }

    @Test
    void isAllowed() {
        Assertions.assertEquals(true, permission.isAllowed());
    }

    @Test
    void setAllowed() {
        permission.setAllowed(false);
        Assertions.assertEquals(false, permission.isAllowed());
    }

    @Test
    void isAutoStart() {
        Assertions.assertEquals(true, permission.isAutoStart());
    }

    @Test
    void setAutoStart() {
        permission.setAutoStart(false);
        Assertions.assertEquals(false, permission.isAutoStart());
    }

    @Test
    void testHashCode() {
        Assertions.assertEquals(455641208, permission.hashCode());
    }

    @Test
    void testEquals() {
        Permission otherPermission = new Permission(AGENT_NAME, TRACE_ID, "Main", false, false);
        Assertions.assertEquals(otherPermission, permission);
    }

    @Test
    void testNotEqualsName() {
        Permission otherPermission = new Permission(OTHER_AGENT_NAME, "9f64b865ed237ab9905a4ce2ec99146b", "Main", false, false);
        Assertions.assertNotEquals(otherPermission, permission);
    }

    @Test
    void testNotEqualsHashCode() {
        Permission otherPermission = new Permission(AGENT_NAME, "4f64b865ed237ab9905a4ce2ec99146b", "Main", false, false);
        Assertions.assertNotEquals(otherPermission, permission);
    }

    @Test
    void testNotEqualsPlaceName() {
        Permission otherPermission = new Permission(AGENT_NAME, "9f64b865ed237ab9905a4ce2ec99146b", INCOMING, false, false);
        Assertions.assertNotEquals(otherPermission, permission);
    }

    @Test
    void testToString() {
        Assertions.assertEquals("Permission{agentName=TestAgent, agentTraceId=99146b, placeName=Main, allowed=true, autoStart=true}", permission.toString());
    }

}
