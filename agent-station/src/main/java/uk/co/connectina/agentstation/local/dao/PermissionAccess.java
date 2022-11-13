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

import uk.co.connectina.agentstation.api.Permission;
import uk.co.connectina.agentstation.api.PermissionIdentity;
import uk.co.connectina.agentstation.api.client.OperationException;

/**
 * A DAO for permissions.
 *
 * @author Dr Christos Bohoris
 */
public class PermissionAccess extends AbstractAccess implements Access<Permission, PermissionIdentity> {

    private static final String AGENT_TRACE_ID = "agentTraceId";
    private static final String PLACE_NAME = "placeName";
    private static final String ALLOWED = "allowed";
    private static final String AUTO_START = "autoStart";
    private static final String AGENT_NAME = "agentName";
    private static final String WHERE_UNIQUE_KEY = "agentName = ? AND agentTraceId = ? AND placeName = ? AND stationName = ?";
    private static final String CREATE_PERMISSION_SQL = "CREATE TABLE IF NOT EXISTS PERMISSION (sid BIGINT AUTO_INCREMENT, agentName VARCHAR(256) NOT NULL, agentTraceId VARCHAR(32) NOT NULL, stationName VARCHAR(256) NOT NULL, placeName VARCHAR(256) NOT NULL, allowed BOOLEAN NOT NULL, autoStart BOOLEAN NOT NULL, PRIMARY KEY (agentName, agentTraceId, placeName, stationName))";
    private String stationName;

    public PermissionAccess(String stationName) {
        super();
        this.stationName = stationName;
    }

    public PermissionAccess(String stationName, String dbName) {
        super(dbName);
        this.stationName = stationName;
    }

    @Override
    public void create(Permission permission) throws OperationException {
        final String SQL = "INSERT INTO PERMISSION (agentName, agentTraceId, stationName, placeName, allowed, autoStart) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection != null
                ? connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)
                : null) {
            if (statement == null) {

                throw new OperationException(NULL_STATEMENT);
            }

            statement.setString(1, permission.getAgentName());
            statement.setString(2, permission.getAgentShortId());
            statement.setString(3, stationName);
            statement.setString(4, permission.getPlaceName());
            statement.setBoolean(5, permission.isAllowed());
            statement.setBoolean(6, permission.isAutoStart());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 1) {
                ResultSet keySet = statement.getGeneratedKeys();
                if (keySet.next()) {
                    permission.setSid(keySet.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new OperationException(e);
        }
    }

    @Override
    public List<Permission> readList() throws OperationException {
        List<Permission> results = new ArrayList<>();
        final String SQL = "SELECT * FROM PERMISSION WHERE stationName = ? ORDER BY sid ASC";

        try (PreparedStatement statement = connection != null ? connection.prepareStatement(SQL) : null) {
            if (statement == null) {

                throw new OperationException(NULL_STATEMENT);
            }

            statement.setString(1, stationName);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                results.add(getPermission(rs));
            }
        } catch (SQLException e) {
            throw new OperationException(e);
        }

        return results;
    }

    @Override
    public Permission read(PermissionIdentity permissionIdentity) throws OperationException {
        final String SQL = "SELECT * FROM PERMISSION WHERE " + WHERE_UNIQUE_KEY;

        try (PreparedStatement statement = connection != null ? connection.prepareStatement(SQL) : null) {
            if (statement == null) {

                throw new OperationException(NULL_STATEMENT);
            }

            whereUniqueKeyStatement(statement, permissionIdentity);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {

                return getPermission(rs);
            }
        } catch (SQLException e) {
            throw new OperationException(e);
        }

        return null;
    }

    @Override
    public void update(Permission permission) throws OperationException {
        final String SQL = "UPDATE PERMISSION SET allowed = ?, autoStart = ? WHERE " + WHERE_UNIQUE_KEY;

        try (PreparedStatement statement = connection != null ? connection.prepareStatement(SQL) : null) {
            if (statement == null) {

                throw new OperationException(NULL_STATEMENT);
            }
            statement.setBoolean(1, permission.isAllowed());
            statement.setBoolean(2, permission.isAutoStart());
            whereUniqueKeyStatement(statement, permission, 3);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new OperationException(e);
        }
    }

    @Override
    public void delete(PermissionIdentity permissionIdentity) throws OperationException {
        final String SQL = "DELETE FROM PERMISSION WHERE " + WHERE_UNIQUE_KEY;

        try (PreparedStatement statement = connection != null ? connection.prepareStatement(SQL) : null) {
            if (statement == null) {

                throw new OperationException(NULL_STATEMENT);
            }

            whereUniqueKeyStatement(statement, permissionIdentity);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new OperationException(e);
        }
    }

    @Override
    public boolean exists(PermissionIdentity permissionIdentity) throws OperationException {
        List<Permission> permissions = readList();

        return permissions.contains(permissionIdentity);
    }

    public List<Permission> readByPlaceName(String placeName) throws OperationException {
        List<Permission> results = new ArrayList<>();
        final String SQL = "SELECT * FROM PERMISSION WHERE placeName = ? AND stationName = ? ORDER BY sid ASC";

        try (PreparedStatement statement = connection != null ? connection.prepareStatement(SQL) : null) {
            if (statement == null) {

                throw new OperationException(NULL_STATEMENT);
            }

            statement.setString(1, placeName);
            statement.setString(2, stationName);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                results.add(getPermission(rs));
            }
        } catch (SQLException e) {
            throw new OperationException(e);
        }

        return results;
    }

    @Override
    public void createSchema() throws OperationException {
        createSchema(CREATE_PERMISSION_SQL);
    }

    private void whereUniqueKeyStatement(final PreparedStatement statement, PermissionIdentity permissionIdentity)
            throws SQLException {
        this.whereUniqueKeyStatement(statement, permissionIdentity, 1);
    }

    private void whereUniqueKeyStatement(final PreparedStatement statement, PermissionIdentity permissionIdentity,
            int start)
            throws SQLException {
        statement.setString(start++, permissionIdentity.getAgentName());
        statement.setString(start++, permissionIdentity.getAgentShortId());
        statement.setString(start++, permissionIdentity.getPlaceName());
        statement.setString(start, stationName);
    }

    private Permission getPermission(ResultSet rs) throws SQLException {
        Permission permission = new Permission(rs.getString(AGENT_NAME), rs.getString(AGENT_TRACE_ID),
                rs.getString(PLACE_NAME), rs.getBoolean(ALLOWED), rs.getBoolean(AUTO_START));
        permission.setSid(rs.getLong("sid"));

        return permission;
    }

}
