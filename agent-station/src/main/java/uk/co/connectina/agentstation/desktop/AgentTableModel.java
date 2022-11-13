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
import uk.co.connectina.agentstation.api.Instance;

/**
 * The table model for agents within a place.
 *
 * @author Dr Christos Bohoris
 */
public class AgentTableModel extends AbstractTableModel {

    private final List<Instance> instances;
    private final String[] columnNames = {"Agent Name", "Organisation", "Version", "Id", "State"};

    AgentTableModel() {
        instances = new ArrayList<>();
    }

    public Instance get(int index) {
        return instances.get(index);
    }
    
    public int indexOf(Instance instance) {
        return instances.indexOf(instance);
    }

    public void clear() {
        instances.clear();
    }
    
    public void updateState(Instance instance) {
        int index = instances.indexOf(instance);
        if (index >= 0) {
            instances.get(index).setState(instance.getState());
            fireTableDataChanged();
        }
    }

    public void add(Instance instance) {
        if (!instances.contains(instance)) {
            instances.add(instance);
            fireTableDataChanged();
        }
    }

    public void remove(Instance instance) {
        instances.remove(instance);
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {

        return instances.size();
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
                return instances.get(rowIndex).getIdentity().getName();
            case 1:
                return instances.get(rowIndex).getIdentity().getOrganisation();
            case 2:
                return Double.toString(instances.get(rowIndex).getIdentity().getVersion());
            case 3:
                return instances.get(rowIndex).getShortId();
            case 4:
                return instances.get(rowIndex).getState().toString();
            default:
                break;
        }

        return null;
    }

}
