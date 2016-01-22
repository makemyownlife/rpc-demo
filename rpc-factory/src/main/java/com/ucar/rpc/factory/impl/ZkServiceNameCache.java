package com.ucar.rpc.factory.impl;

import com.ucar.rpc.common.RemotingUtil;
import com.ucar.rpc.factory.ServiceNameCache;
import com.ucar.rpc.factory.utils.ZkUtils;
import com.ucar.rpc.server.netty.RpcServerConfig;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created with IntelliJ IDEA.
 * User: zhangyong
 * Date: 2016/1/21
 * Time: 21:34
 * To change this template use File | Settings | File Templates.
 */
public class ZkServiceNameCache implements ServiceNameCache {

    private final Logger logger = LoggerFactory.getLogger(ZkServiceNameCache.class);

    private ZkClient zkClient;

    private ZkServiceNameConfig zkServiceNameConfig;

    private RpcServerConfig rpcServerConfig;

    private ConcurrentHashMap<String, List<String>> serverCache;

    private ReadWriteLock readWriteLock;

    public ZkServiceNameCache(ZkServiceNameConfig zkServiceNameConfig, RpcServerConfig rpcServerConfig) {
        this.serverCache = new ConcurrentHashMap<String, List<String>>();
        this.readWriteLock = new ReentrantReadWriteLock();
        logger.info("开始在集群中注册该节点:{} ", zkServiceNameConfig.getClusterNode());
    }

    private void init() throws Exception {
        this.zkClient = new ZkClient(zkServiceNameConfig.getZkHosts(), zkServiceNameConfig.getZkSessionTimeout(), zkServiceNameConfig.getZkConnectTimeout(), new ZkUtils.StringSerializer());
        //确认根目录存在
        ZkUtils.makeSurePersistentPathExists(zkClient, zkServiceNameConfig.getZkClusterRoot());
        //确认当前服务的节点
        String moduleNode = zkServiceNameConfig.getZkClusterRoot() + "/" + zkServiceNameConfig.getClusterNode();
        ZkUtils.makeSurePersistentPathExists(zkClient, moduleNode);
        //创建临时节点
        String clusterId = RemotingUtil.getLocalAddress() + ":" + rpcServerConfig.getListenPort();
        String clusterEphemeralPath = moduleNode + "/" + clusterId;
        logger.info("模块:{} 创建临时节点:{}", zkServiceNameConfig.getClusterNode(), clusterEphemeralPath);
        ZkServiceNameCacheListener zkServiceNameCacheListener = new ZkServiceNameCacheListener();
        zkClient.subscribeChildChanges(moduleNode, zkServiceNameCacheListener);
        ZkUtils.createEphemeralPathExpectConflict(zkClient, clusterEphemeralPath, null);
    }

    public class ZkServiceNameCacheListener implements IZkChildListener {
        @Override
        public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
            serverCache.put(parentPath, currentChilds);
        }
    }

}
