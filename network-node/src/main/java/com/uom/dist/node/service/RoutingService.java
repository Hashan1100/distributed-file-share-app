package com.uom.dist.node.service;

import com.google.gson.Gson;
import com.uom.dist.node.service.domain.ConnectedNode;
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

    public void insertIntoNodeList(String url, int port) throws Exception {
        ConnectedNode connectedNode = new ConnectedNode(url, port);
        if (!routingTable.contains(connectedNode)) {
            logger.debug("Inserting into routing table url : [{}], port : [{}]"
                    , connectedNode.getNodeIp(), connectedNode.getNodePort());
            routingTable.add(connectedNode);
            logger.debug("Inserting into routing table success [{}]", getRoutingTableValues());
        } else {
            logger.error("Node is already available in the routing table");
            throw new Exception("Node is already connected");
        }
    }

    public List<ConnectedNode> getRoutingTable() {
        return routingTable;
    }

    public String getRoutingTableValues() {
        return new Gson().toJson(routingTable);
    }

    public void sendSearchRequests(SearchRequest searchRequest) {
        routingTable.forEach(connectedNode -> {
            SearchRequest request = new SearchRequest(connectedNode.getNodeIp(),
                    connectedNode.getNodePort() + "",
                    searchRequest.getFileName(),
                    searchRequest.getHops() + 1);
            logger.debug("Sending search request");
            node.send(request, connectedNode.getNodeIp(), connectedNode.getNodePort());
        });
    }
}