package com.ucar.rpc.server.netty;

import com.ucar.rpc.server.RemotingService;
import com.ucar.rpc.server.RpcServiceHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangyong on 16/1/1.
 */
public class RpcRemotingServer  implements RemotingService{

    private final static Logger logger = LoggerFactory.getLogger(RpcRemotingServer.class);

    @Override
    public void start() {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public void registerRPCHook(RpcServiceHook rpcServiceHook) {

    }
}
