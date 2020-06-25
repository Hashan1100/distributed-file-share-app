package com.uom.dist.protocol;

public abstract class Protocol {
    public static enum COMMAND { REG, REGOK, UNREG, UNROK, JOIN, JOINOK, LEAVE, LEAVEOK, SER, SEROK, ERROR}

    private String length;
    private COMMAND command;

    public Protocol(COMMAND command) {
        this.command = command;
    }

    public Protocol(String length, COMMAND command) {
        this.command = command;
        this.length = length;
    }

    public String getLength() {
        return length;
    }

    public void setLength() {
        String protocolStringPart = getProtocolStringPart();
        String length = String.format("%04d", protocolStringPart.length() + 4);
        setLength(length);
    }

    public String serialize() {
        String protocolStringPart = getProtocolStringPart();
        return getLength() + protocolStringPart;
    }

    protected String getProtocolStringPart() {
        return " " + getCommand().toString();
    }

    public void setLength(String length) {
        this.length = length;
    }

    public COMMAND getCommand() {
        return command;
    }

    public void setCommand(COMMAND command) {
        this.command = command;
    }
}
