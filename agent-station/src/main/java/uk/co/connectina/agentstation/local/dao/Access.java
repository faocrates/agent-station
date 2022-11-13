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
import uk.co.connectina.agentstation.api.client.OperationException;

/**
 * Basic CRUD access interface for DAOs.
 *
 * @author Dr Christos Bohoris
 */
public interface Access<T, K> {

    final String DB_NAME = "agentstation";

    /**
     * Creates an entry in the db.
     * 
     * @param t the persistent object
     * @throws OperationException an error occurred
     */
    void create(T t) throws OperationException;

    /**
     * Reads all entries for this Agent Station from the db
     * 
     * @return all entries
     * @throws OperationException an error occurred
     */
    List<T> readList() throws OperationException;

    /**
     * Reads an entry from the db based on the given key.
     * 
     * @param k the key
     * @return the persistent object
     * @throws OperationException an error occurred
     */
    T read(K k) throws OperationException;

    /**
     * Updates an entry in the db.
     * 
     * @param t the persistent object
     * @throws OperationException an error occurred
     */
    void update(T t) throws OperationException;

    /**
     * Deletes an entry from the db based on the given key.
     * 
     * @param k the key
     * @throws OperationException an error occurred
     */
    void delete(K k) throws OperationException;

    /**
     * Returns whether an entry exists in the db based on the given key.
     * 
     * @param k the key
     * @throws OperationException an error occurred
     */
    boolean exists(K k) throws OperationException;

    /**
     * Creates the table for this persistent object.
     * 
     * @throws OperationException an error occurred
     */
    void createSchema() throws OperationException;

}
