package com.uom.dist.node.service;

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

    private static final Logger logger = LoggerFactory.getLogger(RegisterService.class);

    public void register(){
        Protocol registerRequest = new RegisterRequest(udpUrl, udpPort + "", nodeUserName);
        logger.debug("Sending register request [{}]", registerRequest.serialize());
        nodeServer.send(registerRequest, bootstrapServerUrl, bootstrapServerPort);
    }

    public void registerResponseHandler(RegisterResponse registerResponse) throws Exception {
        if (isNodeAvailable(registerResponse.getIp1(), registerResponse.getPort1())) {
            routingService.insertIntoNodeList(registerResponse.getIp1(),
                    Integer.parseInt(registerResponse.getPort1()));
        }

        if (isNodeAvailable(registerResponse.getIp2(), registerResponse.getPort2())) {
            routingService.insertIntoNodeList(registerResponse.getIp2(),
                    Integer.parseInt(registerResponse.getPort2()));
        }

        logger.debug("Register result [{}]", registerResponse.serialize());
    }

    private boolean isNodeAvailable(String url, String port) {
        return url != null &&
                port != null;
    }
}
