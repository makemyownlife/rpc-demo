package com.ucar.rpc.server.netty;

/**
 * 整个系统的配置
 * Created by zhangyong on 16/1/1.
 */
public class RpcSystemConfig {

    public static final int SYSTEM_SOCKET_SNDBUFSIZE = 65535;

    public static final int SYSTEM_SOCKET_RCVBUFSIZE = 65535;

    public static final int SYSTEM_CLIENT_ASYNC_SEMAPHORE_VALUE = 2048;

    public static int CLIENT_ONEWAY_SEMAPHORE_VALUE = 2048;

}
