package com.seeyon.ekds.service;

import com.seeyon.ekds.apps.zdk.vo.ZdkFileInfo;
import com.seeyon.ekds.data.descriptor.DataDescriptor;

import java.util.List;

/**
 * Created by liuwenping on 2021/7/30.
 */
public interface DataGenerateService {

    List<DataDescriptor<ZdkFileInfo>> fetchData();

}
