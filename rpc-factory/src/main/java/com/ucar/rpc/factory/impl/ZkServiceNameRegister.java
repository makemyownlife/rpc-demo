package com.ucar.rpc.factory.impl;

import com.ucar.rpc.factory.ServiceNameRegister;
import com.ucar.rpc.factory.utils.ZkUtils;
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

    @Override
    public void registerService() throws Exception {
        logger.info("开始在集群中注册该节点:{} ", zkServiceNameConfig.getClusterNode());
        zkClient = new ZkClient(zkServiceNameConfig.getZkHosts(), zkServiceNameConfig.getZkSessionTimeout(), zkServiceNameConfig.getZkConnectTimeout(), new ZkUtils.StringSerializer());
        //确认根目录存在
        ZkUtils.makeSurePersistentPathExists(zkClient, zkServiceNameConfig.getZkClusterRoot());
        //确认当前服务的节点
        ZkUtils.makeSurePersistentPathExists(zkClient, zkServiceNameConfig.getZkClusterRoot() + "/" + zkServiceNameConfig.getClusterNode());
        //创建临时节点
    }

    @Override
    public void unRegisterService() throws Exception {

    }

    public void setZkServiceNameConfig(ZkServiceNameConfig zkServiceNameConfig) {
        this.zkServiceNameConfig = zkServiceNameConfig;
    }
}
