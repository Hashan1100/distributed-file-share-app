package com.uom.dist.node.service;

import com.uom.dist.node.service.domain.ConnectedNode;
import com.uom.dist.protocol.Protocol;
import com.uom.dist.protocol.RegisterRequest;
import com.uom.dist.protocol.RegisterResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RegisterService {

    @Value("${udp.receiver.port}")
    private int udpPort;

    @Value("${bootstrap.server.port}")
    private int bootstrapServerPort;

    @Value("${bootstrap.server.url}")
    private String bootstrapServerUrl;

    @Value("${node.username}")
    private String nodeUserName;

    @Value("${udp.receiver.url}")
    private String udpUrl;

    @Autowired
    private Node nodeServer;

    @Autowired
    private RoutingService routingService;

    @Autowired
    private JoinService joinService;

    private static final Logger logger = LoggerFactory.getLogger(RegisterService.class);

    public void register(){
        Protocol registerRequest = new RegisterRequest(udpUrl, udpPort + "", nodeUserName);
        logger.debug("Sending register request [{}]", registerRequest.serialize());
        nodeServer.send(registerRequest, bootstrapServerUrl, bootstrapServerPort);
    }

    public void registerResponseHandler(RegisterResponse registerResponse) throws Exception {
        if (isNodeAvailable(registerResponse.getIp1(), registerResponse.getPort1())) {
            boolean addSuccess = routingService.insertIntoNodeList(registerResponse.getIp1(),
                    Integer.parseInt(registerResponse.getPort1()));
            sendJoin(registerResponse, addSuccess, registerResponse.getIp1(), registerResponse.getPort1());

        }

        if (isNodeAvailable(registerResponse.getIp2(), registerResponse.getPort2())) {
            boolean addSuccess = routingService.insertIntoNodeList(registerResponse.getIp2(),
                    Integer.parseInt(registerResponse.getPort2()));
            sendJoin(registerResponse, addSuccess, registerResponse.getIp2(), registerResponse.getPort2());
        }

        logger.debug("Register result [{}]", registerResponse.serialize());
    }

    private void sendJoin(RegisterResponse registerResponse, boolean addSuccess, String ip1, String port1) {
        if (addSuccess) {
            logger.debug("Sending join request for ip : [{}] port : [{}]",
                    registerResponse.getIp1(), registerResponse.getPort1());
            ConnectedNode connectedNode = new ConnectedNode(ip1,
                    Integer.parseInt(port1));
            joinService.join(connectedNode);
        } else {
            logger.error("Not sending join request for ip : [{}] port : [{}]",
                    ip1, port1);
        }
    }

    private boolean isNodeAvailable(String url, String port) {
        return url != null &&
                port != null;
    }
}
