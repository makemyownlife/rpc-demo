package com.ucar.rpc.client;

import com.ucar.rpc.common.exception.RpcConnectException;
import com.ucar.rpc.common.exception.RpcSendRequestException;
import com.ucar.rpc.common.exception.RpcTimeoutException;
import com.ucar.rpc.server.RemotingService;
import com.ucar.rpc.server.protocol.RpcRequestCommand;
import com.ucar.rpc.server.protocol.RpcResponseCommand;

/**
 * Created by zhangyong on 16/1/3.
 */
public interface RemotingClientService extends RemotingService {

    public RpcResponseCommand invokeSync(final String addr,
                                         final RpcRequestCommand request,
                                         final long timeoutMillis) throws InterruptedException, RpcConnectException, RpcTimeoutException, RpcSendRequestException;

}
