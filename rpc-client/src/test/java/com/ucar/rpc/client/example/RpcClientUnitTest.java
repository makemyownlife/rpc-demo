package com.ucar.rpc.client.example;

import com.ucar.rpc.client.InvokeCallback;
import com.ucar.rpc.client.netty.ResponseFuture;
import com.ucar.rpc.client.netty.RpcClient;
import com.ucar.rpc.client.netty.RpcClientConfig;
import com.ucar.rpc.common.exception.RpcConnectException;
import com.ucar.rpc.common.exception.RpcSendRequestException;
import com.ucar.rpc.common.exception.RpcTimeoutException;
import com.ucar.rpc.common.exception.RpcTooMuchRequestException;
import com.ucar.rpc.server.netty.RpcServer;
import com.ucar.rpc.server.netty.RpcServerConfig;
import com.ucar.rpc.server.protocol.RpcRequestCommand;
import com.ucar.rpc.server.protocol.RpcResponseCommand;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

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
        Map map = new HashMap();
        map.put("hello," ,"12");
        rpcRequestCommand = new RpcRequestCommand("hello", "sayHello", new Object[]{map});
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

    @Test
    public void testAsyn() throws InterruptedException, RpcConnectException, RpcSendRequestException, RpcTimeoutException, RpcTooMuchRequestException {
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
        client.invokeAsync("localhost:8888", rpcRequestCommand, 15000, new InvokeCallback() {
            @Override
            public void operationComplete(ResponseFuture responseFuture) {
                System.out.println("I am executing!");
            }
        });
    }

}
