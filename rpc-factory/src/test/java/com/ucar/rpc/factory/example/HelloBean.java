package com.ucar.rpc.factory.example;

/**
 * Created by zhangyong on 16/1/30.
 */
public class HelloBean {

    public String sayHello(String str) {
        System.out.println("helloBean:" + str);
        return "lilin";
    }

}
