package com.ucar.rpc.factory;

import com.ucar.rpc.client.netty.RpcClient;
import com.ucar.rpc.server.netty.RpcServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * rpc的入口
 * Created by zhangyong on 16/1/9.
 */
public class RpcEntrance {

    private static final Logger logger = LoggerFactory.getLogger(RpcEntrance.class);

    private RpcClient rpcClient;

    private RpcServer rpcServer;

    private ServiceNameRegister serviceNameRegister;

    public void start() throws Exception {
        logger.info("开始启动rpc入口");
        long start = System.currentTimeMillis();
        //启动客户端初始化
        rpcClient.start();
        //启动服务
        rpcServer.start();
        //启动服务注册
        serviceNameRegister.registerService();
        logger.info("结束启动rpc入口...costtime:{} ms", System.currentTimeMillis() - start);
    }

    public void shutdown() throws Exception {
        logger.info("开始关闭rpc入口");
        long start = System.currentTimeMillis();
        //取消服务注册
        serviceNameRegister.unRegisterService();
        //关闭服务
        rpcServer.shutdown();
        //关闭客户端
        rpcClient.shutdown();
        logger.info("结束关闭rpc入口...costtime:{} ms", System.currentTimeMillis() - start);
    }

    //===========================================set method ====================================
    public void setRpcClient(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    public void setRpcServer(RpcServer rpcServer) {
        this.rpcServer = rpcServer;
    }

    public void setServiceNameRegister(ServiceNameRegister serviceNameRegister) {
        this.serviceNameRegister = serviceNameRegister;
    }
}
