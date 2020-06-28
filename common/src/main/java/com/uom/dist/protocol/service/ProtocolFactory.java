package com.uom.dist.protocol.service;

import com.uom.dist.protocol.Protocol;
import org.springframework.stereotype.Component;

public interface ProtocolFactory {
    public Protocol decode(String message) throws Exception;
    public String encode(Protocol protocol);
}
