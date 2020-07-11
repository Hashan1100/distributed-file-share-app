package com.uom.dist.protocol;

import java.util.List;

public class LeaveResponse extends Protocol {
    private String value;

    public LeaveResponse(String value) {
        super(COMMAND.LEAVEOK);
        this.value = value;
        setLength();
    }

    public LeaveResponse(List<String> messagePartList) {
        super(messagePartList.get(0), COMMAND.LEAVEOK);
        this.value = messagePartList.get(2);
    }

    protected String getProtocolStringPart() {
        return " " + super.getCommand().toString() + " " + value;
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

    public String getValue() {
        return value;
    }
}
