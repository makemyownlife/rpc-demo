package com.ucar.rpc.common.utils;

/**
 * Created by zhangyong on 16/1/2.
 */
public class OpaqueGenerator {

    private static int opaque = Integer.MIN_VALUE;

    synchronized static void setOpaque(int target) {
        opaque = target;
    }

    public static final synchronized int getNextOpaque() {
        if (opaque >= Integer.MAX_VALUE - 10) {
            resetOpaque();
        }
        return opaque++;
    }

    public synchronized static void resetOpaque() {
        opaque = Integer.MIN_VALUE;
    }

    synchronized static int getCurrentOpaque() {
        return opaque;
    }

}
