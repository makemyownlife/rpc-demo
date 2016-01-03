package com.ucar.rpc.client.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangyong on 16/1/3.
 */
public class RpcClientEncoder extends MessageToByteEncoder {

    private final static Logger logger = LoggerFactory.getLogger(RpcClientEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {

    }

}
