package com.seeyon.ekds.exception;

/**
 * Created by liuwenping on 2021/7/12.
 */
public class EkdsRuntimeException extends RuntimeException {

    public EkdsRuntimeException() {
        super();
    }

    public EkdsRuntimeException(String message) {
        super(message);
    }

    public EkdsRuntimeException(String message, Throwable t) {
        super(message, t);
    }
    public EkdsRuntimeException(Throwable t) {
        super(t);
    }
}
