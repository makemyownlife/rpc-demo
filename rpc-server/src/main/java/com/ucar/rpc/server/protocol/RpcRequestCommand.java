package com.ucar.rpc.server.protocol;

import com.ucar.rpc.common.buffer.IoBuffer;
import com.ucar.rpc.common.utils.Hessian1Utils;
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

    public RpcRequestCommand(int opaque, final String beanName, final String methodName, final Object[] arguments) {
        super();
        this.opaque = opaque;
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

    @Override
    public IoBuffer encode() {
        byte[] argumentsData = null;
        if (this.arguments != null && this.arguments.length > 0) {
            try {
                argumentsData = Hessian1Utils.encodeObject(arguments);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
        final IoBuffer buffer =
                IoBuffer.allocate(1 + 4 + 4 + this.beanName.length() + 4 + this.methodName.length() + 4
                        + (argumentsData != null ? 4 : 0) + (argumentsData != null ? argumentsData.length : 0));
        buffer.put((byte) 0x70);
        buffer.putInt(this.opaque);
        buffer.putInt(this.beanName.length());
        buffer.put(this.beanName.getBytes());
        buffer.putInt(this.methodName.length());
        buffer.put(this.methodName.getBytes());
        buffer.putInt(this.arguments == null ? 0 : this.arguments.length);
        if (argumentsData != null) {
            buffer.putInt(argumentsData.length);
            buffer.put(argumentsData);
        }
        buffer.flip();
        return buffer;
    }

}
