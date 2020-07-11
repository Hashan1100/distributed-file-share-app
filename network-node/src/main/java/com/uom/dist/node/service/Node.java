package com.uom.dist.node.service;

import com.google.gson.Gson;
import com.uom.dist.protocol.*;
import com.uom.dist.protocol.service.ProtocolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class Node {
    private static final Logger logger = LoggerFactory.getLogger(Node.class);

    @Value("${udp.receiver.port}")
    private int udpPort;

    DatagramSocket sock = null;

    private ExecutorService executor = Executors.newFixedThreadPool(2);

    @Autowired
    private RegisterService registerService;

    @Autowired
    private LeaveNodeService leaveNodeService;

    @Autowired
    private FileService fileService;

    @Autowired
    private ProtocolFactory protocolFactory;

    @Autowired
    private JoinService joinService;

    @PostConstruct
    public void init() {
        executor.submit(this::start);
        registerService.register();
    }

    public void start()
    {
        String s;

        try
        {
            sock = new DatagramSocket(udpPort);
            logger.debug("Node created at [{}]. Waiting for incoming data...", udpPort);
            while(true)
            {
                byte[] buffer = new byte[65536];
                DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
                sock.receive(incoming);

                byte[] data = incoming.getData();
                s = new String(data, 0, incoming.getLength());

                String hostAddress = incoming.getAddress().getHostAddress();
                int port = incoming.getPort();

                logger.debug(hostAddress + " : " + port + " - " + s);

                try {
                    Protocol request = protocolFactory.decode(s);
                    logger.debug("Decoded message [{}]", new Gson().toJson(request));
                    if (request instanceof RegisterResponse) {
                        registerService.registerResponseHandler((RegisterResponse) request);
                    } else if (request instanceof SearchRequest) {
                        fileService.search((SearchRequest) request);
                    } else if (request instanceof SearchResponse) {
                        fileService.handleSearchResponse((SearchResponse) request);
                    } else if (request instanceof LeaveRequest) {
                        leaveNodeService.handleLeaveRequest((LeaveRequest) request);
                    } else if (request instanceof LeaveResponse) {
                        leaveNodeService.handleLeaveResponse((LeaveResponse) request);
                    } else if (request instanceof JoinRequest) {
                        joinService.handleJoin((JoinRequest) request);
                    } else if (request instanceof JoinResponse) {
                        joinService.handleJoinResponse((JoinResponse) request, hostAddress, port);
                    }
                } catch (Exception e) {
                    logger.error("Error occurred while trying to decode request", e);
                }

            }
        }

        catch(IOException e)
        {
            System.err.println("IOException " + e);
        }
    }

    public void send(Protocol protocol, String url, int port) {
        if (sock != null) {
            String msgString = protocol.serialize();
            logger.debug("Sending request [{}]", msgString);
            try {
                DatagramPacket sendPacket = new DatagramPacket(msgString.getBytes() , msgString.getBytes().length , InetAddress.getByName(url), port);
                sock.send(sendPacket);
            } catch (Exception e) {
                logger.debug("Error occurred while trying to send the request", e);
            }
        }
    }
}
