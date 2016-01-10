package com.ucar.rpc.factory.impl;

import com.ucar.rpc.client.netty.RpcClient;
import com.ucar.rpc.factory.RpcClientFactory;
import com.ucar.rpc.factory.ServiceNameRegister;
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
    public Object execute(String serviceId, Object... objects) throws RpcFactoryException, RpcServiceNameRegisterException {
        if(serviceNameRegister == null) {
            throw new RpcFactoryException("cant find serviceNameFinder");
        }
        String address = serviceNameRegister.getRegisterAdress(serviceId);
        if(address == null) {
            throw new RpcServiceNameRegisterException("cant find serviceId :" + serviceId + " address");
        }
        return null;
    }

    @Override
    public Object execute(String address, String serviceId, Object... objects) throws RpcFactoryException {
        return null;
    }

}
