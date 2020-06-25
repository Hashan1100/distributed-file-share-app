package com.uom.dist.protocol;

import java.util.List;

public class UnRegisterRequest extends Protocol {
    private String ipAddress;
    private String port;
    private String userName;

    public UnRegisterRequest(String ipAddress, String port, String userName) {
        super(COMMAND.UNREG);
        this.ipAddress = ipAddress;
        this.port = port;
        this.userName = userName;
        setLength();
    }

    public UnRegisterRequest(List<String> messagePartList) {
        super(messagePartList.get(0), COMMAND.UNREG);
        this.ipAddress = messagePartList.get(2);
        this.port = messagePartList.get(3);
        this.userName = messagePartList.get(4);
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getPort() {
        return port;
    }

    public String getUserName() {
        return userName;
    }

    protected String getProtocolStringPart() {
        return " " + super.getCommand().toString() + " " + ipAddress + " " + port + " " + userName;
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
