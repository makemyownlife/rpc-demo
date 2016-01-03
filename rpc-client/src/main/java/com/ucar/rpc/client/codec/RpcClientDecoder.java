package com.ucar.rpc.client.codec;

import com.ucar.rpc.common.utils.Hessian1Utils;
import com.ucar.rpc.server.protocol.ResponseStatus;
import com.ucar.rpc.server.protocol.ResponseStatusCode;
import com.ucar.rpc.server.protocol.RpcResponseCommand;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Created by zhangyong on 16/1/3.
 */
public class RpcClientDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> objects) throws Exception {
        //magicNumber  opaque   status object-length    Object
        // 1         4          2        4             对象
        if (in.readableBytes() < 1 + 4 + 2 + 4) {
            return;
        }
        int originIndex = in.readerIndex();
        byte magicNumber = in.readByte();
        int opaque = in.readInt();
        ResponseStatus responseStatus = ResponseStatusCode.valueOf(in.readShort());
        if (in.readableBytes() < 4) {
            in.readerIndex(originIndex);
            return;
        }
        int resultLength = in.readInt();
        if (in.readableBytes() < resultLength) {
            in.readerIndex(originIndex);
            return;
        }
        byte[] resultData = new byte[resultLength];
        in.readBytes(resultData);
        Object result = Hessian1Utils.decodeObject(resultData);
        RpcResponseCommand responseCommand = new RpcResponseCommand(opaque, responseStatus, result);
        objects.add(responseCommand);
    }

}
