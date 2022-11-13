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
package uk.co.connectina.agentstation.local.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.co.connectina.agentstation.api.Permission;
import uk.co.connectina.agentstation.api.client.OperationException;

/**
 * Tests for the PermissionAccess class.
 *
 * @author Dr Christos Bohoris
 */
class PermissionAccessTest {

    private static final Logger LOGGER = LogManager.getLogger(PermissionAccessTest.class.toString());
    public static final String AGENT_SHORT_ID = "123456";
    private PermissionAccess pemissionAccess = new PermissionAccess("Test", "Test");

    PermissionAccessTest() {
        try {
            pemissionAccess.createSchema();
        } catch (OperationException e) {
            LOGGER.error(e);
        }
    }

    private Permission getPermission() {

        return new Permission("TestAgent", AGENT_SHORT_ID, "Default", true, true);
    }

    @BeforeEach
    void setUp() throws OperationException {
        List<Permission> permissions = pemissionAccess.readList();
        for (Permission permission : permissions) {
            pemissionAccess.delete(permission);
        }
    }

    @Test
    void testCreate() {
        Permission permission = getPermission();
        try {
            pemissionAccess.create(permission);
        } catch (OperationException e) {
            Assertions.fail();
        }
    }

    @Test
    void testCreateSchema() {
        try {
            pemissionAccess.createSchema();
        } catch (OperationException e) {
            Assertions.fail();
        }
    }

    @Test
    void testDelete() {
        Permission permission = getPermission();

        try {
            pemissionAccess.delete(permission);
        } catch (OperationException e) {
            Assertions.fail();
        }
    }

    @Test
    void testExistsOrNot() {
        Permission permission = getPermission();
        try {
            boolean exists = pemissionAccess.exists(permission);
            Assertions.assertFalse(exists);
        } catch (OperationException e) {
            Assertions.fail();
        }

        permission = createPermission();

        try {
            boolean exists = pemissionAccess.exists(permission);
            Assertions.assertTrue(exists);
        } catch (OperationException e) {
            Assertions.fail();
        }
    }

    private Permission createPermission() {
        Permission permission = getPermission();

        try {
            pemissionAccess.create(permission);
        } catch (OperationException e) {
            LOGGER.error(e);
        }

        return permission;
    }

    @Test
    void readByPlaceName() {
        Permission permission = createPermission();

        try {
            List<Permission> storedPermission = pemissionAccess.readByPlaceName(permission.getPlaceName());
            Assertions.assertNotNull(storedPermission);
            Assertions.assertTrue(!storedPermission.isEmpty());
            Assertions.assertEquals(AGENT_SHORT_ID, storedPermission.get(0).getAgentShortId());
        } catch (OperationException e) {
            Assertions.fail();
        }
    }

    @Test
    void testReadOrNot() {
        Permission permission = getPermission();

        try {
            Permission storedPermission = pemissionAccess.read(permission);
            Assertions.assertNull(storedPermission);
        } catch (OperationException e) {
            Assertions.fail();
        }

        permission = createPermission();

        try {
            Permission storedPermission = pemissionAccess.read(permission);
            Assertions.assertNotNull(storedPermission);
            Assertions.assertEquals(AGENT_SHORT_ID, storedPermission.getAgentShortId());
            Assertions.assertTrue(storedPermission.getSid() > 0);
        } catch (OperationException e) {
            Assertions.fail();
        }
    }

    @Test
    void testReadList() {
        createPermission();

        try {
            List<Permission> storedPermission = pemissionAccess.readList();
            Assertions.assertNotNull(storedPermission);
            Assertions.assertTrue(!storedPermission.isEmpty());
            Assertions.assertEquals(AGENT_SHORT_ID, storedPermission.get(0).getAgentShortId());
        } catch (OperationException e) {
            Assertions.fail();
        }
    }

    @Test
    void testUpdate() {
        Permission permission = createPermission();
        permission.setAllowed(false);
        permission.setAutoStart(false);

        try {
            pemissionAccess.update(permission);
            Permission storedPermission = pemissionAccess.read(permission);
            Assertions.assertNotNull(storedPermission);
            Assertions.assertEquals(false, storedPermission.isAllowed());
            Assertions.assertEquals(false, storedPermission.isAutoStart());
        } catch (OperationException e) {
            Assertions.fail();
        }
    }

}
