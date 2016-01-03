package com.ucar.rpc.server.protocol;

import com.ucar.rpc.common.buffer.IoBuffer;

/**
 * Created by zhangyong on 16/1/2.
 */
public class RpcResponseCommand implements RpcCommand {

    private static final long serialVersionUID = -1L;

    private Integer opaque;

    private Object result;

    public boolean decode(IoBuffer buffer) {
        return false;
    }

    @Override
    public IoBuffer encode() {
        return null;
    }

}
