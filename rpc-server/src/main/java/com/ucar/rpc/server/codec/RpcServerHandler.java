package com.ucar.rpc.server.codec;

import com.ucar.rpc.common.helper.RemotingHelper;
import com.ucar.rpc.server.BeanLocator;
import com.ucar.rpc.server.protocol.ResponseStatus;
import com.ucar.rpc.server.protocol.RpcRequestCommand;
import com.ucar.rpc.server.protocol.RpcResponseCommand;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

/**
 * Created by zhangyong on 16/1/1.
 */
public class RpcServerHandler extends ChannelInboundHandlerAdapter {

    private final static Logger logger = LoggerFactory.getLogger(RpcServerHandler.class);

    private ExecutorService executorService;

    private BeanLocator beanLocator;

    private static Map<String, Method> CACHE_METHOD = new ConcurrentHashMap<String, Method>();

    public RpcServerHandler(ExecutorService executorService, BeanLocator beanLocator) {
        this.executorService = executorService;
        this.beanLocator = beanLocator;
    }

    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
        logger.info("rpc server pipeline: channelRegistered {}", remoteAddress);
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
        logger.warn("rpc server pipeline: exceptionCaught exception.", e);
        ctx.channel().close();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
        final RpcRequestCommand requestCommand = (RpcRequestCommand) msg;
        if (executorService != null) {
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    try {
                        ResponseStatus responseStatus = null;
                        RpcResponseCommand responseCommand = null;
                        boolean nobean = false;
                        if (beanLocator != null) {
                            Object instance = beanLocator.getBean(requestCommand.getBeanName());
                            if (instance == null) {
                                nobean = true;
                            } else {
                                Object[] arguments = requestCommand.getArguments();
                                String methodName = requestCommand.getMethodName();
                                Class clazz = instance.getClass();
                                Method method = null;
                                //调用methodName
                                StringBuilder methodKeyBuilder = new StringBuilder();
                                methodKeyBuilder.append(clazz.getCanonicalName()).append("#");
                                methodKeyBuilder.append(requestCommand.getMethodName()).append("$");
                                Class<?>[] argTypeClasses = new Class<?>[arguments.length];
                                for (int i = 0; i < requestCommand.getArguments().length; i++) {
                                    Class paramTypeClass = arguments[i].getClass();
                                    methodKeyBuilder.append(paramTypeClass.getCanonicalName()).append("_");
                                    argTypeClasses[i] = paramTypeClass;
                                }
                                method = CACHE_METHOD.get(methodKeyBuilder.toString());
                                // cache cant find
                                if (method == null) {
                                    method = clazz.getMethod(methodName, argTypeClasses);
                                    if (method.isAccessible()) {
                                        CACHE_METHOD.put(methodKeyBuilder.toString(), method);
                                    }
                                }
                                //recheck again
                                if (method == null) {
                                    nobean = true;
                                } else {
                                    Object result = null;
                                    try {
                                        result = method.invoke(instance, arguments);
                                        responseStatus = ResponseStatus.NO_ERROR;
                                    } catch (Throwable e) {
                                        logger.error("rpc hanlder method invoke error:", e);
                                        responseStatus = ResponseStatus.EXCEPTION;
                                    }
                                    responseCommand = new RpcResponseCommand(
                                            requestCommand.getOpaque(),
                                            responseStatus,
                                            result);
                                    ctx.writeAndFlush(responseCommand);
                                }
                            }
                        }
                        if (nobean) {
                            responseStatus = ResponseStatus.NO_PROCESSOR;
                            responseCommand = new RpcResponseCommand(
                                    requestCommand.getOpaque(),
                                    responseStatus,
                                    null);
                            ctx.writeAndFlush(responseCommand);
                        }
                    } catch (Exception e) {
                        logger.error("rpc server handler exception : ", e);
                        final RpcResponseCommand responseCommand = new RpcResponseCommand(
                                requestCommand.getOpaque(),
                                ResponseStatus.ERROR,
                                null);
                        ctx.writeAndFlush(responseCommand);
                    }
                }
            };
            try {
                executorService.submit(task);
            } catch (RejectedExecutionException e) {
                final RpcResponseCommand responseCommand = new RpcResponseCommand(
                        requestCommand.getOpaque(),
                        ResponseStatus.THREADPOOL_BUSY,
                        null);
                ctx.writeAndFlush(responseCommand);
            }
        }
    }


}