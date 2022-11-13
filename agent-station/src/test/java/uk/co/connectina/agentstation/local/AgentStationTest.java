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
package uk.co.connectina.agentstation.local;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import uk.co.connectina.agentstation.api.Identity;
import uk.co.connectina.agentstation.api.Instance;
import uk.co.connectina.agentstation.api.client.OperationException;

/**
 * Tests for the AgentStation class.
 *
 * @author Dr Christos Bohoris
 */
class AgentStationTest {

    public static final String DEFAULT = "Default";
    private static final Logger LOGGER = LogManager.getLogger(AgentStationTest.class);
    private AgentRegistry registryMock;
    private AgentStation stationSpy;

    @BeforeEach
    void setUp() {
        registryMock = Mockito.mock(AgentRegistry.class);
        stationSpy = Mockito.spy(AgentStation.class);
        stationSpy.setSupport(registryMock, Mockito.mock(AgentManager.class), Mockito.mock(PlaceManager.class));
        Mockito.doNothing().when(stationSpy).notifyStationListeners(Mockito.anyString(), Mockito.any());
    }

    @Test
    void createPlaceSuccess() throws OperationException {
        String name = "Main";

        Mockito.when(stationSpy.placeExists(ArgumentMatchers.anyString())).thenReturn(false);
        Mockito.when(registryMock.lookupPlaces()).thenReturn(new ArrayList<>());
        try {
            stationSpy.createPlace(name);
        } catch (Exception e) {
            LOGGER.error(e);
            Assertions.fail(e);
        }
    }

    @Test
    void createPlaceFailExists() throws OperationException {
        String name = "Test";

        Mockito.when(stationSpy.placeExists(ArgumentMatchers.anyString())).thenReturn(true);
        Mockito.when(registryMock.lookupPlaces()).thenReturn(Arrays.asList("Test"));

        Assertions.assertThrows(OperationException.class, () ->
            stationSpy.createPlace(name)
        );
    }

    @Test
    void createPlaceFailNull() throws OperationException {
        String name = null;

        Mockito.when(stationSpy.placeExists(ArgumentMatchers.anyString())).thenReturn(false);
        Mockito.when(registryMock.lookupPlaces()).thenReturn(Arrays.asList("Main"));

        Assertions.assertThrows(OperationException.class, () ->
            stationSpy.createPlace(name)
        );
    }

    @Test
    void createPlaceFailBlank() throws OperationException {
        String name = " ";

        Mockito.when(stationSpy.placeExists(ArgumentMatchers.anyString())).thenReturn(false);
        Mockito.when(registryMock.lookupPlaces()).thenReturn(Arrays.asList("Main"));

        Assertions.assertThrows(OperationException.class, () ->
            stationSpy.createPlace(name)
        );
    }

    @Test
    void removePlaceSuccess() throws OperationException {
        String name = "Main";

        Mockito.when(stationSpy.placeExists(ArgumentMatchers.anyString())).thenReturn(true);
        Mockito.when(registryMock.lookupPlaces()).thenReturn(Arrays.asList("Main"));

        try {
            stationSpy.removePlace(name);
        } catch (Exception e) {
            LOGGER.error(e);
            Assertions.fail(e);
        }
    }

    @Test
    void removePlaceFailDefault() throws OperationException {
        String name = DEFAULT;

        Mockito.when(stationSpy.placeExists(ArgumentMatchers.anyString())).thenReturn(true);
        Mockito.when(registryMock.lookupPlaces()).thenReturn(Arrays.asList(DEFAULT));

        Assertions.assertThrows(OperationException.class, () ->
            stationSpy.removePlace(name)
        );
    }

    @Test
    void removePlaceFailNull() throws OperationException {
        String name = null;

        Mockito.when(stationSpy.placeExists(ArgumentMatchers.anyString())).thenReturn(true);
        Mockito.when(registryMock.lookupPlaces()).thenReturn(Arrays.asList("Main"));

        Assertions.assertThrows(OperationException.class, () ->
            stationSpy.removePlace(name)
        );
    }

    @Test
    void removePlaceFailBlank() throws OperationException {
        String name = " ";

        Mockito.when(stationSpy.placeExists(ArgumentMatchers.anyString())).thenReturn(true);
        Mockito.when(registryMock.lookupPlaces()).thenReturn(Arrays.asList("Main"));

        Assertions.assertThrows(OperationException.class, () ->
            stationSpy.removePlace(name)
        );
    }

    @Test
    void removePlaceFailNotExists() throws OperationException {
        String name = "Test";

        Mockito.when(stationSpy.placeExists(ArgumentMatchers.anyString())).thenReturn(false);
        Mockito.when(registryMock.lookupPlaces()).thenReturn(Arrays.asList("Main"));

        Assertions.assertThrows(OperationException.class, () ->
            stationSpy.removePlace(name)
        );
    }
    
    @Test
    void instanceInputFailMissingIdentity() throws OperationException {
        Instance instance = new Instance(null, LocalDateTime.now(), DEFAULT);
        
        Assertions.assertThrows(OperationException.class, () ->
            stationSpy.instanceValidation(instance)
        );
    }
    
    @Test
    void instanceInputFailMissingIdentityInfo() throws OperationException {
        Identity identity = new Identity.IdentityBuilder("uk.Test", "connectina.co.uk").hashCode(" ").packageFile("/home/user").version(1, 0).description("Test agent").build();
        Instance instance = new Instance(identity, LocalDateTime.now(), DEFAULT);
        
        Assertions.assertThrows(OperationException.class, () ->
            stationSpy.instanceValidation(instance)
        );
    }
    
    @Test
    void instanceInputFailMissingInstance() throws OperationException {
        Instance instance = null;
        
        Assertions.assertThrows(OperationException.class, () ->
            stationSpy.instanceValidation(instance)
        );
    }

}
