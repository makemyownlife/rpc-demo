package com.ucar.rpc.server.protocol;

import com.ucar.rpc.common.buffer.IoBuffer;
import com.ucar.rpc.common.utils.Hessian1Utils;

/**
 * Created by zhangyong on 16/1/2.
 */
public class RpcResponseCommand implements RpcCommand {

    private static final long serialVersionUID = -1L;

    private Integer opaque;

    private Object result;

    private ResponseStatus responseStatus;

    public RpcResponseCommand(final Integer opaque, final ResponseStatus responseStatus, final Object result) {
        super();
        this.opaque = opaque;
        this.responseStatus = responseStatus;
        this.result = result;
    }

    @Override
    public IoBuffer encode() {
        byte[] resultData = null;
        if (this.result != null) {
            try {
                resultData = Hessian1Utils.encodeObject(result);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
        final IoBuffer buffer = IoBuffer.allocate(1 + 2 + 4 + 4 + ((resultData == null) ? 0 : resultData.length));
        buffer.put((byte) 0x71);
        buffer.putInt(this.opaque);
        buffer.putShort(ResponseStatusCode.getValue(this.responseStatus));
        buffer.putInt((resultData == null) ? 0 : resultData.length);
        if (resultData != null) {
            buffer.put(resultData);
        }
        buffer.flip();
        return buffer;
    }

    public Integer getOpaque() {
        return opaque;
    }

    public void setOpaque(Integer opaque) {
        this.opaque = opaque;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public ResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

}
