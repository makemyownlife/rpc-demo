package com.ucar.rpc.client.example;

import com.ucar.rpc.server.BeanLocator;

/**
 * Created by zhangyong on 16/1/3.
 */
public class MyBeanLocator implements BeanLocator {

    @Override
    public Object getBean(String beanName) {
        if ("hello".equals(beanName)) {
            return new HelloImpl();
        }
        return null;
    }

}
