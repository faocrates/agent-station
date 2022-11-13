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
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Test;

/**
 * Tests for the Schedule class.
 *
 * @author Dr Christos Bohoris
 */
class ScheduleTest {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Test
    void testGetNextOccurence() {
        LocalDateTime targetDateTime = LocalDateTime.now();
        targetDateTime = targetDateTime.plusDays(1);
        String date = DATE_FORMATTER.format(targetDateTime);
        String time = TIME_FORMATTER.format(targetDateTime);
        Schedule schedule = new Schedule(40, date, time, null, 0, 0);

        LocalDateTime nextDateTime = schedule.getNextOccurence();
        String nextDate = DATE_FORMATTER.format(nextDateTime);
        String nextTime = TIME_FORMATTER.format(nextDateTime);
        Assertions.assertEquals(date, nextDate);
        Assertions.assertEquals(time, nextTime);
    }

}
