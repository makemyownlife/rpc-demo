package com.ucar.rpc.common;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 使用布尔原子变量，信号量保证只释放一次
 * User: zhangyong
 * Date: 2016/1/6
 * Time: 17:47
 * To change this template use File | Settings | File Templates.
 */
public class SemaphoreReleaseOnlyOnce {

    private final AtomicBoolean released = new AtomicBoolean(false);

    private final Semaphore semaphore;

    public SemaphoreReleaseOnlyOnce(Semaphore semaphore) {
        this.semaphore = semaphore;
    }

    public void release() {
        if (this.semaphore != null) {
            if (this.released.compareAndSet(false, true)) {
                this.semaphore.release();
            }
        }
    }

    public Semaphore getSemaphore() {
        return semaphore;
    }

}
