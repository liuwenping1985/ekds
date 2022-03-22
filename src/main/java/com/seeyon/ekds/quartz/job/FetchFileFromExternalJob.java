package com.seeyon.ekds.quartz.job;

import com.alibaba.fastjson.JSON;
import com.seeyon.ekds.apps.zdk.service.ZdkFileService;
import com.seeyon.ekds.apps.zdk.vo.ZdkFileInfo;
import com.seeyon.ekds.dao.repository.ManagedFileRepository;
import com.seeyon.ekds.data.descriptor.DataDescriptor;
import com.seeyon.ekds.domain.po.ConfigItem;
import com.seeyon.ekds.domain.po.ManagedFile;
import com.seeyon.ekds.service.ConfigService;
import com.seeyon.ekds.service.DataGenerateService;
import com.seeyon.ekds.service.impl.ConfigServiceImpl;
import com.seeyon.ekds.util.AppContextUtil;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.io.IOException;
import java.util.*;

/**
 * Created by liuwenping on 2021/7/16.
 *
 * @Author liuwenping
 */
public class FetchFileFromExternalJob extends QuartzJobBean {


    private static final Logger log = LoggerFactory.getLogger(ConfigServiceImpl.class);

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        ZdkFileService zdkFileService = AppContextUtil.getBean(ZdkFileService.class);
        zdkFileService.autoFetchFileInfo();
        zdkFileService.autoDownloadFile();
    }
}
