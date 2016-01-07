package com.ucar.rpc.common.exception;

/**
 * Created by zhangyong on 16/1/7.
 */
public class RpcFactoryException extends RpcException  {

    private static final long serialVersionUID = 2106899185095245979L;

    public RpcFactoryException(String message) {
        super(message);
    }

    public RpcFactoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
