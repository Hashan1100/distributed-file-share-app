package com.uom.dist.node.service;

import com.uom.dist.protocol.Protocol;
import com.uom.dist.protocol.UnRegisterRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class UnregisterService {
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
    private Node nodeUnserver;

    @Autowired
    private RoutingService routingService;

    private static final Logger logger = LoggerFactory.getLogger(RegisterService.class);

    public void Unregister(){
        Protocol unRegisterRequest = new UnRegisterRequest(udpUrl, udpPort + "", nodeUserName);
        logger.debug("Sending register request [{}]", unRegisterRequest.serialize());
        nodeUnserver.send(unRegisterRequest, bootstrapServerUrl, bootstrapServerPort);
    }
}
