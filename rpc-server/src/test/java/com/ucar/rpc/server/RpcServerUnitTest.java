package com.ucar.rpc.server;

import com.ucar.rpc.server.netty.RpcServer;
import com.ucar.rpc.server.netty.RpcServerConfig;
import org.junit.Test;

/**
 * Created by zhangyong on 16/1/1.
 */
public class RpcServerUnitTest {

    public static RpcServer startRpcServer() {
        final RpcServerConfig rpcSystemConfig = new RpcServerConfig();
        RpcServer rpcServer = new RpcServer(rpcSystemConfig, null);
        return rpcServer;
    }

    @Test
    public void testServer() throws InterruptedException {
        RpcServer rpcServer = startRpcServer();
        rpcServer.start();
        Thread.sleep(100000);
    }

}
