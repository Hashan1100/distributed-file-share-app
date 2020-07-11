package com.uom.dist.node.service;

import com.uom.dist.node.service.domain.ConnectedNode;
import com.uom.dist.protocol.LeaveRequest;
import com.uom.dist.protocol.LeaveResponse;
import com.uom.dist.protocol.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class LeaveNodeService {
    @Autowired
    private Node node;

    @Value("${node.username}")
    private String nodeUserName;

    @Value("${udp.receiver.port}")
    private int udpPort;

    @Value("${udp.receiver.url}")
    private String udpUrl;

    @Autowired
    private RoutingService routingService;

    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    public void sendLeaveRequest() {
        String port = Integer.toString(udpPort);
        LeaveRequest leaveRequest = new LeaveRequest(udpUrl, port, nodeUserName);
        logger.debug("Sending leave request, url: [{}], port: [{}], username: [{}]", udpUrl, port, nodeUserName);
        node.send(leaveRequest, udpUrl, udpPort);
    }

    public void handleLeaveRequest(@NonNull LeaveRequest leaveRequest) {
        try {
            String ip = leaveRequest.getIpAddress();
            int port = Integer.parseInt(leaveRequest.getPort());

            List<ConnectedNode> connectedNodes = routingService.getRoutingTable();
            ConnectedNode connectedNode = new ConnectedNode(ip, port);
            // If it does not contain the requested node
            if (!connectedNodes.contains(connectedNode)) {
                throw new Exception("Node not available in private routing table");
            }
            // If connected nodes exists
            connectedNodes.remove(connectedNode);
        } catch (Exception e) {
            logger.error("Error occurred while handling leave request", e);
        }
    }

    public void sendLeaveResponse() {
        String leaveResponseString = Protocol.COMMAND.LEAVE.toString();
        LeaveResponse leaveResponse = new LeaveResponse(leaveResponseString);
        logger.debug("Sending leave response: [{}]", leaveResponseString);
        node.send(leaveResponse, udpUrl, udpPort);
    }

    public void handleLeaveResponse(@NonNull LeaveResponse leaveResponse) {
        try {
            if (leaveResponse.getCommand() != Protocol.COMMAND.LEAVE) {
                throw new Exception("Invalid leave response");
            }
            logger.debug("Leave response received");
        } catch (Exception e) {
            logger.error("Error occurred while handling leave response", e);
        }
    }
}
