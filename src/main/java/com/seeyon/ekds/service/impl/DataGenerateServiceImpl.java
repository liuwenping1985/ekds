package com.seeyon.ekds.service.impl;

import com.alibaba.fastjson.JSON;
import com.seeyon.ekds.apps.zdk.vo.ZdkFileInfo;
import com.seeyon.ekds.data.descriptor.DataDescriptor;
import com.seeyon.ekds.service.DataGenerateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by liuwenping on 2021/7/30.
 * @Author lwp
 */
@Service("dataGenerateService")
public class DataGenerateServiceImpl implements DataGenerateService {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<DataDescriptor<ZdkFileInfo>> fetchData() {
        ResponseEntity<ArrayList> arrayListResponseEntity = restTemplate.getForEntity("http://localhost:9001/zdk/api/v0.1/list", ArrayList.class);
        List<Map> dataList = arrayListResponseEntity.getBody();
        List<DataDescriptor<ZdkFileInfo>> ddList = new ArrayList<>();
        for (Map data : dataList) {
            DataDescriptor<ZdkFileInfo> dd = new DataDescriptor<>();
            ZdkFileInfo info = JSON.parseObject(JSON.toJSONString(data), ZdkFileInfo.class);
            dd.setName(info.getFileName());
            dd.setDescription(info.getFileNo());
            dd.setType("url");
            dd.setValue(info);
            dd.setExtend(info.getExt());
            ddList.add(dd);
        }

        return ddList;
    }
}
