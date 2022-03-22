package com.seeyon.ekds.engine.http.annotaion;

import org.springframework.http.client.ClientHttpResponse;

/**
 * Created by shenzhiping on 2021/9/1.
 */
@FunctionalInterface
public interface HttpResponseHandler<T> {

     T processResponse(ClientHttpResponse response);
}
