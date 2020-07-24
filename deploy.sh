#!/bin/bash

rm -rf release
node_count=5
mvn clean install -DskipTests
mkdir -p "release/nodes"
for (( c=1; c<=$node_count; c++ ))
do
   echo $c
   mkdir -p release/nodes/node-$c/network-node-$c
   cp -r network-node/target/network-node/* "release/nodes/node-$c/network-node-$c"
   cd "release/nodes/node-$c/network-node-$c/conf"
   sed -i "/server.port=9601/c\server.port=960$c" "application.properties"
   sed -i "/node.username=node_1/c\node.username=node_$c" "application.properties"
   sed -i "/udp.receiver.port=20001/c\udp.receiver.port=2000$c" "application.properties"
   cd ../../
   zip -r "../network-node-$c.zip" "network-node-$c"
   cd ../../../
done

