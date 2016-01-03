package com.ucar.rpc.client.netty;

import com.ucar.rpc.client.RemotingClientService;
import com.ucar.rpc.client.codec.RpcClientDecoder;
import com.ucar.rpc.client.codec.RpcClientEncoder;
import com.ucar.rpc.client.codec.RpcClientHandler;
import com.ucar.rpc.common.RemotingUtil;
import com.ucar.rpc.common.exception.RpcConnectException;
import com.ucar.rpc.common.exception.RpcSendRequestException;
import com.ucar.rpc.common.exception.RpcTimeoutException;
import com.ucar.rpc.common.helper.RemotingHelper;
import com.ucar.rpc.server.protocol.RpcRequestCommand;
import com.ucar.rpc.server.protocol.RpcResponseCommand;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * rpc客户端实现
 * Created by zhangyong on 16/1/2.
 */
public class RpcClient implements RemotingClientService {

    private final static Logger logger = LoggerFactory.getLogger(RpcClient.class);

    private final Bootstrap bootstrap = new Bootstrap();

    private static final long LOCK_TIMEOUT_MILLIS = 5000;

    private final ExecutorService publicExecutor;

    private final EventLoopGroup eventLoopGroupWorker;

    private DefaultEventExecutorGroup defaultEventExecutorGroup;

    private final RpcClientConfig rpcClientConfig;

    protected final ConcurrentHashMap<Integer /* opaque */, ResponseFuture> responseTable =
            new ConcurrentHashMap<Integer, ResponseFuture>(256);

    //链接缓存
    private final Lock lockChannelTables = new ReentrantLock();
    //链接相关的内容
    private final ConcurrentHashMap<String /* addr */, ChannelWrapper> channelTables =
            new ConcurrentHashMap<String, ChannelWrapper>();

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
        } catch (Exception e) {
            logger.error("rpcClient shutdown exception, ", e);
        }
        if (this.publicExecutor != null) {
            try {
                this.publicExecutor.shutdown();
            } catch (Exception e) {
                logger.error("rpcClient shutdown exception, ", e);
            }
        }
    }

    @Override
    public RpcResponseCommand invokeSync(String addr, final RpcRequestCommand request, long timeoutMillis) throws InterruptedException, RpcConnectException, RpcTimeoutException, RpcSendRequestException {
        final Channel channel = this.getAndCreateChannel(addr);
        if (channel == null || !channel.isActive()) {
            this.closeChannel(addr, channel);
            throw new RpcConnectException(addr);
        }
        try {
            final ResponseFuture responseFuture = new ResponseFuture(request.getOpaque(), timeoutMillis, null);
            this.responseTable.put(request.getOpaque(), responseFuture);
            channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture f) throws Exception {
                    if (f.isSuccess()) {
                        responseFuture.setSendRequestOK(true);
                        return;
                    } else {
                        responseFuture.setSendRequestOK(false);
                    }
                    responseTable.remove(request.getOpaque());
                    responseFuture.setCause(f.cause());
                    responseFuture.putResponse(null);
                    logger.warn("send a request command to channel <" + channel.remoteAddress() + "> failed.");
                    logger.warn(request.toString());
                }
            });

            RpcResponseCommand responseCommand = responseFuture.waitResponse(timeoutMillis);
            if (null == responseCommand) {
                if (responseFuture.isSendRequestOK()) {
                    throw new RpcTimeoutException(RemotingHelper.parseChannelRemoteAddr(channel), timeoutMillis, responseFuture.getCause());
                } else {
                    throw new RpcSendRequestException(RemotingHelper.parseChannelRemoteAddr(channel), responseFuture.getCause());
                }
            }
            return responseCommand;
        } finally {
            this.responseTable.remove(request.getOpaque());
        }
    }

    private Channel getAndCreateChannel(final String addr) throws InterruptedException {
        ChannelWrapper cw = this.channelTables.get(addr);
        if (cw != null && cw.isOK()) {
            return cw.getChannel();
        }
        return this.createChannel(addr);
    }

    private Channel createChannel(final String addr) throws InterruptedException {
        ChannelWrapper cw = this.channelTables.get(addr);
        if (cw != null && cw.isOK()) {
            return cw.getChannel();
        }
        // 进入临界区后，不能有阻塞操作，网络连接采用异步方式
        if (this.lockChannelTables.tryLock(LOCK_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
            try {
                boolean createNewConnection = false;
                cw = this.channelTables.get(addr);
                if (cw != null) {
                    // channel正常
                    if (cw.isOK()) {
                        return cw.getChannel();
                    }
                    // 正在连接，退出锁等待
                    else if (!cw.getChannelFuture().isDone()) {
                        createNewConnection = false;
                    }
                    // 说明连接不成功
                    else {
                        this.channelTables.remove(addr);
                        createNewConnection = true;
                    }
                }
                // ChannelWrapper不存在
                else {
                    createNewConnection = true;
                }
                if (createNewConnection) {
                    ChannelFuture channelFuture = this.bootstrap.connect(RemotingHelper.string2SocketAddress(addr));
                    logger.info("createChannel: begin to connect remote host[{}] asynchronously", addr);
                    cw = new ChannelWrapper(channelFuture);
                    this.channelTables.put(addr, cw);
                }
            } catch (Exception e) {
                logger.error("createChannel: create channel exception", e);
            } finally {
                this.lockChannelTables.unlock();
            }
        } else {
            logger.warn("createChannel: try to lock channel table, but timeout, {}ms", LOCK_TIMEOUT_MILLIS);
        }
        if (cw != null) {
            ChannelFuture channelFuture = cw.getChannelFuture();
            if (channelFuture.awaitUninterruptibly(this.rpcClientConfig.getConnectTimeoutMillis())) {
                if (cw.isOK()) {
                    logger.info("createChannel: connect remote host[{}] success, {}", addr, channelFuture.toString());
                    return cw.getChannel();
                } else {
                    logger.warn("createChannel: connect remote host[" + addr + "] failed, " + channelFuture.toString(), channelFuture.cause());
                }
            } else {
                logger.warn("createChannel: connect remote host[{}] timeout {}ms, {}", addr, this.rpcClientConfig.getConnectTimeoutMillis(), channelFuture.toString());
            }
        }
        return null;
    }


    public void closeChannel(final String addr, final Channel channel) {
        if (null == channel) {
            return;
        }
        final String addrRemote = null == addr ? RemotingHelper.parseChannelRemoteAddr(channel) : addr;
        try {
            if (this.lockChannelTables.tryLock(LOCK_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
                try {
                    boolean removeItemFromTable = true;
                    final ChannelWrapper prevCW = this.channelTables.get(addrRemote);
                    logger.info("closeChannel: begin close the channel[{}] Found: {}", addrRemote, (prevCW != null));
                    if (null == prevCW) {
                        logger.info("closeChannel: the channel[{}] has been removed from the channel table before", addrRemote);
                        removeItemFromTable = false;
                    } else if (prevCW.getChannel() != channel) {
                        logger.info("closeChannel: the channel[{}] has been closed before, and has been created again, nothing to do.", addrRemote);
                        removeItemFromTable = false;
                    }
                    if (removeItemFromTable) {
                        this.channelTables.remove(addrRemote);
                        logger.info("closeChannel: the channel[{}] was removed from channel table", addrRemote);
                    }
                    RemotingUtil.closeChannel(channel);
                } catch (Exception e) {
                    logger.error("closeChannel: close the channel exception", e);
                } finally {
                    this.lockChannelTables.unlock();
                }
            } else {
                logger.warn("closeChannel: try to lock channel table, but timeout, {}ms", LOCK_TIMEOUT_MILLIS);
            }
        } catch (InterruptedException e) {
            logger.error("closeChannel exception", e);
        }
    }

    public void closeChannel(final Channel channel) {
        if (null == channel) {
            return;
        }
        try {
            if (this.lockChannelTables.tryLock(LOCK_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
                try {
                    boolean removeItemFromTable = true;
                    ChannelWrapper prevCW = null;
                    String addrRemote = null;
                    for (String key : channelTables.keySet()) {
                        ChannelWrapper prev = this.channelTables.get(key);
                        if (prev.getChannel() != null) {
                            if (prev.getChannel() == channel) {
                                prevCW = prev;
                                addrRemote = key;
                                break;
                            }
                        }
                    }
                    if (null == prevCW) {
                        logger.info("eventCloseChannel: the channel[{}] has been removed from the channel table before", addrRemote);
                        removeItemFromTable = false;
                    }
                    if (removeItemFromTable) {
                        this.channelTables.remove(addrRemote);
                        logger.info("closeChannel: the channel[{}] was removed from channel table", addrRemote);
                        RemotingUtil.closeChannel(channel);
                    }
                } catch (Exception e) {
                    logger.error("closeChannel: close the channel exception", e);
                } finally {
                    this.lockChannelTables.unlock();
                }
            } else {
                logger.warn("closeChannel: try to lock channel table, but timeout, {}ms", LOCK_TIMEOUT_MILLIS);
            }
        } catch (InterruptedException e) {
            logger.error("closeChannel exception", e);
        }
    }

}
