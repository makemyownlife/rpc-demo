package com.ucar.rpc.server;

/**
 * Created by zhangyong on 16/1/1.
 */
public interface RemotingService {

    public void start();


    public void shutdown();


    public void registerRPCHook(RpcServiceHook rpcServiceHook);

}
