package com.seeyon.ekds.exception;

/**
 *
 * Created by liuwenping on 2021/7/12.
 */
public class EkdsServiceException extends Exception {
    public EkdsServiceException() {
        super();
    }

    public EkdsServiceException(String message) {
        super(message);
    }

    public EkdsServiceException(String message, Throwable t) {
        super(message, t);
    }
}
