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
package uk.co.connectina.agentstation.desktop;

import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.connectina.agentstation.api.Permission;

/**
 * A table model for the permissions of agents within a particular place. 
 *
 * @author Dr Christos Bohoris
 */
public class PermissionTableModel extends AbstractTableModel {

    private final List<Permission> permissions;
    private final String[] columnNames = {"Agent Name", "Id", "Place Name", "Allowed", "Auto Start"};
    private static final Logger LOGGER = LogManager.getLogger(PermissionTableModel.class.toString());

    public PermissionTableModel(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    void addPermission(Permission permission) {
        permissions.add(permission);
    }

    void removePermission(int index) {
        permissions.remove(index);
    }

    @Override
    public int getRowCount() {
        return permissions.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Class getColumnClass(int c) {

        return c < 3 ? String.class : Boolean.class;
    }

    @Override
    public String getColumnName(int col) {

        return columnNames[col];
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        if (col == 0 || col == 1 || col == 3) {
            
            return true;
        }
        if (col == 2) {
            
            return false;
        }
        return col == 4 && permissions.get(row).isAllowed();
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0 -> permissions.get(rowIndex).setAgentName((String) aValue);
            case 1 -> permissions.get(rowIndex).setAgentShortId((String) aValue);
            case 3 -> {
                permissions.get(rowIndex).setAllowed((Boolean) aValue);
                if (!((Boolean) aValue).booleanValue()) {
                    permissions.get(rowIndex).setAutoStart(false);
                }
            }
            case 4 -> permissions.get(rowIndex).setAutoStart((Boolean) aValue);
            default ->
                LOGGER.error("Invalid column index {}", columnIndex);
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return permissions.get(rowIndex).getAgentName();
            case 1:
                return permissions.get(rowIndex).getAgentShortId();
            case 2:
                return permissions.get(rowIndex).getPlaceName();
            case 3:
                return permissions.get(rowIndex).isAllowed();
            case 4:
                return permissions.get(rowIndex).isAutoStart();
            default:
                break;
        }

        return null;
    }

}
