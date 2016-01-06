package com.ucar.rpc.client.netty;

import com.ucar.rpc.client.InvokeCallback;
import com.ucar.rpc.common.SemaphoreReleaseOnlyOnce;
import com.ucar.rpc.server.protocol.RpcResponseCommand;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by zhangyong on 16/1/3.
 */
public class ResponseFuture {

    private volatile boolean sendRequestOK = true;

    private RpcResponseCommand responseCommand;

    private final int opaque;

    private final long timeoutMillis;

    private volatile Throwable cause;

    private final InvokeCallback invokeCallback;

    private SemaphoreReleaseOnlyOnce semaphoreReleaseOnlyOnce;

    // 保证回调的callback方法至多至少只被执行一次
    private final AtomicBoolean executeCallbackOnlyOnce = new AtomicBoolean(false);

    private final long beginTimestamp = System.currentTimeMillis();

    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    public ResponseFuture(int opaque, long timeoutMillis, InvokeCallback invokeCallback, SemaphoreReleaseOnlyOnce semaphoreReleaseOnlyOnce) {
        this.opaque = opaque;
        this.timeoutMillis = timeoutMillis;
        this.invokeCallback = invokeCallback;
        this.semaphoreReleaseOnlyOnce = semaphoreReleaseOnlyOnce;
    }

    public boolean isTimeout() {
        long diff = System.currentTimeMillis() - this.beginTimestamp;
        return diff > this.timeoutMillis;
    }

    public RpcResponseCommand waitResponse(final long timeoutMillis) throws InterruptedException {
        this.countDownLatch.await(timeoutMillis, TimeUnit.MILLISECONDS);
        return this.responseCommand;
    }


    public void putResponse(final RpcResponseCommand responseCommand) {
        this.responseCommand = responseCommand;
        this.countDownLatch.countDown();
    }

    public void executeInvokeCallback() {
        if (invokeCallback != null) {
            if (this.executeCallbackOnlyOnce.compareAndSet(false, true)) {
                invokeCallback.operationComplete(this);
            }
        }
    }

    public void release() {
        if (this.semaphoreReleaseOnlyOnce != null) {
            this.semaphoreReleaseOnlyOnce.release();
        }
    }

    public long getBeginTimestamp() {
        return beginTimestamp;
    }

    public boolean isSendRequestOK() {
        return sendRequestOK;
    }

    public void setSendRequestOK(boolean sendRequestOK) {
        this.sendRequestOK = sendRequestOK;
    }

    public long getTimeoutMillis() {
        return timeoutMillis;
    }

    public InvokeCallback getInvokeCallback() {
        return invokeCallback;
    }

    public int getOpaque() {
        return opaque;
    }

    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    public void setResponseCommand(RpcResponseCommand responseCommand) {
        this.responseCommand = responseCommand;
    }
}
