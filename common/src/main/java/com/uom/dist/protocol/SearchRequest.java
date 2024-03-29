package com.uom.dist.protocol;

import java.util.List;

public class SearchRequest extends Protocol {
    private String ipAddress;
    private String port;
    private String fileName;
    private int hops;

    public SearchRequest(String ipAddress, String port, String fileName, int hops) {
        super(COMMAND.SER);
        this.ipAddress = ipAddress;
        this.port = port;
        this.fileName = fileName;
        this.hops = hops;
        setLength();
    }

    public SearchRequest(List<String> messagePartList, String fileName) {
        super(messagePartList.get(0), COMMAND.SER);
        this.ipAddress = messagePartList.get(2);
        this.port = messagePartList.get(3);
        this.fileName = fileName;
        this.hops = Integer.parseInt(messagePartList.get(4).replaceAll("\\s+",""));
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getPort() {
        return port;
    }

    public String getFileName() {
        return fileName;
    }

    public int getHops() {
        return hops;
    }

    public int incrementHops() {
        return (hops ++);
    }

    protected String getProtocolStringPart() {
        return " " + super.getCommand().toString() + " " + ipAddress + " " + port + " " + fileName + " " + hops;
    }

    public String serialize() {
        String protocolStringPart = getProtocolStringPart();
        return super.getLength() + protocolStringPart;
    }

    public String cacheKey() {
        return super.getCommand().toString() + "|" + ipAddress + "|" + port + "|" + fileName;
    }

    public void setLength() {
        String protocolStringPart = getProtocolStringPart();
        String length = String.format("%04d", protocolStringPart.length() + 4);
        super.setLength(length);
    }
}
