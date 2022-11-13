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

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import uk.co.connectina.agentstation.api.Identity;

/**
 * A table model for agent identity information.
 *
 * @author Dr Christos Bohoris
 */
public class IdentityTableModel extends AbstractTableModel {

    private final List<String> info;
    private final String[] rowNames = {"Agent Name", "Organisation", "Package Hash Code", "Major Version", "Minor Version", "Description"};
    private final String[] columnNames = {"Name", "Value"};

    IdentityTableModel(Identity id) {
        info = new ArrayList<>();
        update(id);
    }

    public void clear() {
        info.clear();
        fireTableDataChanged();
    }

    public void update(Identity id) {
        clear();
        if (id != null) {
            info.add(id.getName());
            info.add(id.getOrganisation());
            info.add(id.getHashCode());
            info.add(String.valueOf(id.getMajorVersion()));
            info.add(String.valueOf(id.getMinorVersion()));
            info.add(id.getDescription());
            fireTableDataChanged();
        }
    }

    @Override
    public int getRowCount() {

        return 6;
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
                return rowNames[rowIndex];
            case 1:
                String text = rowIndex == 2 ? HashCodeRenderer.getHashCodeRender(info.get(rowIndex)) : info.get(rowIndex);
                return info.size() > rowIndex ? text : "";
            default:
                break;
        }

        return null;
    }

}
