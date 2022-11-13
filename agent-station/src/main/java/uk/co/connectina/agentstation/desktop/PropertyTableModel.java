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

import javax.swing.table.AbstractTableModel;

/**
 * A table model for property values.
 *
 * @author Dr Christos Bohoris
 */
public class PropertyTableModel extends AbstractTableModel {

    private final String[] columnNames = {"Property", "Value"};
    private final String[] properties;
    private final String[] values;

    PropertyTableModel(String[] properties, String[] values) {
        this.properties = properties;
        this.values = values;
    }

    @Override
    public int getRowCount() {

        return properties.length;
    }

    @Override
    public int getColumnCount() {

        return columnNames.length;
    }

    @Override
    public Class<String> getColumnClass(int c) {

        return String.class;
    }

    @Override
    public String getColumnName(int col) {

        return columnNames[col];
    }

    @Override
    public boolean isCellEditable(int row, int col) {

        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return properties[rowIndex];
            case 1:
                return values[rowIndex];
            default:
                break;
        }

        return null;
    }

}
