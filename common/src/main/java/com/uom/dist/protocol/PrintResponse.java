package com.uom.dist.protocol;

import java.util.List;

public class PrintResponse extends Protocol {
    private String routingTable;
    public PrintResponse() {
        super(COMMAND.PRINT);
        setLength();
    }

    public PrintResponse(List<String> messagePartList) {
        super("" + COMMAND.PRINT.name().length(), COMMAND.PRINT);
    }

    public PrintResponse(String routingTable) {
        super(COMMAND.PRINT);
        this.routingTable = routingTable;
    }

    protected String getProtocolStringPart() {
        return " " + super.getCommand().toString() + " " + routingTable;
    }

    public String serialize() {
        return routingTable;
    }

    public void setLength() {
        String protocolStringPart = getProtocolStringPart();
        String length = String.format("%04d", protocolStringPart.length() + 4);
        super.setLength(length);
    }
}
