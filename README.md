# distributed-file-share-app

## How to get the project
`git clone https://github.com/Hashan1100/distributed-file-share-app.git`

## Specification
* Java version : openjdk 11
* maven : 3.6 (> 3.2)
* preferred IDE : intellij idea

## How to build
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

## How to get multiple nodes

* Build the application
* Go to ```/target/network-node/conf/application.properties```
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