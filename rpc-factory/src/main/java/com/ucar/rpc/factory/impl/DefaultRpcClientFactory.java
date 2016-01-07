package com.ucar.rpc.factory.impl;

import com.ucar.rpc.common.exception.RpcFactoryException;
import com.ucar.rpc.factory.RpcClientFactory;
import com.ucar.rpc.factory.ServiceNameFinder;

/**
 * Created by zhangyong on 16/1/7.
 */
public class DefaultRpcClientFactory implements RpcClientFactory {

    private ServiceNameFinder serviceNameFinder;

    public void setServiceNameFinder(ServiceNameFinder serviceNameFinder) {
        this.serviceNameFinder = serviceNameFinder;
    }

    @Override
    public Object execute(String serviceId, Object... objects) throws RpcFactoryException {
        if(serviceNameFinder == null) {
            throw new RpcFactoryException("cant find serviceNameFinder");
        }
        
        return null;
    }

}
