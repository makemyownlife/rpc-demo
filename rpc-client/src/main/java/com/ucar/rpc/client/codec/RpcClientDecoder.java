package com.ucar.rpc.client.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Created by zhangyong on 16/1/3.
 */
public class RpcClientDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> objects) throws Exception {

    }

}
