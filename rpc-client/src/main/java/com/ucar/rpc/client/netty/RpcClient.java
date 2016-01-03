package com.ucar.rpc.client.netty;

import com.ucar.rpc.client.RemotingClientService;
import com.ucar.rpc.client.codec.RpcClientDecoder;
import com.ucar.rpc.client.codec.RpcClientEncoder;
import com.ucar.rpc.client.codec.RpcClientHandler;
import com.ucar.rpc.server.RemotingService;
import com.ucar.rpc.server.RpcServiceHook;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * rpc客户端实现
 * Created by zhangyong on 16/1/2.
 */
public class RpcClient implements RemotingClientService {

    private final static Logger logger = LoggerFactory.getLogger(RpcClient.class);

    private final Bootstrap bootstrap = new Bootstrap();

    private final ExecutorService publicExecutor;

    private final EventLoopGroup eventLoopGroupWorker;

    private DefaultEventExecutorGroup defaultEventExecutorGroup;

    private final RpcClientConfig rpcClientConfig;

    public RpcClient(final RpcClientConfig rpcClientConfig) {
        this.rpcClientConfig = rpcClientConfig;
        int publicThreadNums = rpcClientConfig.getClientCallbackExecutorThreads();
        if (publicThreadNums <= 0) {
            publicThreadNums = 4;
        }
        this.publicExecutor = Executors.newFixedThreadPool(publicThreadNums, new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "RpcClientPublicExecutor_" + this.threadIndex.incrementAndGet());
            }
        });
        this.eventLoopGroupWorker = new NioEventLoopGroup(1, new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, String.format("RpcClientSelector_%d",
                        this.threadIndex.incrementAndGet()));
            }
        });
    }

    @Override
    public void start() {
        this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(//
                rpcClientConfig.getClientWorkerThreads(), //
                new ThreadFactory() {
                    private AtomicInteger threadIndex = new AtomicInteger(0);
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "NettyClientWorkerThread_" + this.threadIndex.incrementAndGet());
                    }
                });
        Bootstrap handler = this.bootstrap.group(this.eventLoopGroupWorker).channel(NioSocketChannel.class)//
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, false)
                .option(ChannelOption.SO_SNDBUF, rpcClientConfig.getClientSocketSndBufSize())
                .option(ChannelOption.SO_RCVBUF, rpcClientConfig.getClientSocketRcvBufSize())
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(
                                defaultEventExecutorGroup,
                                new RpcClientEncoder(),
                                new RpcClientDecoder(),
                                new IdleStateHandler(0, 0, rpcClientConfig.getClientChannelMaxIdleTimeSeconds()),
                                new RpcClientHandler());
                    }
                });
    }

    @Override
    public void shutdown() {
        try {
            this.eventLoopGroupWorker.shutdownGracefully();
            if (this.defaultEventExecutorGroup != null) {
                this.defaultEventExecutorGroup.shutdownGracefully();
            }
        }
        catch (Exception e) {
            logger.error("rpcClient shutdown exception, ", e);
        }
        if (this.publicExecutor != null) {
            try {
                this.publicExecutor.shutdown();
            }
            catch (Exception e) {
                logger.error("rpcClient shutdown exception, ", e);
            }
        }
    }

}
