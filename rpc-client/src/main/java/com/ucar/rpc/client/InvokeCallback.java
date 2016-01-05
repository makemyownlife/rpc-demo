package com.ucar.rpc.client;
import com.ucar.rpc.client.netty.ResponseFuture;

/**
 * 异步调用应答回调接口
 */
public interface InvokeCallback {

    public void operationComplete(final ResponseFuture responseFuture);

}
