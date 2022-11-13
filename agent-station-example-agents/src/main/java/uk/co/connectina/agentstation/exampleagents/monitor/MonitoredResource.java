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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A monitored resource type.
 * 
 * @author Dr Christos Bohoris
 */
public class MonitoredResource {
    
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    
    private String name;
    private String unit;
    private double value;
    private String dateTime;

    public MonitoredResource() {
    }

    public MonitoredResource(String name, String unit, double value) {
        this.name = name;
        this.unit = unit;
        this.value = value;
        this.dateTime = DATETIME_FORMATTER.format(LocalDateTime.now());
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "MonitoredResource{" + "name=" + name + ", unit=" + unit + ", value=" + value + ", dateTime=" + dateTime + '}';
    }
    
}
