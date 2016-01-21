package com.ucar.rpc.factory.impl;

import org.I0Itec.zkclient.IZkChildListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

/**
 * zk名字空间 存储模块节点 对应的服务器列表
 * User: zhangyong
 * Date: 2016/1/21
 * Time: 11:30
 * To change this template use File | Settings | File Templates.
 */
public class ZkServiceNameCacheListener implements IZkChildListener {

    private final static Logger logger = LoggerFactory.getLogger(ZkServiceNameCacheListener.class);

    @Override
    public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {

    }

}
