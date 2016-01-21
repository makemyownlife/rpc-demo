package com.ucar.rpc.factory.impl;

import org.I0Itec.zkclient.IZkChildListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: zhangyong
 * Date: 2016/1/21
 * Time: 21:34
 * To change this template use File | Settings | File Templates.
 */
public class ZkServiceNameCache {

    private final Logger logger = LoggerFactory.getLogger(ZkServiceNameCache.class);

    private ConcurrentHashMap<String, List<String>> serverCache = new ConcurrentHashMap<String, List<String>>();

    public class ZkServiceNameCacheListener implements IZkChildListener {
        @Override
        public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
            serverCache.put(parentPath, currentChilds);
        }
    }

}
