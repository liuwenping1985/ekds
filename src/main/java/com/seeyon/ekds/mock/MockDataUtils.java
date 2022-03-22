package com.seeyon.ekds.mock;

import com.alibaba.fastjson.JSON;
import com.seeyon.ekds.apps.zdk.vo.ZdkFileInfo;
import com.seeyon.ekds.util.EkdsUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liuwenping on 2021/7/26.
 */
public class MockDataUtils {



     public static List<ZdkFileInfo> getMockFileInfoList(){

         String path = MockDataUtils.class.getResource("file.json").getPath();
         try {
             String content = EkdsUtil.getFilePlainContent(new File(path));
             List<ZdkFileInfo> dataList = new ArrayList<>();
             dataList.addAll(JSON.parseArray(content,ZdkFileInfo.class));
             return dataList;
         } catch (IOException e) {
             e.printStackTrace();
         }
         return new ArrayList<>(0);


     }


}
