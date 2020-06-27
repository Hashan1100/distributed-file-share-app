package com.uom.dist.node.connector;

import com.uom.dist.protocol.Protocol;
import org.springframework.integration.ip.udp.UnicastSendingMessageHandler;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class UDPMessageSender {
    public void send(Protocol protocol, int port, String url){
        UnicastSendingMessageHandler handler =
                new UnicastSendingMessageHandler(url, port);

        String payload = protocol.serialize();
        handler.handleMessage(MessageBuilder.withPayload(payload).build());
    }
}
