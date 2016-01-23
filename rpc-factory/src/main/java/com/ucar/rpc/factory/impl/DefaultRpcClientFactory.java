package com.ucar.rpc.factory.impl;

import com.ucar.rpc.client.netty.RpcClient;
import com.ucar.rpc.common.exception.RpcConnectException;
import com.ucar.rpc.common.exception.RpcSendRequestException;
import com.ucar.rpc.common.exception.RpcTimeoutException;
import com.ucar.rpc.factory.RpcClientFactory;
import com.ucar.rpc.factory.ServiceNameCache;
import com.ucar.rpc.factory.ServiceNameRegister;
import com.ucar.rpc.factory.bean.RemoteServiceBean;
import com.ucar.rpc.factory.exception.RpcFactoryException;
import com.ucar.rpc.factory.exception.RpcServiceNameRegisterException;
import com.ucar.rpc.server.protocol.RpcRequestCommand;

/**
 * Created by zhangyong on 16/1/7.
 */
public class DefaultRpcClientFactory implements RpcClientFactory {

    private RpcClient rpcClient;

    private ServiceNameRegister serviceNameRegister;

    public void setServiceNameRegister(ServiceNameRegister serviceNameRegister) {
        this.serviceNameRegister = serviceNameRegister;
    }

    public void setRpcClient(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    @Override
    public Object execute(String serviceId, Object... objects) throws RpcFactoryException, RpcServiceNameRegisterException, InterruptedException, RpcConnectException, RpcSendRequestException, RpcTimeoutException {
        if (serviceNameRegister == null) {
            throw new RpcFactoryException("cant find serviceNameFinder");
        }
        ServiceNameCache serviceNameCache = serviceNameRegister.getServiceNameCache();
        RemoteServiceBean remoteServiceBean = serviceNameCache.getRemoteServiceById(serviceId);
        String address = null;
        if (address == null) {
            throw new RpcServiceNameRegisterException("cant find serviceId :" + serviceId + " address");
        }
        RpcRequestCommand requestCommand = new RpcRequestCommand(remoteServiceBean.getBeanName(), remoteServiceBean.getMethodName(),objects);
        return rpcClient.invokeSync(address, requestCommand, remoteServiceBean.getInvokeTime());
    }

    @Override
    public Object execute(String address, String serviceId, Object... objects) throws RpcFactoryException {
        return null;
    }

}
