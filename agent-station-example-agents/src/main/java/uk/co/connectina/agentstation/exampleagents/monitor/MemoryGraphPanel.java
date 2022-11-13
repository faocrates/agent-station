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
package uk.co.connectina.agentstation.exampleagents.monitor;

import java.awt.*;
import javax.swing.*;
import java.awt.geom.*;

import static javax.swing.WindowConstants.HIDE_ON_CLOSE;

/**
 * A panel that contains used memory moving average information. 
 * 
 * @author Dr Christos Bohoris
 */
public class MemoryGraphPanel extends JPanel {

    private double[] coordinates = {};
    private int margin = 20;
    private JDialog dialog;

    public void updateData(double[] data) {
        coordinates = data;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int width = getWidth();
        int height = getHeight();
        double max = getMax();
        double min = getMin();

        // Draw axis lines
        g2d.draw(new Line2D.Double(margin, margin, margin, (double)height - margin));
        g2d.draw(new Line2D.Double(margin, (double)height - margin, (double)width - margin, (double)height - margin));

        double x = (double) (width - 2 * margin) / (coordinates.length - 1);
        double scale = (height - 2 * margin) / max;

        // Draw maximum memory text and y point
        String memoryMaxLabel = "MAX " + String.format("%.1f", max) + "MB";
        g2d.setPaint(Color.BLACK);
        g2d.drawBytes(memoryMaxLabel.getBytes(), 0, memoryMaxLabel.length(), margin + 3, margin + 5);
        g2d.fill(new Ellipse2D.Double(margin - 2.0d, margin - 2.0d, 5, 5));

        boolean minOnGraph = false;
        double prevx1 = 0d;
        double prevy1 = 0d;
        // Draw data points
        for (int i = 0; i < coordinates.length; i++) {
            double x1 = margin + i * x;
            double y1 = height - margin - scale * coordinates[i];
            g2d.setPaint(Color.GRAY);
            g2d.fill(new Ellipse2D.Double(x1 - 2, y1 - 2, 4, 4));

            // Draw minimum memory text and y point
            if (!minOnGraph && coordinates[i] == min) {
                String memoryMinLabel = "MIN " + String.format("%.1f", min) + "MB";
                g2d.setPaint(Color.BLACK);
                g2d.drawBytes(memoryMinLabel.getBytes(), 0, memoryMinLabel.length(), margin + 3, (int) (y1 + 15));
                g2d.fill(new Ellipse2D.Double(margin - 2.0d, (int) y1 - 2.0d, 5, 5));
                minOnGraph = true;
            }

            // Draw line between current and previous points
            if (prevx1 > 0d) {
                g2d.setPaint(Color.BLUE);
                g2d.draw(new Line2D.Double(prevx1, prevy1, x1, y1));
            }

            // Now the current points become the previous
            prevx1 = x1;
            prevy1 = y1;
        }

        // Draw average memory text
        String memoryAvgLabel = "AVG " + String.format("%.1f", getAvg()) + "MB";
        g2d.setPaint(Color.RED);
        g2d.drawBytes(memoryAvgLabel.getBytes(), 0, memoryAvgLabel.length(), margin + 3, height - margin - 3);
    }

    private double getMax() {
        double max = -Integer.MAX_VALUE;
        for (int i = 0; i < coordinates.length; i++) {
            if (coordinates[i] > max) {
                max = coordinates[i];
            }

        }

        return max;
    }

    private double getMin() {
        double min = Integer.MAX_VALUE;
        for (int i = 0; i < coordinates.length; i++) {
            if (coordinates[i] < min) {
                min = coordinates[i];
            }

        }

        return min;
    }

    private double getAvg() {
        double sum = 0;
        for (int i = 0; i < coordinates.length; i++) {
            sum += coordinates[i];
        }

        return sum / coordinates.length;
    }

    public void displayInDialog(String title) {
        dialog = new JDialog();
        dialog.setTitle(title);
        dialog.setDefaultCloseOperation(HIDE_ON_CLOSE);
        dialog.add(this);
        dialog.setSize(650, 350);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    public void disposeDialog() {
        dialog.setVisible(false);
        coordinates = new double[] {};
    }

}
