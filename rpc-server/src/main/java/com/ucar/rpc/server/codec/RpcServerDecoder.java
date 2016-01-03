package com.ucar.rpc.server.codec;

import com.ucar.rpc.common.RemotingUtil;
import com.ucar.rpc.common.helper.RemotingHelper;
import com.ucar.rpc.common.utils.Hessian1Utils;
import com.ucar.rpc.server.protocol.RpcRequestCommand;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by zhangyong on 16/1/1.
 */
public class RpcServerDecoder extends ByteToMessageDecoder {

    private final static Logger logger = LoggerFactory.getLogger(RpcServerDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        try {
//            magicNumber  opaque   beanName-length   beanName  method-length methodName params-length params
//            1              4            4                        4                          4
            if (in.readableBytes() < 1 + 4 + 4 + 4 + 4) {
                return;
            }
            int originIndex = in.readerIndex();
            byte magicNumber = in.readByte();
            int opaque = in.readInt();
            int beanNameLength = in.readInt();
            if (in.readableBytes() < beanNameLength) {
                in.readerIndex(originIndex);
                return;
            }
            byte[] beanNameBytes = new byte[beanNameLength];
            in.readBytes(beanNameBytes);
            if (in.readableBytes() < 4) {
                in.readerIndex(originIndex);
                return;
            }
            int methodLength = in.readInt();
            if (in.readableBytes() < methodLength) {
                in.readerIndex(originIndex);
                return;
            }
            byte[] methodNameBytes = new byte[methodLength];
            in.readBytes(methodNameBytes);
            if (in.readableBytes() < 4) {
                in.readerIndex(originIndex);
                return;
            }
            int paramcount = in.readInt();
            if (in.readableBytes() < 4) {
                in.readerIndex(originIndex);
                return;
            }
            int paramDataLenght = in.readInt();
            if (in.readableBytes() < 4) {
                in.readerIndex(originIndex);
                return;
            }
            if (in.readableBytes() < paramDataLenght) {
                in.readerIndex(originIndex);
                return;
            }
            byte[] paramsBytes = new byte[paramDataLenght];
            in.readBytes(paramsBytes);

            Object[] params = Hessian1Utils.decodeObject(paramsBytes, paramcount);
            String beanName = new String(beanNameBytes);
            String methodName = new String(methodNameBytes);

            RpcRequestCommand rpcRequestCommand = new RpcRequestCommand(opaque, beanName, methodName, params);
            out.add(rpcRequestCommand);
        } catch (Exception e) {
            logger.error("decode exception, " + RemotingHelper.parseChannelRemoteAddr(ctx.channel()), e);
            RemotingUtil.closeChannel(ctx.channel());
        }
    }

}
