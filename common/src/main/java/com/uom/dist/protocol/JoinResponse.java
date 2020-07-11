package com.uom.dist.protocol;

import java.util.List;

public class JoinResponse extends Protocol {
    private String status;

    public JoinResponse(String status) {
        super(COMMAND.JOINOK);
        this.status = status;
        setLength();
    }

    public JoinResponse(List<String> messagePartList) {
        super(messagePartList.get(0), COMMAND.JOINOK);
        this.status = messagePartList.get(2);
    }

    protected String getProtocolStringPart() {
        return " " + super.getCommand().toString() + " " + status;
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

    public String getStatus() {
        return status;
    }
}
