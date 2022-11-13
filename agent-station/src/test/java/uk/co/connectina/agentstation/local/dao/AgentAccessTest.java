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

import java.time.LocalDateTime;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.co.connectina.agentstation.App;
import uk.co.connectina.agentstation.api.Identity;
import uk.co.connectina.agentstation.api.Instance;
import uk.co.connectina.agentstation.api.Instance.State;
import uk.co.connectina.agentstation.api.client.OperationException;

/**
 * Tests for the AgentAccess class.
 *
 * @author Dr Christos Bohoris
 */
class AgentAccessTest {

    private static final Logger LOGGER = LogManager.getLogger(AgentAccessTest.class.toString());
    public static final String EXPECTED_SHORT_ID = "3679ed";
    private AgentAccess agentAccess = new AgentAccess("Test", "Test");

    AgentAccessTest() {
        try {
            agentAccess.createSchema();
        } catch (OperationException e) {
            LOGGER.error(e);
        }
    }

    @BeforeEach
    void setUp() throws OperationException {
        List<Instance> instances = agentAccess.readList();
        for (Instance instance : instances) {
            agentAccess.delete(instance);
        }
    }

    @Test
    void testCreate() {
        Instance instance = getInstance();

        try {
            agentAccess.create(instance);
        } catch (OperationException e) {
            Assertions.fail();
        }
    }

    @Test
    void testCreateSchema() {
        try {
            agentAccess.createSchema();
        } catch (OperationException e) {
            Assertions.fail();
        }
    }

    @Test
    void testDelete() {
        Instance instance = getInstance();

        try {
            agentAccess.delete(instance);
        } catch (OperationException e) {
            Assertions.fail();
        }
    }

    @Test
    void testExistsOrNot() {
        Instance instance = getInstance();
        try {
            boolean exists = agentAccess.exists(instance);
            Assertions.assertFalse(exists);
        } catch (OperationException e) {
            Assertions.fail();
        }

        instance = createInstance();

        try {
            boolean exists = agentAccess.exists(instance);
            Assertions.assertTrue(exists);
        } catch (OperationException e) {
            Assertions.fail();
        }
    }

    @Test
    void testReadOrNot() {
        Instance instance = getInstance();

        try {
            Instance storedInstance = agentAccess.read(instance);
            Assertions.assertNull(storedInstance);
        } catch (OperationException e) {
            Assertions.fail();
        }

        instance = createInstance();

        try {
            Instance storedInstance = agentAccess.read(instance);
            Assertions.assertNotNull(storedInstance);
            Assertions.assertEquals(EXPECTED_SHORT_ID, storedInstance.getShortId());
            Assertions.assertTrue(storedInstance.getSid() > 0);
        } catch (OperationException e) {
            Assertions.fail();
        }
    }

    @Test
    void testReadByPackageFile() {
        createInstance();

        try {
            List<Instance> storedInstance = agentAccess.readByPackageFile("file:/home/christos/test-agent.jar");
            Assertions.assertNotNull(storedInstance);
            Assertions.assertTrue(storedInstance.size() > 0);
            Assertions.assertEquals(EXPECTED_SHORT_ID, storedInstance.get(0).getShortId());
        } catch (OperationException e) {
            Assertions.fail();
        }
    }

    @Test
    void testReadByPlaceName() {
        createInstance();

        try {
            List<Instance> storedInstance = agentAccess.readByPlaceName("Test");
            Assertions.assertNotNull(storedInstance);
            Assertions.assertTrue(storedInstance.size() > 0);
            Assertions.assertEquals(EXPECTED_SHORT_ID, storedInstance.get(0).getShortId());
        } catch (OperationException e) {
            Assertions.fail();
        }
    }

    @Test
    void testReadList() {
        createInstance();

        try {
            List<Instance> storedInstance = agentAccess.readList();
            Assertions.assertNotNull(storedInstance);
            Assertions.assertTrue(storedInstance.size() > 0);
            Assertions.assertEquals(EXPECTED_SHORT_ID, storedInstance.get(0).getShortId());
        } catch (OperationException e) {
            Assertions.fail();
        }
    }

    @Test
    void testUpdate() {
        Instance instance = createInstance();
        instance.setState(State.ACTIVE);
        instance.setParameters(null);

        try {
            agentAccess.update(instance);
            Instance storedInstance = agentAccess.read(instance);
            Assertions.assertNotNull(storedInstance);
            Assertions.assertEquals(State.ACTIVE, storedInstance.getState());
            Assertions.assertNull(storedInstance.getParameters());
        } catch (OperationException e) {
            Assertions.fail();
        }
    }

    private Instance getInstance() {
        Identity identity = new Identity.IdentityBuilder("uk.co.connectina.test.TestAgent", "connectina.co.uk").description("Test agent description").hashCode("9f64b865ed237ab9905a4ce2ec99146b").packageFile("file:/home/christos/test-agent.jar").version(1, 3).build();
        LocalDateTime dateTime = LocalDateTime.parse("2022-05-21T17:25:31", App.DATETIME_FORMATTER);

        return new Instance(identity, dateTime, "Test", "Param1", "Param2", "Param3");
    }

    private Instance createInstance() {
        Instance instance = getInstance();

        try {
            agentAccess.create(instance);
        } catch (OperationException e) {
            LOGGER.error(e);
        }

        return instance;
    }

}
