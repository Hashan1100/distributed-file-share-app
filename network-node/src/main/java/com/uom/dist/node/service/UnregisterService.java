package com.uom.dist.node.service;

import com.uom.dist.node.service.shell.Shell;
import com.uom.dist.protocol.Protocol;
import com.uom.dist.protocol.UnRegisterRequest;
import com.uom.dist.protocol.UnRegisterResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Map;

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

    @Autowired
    private Shell shell;

    private static final Logger logger = LoggerFactory.getLogger(RegisterService.class);

    private Map<String, String> unregisterErrorMap = Map.of(
            "0","successful",
            "9999","error while unregistering. IP and port may not be in the registry or command is incorrect."
    );

    @PreDestroy
    public void unregister() {
        Protocol unRegisterRequest = new UnRegisterRequest(udpUrl, udpPort + "", nodeUserName);
        logger.debug("Sending unregister request [{}]", unRegisterRequest.serialize());
        try {
            node.send(unRegisterRequest, bootstrapServerUrl, bootstrapServerPort);
        } catch (Exception e) {
            logger.error("Error occurred");
            shell.print("Error occurred");
        }
    }

    public void unregisterResponseHandler(UnRegisterResponse unregisterResponse) {
        logger.debug("Sending unregister response : [{}]", unregisterResponse.serialize());
        String status = unregisterErrorMap.get(unregisterResponse.getValue());
        shell.print("Unregister status:[" + unregisterResponse.getValue() + "] status description: [" + status + "]");
    }
}
