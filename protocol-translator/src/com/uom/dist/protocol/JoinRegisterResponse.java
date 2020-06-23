package com.uom.dist.protocol;

public class JoinRegisterResponse extends Protocol {
    private String value;

    public JoinRegisterResponse(String value) {
        super(COMMAND.JOINOK);
        this.value = value;
        setLength();
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
