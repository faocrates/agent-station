# High Level Requirements

## 1. Add Place 
As a User I want to create a Place, so that I keep Permissions and Agents well organised. 

**Rules:** 
 - 1.1 I cannot create a Place using the name of an existing place. 
 - 1.2 The new Place will have no Agents and Permissions.

## 2. Remove Place
As a User I want to remove a Place, so that I change the way Permissions and Agents are organised when my preferences change. 

**Rules:**
- 2.1 The Place cannot be the Default. 
- 2.2 The Place must not have any Agents.

## 3. Manage Permissions
As a User I want to manage Permissions, so that I am in control of which Agent is allowed and how within the Place.

**Rules:** 
- 3.1 A Permission is unique by agent name and id within the Place. 
- 3.2 The User cannot have multiples of the same Permission.
- 3.3. Changing a Permission to 'not allowed' while this Agent resides in the Place causes the Agent to be removed.

## 4. Add Agent
- As a User I want to create an Agent, so that it can operate within the Agent Station.

**Rules:** 
- 4.1 I can have multiple instances of the same Agent in the same Place.
- 4.2 I can create an Agent by manually entering required information.
- 4.3 I can create an Agent by importing the required information from a Property File.
- 4.4 When an Agent is created, a log message appears with the Agent description.
- 4.5 When an Agent is created, a log message appears with the Agent start parameters that apply, if there are any.

## 5. Agent Migration
- As a User I want to migrate an Agent, so that it can operate within a remote Agent Station.

**Rules:** 
- 5.1 The origin and destination Agent System need to support the same remote communication technology (i.e. utilising either gRPC or RMI).
- 5.2 The origin Agent System contacts the destination Agent System to check whether the Agent is allowed. The Agent is migrated only if there is already a permission that allows it to reside in the destination Agent System Place.

## 6. Agent Collaboration
- As an Agent I want to collaborate with another Agent, so that I can complete complex tasks.

**Rules:** 
- 6.1 An Agent can collaborate with any other active Agent present in the Place they reside.
- 6.2 The recommended format for messages exchanged between Agents is JSON. Support for converting between JSON and JavaBean objects exists within the Agent Station Client API (see `JSONConverter`).

## 7. Start Agent
- As a User I want to start an Agent, so that it carries out a task for me.

**Rules:** 
- 7.1 An Agent can be started (i.e. its state becomes Active) if it's already in a Place as Inactive.

## 8. Stop Agent
- As a User I want to stop an Agent, so that it's not actively consuming resources while it's not used.

**Rules:** 
- 8.1 An Agent can be stopped (i.e. its state becomes Inactive) if it's already in a Place as Active.

## 9. Remove Agent
- As a User I want to remove an Agent, so that it's not consuming any resources while I do not want it to operate.

**Rules:** 
- 9.1 An Agent can be removed if it's already in a Place. If it is currently Active the Agent is stopped before it's removed.
- 9.2 An Agent can request its removal when all its tasks are completed.

## 10. Agent Logs
- As a User I want to read Agent logs, so that I'm informed about the Agent's processes and status.

**Rules:** 
- 10.1 The more recent Agent log message appears at the top.
- 10.2 Only the 50 most recent log messages appear in the Graphical User Interface.

## 11. Agent Information
- As a User I want to have Agent information, so that I'm informed about its details.

**Rules:** 
- 11.1 All the known details for an Agent are displayed in the Graphical User Interface.

## 12. Change Agent
- As a User I want to make changes to an agent, so that it's operation changes according to my intentions.

**Rules:** 
- 12.1 A message in the Agent logs confirms the change made.
- 12.2 The User can change the parameters used when the Agent starts.
- 12.3 The User can schedule the Agent to start at a time in the future.

## 13. Agent System Logs
- As a User I want to read Agent System logs, so that I'm informed about the Agent System's processes.

**Rules:** 
- 13.1 The more recent Agent Station log message appears at the top.
- 13.2 Only the 200 most recent log messages appear in the Graphical User Interface.

## 14. Agent System Information
- As a User I want to have Agent Station information, so that I'm informed about its details.

**Rules:** 
- 14.1 All the known details for the Agent System are displayed in the Graphical User Interface.

## 15. Non-Functional Characteristics
- 15.1 The Agent Station project provides to Users (a) a runtime enviroment for static and mobile software Agents and (b) a Client API allowing the development of software Agents.
- 15.2 Both a Graphical User Interface and a Textual User Interface are provided.
- 15.3 gRPC communication is provided over a secure, encyrpted channel where both the client and the server have successfully provided valid certificates (SSL/TLS).
- 15.4 The use of gRPC is recommended for Production environments.
- 15.5 RMI communication is provided over a plain text, insecure connection.
- 15.6 The use of RMI is recommended for Development or QA environments where added security is not required. It allows for an easier environment setup during tasks like development, prototyping or verification.
- 15.7 Any developed Agents need to be packaged in a "fat .jar" that contains the Agent(s) along with all required dependencies.
- 15.8 Agents are developed using the Agent System Client API provided in the Java package: `uk.co.connectina.agentstation.api.client`.
- 15.9 Agent System logging is provided in a daily log file (.log). Past log files are preserved for 30 days in a compressed fle format (.gz).
- 15.10 The application environment utilises a folder named '.AgentSystem' in the user's home folder. The following sub-folders are present:
   - certificates: Location for the client and server certificate and key files (.pem).
   - db: Location of the H2 database.
   - logs: Location of the log files (current .log and archive .gz).
   - packages: Location of .jar package files (containing Agents) that were added to the Agent System runtime environment.
   - processes: Location of lock files indicating a running Agent System process.
   - properties: Default location for any Property Files that allow an agent to be imported (see 4.3).
- 15.11: An example of a valid Property File used to import an Agent appears below. The recommended convention for the property file name is `{Agent Name}.properties`.

```
className=uk.co.connectina.example.MemoryMonitorAgent
organisation=connectina.co.uk
hashCode=129a0c115f04f7de1b7aa2ffc08401bc
majorVersion=1
minorVersion=0
packageLocation=/home/adam/Documents/Projects/Examples/example-1.0.jar
Description=Monitors used or available memory within a JVM
parameters=usedMemory,192.168.0.11,1099
placeName=Default
allowed=true
autoStart=false
```
- 15.12: A jar package file containing an agent is removed from `~/.AgentStation/packages` when there are no running Agent instances from it in the agent station. This relates to the cases of Agent removal and migration.
