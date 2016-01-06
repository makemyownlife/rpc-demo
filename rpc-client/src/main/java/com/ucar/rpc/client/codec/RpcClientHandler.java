package com.ucar.rpc.client.codec;

import com.ucar.rpc.client.netty.ResponseFuture;
import com.ucar.rpc.client.netty.RpcClient;
import com.ucar.rpc.server.protocol.RpcResponseCommand;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

/**
 * Created by zhangyong on 16/1/3.
 */
public class RpcClientHandler extends ChannelInboundHandlerAdapter {

    private final static Logger logger = LoggerFactory.getLogger(RpcClientHandler.class);

    private RpcClient rpcClient;

    public RpcClientHandler(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        ctx.channel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e)
            throws Exception {
        if (!(e.getCause() instanceof IOException)) {
            logger.error("catch some exception not IOException", e);
        }
        ctx.channel().close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcResponseCommand responseCommand = (RpcResponseCommand) msg;
        final ResponseFuture responseFuture = rpcClient.getResponseTable().get(responseCommand.getOpaque());
        if (responseFuture != null) {
            responseFuture.setResponseCommand(responseCommand);
            rpcClient.getResponseTable().remove(responseCommand.getOpaque());
            if (responseFuture.getInvokeCallback() != null) {
                boolean runInThisThread = false;
                ExecutorService executor = rpcClient.getPublicExecutor();
                if (executor != null) {
                    try {
                        executor.submit(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    responseFuture.executeInvokeCallback();
                                } catch (Throwable e) {
                                    logger.warn("excute callback in executor exception, and callback throw", e);
                                }
                            }
                        });
                    } catch (Exception e) {
                        runInThisThread = true;
                        logger.warn("excute callback in executor exception, maybe executor busy", e);
                    }
                } else {
                    runInThisThread = true;
                }
                if (runInThisThread) {
                    try {
                        responseFuture.executeInvokeCallback();
                    }
                    catch (Throwable e) {
                        logger.warn("executeInvokeCallback Exception", e);
                    }
                }
            } else {
                responseFuture.putResponse(responseCommand);
            }
        }
    }

}
