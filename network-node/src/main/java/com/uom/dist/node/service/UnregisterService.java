package com.uom.dist.node.service;

import com.uom.dist.protocol.Protocol;
import com.uom.dist.protocol.UnRegisterRequest;
import com.uom.dist.protocol.UnRegisterResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
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
    private Node node;

    private static final Logger logger = LoggerFactory.getLogger(RegisterService.class);

    @PreDestroy
    public void unregister() {
        Protocol unRegisterRequest = new UnRegisterRequest(udpUrl, udpPort + "", nodeUserName);
        logger.debug("Sending unregister request [{}]", unRegisterRequest.serialize());
        node.send(unRegisterRequest, bootstrapServerUrl, bootstrapServerPort);
    }

    public void unregisterResponseHandler(UnRegisterResponse unregisterResponse) {
        logger.debug("Sending unregister response : [{}]", unregisterResponse.serialize());
    }
}
