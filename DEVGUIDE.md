Agent Development Guide
=======================

## Create a static agent

The static agent code template below can start you up. (Also see the Hello example)

```
public class MyStaticAgent extends StaticAgent {
    
    // Add properties here
    
    @Override
    public void atHomeStation() {
        // Implement atHomeStation behaviour of StaticAgent class
        
        // If the agent takes parameters get them here
        if (agentInstance.getParameters().length != 1) {
            stationAssistant.log(agentInstance, LogType.ERROR, "One comma-separated parameter is expected. DURATION_MINS");

            return;
        }
        int durationMins = agentInstance.getParameters()[0];
        // Log a message
        stationAssistant.log(agentInstance, LogType.INFO, "Parameters successfully assigned");
        
        // Add your code here
    }
    
}
```
## Create a mobile agent

The mobile agent code template below can start you up. (Also see the DiskInfo example)

```
public class MyMobileAgent extends MobileAgent {

    // Add Serializable properties here

    @Override
    public void atHomeStation() {
        // Extend atHomeStation behaviour of MobileAgent class
        super.atHomeStation();
        
        // If the agent takes parameters get them here
        if (agentInstance.getParameters().length != 2) {
            stationAssistant.log(agentInstance, LogType.ERROR, "Two comma-separated parameters are expected. Remote Server,Remote Port");

            return;
        }
        remoteServer = agentInstance.getParameters()[0];
        int remotePort = Integer.parseInt(agentInstance.getParameters()[1]);
        // Log a message
        stationAssistant.log(agentInstance, LogType.INFO, "Parameters successfully assigned");
 
        // Add your code here
    }

    @Override
    public void atRemoteStation() {
        // Extend atRemoteStation behaviour of MobileAgent class
        super.atRemoteStation();
        
        // Add your code here 
    }

    // Only override if the agent returns back to the home station
    @Override
    public void backAtHomeStation() {
        // Extend backAtHomeStation behaviour of MobileAgent class
        super.backAtHomeStation();
        
        // Add your code here
    }

}
```
## Agent migration

This is an example of a mobile agent that migrates to a remote station after operating at the home station. (Also see the DiskInfo example)

```
    @Override
    public void atHomeStation() {
        // ...
         
        try {
            stationAssistant.log(agentInstance, LogType.INFO, MessageFormat.format("Migrating to {0}...", remoteServer));
            // Change agent status for the remote station
            status = Status.AT_REMOTE;
            stationAssistant.migrate(agentInstance, remoteServer, remotePort, Assistant.DEFAULT_PLACE);
        } catch (OperationException e) {
            stationAssistant.log(agentInstance, LogType.ERROR, e.getMessage());
            // On error the status remains at home
            status = Status.AT_HOME;
        }
    }
```

This is an example of a mobile agent that migrates back to the home station after operating at the remote station. (Also see the DiskInfo example)

```
    @Override
    public void atRemoteStation() {
        // ...
        
        try {
            stationAssistant.log(agentInstance, LogType.INFO, MessageFormat.format("Migrating to {0}...", agentInstance.getHomeServer()));
            // Change agent status for the home station
            status = Status.BACK_AT_HOME;
            stationAssistant.migrate(agentInstance, agentInstance.getHomeServer(), agentInstance.getHomePort(), agentInstance.getHomePlace());
        } catch (OperationException e) {
            stationAssistant.log(agentInstance, LogType.ERROR, e.getMessage());
            // On error the status remains at remote
            status = Status.AT_REMOTE;
        }
    }
```

## Agent collaboration

This is an example of an agent that collaborates with a MemoryMonitorAgent. (Also see the Monitor example)

```
    @Override
    public void atHomeStation() {
        // ...

        try {
            // Collect measurements until stopped
            do {
                // Collaborate with the memory usage agent
                Collaboration memoryUsageAgent = stationAssistant.collaborate(agentInstance, "MemoryMonitorAgent", "connectina.co.uk", 1, 0);
                if (memoryUsageAgent == null) {
                    stationAssistant.log(agentInstance, LogType.INFO, "MemoryMonitor agent not available");
                    break;
                }
                
                // Communicate to collect data
                Message response = memoryUsageAgent.communicate(new Message("used-memory", null));
                JSONConverter<MonitoredResource> converter = new JSONConverter<>(MonitoredResource.class);
                MonitoredResource monitoredResource = converter.jsonToObject(response.getData());
                // ...
                 
                // Collect data every SECONDS_DELAY
                sleep(SECONDS_DELAY * 1000L);
            } while (!stopRequest);
        } catch (OperationException e) {
            stationAssistant.log(agentInstance, LogType.ERROR, e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
```

## Wait for agents to communicate

This is an example of an agent that waits for other agents to communicate and collaborate. (Also see the Monitor example)

```
    @Override
    public void atHomeStation() {
        // Implement atHomeStation behaviour of StaticAgent class
        this.stationAssistant.log(this.agentInstance, LogType.INFO, "Waiting for requests ...");
        // Wait for collaboration requests from other agents
        startWaiting();

        this.stationAssistant.log(this.agentInstance, LogType.INFO, "Stopped");
    }
```

## Custom agent stop

This is an example of an agent that extends the default stop. (Also see the Monitor example)

```
    @Override
    public void stop() {
        // Extend stop behaviour of StaticAgent class
        super.stop();
        
        // Stop collecting measurements
        stopRequest = true;
        memoryGraphPanel.disposeDialog();
    }
```

## Agent sleep

This is an example of an agent that does something, sleeps and repeats. (Also see the Monitor example)

```
    @Override
    public void atHomeStation() {
        // ...
        while (!stopRequested) {       
            // Do something
            
            sleep(SECONDS_DELAY * 1000L);
        }    
    }
```

## Property file for agent import

This property file template can start you up.

```
className=
organisation=
hashCode=
majorVersion=
minorVersion=
packageLocation=
Description=
parameters=
placeName=
allowed=
autoStart=
```

Here is a property file example.

```
className=uk.co.connectina.agentstation.exampleagents.monitor.MonitorReportAgent
organisation=connectina.co.uk
hashCode=5c071b18af0190ec70e6b59abe5b79a7
majorVersion=1
minorVersion=0
packageLocation=/home/christos/Documents/AgentStation/agent-station-example-agents/build/libs/agent-station-example-agents-0.1.0.jar
Description=Monitors the local JVM and reports on the values gathered
parameters=10,30,Used Memory,120,true
placeName=Default
allowed=true
autoStart=false
```

Make sure that the jar package contains your agents and all their dependencies. Such a jar is sometimes called a 'fat jar'.  

The hash code corresponds to the MD5 hash code of the jar containing the agent. e.g. It is the code returned when running the following command on Linux: e.g. `md5sum agent-station-example-agents-0.1.0.jar`

Name your property file with the agent name. e.g. MonitorReportAgent.properties

In the GUI, navigate to the location of the property file and import the agent details. For convenience, you can place your agent property file in the default location `~/.AgentStation/properties`

## Sub-projects

`agent-station`: The files implementing the Agent Station environment.

`agent-station-client-api`: The client API you will use to develop your own agents.

`agent-station-example-agents`: Examples of static and mobile agents.

Use Java 17 with all sub-projects.
