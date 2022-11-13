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
import java.util.ArrayList;
import java.util.List;

import uk.co.connectina.agentstation.api.client.OperationException;

/**
 * A DAO for places.
 *
 * @author Dr Christos Bohoris
 */
public class PlaceAccess extends AbstractAccess implements Access<String, String> {

    private static final String WHERE_UNIQUE_KEY = "name = ? AND stationName = ?";
    private static final String CREATE_PLACE_SQL = "CREATE TABLE IF NOT EXISTS PLACE (sid BIGINT AUTO_INCREMENT, stationName VARCHAR(256) NOT NULL, name VARCHAR(256), PRIMARY KEY (name, stationName))";
    private String stationName;

    public PlaceAccess(String stationName) {
        super();
        this.stationName = stationName;
    }

    public PlaceAccess(String stationName, String dbName) {
        super(dbName);
        this.stationName = stationName;
    }

    @Override
    public void create(String name) throws OperationException {
        final String SQL = "INSERT INTO PLACE (name, stationName) VALUES (?, ?)";

        try (PreparedStatement statement = connection != null ? connection.prepareStatement(SQL) : null) {
            if (statement == null) {

                throw new OperationException(NULL_STATEMENT);
            }

            statement.setString(1, name);
            statement.setString(2, stationName);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new OperationException(e);
        }
    }

    @Override
    public List<String> readList() throws OperationException {
        List<String> results = new ArrayList<>();
        final String SQL = "SELECT name FROM PLACE WHERE stationName = ? ORDER BY sid ASC";

        try (PreparedStatement statement = connection != null ? connection.prepareStatement(SQL) : null) {
            if (statement == null) {

                throw new OperationException(NULL_STATEMENT);
            }

            statement.setString(1, stationName);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                results.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            throw new OperationException(e);
        }

        return results;
    }

    @Override
    public String read(String name) throws OperationException {
        final String SQL = "SELECT name FROM PLACE WHERE " + WHERE_UNIQUE_KEY;

        try (PreparedStatement statement = connection != null ? connection.prepareStatement(SQL) : null) {
            if (statement == null) {

                throw new OperationException(NULL_STATEMENT);
            }

            whereUniqueKeyStatement(statement, name);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {

                return rs.getString("name");
            }
        } catch (SQLException e) {
            throw new OperationException(e);
        }

        return null;
    }

    @Override
    public void update(String key) throws OperationException {

        throw new OperationException("Not supported.");
    }

    @Override
    public void delete(String name) throws OperationException {
        final String SQL = "DELETE FROM PLACE WHERE " + WHERE_UNIQUE_KEY;

        try (PreparedStatement statement = connection != null ? connection.prepareStatement(SQL) : null) {
            if (statement == null) {

                throw new OperationException(NULL_STATEMENT);
            }

            whereUniqueKeyStatement(statement, name);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new OperationException(e);
        }
    }

    @Override
    public boolean exists(String name) throws OperationException {

        return read(name) != null;
    }

    @Override
    public void createSchema() throws OperationException {
        createSchema(CREATE_PLACE_SQL);
    }

    private void whereUniqueKeyStatement(final PreparedStatement statement, String name) throws SQLException {
        statement.setString(1, name);
        statement.setString(2, stationName);
    }

}
