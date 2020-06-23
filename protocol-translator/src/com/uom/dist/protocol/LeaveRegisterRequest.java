package com.uom.dist.protocol;

public class LeaveRegisterRequest extends Protocol {
    private String ipAddress;
    private String port;

    public LeaveRegisterRequest(String ipAddress, String port, String userName) {
        super(COMMAND.LEAVE);
        this.ipAddress = ipAddress;
        this.port = port;
        setLength();
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
