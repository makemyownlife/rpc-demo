package com.ucar.rpc.client;

/**
 * Created by zhangyong on 16/1/3.
 */
public class HelloImpl implements Hello {

    @Override
    public String sayHello(String name) {
        return "hello ,mylife welcome to the world ,name:" + name;
    }

    @Override
    public String sayHello(String name, Integer age) {
        return "hello lilin,age:" + age;
    }

    @Override
    public String sayHello(String name, String  age) {
        return "hello lilin,wtafsadf:" + age;
    }

}
