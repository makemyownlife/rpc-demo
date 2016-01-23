package com.ucar.rpc.factory;

import com.ucar.rpc.factory.bean.RemoteServiceBean;

/**
 * Created with IntelliJ IDEA.
 * User: zhangyong
 * Date: 2016/1/22
 * Time: 22:20
 * To change this template use File | Settings | File Templates.
 */
public interface ServiceNameCache {

    public RemoteServiceBean getRemoteServiceById(String serviceId);

}
