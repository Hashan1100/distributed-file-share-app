package com.uom.dist.node.connector;

import com.uom.dist.node.NetworkNodeApplication;
import com.uom.dist.node.service.HandleRequest;
import com.uom.dist.protocol.Protocol;
import com.uom.dist.protocol.service.ProtocolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
public class UDPServer {
    private static final Logger logger = LoggerFactory.getLogger(NetworkNodeApplication.class);
    @Autowired
    private ProtocolFactory protocolFactory;

    @Autowired
    private HandleRequest handleRequest;

    public void handleMessage(Message message)
    {
        String data = new String((byte[]) message.getPayload());
        logger.debug("Message Received [{}]", data);
        try {
            Protocol decode = protocolFactory.decode(data);
            handleRequest.handle(decode);
        } catch (Exception e) {
            logger.error("Error occurred while decoding message [{}]", e.getMessage());
        }
    }
}
