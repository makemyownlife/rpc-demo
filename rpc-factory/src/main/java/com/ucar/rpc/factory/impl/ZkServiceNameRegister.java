package com.ucar.rpc.factory.impl;

import com.ucar.rpc.common.RemotingUtil;
import com.ucar.rpc.factory.ServiceNameRegister;
import com.ucar.rpc.factory.utils.ZkUtils;
import com.ucar.rpc.server.netty.RpcServerConfig;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangyong on 16/1/7.
 */
public class ZkServiceNameRegister implements ServiceNameRegister {

    private static final Logger logger = LoggerFactory.getLogger(ZkServiceNameRegister.class);

    private ZkClient zkClient;

    private ZkServiceNameConfig zkServiceNameConfig;

    private RpcServerConfig rpcServerConfig;

    private ZkServiceNameCacheListener zkServiceNameCacheListener;

    @Override
    public void registerService() throws Exception {
        logger.info("开始在集群中注册该节点:{} ", zkServiceNameConfig.getClusterNode());
        this.zkClient = new ZkClient(zkServiceNameConfig.getZkHosts(), zkServiceNameConfig.getZkSessionTimeout(), zkServiceNameConfig.getZkConnectTimeout(), new ZkUtils.StringSerializer());
        this.zkServiceNameCacheListener = new ZkServiceNameCacheListener();
        //确认根目录存在
        ZkUtils.makeSurePersistentPathExists(zkClient, zkServiceNameConfig.getZkClusterRoot());
        //确认当前服务的节点
        String moduleNode = zkServiceNameConfig.getZkClusterRoot() + "/" + zkServiceNameConfig.getClusterNode();
        ZkUtils.makeSurePersistentPathExists(zkClient, moduleNode);
        //创建临时节点
        String clusterId = RemotingUtil.getLocalAddress() + ":" + rpcServerConfig.getListenPort();
        String clusterEphemeralPath = moduleNode + "/" + clusterId;
        logger.info("模块:{} 创建临时节点:{}", zkServiceNameConfig.getClusterNode(), clusterEphemeralPath);
        zkClient.subscribeChildChanges(moduleNode, zkServiceNameCacheListener);
        ZkUtils.createEphemeralPathExpectConflict(zkClient, clusterEphemeralPath, null);
    }

    @Override
    public void unRegisterService() throws Exception {
        if (zkClient != null) {
            zkClient.close();
        }
    }

    public void setZkServiceNameConfig(ZkServiceNameConfig zkServiceNameConfig) {
        this.zkServiceNameConfig = zkServiceNameConfig;
    }

    public void setRpcServerConfig(RpcServerConfig rpcServerConfig) {
        this.rpcServerConfig = rpcServerConfig;
    }

}
