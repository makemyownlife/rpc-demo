package com.ucar.rpc.server.netty;

import com.ucar.rpc.server.RemotingService;
import com.ucar.rpc.server.RpcServiceHook;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    }

    @Override
    public void start() {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public void registerRPCHook(RpcServiceHook rpcServiceHook) {

    }
}
