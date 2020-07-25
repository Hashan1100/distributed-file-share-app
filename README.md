# distributed-file-share-app

## How to get the project
`git clone https://github.com/Hashan1100/distributed-file-share-app.git`

## Specification
* Java version : openjdk 11
* maven : 3.6 (> 3.2)
* preferred IDE : intellij idea

## How to build (Before running nodes please read all readme instructions)
* Go to the project root and run
`mvn clean install -DskipTest`
* Build will appear in target folder
* Go the `target/<module-name>/bin` and run
`<./module-name console>`

## Print routing table via telnet using nc (For linux systems)
* Connect to node using following command
```$xslt
nc -u <node-ip> <port>
```
* Enter command ```PRINT``` and press Enter

## How to test file search (In linux)

```$xslt
echo <request_length> SER <node_port> <node_ip> \"<file_name>\" 0 > /dev/udp/<node_ip>/<node_port>
```
* Result file urls will be printed in <node_port> <node_ip> is debug log.

## How to build and bootstrap server
* Build the application.
* Bootstrap server will be in ```target/bootstrap-server/```
* Change the following values accordingly
```$xslt
server.port=
udp.receiver.port=
```
* server.port and udp.receiver.port need to be different
* Bootstrap server actually starts in udp.receiver.port
* Run the application according the Operating system using files in ```target/bootstrap-server/bin/```

## Running deployment script to get multiple nodes

* This script will build the project and will generate multiple nodes by coping ```~/network-node/target/network-node```
* Deployment script available in ```deploy.sh```
* Change ```node_count``` variable to node count you need.
* Following bash arrays will set manual config values 
```
     serverPorts=()
     udpPorts=()
     udpUrls=()
     serverUserNames=()
```
```serverPorts``` is for spring boot applications ports
```udpPorts``` is for nodes ports
```udpUrls``` is for nodes urls
```serverUserNames``` is for nodes user names

ex :

```  
     serverPorts=(9601 9602 9603 9604)
     udpPorts=(20001 20002 20003 20004)
     udpUrls=("192.9.8.1" "192.9.8.2" "192.9.8.3" "192.9.8.4")
     serverUserNames=("node_user_name_1" "node_user_name_2" "node_user_name_3" "node_user_name_4")
```

* If manual arrays are not set script will generate default values
* Each array element count should match the ```node_count```. If not script will again generate default configuration values
* Once script is completed result will appear in ```release/nodes```.

## How Change node configurations manually after deployment

* Go to ```/network-node/conf/application.properties```
* Change following parameters accordingly
```$xslt
server.port=
node.username=
udp.receiver.port=
udp.receiver.url=
file.list=
```
* Each node should have **unique udp.receiver.port or udp.receiver.url or both and unique user name.**
* server.port and udp.receiver.port need to be different
* udp.receiver.port is the actual node port 
* Node will be in ```target/network-node/```
* Run the application according the Operating system using files in ```target/network-node/bin/```

## How to use the shell in nodes

* Once node is started in console mode shell will appear in console.
* Type ```help``` and press enter to get command details.
* If you exit from the shell node need to be restarted to use the shell again.  