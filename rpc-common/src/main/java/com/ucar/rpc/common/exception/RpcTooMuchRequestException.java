package com.ucar.rpc.common.exception;

/**
 * Created by zhangyong on 16/1/3.
 */
public class RpcTooMuchRequestException extends RpcException {

    private static final long serialVersionUID = 4326919581254519654L;

    public RpcTooMuchRequestException(String message) {
        super(message);
    }

}
