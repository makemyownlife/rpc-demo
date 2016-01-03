package com.ucar.rpc.client;

import java.util.HashMap;
import java.util.Map;

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
    public String sayHello(String name, String age) {
        return "hello lilin,wtafsadf:" + age;
    }

    @Override
    public Map sayHello(Map name) {
        System.out.println(name);
        name.put("he", 12);
        return name;
    }

    public Map testUser(User user) {
        System.out.println("user: +" + user);
        Map map = new HashMap();
        map.put("hall", user);
        return map;
    }

}
