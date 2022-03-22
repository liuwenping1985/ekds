package com.seeyon.ekds.platform.log.controller;

import com.seeyon.ekds.apps.zdk.vo.EsSearchParameters;
import com.seeyon.ekds.platform.domain.LogItem;
import com.seeyon.ekds.platform.log.service.EkdsLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shenzhiping on 2021/11/12.
 */
@RestController
@RequestMapping("/zdk/log/v0.1/")
public class LogController {

    @Autowired
    @Qualifier("logService")
    private EkdsLogService ekdsLogService;

    @RequestMapping(method = RequestMethod.GET, value = "list")
    public List<LogItem> list(EsSearchParameters params) {
        List<LogItem> logItemList = new ArrayList<>();
        //List<ZdkFileInfo> dataList = MockDataUtils.getMockFileInfoList();
        return logItemList;

    }

}
