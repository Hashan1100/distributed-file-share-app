package com.uom.dist.protocol;

import java.util.List;

public class ErrorResponse extends Protocol {
    public ErrorResponse() {
        super(COMMAND.ERROR);
        setLength();
    }

    public ErrorResponse(List<String> messagePartList) {
        super(messagePartList.get(0), COMMAND.ERROR);
    }

    protected String getProtocolStringPart() {
        return " " + super.getCommand().toString();
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
