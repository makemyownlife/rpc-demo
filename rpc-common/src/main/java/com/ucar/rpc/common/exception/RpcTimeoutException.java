package com.ucar.rpc.common.exception;

/**
 * Created by zhangyong on 16/1/3.
 */
public class RpcTimeoutException extends RpcException {

    private static final long serialVersionUID = 4106899185095245979L;

    public RpcTimeoutException(String message) {
        super(message);
    }

    public RpcTimeoutException(String addr, long timeoutMillis) {
        this(addr, timeoutMillis, null);
    }

    public RpcTimeoutException(String addr, long timeoutMillis, Throwable cause) {
        super("wait response on the channel <" + addr + "> timeout, " + timeoutMillis + "(ms)", cause);
    }

}
