package com.uom.dist.node.service;

import com.google.gson.Gson;
import com.uom.dist.node.service.domain.ConnectedNode;
import com.uom.dist.protocol.PrintResponse;
import com.uom.dist.protocol.SearchRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RoutingService {

    @Autowired
    private Node node;

    private ArrayList<ConnectedNode> routingTable = new ArrayList<>();
    private static final Logger logger = LoggerFactory.getLogger(RoutingService.class);

    public boolean insertIntoNodeList(String url, int port) {
        ConnectedNode connectedNode = new ConnectedNode(url, port);
        if (!routingTable.contains(connectedNode)) {
            logger.debug("Inserting into routing table url : [{}], port : [{}]"
                    , connectedNode.getNodeIp(), connectedNode.getNodePort());
            boolean add = routingTable.add(connectedNode);
            if (add) {
                logger.debug("Inserting into routing table success [{}]", getRoutingTableValues());
            } else {
                logger.error("Inserting into routing table failed [{}]", getRoutingTableValues());
            }
            return add;
        } else {
            logger.error("Node is already available in the routing table");
            return false;
        }
    }

    public boolean removeFromRoutingTable(ConnectedNode connectedNode) {
        if (routingTable.contains(connectedNode)) {
            logger.debug("Removing from routing table url : [{}], port : [{}]"
                    , connectedNode.getNodeIp(), connectedNode.getNodePort());
            boolean remove = routingTable.remove(connectedNode);
            if (remove) {
                logger.debug("Removing from routing table success [{}]", getRoutingTableValues());
            } else {
                logger.error("Removing from routing table failed [{}]", getRoutingTableValues());
            }
            return remove;
        } else {
            logger.debug("Node is not available in routing table");
            return true;
        }
    }

    public void removeFromNodeList(String url, int port) throws Exception {
        ConnectedNode connectedNode = new ConnectedNode(url, port);
        if (routingTable.contains(connectedNode)) {
            logger.debug("Removing the node from routing table, url: [{}], port: [{}]", connectedNode.getNodeIp(), connectedNode.getNodePort());
            routingTable.remove(connectedNode);
            logger.debug("Node removed successfully [{}]", getRoutingTableValues());
        } else {
            logger.error("Node already left from the routing table");
            throw new Exception("Node already left from the routing table");
        }
    }

    public List<ConnectedNode> getRoutingTable() {
        return routingTable;
    }

    public void handlePrint(String url, int port) {
        String routingTableString = new Gson().toJson(routingTable);
        logger.debug("Routing table : [{}]", routingTableString);
        System.out.println(routingTableString);
        PrintResponse response = new PrintResponse(routingTableString);
        node.send(response, url, port);
    }

    public String getRoutingTableValues() {
        return new Gson().toJson(routingTable);
    }

    public void sendSearchRequests(SearchRequest searchRequest) {
        if (routingTable.isEmpty()) {
            logger.debug("No neighbours in the routing table");
        }

        routingTable.forEach(connectedNode -> {
            SearchRequest request = new SearchRequest(searchRequest.getIpAddress(),
                    searchRequest.getPort(),
                    searchRequest.getFileName(),
                    searchRequest.getHops() + 1);
            logger.debug("Sending search request");
            node.send(request, connectedNode.getNodeIp(), connectedNode.getNodePort());
        });
    }
}