package com.ucar.rpc.factory.impl;

/**
 * Created by zhangyong on 16/1/10.
 */
public class ZkServiceNameConfig {

    private String zkHosts;

    private int zkConnectTimeout = 15000;

    private int zkSessionTimeout = 30000;

    private String zkClusterRoot;

    private String clusterNode;

    public String getZkHosts() {
        return zkHosts;
    }

    public void setZkHosts(String zkHosts) {
        this.zkHosts = zkHosts;
    }

    public int getZkConnectTimeout() {
        return zkConnectTimeout;
    }

    public void setZkConnectTimeout(int zkConnectTimeout) {
        this.zkConnectTimeout = zkConnectTimeout;
    }

    public int getZkSessionTimeout() {
        return zkSessionTimeout;
    }

    public void setZkSessionTimeout(int zkSessionTimeout) {
        this.zkSessionTimeout = zkSessionTimeout;
    }

    public String getClusterNode() {
        return clusterNode;
    }

    public void setClusterNode(String clusterNode) {
        this.clusterNode = clusterNode;
    }

    public void setZkClusterRoot(String zkClusterRoot) {
        this.zkClusterRoot = zkClusterRoot;
    }

    public String getZkClusterRoot() {
        return zkClusterRoot;
    }

}
