package com.ucar.rpc.server.protocol;

import com.ucar.rpc.common.utils.OpaqueGenerator;

/**
 * Created by zhangyong on 16/1/2.
 */
public class RpcRequestCommand implements RpcCommand {

    private static final long serialVersionUID = -1L;

    private Integer opaque;

    private String beanName;

    private String methodName;

    private Object[] arguments;

    public String getBeanName() {
        return this.beanName;
    }

    public void setBeanName(final String beanName) {
        this.beanName = beanName;
    }

    public void setMethodName(final String methodName) {
        this.methodName = methodName;
    }

    public void setArguments(final Object[] arguments) {
        this.arguments = arguments;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public Object[] getArguments() {
        return this.arguments;
    }

    public RpcRequestCommand(final String beanName, final String methodName, final Object[] arguments) {
        super();
        this.opaque = OpaqueGenerator.getNextOpaque();
        this.beanName = beanName;
        this.methodName = methodName;
        this.arguments = arguments;
    }

    public void setOpaque(final Integer opaque) {
        this.opaque = opaque;
    }

    public Integer getOpaque() {
        return opaque;
    }

}
