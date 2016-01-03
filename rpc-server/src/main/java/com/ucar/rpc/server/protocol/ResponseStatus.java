package com.ucar.rpc.server.protocol;

/**
 * Created by zhangyong on 16/1/3.
 */
public enum ResponseStatus {

    NO_ERROR(null),
    ERROR("Error by user"),
    EXCEPTION("Exception occured"),
    UNKNOWN("Unknow error"),
    THREADPOOL_BUSY("Thread pool is busy"),
    ERROR_COMM("Communication error"),
    NO_PROCESSOR("There is no processor or method to handle this request"),
    TIMEOUT("Operation timeout");

    private String errorMessage;

    private ResponseStatus(final String errorMessage) {
        this.errorMessage = errorMessage;
    }


    public String getErrorMessage() {
        return this.errorMessage;
    }

}
