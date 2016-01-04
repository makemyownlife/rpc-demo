package com.ucar.rpc.server.codec;

import com.ucar.rpc.common.RemotingUtil;
import com.ucar.rpc.common.buffer.IoBuffer;
import com.ucar.rpc.common.helper.RemotingHelper;
import com.ucar.rpc.server.protocol.RpcResponseCommand;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangyong on 16/1/1.
 */
public class RpcServerEncoder extends MessageToByteEncoder {

    private final static Logger logger = LoggerFactory.getLogger(RpcServerEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        RpcResponseCommand responseCommand = (RpcResponseCommand) msg;
        try {
            IoBuffer ioBuffer = responseCommand.encode();
            byte[] buf = ioBuffer.array();
            out.writeBytes(buf);
        } catch (Exception e) {
            logger.error("encode exception, " + RemotingHelper.parseChannelRemoteAddr(ctx.channel()), e);
            if (responseCommand != null) {
                logger.error(responseCommand.toString());
            }
            RemotingUtil.closeChannel(ctx.channel());
        }
    }

}
