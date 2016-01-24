package com.ucar.rpc.factory;

import com.ucar.rpc.common.exception.RpcConnectException;
import com.ucar.rpc.common.exception.RpcSendRequestException;
import com.ucar.rpc.common.exception.RpcTimeoutException;
import com.ucar.rpc.factory.exception.RpcFactoryException;
import com.ucar.rpc.factory.exception.RpcServiceNameRegisterException;

/**
 * rpc 客户端工厂
 * User: zhangyong
 * Date: 2016/1/7
 * Time: 16:09
 * To change this template use File | Settings | File Templates.
 */
public interface RpcClientFactory {

    public Object execute(String serviceId, Object... objects) throws RpcFactoryException, RpcServiceNameRegisterException, InterruptedException, RpcConnectException, RpcSendRequestException, RpcTimeoutException;

    public Object executeUrl(String address, String serviceId, Object... objects) throws RpcFactoryException, RpcServiceNameRegisterException, InterruptedException, RpcConnectException, RpcSendRequestException, RpcTimeoutException;

}
