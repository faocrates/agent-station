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
package uk.co.connectina.agentstation.terminal;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.connectina.agentstation.api.Permission;
import uk.co.connectina.agentstation.api.Registry;
import uk.co.connectina.agentstation.api.client.OperationException;

/**
 * An interactive commands client for permissions management.
 *
 * @author Dr Christos Bohoris
 */
final class PermissionsClient {

    private final Registry registry;
    private static final Logger LOGGER = LogManager.getLogger(PermissionsClient.class.toString());

    PermissionsClient(Registry registry) {
        this.registry = registry;
    }

    void listPermissions() {
        try {
            List<Permission> permissions = registry.lookupPermissions();
            System.out.println("Permissions:");
            int index = 1;
            for (Permission permission : permissions) {
                System.out.println(" " + index++ + ". " + permission.getAgentName() + " " + permission.getAgentShortId() + ", " + permission.getPlaceName());
            }
        } catch (OperationException e) {
            LOGGER.error(e);
        }
    }
    
    void removePermission(String[] cmd) {
        try {
            Permission permission = lookupPermission(cmd);
            if (permission != null) {
                registry.deregisterPermission(permission);
            }
        } catch (OperationException e) {
            LOGGER.error(e);
        }
    }
    
    private Permission lookupPermission(String[] cmd) {
        if (cmd.length != 3) {

            return null;
        }

        int index;
        try {
            index = Integer.valueOf(cmd[2]);
        } catch (NumberFormatException e) {
            LOGGER.error(e);

            return null;
        }

        List<Permission> permissions;
        try {
            permissions = registry.lookupPermissions();
        } catch (OperationException e) {
            LOGGER.error(e);

            return null;
        }

        if (index < 1 || index > permissions.size()) {
            LOGGER.error("Invalid permission list index");

            return null;
        }

        return permissions.get(index - 1);
    }

}
