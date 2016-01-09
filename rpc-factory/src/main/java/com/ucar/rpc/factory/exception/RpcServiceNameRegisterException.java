package com.ucar.rpc.factory.exception;

import com.ucar.rpc.common.exception.RpcException;

/**
 * Created by zhangyong on 16/1/7.
 */
public class RpcServiceNameRegisterException extends RpcException  {

    private static final long serialVersionUID = 7106899185095245979L;

    public RpcServiceNameRegisterException(String message) {
        super(message);
    }

    public RpcServiceNameRegisterException(String message, Throwable cause) {
        super(message, cause);
    }
}
