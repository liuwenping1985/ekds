package com.seeyon.ekds.service;

import com.seeyon.ekds.apps.zdk.vo.EsSearchParameters;
import com.seeyon.ekds.apps.zdk.vo.ZdkFileInfo;
import com.seeyon.ekds.data.descriptor.DataDescriptor;
import com.seeyon.ekds.domain.mo.ManagedObject;
import com.seeyon.ekds.data.vo.GenericDataHolder;
import com.seeyon.ekds.domain.po.es.EkdsFileContent;
import com.seeyon.ekds.web.EkdsJSONResponse;
import org.elasticsearch.search.aggregations.Aggregation;

import java.util.List;

/**
 * Created by liuwenping on 2021/7/12.
 */
public interface EsGenericService {

     void createIndex(GenericDataHolder data);

     <T> List<T> search(EsSearchParameters params);
     EkdsJSONResponse searchAndCounting(EsSearchParameters params);
     List<EkdsFileContent> searchRaw(EsSearchParameters params);

     List<Aggregation> searchNative(EsSearchParameters params);

     <T extends ManagedObject<String>> void createIndex(T data);

     void dropIndex(String indexUUID);

     void updateIndex(GenericDataHolder data);

     <T extends ManagedObject<String>> void updateIndex(T data);

     <T extends ManagedObject<String>> T findIndexById(String uuid);

     List<DataDescriptor<ZdkFileInfo>> getDataDescriptorList();

}
