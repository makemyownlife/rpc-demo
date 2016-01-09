package com.ucar.rpc.factory;


import com.ucar.rpc.factory.exception.RpcFactoryException;

/**
 * rpc 客户端工厂
 * User: zhangyong
 * Date: 2016/1/7
 * Time: 16:09
 * To change this template use File | Settings | File Templates.
 */
public interface RpcClientFactory {

    public Object execute(String serviceId, Object... objects) throws RpcFactoryException;

    public Object execute(String address, String serviceId, Object... objects) throws RpcFactoryException;

}
