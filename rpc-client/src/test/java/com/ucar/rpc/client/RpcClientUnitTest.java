package com.ucar.rpc.client;

import com.ucar.rpc.client.netty.RpcClient;
import com.ucar.rpc.client.netty.RpcClientConfig;

/**
 * Created by zhangyong on 16/1/2.
 */
public class RpcClientUnitTest {

    public RpcClient createRpcClient() {
        RpcClientConfig rpcClientConfig = new RpcClientConfig();
        RpcClient rpcClient = new RpcClient(rpcClientConfig);
        return rpcClient;
    }



}
