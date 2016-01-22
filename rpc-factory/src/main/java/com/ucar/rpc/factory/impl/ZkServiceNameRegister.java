package com.ucar.rpc.factory.impl;

import com.ucar.rpc.factory.ServiceNameCache;
import com.ucar.rpc.factory.ServiceNameRegister;
import com.ucar.rpc.factory.exception.RpcFactoryException;
import com.ucar.rpc.server.netty.RpcServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by zhangyong on 16/1/7.
 */
public class ZkServiceNameRegister implements ServiceNameRegister {

    private static final Logger logger = LoggerFactory.getLogger(ZkServiceNameRegister.class);

    private final AtomicBoolean inited = new AtomicBoolean(false);

    private ZkServiceNameConfig zkServiceNameConfig;

    private RpcServerConfig rpcServerConfig;

    private ServiceNameCache serviceNameCache;

    @Override
    public synchronized void start() throws Exception {
        if (inited.get()) {
            throw new RpcFactoryException("模块:" + zkServiceNameConfig.getClusterNode() + " 服务已经添加");
        }
        this.serviceNameCache = new ZkServiceNameCache(zkServiceNameConfig, rpcServerConfig);
        inited.compareAndSet(false, true);
    }

    @Override
    public synchronized void shutdown() throws Exception {

    }

    @Override
    public ServiceNameCache getServiceNameCache() {
        return this.serviceNameCache;
    }

    public void setZkServiceNameConfig(ZkServiceNameConfig zkServiceNameConfig) {
        this.zkServiceNameConfig = zkServiceNameConfig;
    }

    public void setRpcServerConfig(RpcServerConfig rpcServerConfig) {
        this.rpcServerConfig = rpcServerConfig;
    }

}
