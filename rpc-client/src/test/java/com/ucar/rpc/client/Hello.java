package com.ucar.rpc.client;

/**
 * Created by zhangyong on 16/1/3.
 */
public interface Hello {

    public String sayHello(String name);

    public String sayHello(String name, Integer age);

    public String sayHello(String name, String age);

}
