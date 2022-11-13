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

import uk.co.connectina.agentstation.api.client.OperationException;

/**
 * Tests for the PlaceAccess class.
 *
 * @author Dr Christos Bohoris
 */
class PlaceAccessTest {

    private static final Logger LOGGER = LogManager.getLogger(PlaceAccessTest.class.toString());
    private PlaceAccess placeAccess = new PlaceAccess("Test", "Test");

    PlaceAccessTest() {
        try {
            placeAccess.createSchema();
        } catch (OperationException e) {
            LOGGER.error(e);
        }
    }

    @BeforeEach
    void setUp() throws OperationException {
        List<String> places = placeAccess.readList();
        for (String place : places) {
            placeAccess.delete(place);
        }
    }

    @Test
    void testCreate() {
        try {
            placeAccess.create("Main");
        } catch (OperationException e) {
            Assertions.fail();
        }
    }

    @Test
    void testCreateSchema() {
        try {
            placeAccess.createSchema();
        } catch (OperationException e) {
            Assertions.fail();
        }
    }

    @Test
    void testDelete() {
        createPlace("Main");

        try {
            placeAccess.delete("Main");
        } catch (OperationException e) {
            Assertions.fail();
        }
    }

    @Test
    void testExistsOrNot() {
        try {
            boolean exists = placeAccess.exists("Incoming");
            Assertions.assertFalse(exists);
        } catch (OperationException e) {
            Assertions.fail();
        }

        createPlace("Main");

        try {
            boolean exists = placeAccess.exists("Main");
            Assertions.assertTrue(exists);
        } catch (OperationException e) {
            Assertions.fail();
        }
    }

    @Test
    void testReadOrNot() {
        try {
            String place = placeAccess.read("Incoming");
            Assertions.assertNull(place);
        } catch (OperationException e) {
            Assertions.fail();
        }

        createPlace("Main");

        try {
            String place = placeAccess.read("Main");
            Assertions.assertNotNull(place);
            Assertions.assertEquals("Main", place);
        } catch (OperationException e) {
            Assertions.fail();
        }
    }

    @Test
    void testReadList() {
        createPlace("Main");

        try {
            List<String> storedPlaces = placeAccess.readList();
            Assertions.assertNotNull(storedPlaces);
            Assertions.assertTrue(!storedPlaces.isEmpty());
            Assertions.assertEquals("Main", storedPlaces.get(0));
        } catch (OperationException e) {
            Assertions.fail();
        }
    }

    @Test
    void testUpdate() {
        Assertions.assertThrows(OperationException.class, () ->
            placeAccess.update("Main")
        );
    }

    private void createPlace(String name) {
        try {
            placeAccess.create(name);
        } catch (OperationException e) {
            LOGGER.error(e);
        }
    }
    
}
