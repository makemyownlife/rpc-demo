package com.ucar.rpc.factory.impl;

import com.ucar.rpc.server.BeanLocator;
import org.springframework.context.support.ApplicationObjectSupport;

/**
 * Created by zhangyong on 16/1/9.
 */
public class SpringBeanLocator extends ApplicationObjectSupport implements BeanLocator {

    @Override
    public Object getBean(String beanName) {
        return getApplicationContext().getBean(beanName);
    }

}
