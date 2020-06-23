package com.uom.dist.protocol;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class SearchResponse extends Protocol {
    private int noOfFiles;
    private String ip;
    private String port;
    private int hops;
    private List<String> fileNames;

    public SearchResponse(int noOfFiles, String ip, String port, int hops, String... fileNames) {
        super(COMMAND.SEROK);
        this.fileNames = Arrays.asList(fileNames);
        this.noOfFiles = noOfFiles;
        setLength();
    }

    public int getNoOfFiles() {
        return noOfFiles;
    }

    public String getIp() {
        return ip;
    }

    public String getPort() {
        return port;
    }

    public int getHops() {
        return hops;
    }

    public List<String> getFileNames() {
        return fileNames;
    }

    protected String getProtocolStringPart() {
        AtomicReference<String> fileNameStringPart = new AtomicReference<>("");
        fileNames.forEach(fileNames -> {
            fileNameStringPart.set(fileNameStringPart.get() + fileNames + " ");
        });
        return " " + super.getCommand().toString() + " " + noOfFiles + " " + ip + " " + port + " " + hops + " " + fileNameStringPart.get();
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
