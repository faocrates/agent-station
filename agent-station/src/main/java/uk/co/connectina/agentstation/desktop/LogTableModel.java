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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

import uk.co.connectina.agentstation.App;
import uk.co.connectina.agentstation.api.client.LogType;
import uk.co.connectina.agentstation.local.LogEntry;

/**
 * A table model for agent logging information.
 *
 * @author Dr Christos Bohoris
 */
public class LogTableModel extends AbstractTableModel {

    private final transient List<LogEntry> entries;
    private final String[] columnNames;
    private static final int LOG_LIMIT = 200;

    LogTableModel(boolean showAbout) {
        if (showAbout) {
            columnNames = new String[]{"Time", "Type", "About", "Message"};
        } else {
            columnNames = new String[]{"Time", "Type", "Message"};
        }
        entries = new ArrayList<>();
    }

    public void clear() {
        entries.clear();
        fireTableDataChanged();
    }

    public void update(List<LogEntry> inputEntries) {
        entries.clear();
        if (inputEntries != null && !inputEntries.isEmpty()) {
            for (LogEntry l : inputEntries) {
                entries.add(new LogEntry(l.getDateTime(), l.getType(), l.getAbout(), l.getMessage()));
                maintainMaximumSize();
            }
        }
        fireTableDataChanged();
    }

    public void add(LocalDateTime dateTime, LogType type, String about, String message) {
        entries.add(0, new LogEntry(dateTime, type, about, message));
        maintainMaximumSize();
        fireTableDataChanged();
    }

    public void addInfo(String about, String message) {
        entries.add(0, new LogEntry(LocalDateTime.now(), LogType.INFO, about, message));
        maintainMaximumSize();
        fireTableDataChanged();
    }

    public void addError(String about, String message) {
        entries.add(0, new LogEntry(LocalDateTime.now(), LogType.ERROR, about, message));
        maintainMaximumSize();
        fireTableDataChanged();
    }

    private void maintainMaximumSize() {
        int size = entries.size();
        if (size > LOG_LIMIT) {
            entries.subList(LOG_LIMIT, size).clear();
        }
    }

    @Override
    public int getRowCount() {
        return entries.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return App.DATETIME_FORMATTER.format(entries.get(rowIndex).getDateTime());
            case 1:
                return entries.get(rowIndex).getType();
            case 2:
                return columnNames.length == 4 ? entries.get(rowIndex).getAbout() : entries.get(rowIndex).getMessage();
            case 3:
                return entries.get(rowIndex).getMessage();
            default:
                break;
        }

        return null;
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

}
