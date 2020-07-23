package com.uom.dist.node.service;

import com.google.gson.Gson;
import com.uom.dist.node.service.domain.ConnectedNode;
import com.uom.dist.node.service.shell.Shell;
import com.uom.dist.protocol.LeaveRequest;
import com.uom.dist.protocol.LeaveResponse;
import com.uom.dist.protocol.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class LeaveNodeService {
    @Autowired
    private Node node;

    @Value("${udp.receiver.port}")
    private int udpPort;

    @Value("${udp.receiver.url}")
    private String udpUrl;

    private static final String LEAVE_ERROR_CODE = "9999";
    private static final String LEAVE_SUCCESS_CODE = "0";

    private Map<String, String> leaveNodeErrorMap = Map.of(
            "0","successful",
            "9999","error while adding new node to routing table"
    );

    @Autowired
    private RoutingService routingService;

    @Autowired
    private Shell shell;

    private static final Logger logger = LoggerFactory.getLogger(LeaveNodeService.class);

    @PreDestroy
    public void sendLeaveRequest() {
        LeaveRequest leaveRequest = new LeaveRequest(udpUrl, udpPort + "");
        logger.debug("Sending leave requests");
        routingService.broadCast(leaveRequest);;
    }

    public void handleLeaveRequest(@NonNull LeaveRequest leaveRequest) {
        try {
            String ip = leaveRequest.getIpAddress();
            int port = Integer.parseInt(leaveRequest.getPort());

            boolean result = routingService.removeFromNodeList(ip, port);
            if (result) {
                logger.debug("Leave successful for ip [{}] port [{}]", ip, port);
                sendLeaveResponse(LEAVE_SUCCESS_CODE, ip, port);
            } else {
                logger.debug("Leave failed for ip [{}] port [{}]", ip, port);
                sendLeaveResponse(LEAVE_ERROR_CODE, ip, port);
            }
        } catch (Exception e) {
            logger.error("Error occurred while handling leave request", e);
        }
    }

    public void sendLeaveResponse(String errorCode, String url, int port) {
        LeaveResponse leaveResponse = new LeaveResponse(errorCode);
        logger.debug("Sending leave response: [{}]", leaveResponse.serialize());
        try {
            node.send(leaveResponse, url, port);
        } catch (Exception e) {
            logger.error("Error occurred");
        }
    }

    public void handleLeaveResponse(@NonNull LeaveResponse leaveResponse, String url, int port) {
        logger.debug("Leave response received [{}]", leaveResponse.serialize());
        if(Objects.equals(leaveResponse.getValue(), LEAVE_SUCCESS_CODE)) {
            routingService.removeFromNodeList(url, port);
        } else {
            logger.debug("Leave response received with error code");
        }

        String status = leaveNodeErrorMap.get(leaveResponse.getValue());
        shell.print("Leave network status: [" + leaveResponse.getValue() + "] " + " status description: [" + status + "]");
    }
}
