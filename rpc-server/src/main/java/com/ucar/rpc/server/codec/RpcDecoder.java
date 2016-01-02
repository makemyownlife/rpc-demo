package com.ucar.rpc.server.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by zhangyong on 16/1/1.
 */
public class RpcDecoder extends ByteToMessageDecoder {

    private final static Logger logger = LoggerFactory.getLogger(RpcDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

    }

}
