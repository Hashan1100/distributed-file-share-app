package com.uom.dist.protocol;

import java.util.List;

public class JoinRequest extends Protocol {
    private String ipAddress;
    private String port;

    public JoinRequest(String ipAddress, String port) {
        super(COMMAND.JOIN);
        this.ipAddress = ipAddress;
        this.port = port;
        setLength();
    }

    public JoinRequest(List<String> messagePartList) {
        super(messagePartList.get(0), COMMAND.JOIN);
        this.ipAddress = messagePartList.get(2);
        this.port = messagePartList.get(3);
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getPort() {
        return port;
    }

    protected String getProtocolStringPart() {
        return " " + super.getCommand().toString() + " " + ipAddress + " " + port;
    }

    public String serialize() {
        String protocolStringPart = getProtocolStringPart();
        return super.getLength() + protocolStringPart;
    }

    public void setLength() {
        String protocolStringPart = getProtocolStringPart();
        String length = String.format("%04d", protocolStringPart.length() + 4);
        super.setLength(length);
    }
}
