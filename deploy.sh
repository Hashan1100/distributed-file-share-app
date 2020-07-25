#!/bin/bash

rm -rf release

default=false
node_count=15

# Manually configure nodes params. element count in each array need to match node_count. other will use the default generated values
serverPorts=()
udpPorts=()
udpUrls=()
serverUserNames=()

echo ${#serverPorts[@]}

if [ ${#serverPorts[@]} != $node_count ] || [ ${#udpPorts[@]} != $node_count ] || [ ${#udpUrls[@]} != $node_count  ] || [ ${#serverUserNames[@]} != $node_count ];
then
  echo "Array sizes doesn't match the node count using generated default values"
  default=true
fi
echo $default
#mvn clean install -DskipTests
mkdir -p "release/nodes"
for (( c=1; c<=$node_count; c++ ))
do
   mkdir -p release/nodes/node-$c/network-node-$c
   cp -r network-node/target/network-node/* "release/nodes/node-$c/network-node-$c"
   cd "release/nodes/node-$c/network-node-$c/conf"
   if [ $default != true ]; then
          echo "${serverPorts[($c - 1)]}"
          sed -i "/server.port=9601/c\server.port=${serverPorts[($c - 1)]}" "application.properties"
          sed -i "/node.username=*/c\node.username=${serverUserNames[($c - 1)]}" "application.properties"
          sed -i "/udp.receiver.port=*/c\udp.receiver.port=${udpPorts[($c - 1)]}" "application.properties"
          sed -i "/udp.receiver.url=*/c\udp.receiver.url=${udpUrls[($c - 1)]}" "application.properties"
        else
          sed -i "/server.port=9601/c\server.port=$((9600 + $c))" "application.properties"
          sed -i "/node.username=*/c\node.username=node_$c" "application.properties"
          sed -i "/udp.receiver.port=*/c\udp.receiver.port=$((20000 + $c))" "application.properties"
   fi
   cd ../../
   zip -r "../network-node-$c.zip" "network-node-$c"
   cd ../../../
done