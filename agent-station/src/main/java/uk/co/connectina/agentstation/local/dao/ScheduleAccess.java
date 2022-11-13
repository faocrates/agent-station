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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import uk.co.connectina.agentstation.local.Schedule;
import uk.co.connectina.agentstation.local.Schedule.RepeatType;
import uk.co.connectina.agentstation.api.client.OperationException;

/**
 * A DAO for agent start schedules.
 *
 * @author Dr Christos Bohoris
 */
public class ScheduleAccess extends AbstractAccess implements Access<Schedule, Long> {

    private static final String WHERE_UNIQUE_KEY = "agentSid = ?";
    private static final String CREATE_SCHEDULE_SQL = "CREATE TABLE IF NOT EXISTS SCHEDULE (sid BIGINT AUTO_INCREMENT, agentSid BIGINT NOT NULL, startDate VARCHAR(16) NOT NULL, startTime VARCHAR(16) NOT NULL, repeat VARCHAR(16), interv INTEGER, occur INTEGER,  PRIMARY KEY (sid))";

    public ScheduleAccess() {
        super();
    }

    public ScheduleAccess(String dbName) {
        super(dbName);
    }

    @Override
    public void create(Schedule schedule) throws OperationException {
        final String SQL = "INSERT INTO SCHEDULE (agentSid, startDate, startTime, repeat, interv, occur) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection != null
                ? connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)
                : null) {
            if (statement == null) {

                throw new OperationException(NULL_STATEMENT);
            }

            statement.setLong(1, schedule.getAgentSid());
            statement.setString(2, schedule.getStartDate());
            statement.setString(3, schedule.getStartTime());
            statement.setString(4, schedule.getRepeat() != null ? schedule.getRepeat().name() : null);
            statement.setInt(5, schedule.getInterval());
            statement.setInt(6, schedule.getOccur());

            statement.executeUpdate();

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 1) {
                ResultSet keySet = statement.getGeneratedKeys();
                if (keySet.next()) {
                    schedule.setSid(keySet.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new OperationException(e);
        }
    }

    @Override
    public List<Schedule> readList() throws OperationException {
        List<Schedule> results = new ArrayList<>();
        final String SQL = "SELECT * FROM SCHEDULE ORDER BY sid ASC";

        try (PreparedStatement statement = connection != null ? connection.prepareStatement(SQL) : null) {
            if (statement == null) {

                throw new OperationException(NULL_STATEMENT);
            }

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                results.add(getSchedule(rs));
            }
        } catch (SQLException e) {
            throw new OperationException(e);
        }

        return results;
    }

    @Override
    public Schedule read(Long agentSid) throws OperationException {
        final String SQL = "SELECT * FROM SCHEDULE WHERE " + WHERE_UNIQUE_KEY;

        try (PreparedStatement statement = connection != null ? connection.prepareStatement(SQL) : null) {
            if (statement == null) {

                throw new OperationException(NULL_STATEMENT);
            }

            whereUniqueKeyStatement(statement, agentSid);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {

                return getSchedule(rs);
            }
        } catch (SQLException e) {
            throw new OperationException(e);
        }

        return null;
    }

    @Override
    public void update(Schedule schedule) throws OperationException {
        final String SQL = "UPDATE SCHEDULE SET startDate = ?, startTime = ?, repeat = ?, interv = ?, occur = ? WHERE "
                + WHERE_UNIQUE_KEY;

        try (PreparedStatement statement = connection != null ? connection.prepareStatement(SQL) : null) {
            if (statement == null) {

                throw new OperationException(NULL_STATEMENT);
            }

            statement.setString(1, schedule.getStartDate());
            statement.setString(2, schedule.getStartTime());
            statement.setString(3, schedule.getRepeat() != null ? schedule.getRepeat().name() : null);
            statement.setInt(4, schedule.getInterval());
            statement.setInt(5, schedule.getOccur());
            whereUniqueKeyStatement(statement, schedule.getAgentSid(), 6);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new OperationException(e);
        }
    }

    @Override
    public void delete(Long agentSid) throws OperationException {
        final String SQL = "DELETE FROM SCHEDULE WHERE " + WHERE_UNIQUE_KEY;

        try (PreparedStatement statement = connection != null ? connection.prepareStatement(SQL) : null) {
            if (statement == null) {

                throw new OperationException(NULL_STATEMENT);
            }

            whereUniqueKeyStatement(statement, agentSid);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new OperationException(e);
        }
    }

    @Override
    public boolean exists(Long agentSid) throws OperationException {

        return read(agentSid) != null;
    }

    @Override
    public void createSchema() throws OperationException {
        createSchema(CREATE_SCHEDULE_SQL);
    }

    private Schedule getSchedule(ResultSet rs) throws SQLException {
        Schedule schedule = new Schedule();
        schedule.setSid(rs.getLong("sid"));
        schedule.setAgentSid(rs.getLong("agentSid"));
        schedule.setInterval(rs.getInt("interv"));
        schedule.setOccur(rs.getInt("occur"));
        if (rs.getString("repeat") != null) {
            schedule.setRepeat(RepeatType.valueOf(rs.getString("repeat")));
        }
        schedule.setStartDate(rs.getString("startDate"));
        schedule.setStartTime(rs.getString("startTime"));

        return schedule;
    }

    private void whereUniqueKeyStatement(final PreparedStatement statement, long agentSid) throws SQLException {
        this.whereUniqueKeyStatement(statement, agentSid, 1);
    }

    private void whereUniqueKeyStatement(final PreparedStatement statement, long agentSid, int start)
            throws SQLException {

        statement.setLong(start, agentSid);
    }

}
