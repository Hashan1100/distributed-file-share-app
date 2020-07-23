package com.uom.dist.node.service;

import com.uom.dist.node.service.domain.ConnectedNode;
import com.uom.dist.node.service.shell.Shell;
import com.uom.dist.protocol.Protocol;
import com.uom.dist.protocol.RegisterRequest;
import com.uom.dist.protocol.RegisterResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

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

    @Autowired
    private Shell shell;

    private static final Logger logger = LoggerFactory.getLogger(RegisterService.class);

    private Map<String, String> registerStatusCodeMap = Map.of(
            "0","request is successful, no nodes in the system",
            "1","request is successful, 1 contact is returned",
            "2","request is successful, 2 nodes contacts is returned",
            "9999","failed, there is some error in the command",
            "9998","failed, already registered to you, unregister first",
            "9997","failed, registered to another user, try a different IP and port",
            "9996","failed, can’t register. BS full."
    );

    public void register(){
        Protocol registerRequest = new RegisterRequest(udpUrl, udpPort + "", nodeUserName);
        logger.debug("Sending register request [{}]", registerRequest.serialize());
        try {
            nodeServer.send(registerRequest, bootstrapServerUrl, bootstrapServerPort);
        } catch (Exception e) {
            logger.error("Error occurred");
            shell.print("Error occurred");
        }
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
        String status = registerStatusCodeMap.get(registerResponse.getNoNodes());
        shell.print("Register response status:["+ registerResponse.getNoNodes() +"] status description: [" + status + "]");
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
