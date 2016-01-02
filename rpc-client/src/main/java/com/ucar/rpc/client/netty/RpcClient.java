package com.ucar.rpc.client.netty;

import com.ucar.rpc.server.RemotingService;
import com.ucar.rpc.server.RpcServiceHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * rpc客户端实现
 * Created by zhangyong on 16/1/2.
 */
public class RpcClient implements RemotingService {

    private final static Logger logger = LoggerFactory.getLogger(RpcClient.class);

    private final RpcClientConfig rpcClientConfig;

    public RpcClient(final RpcClientConfig rpcClientConfig) {
        this.rpcClientConfig = rpcClientConfig;
    }

    @Override
    public void start() {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public void registerRpcHook(RpcServiceHook rpcServiceHook) {

    }
}
