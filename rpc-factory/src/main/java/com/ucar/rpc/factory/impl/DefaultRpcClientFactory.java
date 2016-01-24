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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangyong on 16/1/7.
 */
public class DefaultRpcClientFactory implements RpcClientFactory {

    private final static Logger logger = LoggerFactory.getLogger(DefaultRpcClientFactory.class);

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
            throw new RpcFactoryException("can't find serviceNameFinder");
        }
        ServiceNameCache serviceNameCache = serviceNameRegister.getServiceNameCache();
        RemoteServiceBean remoteServiceBean = serviceNameCache.getRemoteServiceById(serviceId);
        if (remoteServiceBean == null) {
            logger.error("can't find serviceId:{} may be you should register first ", serviceId);
            throw new RpcServiceNameRegisterException("can't getRemoteServiceById :" + serviceId);
        }
        String address = serviceNameCache.getRemoteAddressById(serviceId);
        if (address == null) {
            throw new RpcServiceNameRegisterException("can't find serviceId :" + serviceId + " address");
        }
        RpcRequestCommand requestCommand = new RpcRequestCommand(remoteServiceBean.getBeanName(), remoteServiceBean.getMethodName(), objects);
        return rpcClient.invokeSync(address, requestCommand, remoteServiceBean.getInvokeTime());
    }

    @Override
    public Object executeUrl(String address, String serviceId, Object... objects) throws RpcFactoryException, RpcServiceNameRegisterException, InterruptedException, RpcConnectException, RpcSendRequestException, RpcTimeoutException {
        return null;
    }

}
