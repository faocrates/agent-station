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

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import uk.co.connectina.agentstation.api.client.OperationException;

/**
 * Tests for the IOAccess class.
 *
 * @author Dr Christos Bohoris
 */
class IOAccessTest {

    public static final String HOME_PATH_AGENTS_JAR = "/home/path/agents.jar";

    @Test
    void getServerAndPortText() {
        String server = "192.168.0.1";
        int port = 1099;
        Assertions.assertEquals("192.168.0.1:1099", IOAccess.getServerAndPortText(server, port));
    }

    @Test
    void toByteArray() {
        try {
            byte[] bytes = IOAccess.toByteArray("Hello");
            Assertions.assertEquals(-2039882882, new String(bytes).hashCode());
        } catch (OperationException e) {
            Assertions.fail(e);
        }
    }

    @Test
    void fromPathToURL() {
        try {
            String url = IOAccess.fromPathToURL(HOME_PATH_AGENTS_JAR);
            Assertions.assertEquals("file:/home/path/agents.jar", url);
        } catch (OperationException e) {
            Assertions.fail(e);
        }
    }

    @Test
    void getStringMD5() {
        String md5 = IOAccess.getStringMD5("Hello");
        Assertions.assertEquals("8b1a9953c4611296a827abf8c47804d7", md5);
    }

    @Test
    void getLocalPackageLocation() {
        String loc = IOAccess.getLocalPackageLocation(HOME_PATH_AGENTS_JAR);
        Assertions.assertEquals(System.getProperty("user.home") + "/.AgentStation/packages/agents.jar", loc);
    }

    @Test
    void getURLFromLocation() {
        URL loc = null;
        try {
            loc = IOAccess.getURLFromLocation(HOME_PATH_AGENTS_JAR);
            Assertions.assertEquals("file:/home/path/agents.jar", loc.toExternalForm());
        } catch (MalformedURLException e) {
            Assertions.fail(e);
        }
    }

    @Test
    void getAboutPlace() {
        String about = IOAccess.getAboutPlace("Test");
        Assertions.assertEquals("Test Place", about);
    }

}