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
        ZkUtils.makeSurePersistentPathExists(zkClient, zkServiceNameConfig.getZkClusterRoot() + "/" + zkServiceNameConfig.getClusterNode());
        //创建临时节点
        String clusterId = RemotingUtil.getLocalAddress() + ":" + rpcServerConfig.getListenPort();
        String clusterEphemeralPath = zkServiceNameConfig.getZkClusterRoot() + "/" + zkServiceNameConfig.getClusterNode() + "/" + clusterId;
        logger.info("创建临时节点: {}", clusterEphemeralPath);
        ZkUtils.createEphemeralPath(zkClient, clusterEphemeralPath, null);
        zkClient.subscribeChildChanges(clusterEphemeralPath, zkServiceNameCacheListener);
    }

    @Override
    public void unRegisterService() throws Exception {
        if (zkClient != null) {
            zkClient.close();
        }
    }

    @Override
    public String getRegisterAdress(String serviceId) {
        return null;
    }

    public void setZkServiceNameConfig(ZkServiceNameConfig zkServiceNameConfig) {
        this.zkServiceNameConfig = zkServiceNameConfig;
    }

    public void setRpcServerConfig(RpcServerConfig rpcServerConfig) {
        this.rpcServerConfig = rpcServerConfig;
    }


}
