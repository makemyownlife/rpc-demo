package com.ucar.rpc.common.exception;

/**
 * Created by zhangyong on 16/1/3.
 */
public class RpcConnectException extends RpcException {

    private static final long serialVersionUID = -5565366231695911316L;

    public RpcConnectException(String addr) {
        this(addr, null);
    }

    public RpcConnectException(String addr, Throwable cause) {
        super("connect to <" + addr + "> failed", cause);
    }

}
