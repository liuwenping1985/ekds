package com.seeyon.ekds.platform.log.service;

import com.seeyon.ekds.platform.domain.LogItem;

/**
 * Created by shenzhiping on 2021/11/12.
 */
public interface EkdsLogService {

    void log(LogItem logItem);
    void quickLog(String content);

}
