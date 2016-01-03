package com.ucar.rpc.server.protocol;

import com.ucar.rpc.common.buffer.IoBuffer;

/**
 * Created by zhangyong on 16/1/1.
 */
public interface RpcCommand {

    public IoBuffer encode();

}
