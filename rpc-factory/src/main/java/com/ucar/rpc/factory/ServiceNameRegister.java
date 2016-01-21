package com.ucar.rpc.factory;

/**
 * Created by zhangyong on 16/1/7.
 */
public interface ServiceNameRegister {

    //初始化方法
    public void registerService() throws Exception;

    //关闭时调用
    public void unRegisterService() throws Exception;

}
