package com.uom.dist.node.service.domain;

public class ConnectedNode {
    private String nodeIp;
    private int nodePort;

    public ConnectedNode(String nodeIp, int nodePort) {
        this.nodeIp = nodeIp;
        this.nodePort = nodePort;
    }

    public String getNodeIp() {
        return nodeIp;
    }

    public int getNodePort() {
        return nodePort;
    }
}
