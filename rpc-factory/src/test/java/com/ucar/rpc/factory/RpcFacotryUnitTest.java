package com.ucar.rpc.factory;

import com.alibaba.fastjson.JSON;
import com.ucar.rpc.common.exception.RpcConnectException;
import com.ucar.rpc.common.exception.RpcSendRequestException;
import com.ucar.rpc.common.exception.RpcTimeoutException;
import com.ucar.rpc.factory.bean.RemoteServiceBean;
import com.ucar.rpc.factory.exception.RpcFactoryException;
import com.ucar.rpc.factory.exception.RpcServiceNameRegisterException;
import com.ucar.rpc.factory.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangyong on 16/1/9.
 */
public class RpcFacotryUnitTest {

    private ApplicationContext context;

    private String serviceId = "cdms.sayHello";

    public void createModuleRemoteService() throws Exception {
        ZkClient zkClient = new ZkClient("localhost:2181", 30000, 150000, new ZkUtils.StringSerializer());
        List list = new ArrayList();
        RemoteServiceBean remoteServiceBean = new RemoteServiceBean();
        remoteServiceBean.setBeanName("hello");
        remoteServiceBean.setMethodName("sayHello");
        remoteServiceBean.setInvokeTime(15000L);
        remoteServiceBean.setServiceId(serviceId);
        list.add(remoteServiceBean);
        ZkUtils.updatePersistentPath(zkClient, "/remoteService/cdms", JSON.toJSONString(list));
    }

    @Before
    public void setUp() throws Exception {
        this.context = new ClassPathXmlApplicationContext("classpath:spring.xml");
        createModuleRemoteService();
    }

    @Test
    public void testServiceFinder() throws InterruptedException, RpcServiceNameRegisterException, RpcTimeoutException, RpcConnectException, RpcSendRequestException, RpcFactoryException {
        RpcClientFactory rpcClientFactory = (RpcClientFactory) context.getBean("clientFactory");
        Object object = rpcClientFactory.execute(serviceId, "zhangyong");
        System.out.println(object);
    }

}
