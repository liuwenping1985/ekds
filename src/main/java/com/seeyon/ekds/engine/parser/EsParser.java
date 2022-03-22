package com.seeyon.ekds.engine.parser;

/**
 * Created by liuwenping on 2021/6/25.
 */
public interface EsParser<T,RESULT_DATA> {

    RESULT_DATA parse(T data);


}
