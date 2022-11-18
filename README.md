Agent Station
=============
The Agent Station project provides a modern environment for static and mobile software agents. Also, provides a client api that allows you to create your own agents. In simple terms, a software agent carries out tasks on behalf of a user. In order to carry out its tasks, an agent can collaborate with other local agents or migrate to a remote Agent Station. See [TERMS.md](TERMS.md) for the terminology used. Agent Station is based on the engineering research I did between 1998-2003. This research led to my PhD in 2003 from the University of Surrey, UK.
 [Dr Christos Bohoris](https://www.connectina.co.uk/about) | [PhD Thesis](https://www.connectina.co.uk/doc/cbohoris-phdthesis-unis-2003.pdf)

Visit the [Project Site](https://www.connectina.co.uk/agent-station) to download the latest, stable release packages.

---

## Features
- Supports both mobile and static software agents
- Based on secure [gRPC](https://grpc.io/) (over HTTP/2 with SSL, TLS) or plain Java-RMI (JRMP) remote communications
- Supports collaboration between agents with messages in JSON format
- Schedule an agent to carry out a task (once or repeatedly)
- Permission-based mobile agent migration to remote Agent Stations
- Specified permissions determine whether an agent is allowed in the environment
- Jar files containing agents are verified against known MD5 hash code
- Offers two user interface alternatives, a GUI or an interactive TUI
- Runs on any OS supporting Java 17 LTS
- For a detailed list of features see [REQS.md](REQS.md)

<img src="https://www.connectina.co.uk/agentstation/agent-station-main-frame.png" alt="Agent Station Main Frame" /> 

---

## Buy me a coffee

Whether you use this project, have learned something from it, or just like it, please consider supporting it by buying me a coffee, so I can dedicate more time to it :)

<a href="https://www.buymeacoffee.com/faocrates" target="_blank"><img src="https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png" alt="Buy Me A Coffee" style="height: 60px !important;width: 217px !important;" ></a>

---

## Setup
- The application runs on Java 17. The recommended way of installing Java 17 is through [SDKMan](https://sdkman.io/)
- Confirm that Java 17 is available on your system by opening a Terminal and typing: `java --version`
- Select a suitable application folder and extract the contents of the distribution package (agent-station-a.b.c.zip or agent-station-a.b.c.tar).

---

## Usage
The simplest way to start an Agent Station environment is as follows:
- Open a Terminal and navigate to the `agent-station-{version}/bin` folder,
- On Linux-based systems type: `./agent-station --server=IP-ADDRESS/NODE-NAME` e.g. `./agent-station --server=192.168.0.78`
- On Windows systems type: `.\agent-station.bat --server=IP-ADDRESS/NODE-NAME` e.g. `.\agent-station.bat --server=192.168.0.78`
This starts an Agent Station with all the defaults applied (i.e. GUI interface, based on plain Java-RMI, port 1099, etc). For Help on all optional parameters available, type `./agent-station` on Linux or `.\agent-station.bat` on Windows
- Use the optional `--ui=tui` parameter if you want to start the Agent Station with an interactive TUI

Use of gRPC depends on valid client and server certificates that need to be generated and to be made available. In order to generate the certificates on Linux:
- Open a Terminal and navigate to the `library/GenCerts` folder,
- Modify the random digits in the ca-cert.srl file
- Modify the `client-ext.cnf` and `server-ext.cnf` files with the IP addresses or names of any node in your network hosting an Agent Station
- Modify the `gen-certs.sh` script with the certificate details that represent you
- Run `./gen-certs.sh` to generate the .pem certificate/key files
- Run `./copy-certs.sh` to copy the .pem certificate/key files so that they are available for Agent Station (`~/.AgentStation/certificates`)
- The simplest way to start an Agent Station environment that is based on gRPC is: `./agent-station --remote=grpc --server=IP-ADDRESS/NODE-NAME`

---

## Example Agents
When starting with Agent Station it is recommended to try out the agent examples in the order below:
- Hello: A simple, static agent that logs a greeting message for the current OS user. This is the most basic of examples that demonstrates an agent that starts, carries out a very simple task and then stops. 
- Monitor: Two collaborating agents, one provides JVM memory data, the other gets the data, calculates moving averages and reports them in a graph for the user. This example demonstrates how an agent can collaborate with another in order to carry out its task. 
- DiskInfo: A mobile agent that migrates to a remote Agent Station, gets disk space-related information from that node and finally returns to the origin Agent Station to report the information gathered. This example demonstrates how you can implement a mobile agent that migrates and operates in a remote Agent Station.
- AptUpdate: A mobile agent that migrates to a remote Agent Station running on a Linux system managed by apt. The agent uses apt to update the remote OS and returns to the origin Agent Station to report on the update performed. This example demonstrates a mobile agent performing a routine task on a remote node on behalf of a user. The agent can also be scheduled to carry out the update on a repeated, set day interval to regularly update in an automated manner.

### Importing
- You can import an agent using the starter property files located in `library/AgentPropertyFiles`
- Open a property file and modify `packageLocation` to correspond to the location of the agent examples jar
- Modify any agent `parameters` to your needs. For the DiskInfo and AptUpdate agents the values should correspond to the DESTINATION-IP-ADDRESS,DESTINATION-PORT, e.g. `parameters=192.168.0.29,1099`
- The hash code corresponds to the MD5 hash code for the jar. e.g. It is the code returned when running the following command on Linux: `md5sum agent-station-example-agents-{version}.jar`
- Once you have finished modifying the property files, on Linux run: `./copy-props.sh`. This copies the property files to the default folder, `~/.AgentStation/properties`

---

## The Interactive TUI
The simplest way to start an Agent Station with the interactive TUI is using a command like: `./agent-station --ui=tui --server=IP-ADDRESS/NODE-NAME`:
- Type 'help<Enter>' to get a list of available commands
Here are some examples of commands,
- Type 'list places<Enter>'. This displays the list of available places
- Type 'create agent HelloUserAgent<Enter>'. This will create this agent assuming its property file exists in the default location i.e. in `~/.AgentStation/properties/HelloUserAgent.properties`
- Type 'list agents<Enter>'. This displays the list of running agents e.g. 1. Agent HelloUserAgent Id=840201, Default: INACTIVE
- Lets assume the above example where HelloUserAgent is displayed with list index 1. Type 'start agent 1<Enter>'. The agent starts and carries out its task
- Continuing with this example, type 'remove agent 1<Enter>'. This removes the agent which you can verify with 'list agents<Enter>'

---

## License
This project is licensed under the terms of the GNU General Public License version 3 or any later version. See [LICENSE.md](LICENSE.md) for the
full license text.
