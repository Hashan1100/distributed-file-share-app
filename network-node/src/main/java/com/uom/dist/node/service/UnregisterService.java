package com.uom.dist.node.service;

import com.uom.dist.protocol.Protocol;
import com.uom.dist.protocol.UnRegisterRequest;
import com.uom.dist.protocol.UnRegisterResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    private RemoveNode removeNode;

    private RoutingService routingService;

    public String status;

    private static final Logger logger = LoggerFactory.getLogger(RegisterService.class);

    public void Unregister(){
        Protocol unRegisterRequest = new UnRegisterRequest(udpUrl, udpPort + "", nodeUserName);
        logger.debug("Sending unregister request [{}]", unRegisterRequest.serialize());
        removeNode.send(unRegisterRequest, bootstrapServerUrl, bootstrapServerPort);
    }

    public void UnregisterResponseHandler (UnRegisterResponse unregisterResponse) throws Exception{
        status = unregisterResponse.getValue();
        // remove from routing table implementation
        routingService.removeFromNodeList(udpUrl, udpPort);
    }
}
