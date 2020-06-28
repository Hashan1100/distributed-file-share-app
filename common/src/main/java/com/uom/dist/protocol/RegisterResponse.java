package com.uom.dist.protocol;

import java.util.List;

public class RegisterResponse extends Protocol {
    private String noNodes;
    private String Ip1;
    private String port1;
    private String Ip2;
    private String port2;

    public RegisterResponse(String noNodes, String Ip1, String port1, String Ip2, String port2) {
        super(COMMAND.REGOK);
        this.noNodes = noNodes;
        this.Ip1 = Ip1;
        this.port1 = port1;
        this.Ip2 = Ip2;
        this.port2 = port2;
        setLength();
    }

    public RegisterResponse(List<String> messagePartList) {
        super(messagePartList.get(0), COMMAND.REGOK);
        this.noNodes = messagePartList.get(2);
        this.Ip1 = messagePartList.get(3);
        this.port1 = messagePartList.get(4);
        this.Ip2 = messagePartList.get(5);
        this.port2 = messagePartList.get(6);
    }
    public String getNoNodes() {
        return noNodes;
    }

    public String getIp1() {
        return Ip1;
    }

    public String getPort1() {
        return port1;
    }

    public String getIp2() {
        return Ip2;
    }

    public String getPort2() {
        return port2;
    }

    protected String getProtocolStringPart() {
        return " " + super.getCommand().toString() + " " + noNodes + " " + Ip1 + " " + port1 + " " + Ip2 + " " + port2;
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
