package com.ucar.rpc.server.netty;

import com.ucar.rpc.server.RemotingService;
import com.ucar.rpc.server.RpcServiceHook;
import com.ucar.rpc.server.codec.RpcDecoder;
import com.ucar.rpc.server.codec.RpcEncoder;
import com.ucar.rpc.server.codec.RpcServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhangyong on 16/1/1.
 */
public class RpcServer implements RemotingService {

    private final static Logger logger = LoggerFactory.getLogger(RpcServer.class);

    private ServerBootstrap serverBootstrap;

    private final ExecutorService publicExecutor;

    private final EventLoopGroup eventLoopGroupBoss;

    private final EventLoopGroup eventLoopGroupWorker;

    private DefaultEventExecutorGroup defaultEventExecutorGroup;

    private RpcServerConfig rpcServerConfig;

    public RpcServer(final RpcServerConfig rpcServerConfig) {
        this.serverBootstrap = new ServerBootstrap();
        this.rpcServerConfig = rpcServerConfig;

        int publicThreadNums = rpcServerConfig.getServerCallbackExecutorThreads();
        if (publicThreadNums <= 0) {
            publicThreadNums = 4;
        }

        this.publicExecutor = Executors.newFixedThreadPool(publicThreadNums, new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "RpcServerPublicExecutor_" + this.threadIndex.incrementAndGet());
            }
        });

        this.eventLoopGroupBoss = new NioEventLoopGroup(1, new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r,
                        String.format("RpcBossSelector_%d", this.threadIndex.incrementAndGet()));
            }
        });

        this.eventLoopGroupWorker =
                new NioEventLoopGroup(rpcServerConfig.getServerSelectorThreads(), new ThreadFactory() {
                    private AtomicInteger threadIndex = new AtomicInteger(0);
                    private int threadTotal = rpcServerConfig.getServerSelectorThreads();

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, String.format("RpcServerWorkerSelector_%d_%d", threadTotal,
                                this.threadIndex.incrementAndGet()));
                    }
                });

        this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(
                rpcServerConfig.getServerWorkerThreads(),
                new ThreadFactory() {
                    private AtomicInteger threadIndex = new AtomicInteger(0);

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "RpcServerWorkerThread_" + this.threadIndex.incrementAndGet());
                    }
                });
    }

    @Override
    public void start() {
        ServerBootstrap childHandler =
                this.serverBootstrap.group(this.eventLoopGroupBoss, this.eventLoopGroupWorker)
                        .channel(NioServerSocketChannel.class)
                        .option(ChannelOption.SO_BACKLOG, 1024)
                        .option(ChannelOption.SO_REUSEADDR, true)
                        .option(ChannelOption.SO_KEEPALIVE, false)
                        .childOption(ChannelOption.TCP_NODELAY, true)
                        .option(ChannelOption.SO_SNDBUF, rpcServerConfig.getServerSocketSndBufSize())
                        .option(ChannelOption.SO_RCVBUF, rpcServerConfig.getServerSocketRcvBufSize())
                        .localAddress(new InetSocketAddress(this.rpcServerConfig.getListenPort()))
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel ch) throws Exception {
                                ch.pipeline().addLast(
                                        defaultEventExecutorGroup,
                                        new RpcEncoder(),
                                        new RpcDecoder(),
                                        new IdleStateHandler(0, 0, rpcServerConfig
                                                .getServerChannelMaxIdleTimeSeconds()),
                                        new RpcServerHandler());
                            }
                        });

        if (rpcServerConfig.isServerPooledByteBufAllocatorEnable()) {
            // 这个选项有可能会占用大量堆外内存，暂时不使用。
            childHandler.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        }

        logger.info("-----------------开始启动--------------------------");
        try {
            ChannelFuture sync = this.serverBootstrap.bind().sync();
            InetSocketAddress addr = (InetSocketAddress) sync.channel().localAddress();
            logger.info("端口号：" + addr.getPort() + "的服务端已经启动");
        } catch (InterruptedException e1) {
            throw new RuntimeException("this.serverBootstrap.bind().sync() InterruptedException", e1);
        }
        logger.info("-----------------启动结束--------------------------");
    }

    @Override
    public void shutdown() {
        try {
            this.eventLoopGroupBoss.shutdownGracefully();
            this.eventLoopGroupWorker.shutdownGracefully();
            if (this.defaultEventExecutorGroup != null) {
                this.defaultEventExecutorGroup.shutdownGracefully();
            }
        } catch (Exception e) {
            logger.error("RpcServer shutdown exception, ", e);
        }
        if (this.publicExecutor != null) {
            try {
                this.publicExecutor.shutdown();
            } catch (Exception e) {
                logger.error("NettyRemotingServer shutdown exception, ", e);
            }
        }
    }

    @Override
    public void registerRpcHook(RpcServiceHook rpcServiceHook) {

    }

}
