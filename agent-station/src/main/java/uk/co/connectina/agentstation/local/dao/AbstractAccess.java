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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uk.co.connectina.agentstation.api.client.OperationException;

/**
 * Abstract class containing common aspects amongst specific DAOs.  
 * 
 * @author Dr Christos Bohoris
 */
abstract class AbstractAccess {

    protected static final String NULL_STATEMENT = "Null SQL statement";
    protected static final String NOT_SUPPORTED = "Not supported.";
    private static final Logger LOGGER = LogManager.getLogger(AbstractAccess.class.toString());
    protected Connection connection;

    AbstractAccess() {
        this(Access.DB_NAME);
    }

    AbstractAccess(String dbName) {
        connection = getConnection(dbName);
    }

    protected void createSchema(String sql) throws OperationException {
        try (Statement statement = connection != null ? connection.createStatement() : null) {
            if (statement == null) {

                throw new OperationException(NULL_STATEMENT);
            }

            statement.executeUpdate(sql);
        } catch (SQLException e) {
            LOGGER.error(e);
        }
    }

    private Connection getConnection(String dbName) {    
        if (connection == null) {
            try {
                final String DB_URL = MessageFormat
                        .format("jdbc:h2:~/.AgentStation/db/{0};AUTOCOMMIT=ON;AUTO_SERVER=TRUE", dbName);
                connection = DriverManager.getConnection(DB_URL);

            } catch (SQLException e) {
                LOGGER.error(e);
            }
        }

        return connection;
    }

}
