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
package uk.co.connectina.agentstation.exampleagents.hello;

import java.text.MessageFormat;
import java.time.LocalDateTime;

import uk.co.connectina.agentstation.api.client.LogType;
import uk.co.connectina.agentstation.api.client.StaticAgent;

/**
 * A simple, static agent that logs a greeting message for the current OS user. This is the most basic of examples that demonstrates an
 * agent that starts, carries out a very simple task and then stops. 
 *
 * @author Dr Christos Bohoris
 */
public class HelloUserAgent extends StaticAgent {
    
    @Override
    public void atHomeStation() {
        // Implement atHomeStation behaviour of StaticAgent class
        
        String username = System.getProperty("user.name");
        int hour = LocalDateTime.now().getHour();
        String greeting = "Hello";
        
        if (hour >= 0 && hour < 12) {
            greeting = "Good morning";
        } else if (hour >= 12 && hour < 16) {
            greeting = "Good afternoon";
        } else if (hour >= 16 && hour < 21) {
            greeting = "Good evening";
        } else if (hour >= 21 && hour <= 23) {
            greeting = "Good night";
        }

        stationAssistant.log(agentInstance, LogType.INFO, MessageFormat.format("{0} {1}", greeting, username.toUpperCase()));
    }

}
