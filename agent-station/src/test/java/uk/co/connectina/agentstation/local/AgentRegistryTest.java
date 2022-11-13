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
import org.junit.jupiter.api.*;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import uk.co.connectina.agentstation.api.Identity;
import uk.co.connectina.agentstation.api.Instance;
import uk.co.connectina.agentstation.api.Permission;
import uk.co.connectina.agentstation.api.client.OperationException;
import uk.co.connectina.agentstation.local.dao.AgentAccess;
import uk.co.connectina.agentstation.local.dao.PermissionAccess;
import uk.co.connectina.agentstation.local.dao.PlaceAccess;

/**
 * Tests for the AgentRegistry class.
 *
 * @author Dr Christos Bohoris
 */
class AgentRegistryTest {

    public static final String DEFAULT = "Default";
    public static final String UK_TEST = "uk.Test";
    private static final Logger LOGGER = LogManager.getLogger(AgentRegistryTest.class);
    private AgentRegistry registrySpy;

    @BeforeEach
    void setUp() {
        registrySpy = Mockito.spy(AgentRegistry.class);
        AgentAccess agentsAccess = Mockito.mock(AgentAccess.class);
        PlaceAccess placesAccess = Mockito.mock(PlaceAccess.class);
        PermissionAccess permissionsAccess = Mockito.mock(PermissionAccess.class);
        registrySpy.setAccessSupport(agentsAccess, placesAccess, permissionsAccess);
    }

    @Test
    void registerPlaceSuccess() throws OperationException {
        String name = DEFAULT;
        try {
            Mockito.doReturn(false).when(registrySpy).placeExists(ArgumentMatchers.anyString());
            Mockito.doReturn(new ArrayList<String>()).when(registrySpy).lookupPlaces();

            registrySpy.registerPlace(name);
        } catch (Exception e) {
            LOGGER.error(e);
            Assertions.fail(e);
        }
    }

    @Test
    void registerPlaceFailExists() throws OperationException {
        String name = DEFAULT;

        Mockito.doReturn(true).when(registrySpy).placeExists(ArgumentMatchers.anyString());
        Mockito.doReturn(Arrays.asList(DEFAULT)).when(registrySpy).lookupPlaces();

        Assertions.assertThrows(OperationException.class, () ->
            registrySpy.registerPlace(name)
        );
    }

    @Test
    void registerPlaceFailNull() throws OperationException {
        String name = null;

        Mockito.doReturn(false).when(registrySpy).placeExists(ArgumentMatchers.anyString());
        Mockito.doReturn(Arrays.asList("Main")).when(registrySpy).lookupPlaces();

        Assertions.assertThrows(OperationException.class, () ->
            registrySpy.registerPlace(name)
        );
    }

    @Test
    void registerPlaceFailBlank() throws OperationException {
        String name = " ";

        Mockito.doReturn(false).when(registrySpy).placeExists(ArgumentMatchers.anyString());
        Mockito.doReturn(Arrays.asList("Main")).when(registrySpy).lookupPlaces();

        Assertions.assertThrows(OperationException.class, () ->
            registrySpy.registerPlace(name)
        );
    }

    @Test
    void registerAgentSuccess() throws OperationException {
        Identity identity = new Identity.IdentityBuilder(UK_TEST, "connectina.co.uk").hashCode("dfsdfwiegfhbq").packageFile("/home/user").version(1, 0).description("Test agent").build();
        Instance instance = new Instance(identity, LocalDateTime.now(), DEFAULT, new String[]{});
        try {
            Mockito.doReturn(false).when(registrySpy).agentExists(ArgumentMatchers.any());
            Mockito.doReturn(new ArrayList<String>()).when(registrySpy).lookupAgents();

            registrySpy.registerAgent(instance);
        } catch (Exception e) {
            LOGGER.error(e);
            Assertions.fail(e);
        }
    }

    @Test
    void registerAgentFailExists() throws OperationException {
        Identity identity = new Identity.IdentityBuilder(UK_TEST, "connectina.co.uk").hashCode("dfsdfwiegfhbq").packageFile("/home/user").version(1, 0).description("Test agent").build();
        Instance instance = new Instance(identity, LocalDateTime.now(), DEFAULT, new String[]{});

        Mockito.doReturn(true).when(registrySpy).agentExists(ArgumentMatchers.any());

        Assertions.assertThrows(OperationException.class, () ->
            registrySpy.registerAgent(instance)
        );

    }

    @Test
    void registerAgentFailNullInstance() throws OperationException {
        Mockito.doReturn(true).when(registrySpy).agentExists(ArgumentMatchers.any());

        Assertions.assertThrows(OperationException.class, () ->
            registrySpy.registerAgent(null)
        );
    }

    @Test
    void registerAgentFailNullIdentity() throws OperationException {
        Instance instance = new Instance(null, LocalDateTime.now(), DEFAULT, new String[]{});

        Mockito.doReturn(true).when(registrySpy).agentExists(ArgumentMatchers.any());

        Assertions.assertThrows(OperationException.class, () ->
            registrySpy.registerAgent(instance)
        );
    }

    @Test
    void registerPermissionSuccess() throws OperationException {
        Permission permission = new Permission(UK_TEST, "df4kf1", DEFAULT, true, true);
        try {
            Mockito.doReturn(false).when(registrySpy).permissionExists(ArgumentMatchers.any());
            Mockito.doReturn(new ArrayList<String>()).when(registrySpy).lookupPermissions();

            registrySpy.registerPermission(permission);
        } catch (Exception e) {
            LOGGER.error(e);
            Assertions.fail(e);
        }
    }

    @Test
    void registerPermissionFailExists() throws OperationException {
        Permission permission = new Permission(UK_TEST, "df4kf1", DEFAULT, true, true);
        Mockito.doReturn(true).when(registrySpy).permissionExists(ArgumentMatchers.any());

        Assertions.assertThrows(OperationException.class, () ->
            registrySpy.registerPermission(permission)
        );
    }

    @Test
    void registerPermissionFailNull() throws OperationException {
        Mockito.doReturn(true).when(registrySpy).permissionExists(ArgumentMatchers.any());

        Assertions.assertThrows(OperationException.class, () ->
            registrySpy.registerPermission(null)
        );
    }

    @Test
    void lookupPermissionsByPlaceNameFailEmpty() throws OperationException {
        Assertions.assertThrows(OperationException.class, () ->
            registrySpy.lookupPermissionsByPlaceName(" ")
        );
    }

    @Test
    void lookupPermissionsByPlaceNameFailNull() throws OperationException {
        Assertions.assertThrows(OperationException.class, () ->
            registrySpy.lookupPermissionsByPlaceName(null)
        );
    }

    @Test
    void lookupAgentsByPlaceNameFailEmpty() throws OperationException {
        Assertions.assertThrows(OperationException.class, () ->
            registrySpy.lookupAgentsByPlaceName(" ")
        );
    }

    @Test
    void lookupAgentsByPlaceNameFailNull() throws OperationException {
        Assertions.assertThrows(OperationException.class, () ->
            registrySpy.lookupAgentsByPlaceName(null)
        );
    }

    @Test
    void lookupAgentsByPackageFileFailEmpty() throws OperationException {
        Assertions.assertThrows(OperationException.class, () ->
            registrySpy.lookupAgentsByPackageFile(" ")
        );
    }

    @Test
    void lookupAgentsByPackageFileFailNull() throws OperationException {
        Assertions.assertThrows(OperationException.class, () ->
            registrySpy.lookupAgentsByPackageFile(null)
        );
    }

    @Test
    void lookupPermissionFailEmpty() throws OperationException {
        Permission permission = new Permission(UK_TEST, " ", DEFAULT, true, true);

        Assertions.assertThrows(OperationException.class, () ->
            registrySpy.lookupPermission(permission)
        );
    }

    @Test
    void lookupPermissionFailNull() throws OperationException {
        Assertions.assertThrows(OperationException.class, () ->
            registrySpy.lookupPermission(null)
        );
    }

    @Test
    void deregisterPermissionFailEmpty() throws OperationException {
        Permission permission = new Permission(UK_TEST, " ", DEFAULT, true, true);

        Assertions.assertThrows(OperationException.class, () ->
            registrySpy.deregisterPermission(permission)
        );
    }

    @Test
    void deregisterPermissionFailNull() throws OperationException {
        Assertions.assertThrows(OperationException.class, () ->
            registrySpy.deregisterPermission(null)
        );
    }

    @Test
    void updateAgentStateFailNullInstance() throws OperationException {
        Assertions.assertThrows(OperationException.class, () ->
            registrySpy.updateAgentState(null)
        );
    }

    @Test
    void updateAgentStateFailNullIdentity() throws OperationException {
        Instance instance = new Instance(null, LocalDateTime.now(), DEFAULT, new String[]{});

        Assertions.assertThrows(OperationException.class, () ->
            registrySpy.updateAgentState(instance)
        );
    }

    @Test
    void deregisterAgentFailNullInstance() throws OperationException {
        Assertions.assertThrows(OperationException.class, () ->
            registrySpy.deregisterAgent(null)
        );
    }

    @Test
    void deregisterAgentFailNullIdentity() throws OperationException {
        Instance instance = new Instance(null, LocalDateTime.now(), DEFAULT, new String[]{});

        Assertions.assertThrows(OperationException.class, () ->
            registrySpy.deregisterAgent(instance)
        );
    }

    @Test
    void deregisterPlaceFailEmpty() throws OperationException {
        Assertions.assertThrows(OperationException.class, () ->
            registrySpy.deregisterPlace(" ")
        );
    }

    @Test
    void deregisterPlaceFailNull() throws OperationException {
        Assertions.assertThrows(OperationException.class, () ->
            registrySpy.deregisterPlace(null)
        );
    }

    @Test
    void agentExistsFailNullInstance() throws OperationException {
        Assertions.assertThrows(OperationException.class, () ->
            registrySpy.agentExists(null)
        );
    }

    @Test
    void agentExistsFailNullIdentity() throws OperationException {
        Instance instance = new Instance(null, LocalDateTime.now(), DEFAULT, new String[]{});

        Assertions.assertThrows(OperationException.class, () ->
            registrySpy.agentExists(instance)
        );
    }

    @Test
    void placeExistsFailEmpty() throws OperationException {
        Assertions.assertThrows(OperationException.class, () ->
            registrySpy.placeExists(" ")
        );
    }

    @Test
    void placeExistsFailNull() throws OperationException {
        Assertions.assertThrows(OperationException.class, () ->
            registrySpy.placeExists(null)
        );
    }

    @Test
    void permissionExistsFailNullPlace() throws OperationException {
        Permission permission = new Permission(UK_TEST, "3sd4as", null, true, true);

        Assertions.assertThrows(OperationException.class, () ->
            registrySpy.permissionExists(permission)
        );
    }

    @Test
    void permissionExistsFailNull() throws OperationException {
        Assertions.assertThrows(OperationException.class, () ->
            registrySpy.permissionExists(null)
        );
    }

}
