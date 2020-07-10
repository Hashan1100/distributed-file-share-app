package com.uom.dist.node.service;

import com.google.gson.Gson;
import com.uom.dist.protocol.*;
import com.uom.dist.protocol.service.ProtocolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RemoveNode {

    private static final Logger logger = LoggerFactory.getLogger(Node.class);

    @Value("${udp.receiver.port}")
    private int udpPort;

    DatagramSocket sock = null;

    private ExecutorService executor = Executors.newFixedThreadPool(2);

    @Autowired
    private UnregisterService unregisterService;

    @Autowired
    private ProtocolFactory protocolFactory;

    @PreDestroy
    public void init() {
        executor.submit(this::start);
        unregisterService.Unregister();
    }

    public void start(){
        String s;

        try
        {
            sock = new DatagramSocket(udpPort);
            logger.debug("Node created at [{}]. Waiting for incoming data...", udpPort);
            while(true){
                byte[] buffer = new byte[65536];
                DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
                sock.receive(incoming);
                byte[] data = incoming.getData();
                s = new String(data, 0, incoming.getLength());

                logger.debug(incoming.getAddress().getHostAddress() + " : " + incoming.getPort() + " - " + s);
                try {
                    Protocol request = protocolFactory.decode(s);
                    logger.debug("Decoded message [{}]", new Gson().toJson(request));
                    if (request instanceof RegisterResponse) {
                        unregisterService.UnregisterResponseHandler((UnRegisterResponse) request);
                    }
                } catch (Exception e) {
                    logger.error("Error occurred while trying to decode request", e);
                }
            }
        }catch (IOException e) {
            System.err.println("IOException " + e);
        }

    }

    public void send(Protocol protocol, String url, int port) {
        if (sock != null) {
            String msgString = protocol.serialize();
            logger.debug("Sending request to remove [{}]", msgString);
            try {
                DatagramPacket sendPacket = new DatagramPacket(msgString.getBytes() , msgString.getBytes().length , InetAddress.getByName(url), port);
                sock.send(sendPacket);
            } catch (Exception e) {
                logger.debug("Error occurred while trying to send the request", e);
            }
        }
    }
}
