package com.ucar.rpc.factory.impl;

import com.ucar.rpc.client.netty.RpcClient;
import com.ucar.rpc.factory.RpcClientFactory;
import com.ucar.rpc.factory.ServiceNameFinder;
import com.ucar.rpc.factory.exception.RpcFactoryException;

/**
 * Created by zhangyong on 16/1/7.
 */
public class DefaultRpcClientFactory implements RpcClientFactory {

    private RpcClient rpcClient;

    private ServiceNameFinder serviceNameFinder;

    public void setServiceNameFinder(ServiceNameFinder serviceNameFinder) {
        this.serviceNameFinder = serviceNameFinder;
    }

    public void setRpcClient(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    @Override
    public Object execute(String serviceId, Object... objects) throws RpcFactoryException {
        if(serviceNameFinder == null) {
            throw new RpcFactoryException("cant find serviceNameFinder");
        }
        return null;
    }

    @Override
    public Object execute(String address, String serviceId, Object... objects) throws RpcFactoryException {
        return null;
    }


}
