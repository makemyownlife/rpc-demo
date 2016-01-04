package com.ucar.rpc.client;

import com.ucar.rpc.client.netty.RpcClient;
import com.ucar.rpc.client.netty.RpcClientConfig;
import com.ucar.rpc.common.exception.RpcConnectException;
import com.ucar.rpc.common.exception.RpcSendRequestException;
import com.ucar.rpc.common.exception.RpcTimeoutException;
import com.ucar.rpc.server.netty.RpcServer;
import com.ucar.rpc.server.netty.RpcServerConfig;
import com.ucar.rpc.server.protocol.RpcRequestCommand;
import com.ucar.rpc.server.protocol.RpcResponseCommand;
import org.junit.Test;

/**
 * Created by zhangyong on 16/1/2.
 */
public class RpcClientUnitTest {

    public RpcClient createRpcClient() {
        RpcClientConfig rpcClientConfig = new RpcClientConfig();
        RpcClient rpcClient = new RpcClient(rpcClientConfig);
        return rpcClient;
    }

    public RpcServer createRpcServer() {
        RpcServerConfig rpcServerConfig = new RpcServerConfig();
        RpcServer rpcServer = new RpcServer(rpcServerConfig, new MyBeanLocator());
        return rpcServer;
    }

    @Test
    public void testSayHello() throws InterruptedException, RpcConnectException, RpcSendRequestException, RpcTimeoutException {
        RpcServer rpcServer = createRpcServer();
        rpcServer.start();
        RpcClient client = createRpcClient();
        client.start();
        RpcRequestCommand rpcRequestCommand = null;
        RpcResponseCommand rpcResponseCommand = null;
        rpcRequestCommand = new RpcRequestCommand("hello", "sayHello", new Object[]{"makemyownlife"});
        rpcResponseCommand = client.invokeSync("localhost:8888", rpcRequestCommand, 15000);
        System.out.println(rpcResponseCommand.getResult());
    }

    @Test
    public void testSayHelloUser() throws InterruptedException, RpcConnectException, RpcSendRequestException, RpcTimeoutException {
        RpcServer rpcServer = createRpcServer();
        rpcServer.start();
        RpcClient client = createRpcClient();
        client.start();
        RpcRequestCommand rpcRequestCommand = null;
        RpcResponseCommand rpcResponseCommand = null;
        User user = new User();
        user.setUsername("mylife");
        user.setA(4)
        ;
        rpcRequestCommand = new RpcRequestCommand("hello", "testUser", new Object[]{user});
        rpcResponseCommand = client.invokeSync("localhost:8888", rpcRequestCommand, 15000);
        System.out.println(rpcResponseCommand.getResult());
    }

}
