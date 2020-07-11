package com.uom.dist.node.service;

import com.uom.dist.node.service.domain.ConnectedNode;
import com.uom.dist.protocol.JoinRequest;
import com.uom.dist.protocol.JoinResponse;
import com.uom.dist.protocol.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class JoinService {

    @Autowired
    private Node server;

    @Value("${udp.receiver.url}")
    private String url;

    @Value("${udp.receiver.port}")
    private int port;

    @Autowired
    private RoutingService routingService;

    private static final Logger logger = LoggerFactory.getLogger(JoinService.class);

    private static final String JOIN_ERROR_CODE = "9999";
    private static final String JOIN_SUCCESS_CODE = "0";

    public void join(ConnectedNode connectedNode) {
        Protocol joinRequest = new JoinRequest(url, port + "");
        server.send(joinRequest, connectedNode.getNodeIp(), connectedNode.getNodePort());
    }

    public void handleJoin(JoinRequest joinRequest) {
        if (joinRequest.getIpAddress() != null && joinRequest.getPort() != null) {
            try {
                boolean result = routingService.insertIntoNodeList(joinRequest.getIpAddress(), Integer.parseInt(joinRequest.getPort()));
                if(result) {
                    sendJoinResponse(joinRequest.getIpAddress(), joinRequest.getPort(), JOIN_SUCCESS_CODE);
                } else {
                    sendJoinResponse(joinRequest.getIpAddress(), joinRequest.getPort(), JOIN_ERROR_CODE);
                }
            } catch (Exception e) {
                logger.error("Join failed for join request [{}]", joinRequest.serialize());
                sendJoinResponse(joinRequest.getIpAddress(), joinRequest.getPort(), JOIN_ERROR_CODE);
            }
        } else {
            logger.error("Ip address or port is null in the join request [{}]", joinRequest.serialize());
            sendJoinResponse(joinRequest.getIpAddress(), joinRequest.getPort(), JOIN_ERROR_CODE);
        }
    }

    public void sendJoinResponse(String ipAddress, String port, String status) {
        JoinResponse joinResponse = new JoinResponse(status);
        server.send(joinResponse, ipAddress, Integer.parseInt(port));
    }

    public void handleJoinResponse(JoinResponse joinResponse, String ipAddress, int port) {
        logger.debug("Join response received [{}]", joinResponse.serialize());
        if (Objects.equals(joinResponse.getStatus(), JOIN_SUCCESS_CODE)) {
            logger.debug("Join successful ip : [{}] port : [{}]", ipAddress, port);
        } else if (Objects.equals(joinResponse.getStatus(), JOIN_ERROR_CODE)) {
            logger.error("Join failed ip : [{}] port : [{}] removing from routing table", ipAddress, port);
            if (ipAddress != null) {
                ConnectedNode connectedNode = new ConnectedNode(ipAddress, port);
                routingService.removeFromRoutingTable(connectedNode);
            } else {
                logger.error("Ip address or port is null so can't identify node");
            }
        }
    }
}
