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
        this.ip = ip;
        this.hops = hops;
        this.port = port;
        setLength();
    }

    public SearchResponse(int noOfFiles, String ip, String port, int hops, List<String> fileNames) {
        super(COMMAND.SEROK);
        this.fileNames = fileNames;
        this.noOfFiles = noOfFiles;
        this.ip = ip;
        this.hops = hops;
        this.port = port;
        setLength();
    }

    public SearchResponse(List<String> messagePartList) {
        super(messagePartList.get(0), COMMAND.SEROK);
        this.noOfFiles = Integer.parseInt(messagePartList.get(2));
        this.ip = messagePartList.get(3);
        this.port = messagePartList.get(4);
        this.hops = Integer.parseInt(messagePartList.get(4));
        this.fileNames = messagePartList.subList(5, messagePartList.size() - 1);
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
