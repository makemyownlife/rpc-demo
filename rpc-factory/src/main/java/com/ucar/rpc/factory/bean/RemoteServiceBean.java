package com.ucar.rpc.factory.bean;

/**
 * 远程服务对象
 * Created by zhangyong on 16/1/23.
 */
public class RemoteServiceBean {

    private String serviceId;

    private String beanName;

    private String methodName;

    private Long invokeTime;

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Long getInvokeTime() {
        return invokeTime;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public void setInvokeTime(Long invokeTime) {
        this.invokeTime = invokeTime;
    }

}
