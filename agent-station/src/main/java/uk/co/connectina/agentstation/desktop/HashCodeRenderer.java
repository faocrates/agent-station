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

import java.awt.Component;
import java.text.MessageFormat;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Renders a cell containing a hashcode value.
 *
 * @author Dr Christos Bohoris
 */
public class HashCodeRenderer extends JLabel implements TableCellRenderer {

    private static final int DIGITS_TO_HIGHLIGHT = 3;
   
    public HashCodeRenderer() {
        super.setOpaque(true);
    }
    
    public HashCodeRenderer(int align) {
        super.setOpaque(true);
        super.setHorizontalAlignment(align);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {
        
        String hashCode = ((String) value).trim();

        if (hashCode.isEmpty()) {
            setText("");

            return this;
        }

        setText(getHashCodeRender(hashCode));

        return this;
    }

    public static String getHashCodeRender(String hashCode) {
        String part1 = hashCode.substring(0, hashCode.length() - DIGITS_TO_HIGHLIGHT);
        String part2 = hashCode.substring(hashCode.length() - DIGITS_TO_HIGHLIGHT);
        
        return MessageFormat.format("<html>{0}<strong>{1}</strong></html>", part1, part2);
    }

}
