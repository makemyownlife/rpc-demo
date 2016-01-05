package com.ucar.rpc.client.example;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangyong on 16/1/3.
 */
public interface Hello {

    public String sayHello(String name);

    public String sayHello(String name, Integer age);

    public String sayHello(String name, String age);

    public Map sayHello(HashMap name);

    public Map testUser(User user);

}
