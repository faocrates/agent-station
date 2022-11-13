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
package uk.co.connectina.agentstation.api.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Converts from JSON to a JavaBean Object and vice versa.
 *
 * @author Dr Christos Bohoris
 * @param <T> the targeted class
 */
public class JSONConverter<T> {

    private final Class<T> classType;

    /**
     * Initiates a new object instance.
     * 
     * @param classType the targeted class type
     */
    public JSONConverter(Class<T> classType) {
        this.classType = classType;
    }

    /**
     * Converts from JSON to a JavaBean Object.
     * 
     * @param json the JSON content
     * @return the resulting object
     * @throws OperationException an error occurred
     */
    public T jsonToObject(String json) throws OperationException {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.readValue(json.getBytes(), classType);
        } catch (IOException e) {
            throw new OperationException(e);
        }
    }

    /**
     * Converts from a JavaBean Object to JSON.
     * 
     * @param object the input object
     * @return the JSON content
     * @throws OperationException an error occurred
     */
    public String objectToJson(T object) throws OperationException {
        
        return objectToPrettyFormatJson(object, false);
    }
    
    /**
     * Converts from Object to JSON with pretty format.
     * 
     * @param object the input object
     * @return the JSON content
     * @throws OperationException an error occurred
     */
    public String objectToPrettyFormatJson(T object) throws OperationException {
        
        return objectToPrettyFormatJson(object, true);
    }
    
    private String objectToPrettyFormatJson(T object, boolean prettyPrint) throws OperationException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, prettyPrint);
        
        StringWriter stringWriter = new StringWriter();
        try {
            objectMapper.writeValue(stringWriter, object);
            
            return stringWriter.toString();
        } catch (IOException e) {
            throw new OperationException(e);
        }
    }

}
