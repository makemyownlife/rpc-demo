package com.ucar.rpc.factory.impl;

import com.alibaba.fastjson.JSON;
import com.ucar.rpc.common.RemotingUtil;
import com.ucar.rpc.factory.ServiceNameCache;
import com.ucar.rpc.factory.bean.RemoteServiceBean;
import com.ucar.rpc.factory.utils.ZkUtils;
import com.ucar.rpc.server.netty.RpcServerConfig;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
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

    //模块下的服务器列表
    private ConcurrentHashMap<String, List<String>> serverCache = new ConcurrentHashMap<String, List<String>>();

    //模块下的服务器列表锁
    private ReadWriteLock serverCacheLock = new ReentrantReadWriteLock();

    //模块下的服务列表
    private ConcurrentHashMap<String, Map<String, RemoteServiceBean>> remoteServiceCache = new ConcurrentHashMap<String, Map<String, RemoteServiceBean>>();

    //模块下的服务列表锁
    private ReadWriteLock remoteServiceCacheLock = new ReentrantReadWriteLock();

    //远程服务节点监听
    private ZkRemoteServiceListener zkRemoteServiceListener;

    //模块机器监听
    private ZkServerListListener zkServerListListener;

    //轮询方式
    private AtomicInteger robbinPos = new AtomicInteger(0);

    public ZkServiceNameCache(ZkServiceNameConfig zkServiceNameConfig, RpcServerConfig rpcServerConfig) {
        this.zkServiceNameConfig = zkServiceNameConfig;
        this.rpcServerConfig = rpcServerConfig;
        this.zkRemoteServiceListener = new ZkRemoteServiceListener();
        this.zkServerListListener = new ZkServerListListener();
        this.initModule();
    }

    private synchronized void initModule() {
        logger.info("开始在集群中注册该节点:{} ", zkServiceNameConfig.getClusterNode());
        try {
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
            ZkUtils.createEphemeralPathExpectConflict(zkClient, clusterEphemeralPath, null);
        } catch (Exception e) {
            logger.error("init error:", e);
        }
    }

    @Override
    public RemoteServiceBean getRemoteServiceById(String serviceId) {
        String[] arr = serviceId.split("\\.");
        String module = arr[0];
        String zkModuleNode = zkServiceNameConfig.getZkRemoteServiceRoot() + "/" + module;
        Map<String, RemoteServiceBean> moduleRemoteServices = remoteServiceCache.get(module);
        if (moduleRemoteServices == null) {
            Lock writeLock = remoteServiceCacheLock.writeLock();
            try {
                writeLock.lock();
                moduleRemoteServices = parseModuleRemoteService(zkModuleNode);
                if (moduleRemoteServices == null) {
                    return null;
                }
                //添加监听器
                zkClient.subscribeDataChanges(zkModuleNode, zkRemoteServiceListener);
                remoteServiceCache.put(module, moduleRemoteServices);
            } finally {
                writeLock.unlock();
            }
        }
        return moduleRemoteServices.get(serviceId);
    }

    public String getRemoteAddressById(String serviceId) {
        String[] arr = serviceId.split("\\.");
        String module = arr[0];
        String zkModuleNode = zkServiceNameConfig.getZkClusterRoot() + "/" + module;
        List<String> serverList = serverCache.get(module);
        if (serverList == null) {
            Lock writeLock = serverCacheLock.writeLock();
            try {
                writeLock.lock();
                serverList = zkClient.getChildren(zkModuleNode);
                //添加监听器
                zkClient.subscribeChildChanges(zkModuleNode, zkServerListListener);
                //添加到本地缓存中
                serverCache.put(module, serverList);
            } finally {
                writeLock.unlock();
            }
        }
        if (CollectionUtils.isEmpty(serverList)) {
            return null;
        }
        //roundbin方式
        synchronized (robbinPos) {
            if (robbinPos.get() > serverList.size()) {
                robbinPos.set(0);
            }
            String server = serverList.get(robbinPos.getAndIncrement());
            return server;
        }
    }

    private Map<String, RemoteServiceBean> parseModuleRemoteService(String zkModuleRemoteService) {
        String moduleData = ZkUtils.readData(zkClient, zkModuleRemoteService);
        List<RemoteServiceBean> remoteServiceBeans = JSON.parseArray(moduleData, RemoteServiceBean.class);
        if (CollectionUtils.isEmpty(remoteServiceBeans)) {
            return null;
        }
        Map<String, RemoteServiceBean> map = new HashMap<String, RemoteServiceBean>();
        for (RemoteServiceBean remoteServiceBean : remoteServiceBeans) {
            map.put(remoteServiceBean.getServiceId(), remoteServiceBean);
        }
        return map;
    }

    public class ZkRemoteServiceListener implements IZkDataListener {

        @Override
        public void handleDataChange(String dataPath, Object data) throws Exception {
            logger.info("dataPath:{} data:{}", dataPath, data);
        }

        @Override
        public void handleDataDeleted(String dataPath) throws Exception {
            logger.info("handleDataDeleted:{} data:{}", dataPath);
        }

    }

    public class ZkServerListListener implements IZkChildListener {
        @Override
        public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
            logger.info("parentPath:{}", currentChilds);
        }
    }

}
