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
package uk.co.connectina.agentstation.local.dao;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.co.connectina.agentstation.local.Schedule;
import uk.co.connectina.agentstation.api.client.OperationException;

/**
 * Tests for the ScheduleAccess class.
 *
 * @author Dr Christos Bohoris
 */
class ScheduleAccessTest {

    private static final Logger LOGGER = LogManager.getLogger(ScheduleAccessTest.class.toString());
    private ScheduleAccess scheduleAccess = new ScheduleAccess("Test");

    ScheduleAccessTest() {
        try {
            scheduleAccess.createSchema();
        } catch (OperationException e) {
            LOGGER.error(e);
        }
    }

    @BeforeEach
    void setUp() throws OperationException {
        List<Schedule> schedules = scheduleAccess.readList();
        for (Schedule schedule : schedules) {
            scheduleAccess.delete(schedule.getAgentSid());
        }
    }

    @Test
    void testCreate() {
        Schedule schedule = getSchedule();

        try {
            scheduleAccess.create(schedule);
        } catch (OperationException e) {
            Assertions.fail();
        }

        Assertions.assertTrue(schedule.getSid() > 0);
    }

    @Test
    void testDelete() {
        Schedule schedule = createSchedule();

        try {
            scheduleAccess.delete(schedule.getAgentSid());
        } catch (OperationException e) {
            Assertions.fail();
        }
    }

    @Test
    void testExistsOrNot() {
        try {
            boolean exists = scheduleAccess.exists(42L);
            Assertions.assertFalse(exists);
        } catch (OperationException e) {
            Assertions.fail();
        }

        Schedule schedule = createSchedule();

        try {
            boolean exists = scheduleAccess.exists(schedule.getAgentSid());
            Assertions.assertTrue(exists);
        } catch (OperationException e) {
            Assertions.fail();
        }
    }

    @Test
    void testReadOrNot() {
        try {
            Schedule schedule = scheduleAccess.read(42L);
            Assertions.assertNull(schedule);
        } catch (OperationException e) {
            Assertions.fail();
        }

        Schedule schedule = createSchedule();

        try {
            Schedule storedSchedule = scheduleAccess.read(schedule.getAgentSid());
            Assertions.assertNotNull(storedSchedule);
            Assertions.assertEquals(30L, storedSchedule.getAgentSid());
            Assertions.assertTrue(storedSchedule.getSid() > 0);
        } catch (OperationException e) {
            Assertions.fail();
        }
    }

    @Test
    void testReadList() {
        createSchedule();

        try {
            List<Schedule> storedSchedules = scheduleAccess.readList();
            Assertions.assertNotNull(storedSchedules);
            Assertions.assertTrue(!storedSchedules.isEmpty());
            Assertions.assertEquals(30L, storedSchedules.get(0).getAgentSid());
        } catch (OperationException e) {
            Assertions.fail();
        }
    }

    @Test
    void testUpdate() {
        Schedule schedule = createSchedule();
        schedule.setInterval(11);
        schedule.setOccur(18);

        try {
            scheduleAccess.update(schedule);
            Schedule storedSchedule = scheduleAccess.read(schedule.getAgentSid());
            Assertions.assertNotNull(storedSchedule);
            Assertions.assertEquals(11, storedSchedule.getInterval());
            Assertions.assertEquals(18, storedSchedule.getOccur());
        } catch (OperationException e) {
            Assertions.fail();
        }
    }

    @Test
    void testCreateSchema() {
        try {
            scheduleAccess.createSchema();
        } catch (OperationException e) {
            Assertions.fail();
        }
    }

    private Schedule getSchedule() {
        Schedule schedule = new Schedule();
        schedule.setAgentSid(30);
        schedule.setInterval(3);
        schedule.setOccur(8);
        schedule.setRepeat(Schedule.RepeatType.DAILY);
        schedule.setStartDate("2022-01-18");
        schedule.setStartTime("18:05:00");

        return schedule;
    }

    private Schedule createSchedule() {
        Schedule schedule = getSchedule();

        try {
            scheduleAccess.create(schedule);
        } catch (OperationException e) {
            LOGGER.error(e);
        }

        return schedule;
    }
}
