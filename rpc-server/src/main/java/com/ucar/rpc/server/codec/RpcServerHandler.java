package com.ucar.rpc.server.codec;

import com.ucar.rpc.common.RemotingUtil;
import com.ucar.rpc.common.helper.RemotingHelper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangyong on 16/1/1.
 */
public class RpcServerHandler extends ChannelInboundHandlerAdapter {

    private final static Logger logger = LoggerFactory.getLogger(RpcServerHandler.class);

    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
        logger.info("rpc server PIPELINE: channelRegistered {}", remoteAddress);
        ctx.fireChannelRegistered();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        ctx.channel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
        final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
        logger.warn("rpc server pipeline: exceptionCaught {}", remoteAddress);
        logger.warn("rpc server PIPELINE: exceptionCaught exception.", e);
        ctx.channel().close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

    }

}
