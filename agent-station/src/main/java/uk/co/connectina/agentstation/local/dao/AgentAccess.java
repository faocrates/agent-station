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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import uk.co.connectina.agentstation.App;
import uk.co.connectina.agentstation.api.Identity;
import uk.co.connectina.agentstation.api.Instance;
import uk.co.connectina.agentstation.api.client.OperationException;

/**
 * A DAO for agents.
 *
 * @author Dr Christos Bohoris
 */
public class AgentAccess extends AbstractAccess implements Access<Instance, Instance> {

    private static final String PLACE_NAME = "placeName";
    private static final String ORGANISATION = "organisation";
    private static final String CLASS_NAME = "className";
    private static final String PACKAGE_FILE = "packageFile";
    private static final String MAJOR_VERSION = "majorVersion";
    private static final String MINOR_VERSION = "minorVersion";
    private static final String DESCRIPTION = "description";
    private static final String CREATION = "creation";
    private static final String HASH_CODE = "hashCode";
    private static final String PARAMETERS = "parameters";
    private static final String STATE = "state";
    private static final String WHERE_UNIQUE_KEY = "className = ? AND hashCode = ? AND majorVersion = ? AND minorVersion = ? AND creation = ? AND stationName = ?";
    private static final String CREATE_INSTANCE_SQL = "CREATE TABLE IF NOT EXISTS INSTANCE (sid BIGINT AUTO_INCREMENT, stationName VARCHAR(256) NOT NULL, placeName VARCHAR(256) NOT NULL, name VARCHAR(256) NOT NULL, organisation VARCHAR(256) NOT NULL, hashCode VARCHAR(32) NOT NULL, majorVersion INTEGER NOT NULL, minorVersion INTEGER NOT NULL, description VARCHAR(256) NOT NULL, creation VARCHAR(32) NOT NULL, state VARCHAR(8) NOT NULL, packageFile VARCHAR(256) NOT NULL, className VARCHAR(256) NOT NULL, parameters VARCHAR(1024))";
    private String stationName;

    public AgentAccess(String stationName) {
        super();
        this.stationName = stationName;
    }

    public AgentAccess(String stationName, String dbName) {
        super(dbName);
        this.stationName = stationName;
    }

    @Override
    public void create(Instance instance) throws OperationException {
        final String SQL = "INSERT INTO INSTANCE (placeName, name, organisation, hashCode, majorVersion, minorVersion, description, creation, state, packageFile, className, parameters, stationName) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection != null
                ? connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)
                : null) {
            if (statement == null) {
                
                throw new OperationException(NULL_STATEMENT);
            }
            
            statement.setString(1, instance.getPlaceName());
            statement.setString(2, instance.getIdentity().getName());
            statement.setString(3, instance.getIdentity().getOrganisation());
            statement.setString(4, instance.getIdentity().getHashCode());
            statement.setInt(5, instance.getIdentity().getMajorVersion());
            statement.setInt(6, instance.getIdentity().getMinorVersion());
            statement.setString(7, instance.getIdentity().getDescription());
            statement.setString(8, instance.getCreation());
            statement.setString(9, instance.getState().toString());
            statement.setString(10, instance.getIdentity().getPackageFile());
            statement.setString(11, instance.getIdentity().getClassName());
            statement.setString(12, instance.getCommaSeparatedParameters());
            statement.setString(13, stationName);

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 1) {
                ResultSet keySet = statement.getGeneratedKeys();
                if (keySet.next()) {
                    instance.setSid(keySet.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new OperationException(e);
        }
    }

    @Override
    public List<Instance> readList() throws OperationException {
        List<Instance> results = new ArrayList<>();
        final String SQL = "SELECT * FROM INSTANCE WHERE stationName = ? ORDER BY sid ASC";

        try (PreparedStatement statement = connection != null ? connection.prepareStatement(SQL) : null) {
            if (statement == null) {
                
                throw new OperationException(NULL_STATEMENT);
            }
            
            statement.setString(1, stationName);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                results.add(getInstance(rs));
            }
        } catch (SQLException e) {
            throw new OperationException(e);
        }

        return results;
    }

    @Override
    public Instance read(Instance instance) throws OperationException {
        final String SQL = "SELECT * FROM INSTANCE WHERE " + WHERE_UNIQUE_KEY;
        try (PreparedStatement statement = connection != null ? connection.prepareStatement(SQL) : null) {
            if (statement == null) {
                
                throw new OperationException(NULL_STATEMENT);
            }
            
            whereUniqueKeyStatement(statement, instance);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {

                return getInstance(rs);
            }
        } catch (SQLException e) {
            throw new OperationException(e);
        }

        return null;
    }

    @Override
    public void update(Instance instance) throws OperationException {
        final String SQL = "UPDATE INSTANCE SET parameters = ?, state = ? WHERE " + WHERE_UNIQUE_KEY;
        String params = instance.getParameters() != null && instance.getParameters().length > 0
                ? instance.getCommaSeparatedParameters()
                : null;

        try (PreparedStatement statement = connection != null ? connection.prepareStatement(SQL) : null) {
            if (statement == null) {
                
                throw new OperationException(NULL_STATEMENT);
            }
            
            statement.setString(1, params);
            statement.setString(2, instance.getState().toString());
            whereUniqueKeyStatement(statement, instance, 3);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new OperationException(e);
        }
    }

    @Override
    public void delete(Instance instance) throws OperationException {
        final String SQL = "DELETE FROM INSTANCE WHERE " + WHERE_UNIQUE_KEY;

        try (PreparedStatement statement = connection != null ? connection.prepareStatement(SQL) : null) {
            if (statement == null) {
                
                throw new OperationException(NULL_STATEMENT);
            }
            
            whereUniqueKeyStatement(statement, instance);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new OperationException(e);
        }
    }

    @Override
    public boolean exists(Instance instance) throws OperationException {
        final String SQL = "SELECT name FROM INSTANCE WHERE " + WHERE_UNIQUE_KEY;

        try (PreparedStatement statement = connection != null ? connection.prepareStatement(SQL) : null) {
            if (statement == null) {
                
                throw new OperationException(NULL_STATEMENT);
            }
            
            whereUniqueKeyStatement(statement, instance);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {

                return true;
            }
        } catch (SQLException e) {
            throw new OperationException(e);
        }

        return false;
    }

    public List<Instance> readByPackageFile(String packageFile) throws OperationException {
        List<Instance> results = new ArrayList<>();
        final String SQL = "SELECT * FROM INSTANCE WHERE packageFile = ? AND stationName = ? ORDER BY sid ASC";

        try (PreparedStatement statement = connection != null ? connection.prepareStatement(SQL) : null) {
            if (statement == null) {
                
                throw new OperationException(NULL_STATEMENT);
            }
            
            statement.setString(1, packageFile);
            statement.setString(2, stationName);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                results.add(getInstance(rs));
            }
        } catch (SQLException e) {
            throw new OperationException(e);
        }

        return results;
    }

    public List<Instance> readByPlaceName(String placeName) throws OperationException {
        List<Instance> results = new ArrayList<>();
        final String SQL = "SELECT * FROM INSTANCE WHERE placeName = ? AND stationName = ? ORDER BY sid ASC";

        try (PreparedStatement statement = connection != null ? connection.prepareStatement(SQL) : null) {
            if (statement == null) {
                
                throw new OperationException(NULL_STATEMENT);
            }
            
            statement.setString(1, placeName);
            statement.setString(2, stationName);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                results.add(getInstance(rs));
            }
        } catch (SQLException e) {
            throw new OperationException(e);
        }

        return results;
    }

    @Override
    public void createSchema() throws OperationException {
        createSchema(CREATE_INSTANCE_SQL);
    }

    private void whereUniqueKeyStatement(final PreparedStatement statement, Instance instance) throws SQLException {
        this.whereUniqueKeyStatement(statement, instance, 1);
    }

    private void whereUniqueKeyStatement(final PreparedStatement statement, Instance instance, int start)
            throws SQLException {
        statement.setString(start++, instance.getIdentity().getClassName());
        statement.setString(start++, instance.getIdentity().getHashCode());
        statement.setInt(start++, instance.getIdentity().getMajorVersion());
        statement.setInt(start++, instance.getIdentity().getMinorVersion());
        statement.setString(start++, instance.getCreation());
        statement.setString(start, stationName);
    }

    private Instance getInstance(ResultSet rs) throws OperationException {
        try {
            Identity identity = new Identity.IdentityBuilder(rs.getString(CLASS_NAME), rs.getString(ORGANISATION))
                    .hashCode(rs.getString(HASH_CODE)).packageFile(rs.getString(PACKAGE_FILE))
                    .version(rs.getInt(MAJOR_VERSION), rs.getInt(MINOR_VERSION)).description(rs.getString(DESCRIPTION))
                    .build();
            String params = rs.getString(PARAMETERS);
            String[] outParams = params != null ? params.split(",") : null;
            Instance result = new Instance(identity, LocalDateTime.parse(rs.getString(CREATION), App.DATETIME_FORMATTER),
                    rs.getString(PLACE_NAME), outParams);
            result.setState(Instance.State.valueOf(rs.getString(STATE)));
            result.setSid(rs.getLong("sid"));

            return result;
        } catch (SQLException e) {
            throw new OperationException(e);
        }
    }

}
