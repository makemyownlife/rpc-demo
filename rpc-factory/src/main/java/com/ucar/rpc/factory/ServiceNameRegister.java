package com.ucar.rpc.factory;

/**
 * Created by zhangyong on 16/1/7.
 */
public interface ServiceNameRegister {

    //初始化方法(仅执行一次)
    public void registerService() throws Exception;

    //关闭时调用(仅执行一次)
    public void unRegisterService() throws Exception;

    public String getRegisterAdress(String serviceId);

}
