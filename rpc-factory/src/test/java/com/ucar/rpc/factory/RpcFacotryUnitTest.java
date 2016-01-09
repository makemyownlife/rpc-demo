package com.ucar.rpc.factory;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by zhangyong on 16/1/9.
 */
public class RpcFacotryUnitTest {

    private ApplicationContext context;

    @Before
    public void setUp() {
        this.context = new ClassPathXmlApplicationContext("classpath:spring.xml");
    }

    @Test
    public void testServiceFinder() {
        RpcClientFactory rpcClientFactory = (RpcClientFactory) context.getBean("clientFactory");
    }


}
