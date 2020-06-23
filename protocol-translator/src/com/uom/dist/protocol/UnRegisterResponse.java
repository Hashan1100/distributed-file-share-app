package com.uom.dist.protocol;

public class UnRegisterResponse extends Protocol {
    private String value;

    public UnRegisterResponse(String value) {
        super(COMMAND.UNROK);
        this.value = value;
        setLength();
    }

    public String getValue() {
        return value;
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
}
