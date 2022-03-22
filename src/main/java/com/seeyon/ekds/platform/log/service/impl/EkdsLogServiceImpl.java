package com.seeyon.ekds.platform.log.service.impl;

import com.seeyon.ekds.platform.dao.LogRepository;
import com.seeyon.ekds.platform.domain.LogItem;
import com.seeyon.ekds.platform.log.service.EkdsLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

/**
 * Created by shenzhiping on 2021/11/12.
 */
@Service("logService")
public class EkdsLogServiceImpl implements EkdsLogService {

    @Autowired
    private LogRepository logRepository;

    @Override
    public void log(LogItem logItem) {
        logRepository.save(logItem);
    }

    @Override
    public void quickLog(String content) {
        LogItem item = new LogItem();
        item.setId(UUID.randomUUID().toString());
        item.setCreateDate(new Date());
        item.setContent(content);
        item.setType("QUICK_LOG");
        logRepository.save(item);
    }
    public void findAll(){

    }
}
