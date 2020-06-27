package com.uom.dist.node.service;

import com.uom.dist.node.NetworkNodeApplication;
import com.uom.dist.protocol.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class HandleRequest {
    private static final Logger logger = LoggerFactory.getLogger(NetworkNodeApplication.class);

    public void handle(Protocol protocol) {
        logger.debug(protocol.serialize());
    }
}
