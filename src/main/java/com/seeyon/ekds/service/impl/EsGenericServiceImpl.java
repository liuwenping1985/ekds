package com.seeyon.ekds.service.impl;

import com.alibaba.fastjson.JSON;
import com.seeyon.ekds.apps.zdk.vo.AnalyseData;
import com.seeyon.ekds.apps.zdk.vo.EsSearchParameters;
import com.seeyon.ekds.apps.zdk.vo.ZdkFileInfo;
import com.seeyon.ekds.dao.es.repository.FileContentPerPageRepository;
import com.seeyon.ekds.dao.repository.ManagedFileRepository;
import com.seeyon.ekds.data.descriptor.DataDescriptor;
import com.seeyon.ekds.domain.mo.ManagedObject;
import com.seeyon.ekds.data.vo.GenericDataHolder;
import com.seeyon.ekds.domain.po.ManagedFile;
import com.seeyon.ekds.domain.po.es.EkdsFileContent;
import com.seeyon.ekds.service.EsGenericService;
import com.seeyon.ekds.util.EkdsUtil;
import com.seeyon.ekds.util.WebUtil;
import com.seeyon.ekds.web.EkdsJSONResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * Created by liuwenping on 2021/7/12.
 *
 * @author liuwenping
 */
@Service("esGenericService")
public class EsGenericServiceImpl implements EsGenericService {

    private static final Logger log = LoggerFactory.getLogger(EsGenericServiceImpl.class);

    @Autowired
    private RestHighLevelClient rhlClient;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private FileContentPerPageRepository fcppr;

    @Autowired
    private ManagedFileRepository managedFileRepository;

    @Override
    public void createIndex(GenericDataHolder data) {

    }

    @Override
    public EkdsJSONResponse searchAndCounting(EsSearchParameters params) {
        EkdsJSONResponse ekdsJSONResponse = new EkdsJSONResponse();
        String keyWord = params.getKey_words();
        if (keyWord == null || keyWord.isEmpty()) {
            ekdsJSONResponse.setSuccess(false);
            ekdsJSONResponse.setMessage("关键词不能为空");
            ekdsJSONResponse.setStatus("500");
            return ekdsJSONResponse;
        }
        String pageStr = params.getPage();
        if(Strings.isEmpty(pageStr)){
            pageStr = "1";
        }
        String pageSizeStr = params.getPageSize();
        if(Strings.isEmpty(pageSizeStr)){
            pageSizeStr = "10";
        }
        int stPgae=1,pageSize =10;
        try{
            stPgae = Integer.parseInt(pageStr);
            pageSize = Integer.parseInt(pageSizeStr);
        }catch (Exception e){
            ekdsJSONResponse.setSuccess(false);
            ekdsJSONResponse.setStatus("500");
            ekdsJSONResponse.setMessage("分页信息不正确");
            return ekdsJSONResponse;
        }
        Pageable page = PageRequest.of(stPgae,pageSize);
        String fileType = params.getFile_type();
        int count = 0;
        List<EkdsFileContent> contentList = new ArrayList<EkdsFileContent>();

        if(Strings.isEmpty(fileType)){
            count = fcppr.countDistinctByContentContainsOrFileNameContains(keyWord,keyWord);
            contentList = fcppr.findDistinctByContentContainsOrFileNameContains(keyWord,keyWord,page);
        }else{
            count = fcppr.countDistinctByContentContainsOrFileNameContainsAndContentTypeEquals(keyWord,keyWord,fileType);
            contentList = fcppr.findDistinctByContentContainsOrFileNameContainsAndContentTypeEquals(keyWord,keyWord,fileType,page);
        }

        params.setCounting("true");
        params.setCount(String.valueOf(count));
        ekdsJSONResponse.setData(params);
        ekdsJSONResponse.setSuccess(true);
        ekdsJSONResponse.setItems(contentList);
        return ekdsJSONResponse;
    }

    @Override
    public List<EkdsFileContent> searchRaw(EsSearchParameters params) {
        return null;
    }

    @Override
    public  List<Aggregation> searchNative(EsSearchParameters params) {
        Integer limit = Integer.parseInt(params.getLimit());
        Integer offset = Integer.parseInt(params.getOffset());
          String keyWord = params.getKey_words();
        if (keyWord == null || keyWord.isEmpty()) {
            return new ArrayList<>(0);
        }
        List<Aggregation> aggregationList= new ArrayList<>();
       // List<String> words = EkdsUtil.splitWord(keyWord);

        QueryBuilder tqbContent = QueryBuilders.fuzzyQuery("content",keyWord);
        SearchRequest sr = new SearchRequest("es_file_content");
        SearchSourceBuilder ssb = new SearchSourceBuilder();
        ssb.from(offset);
        ssb.size(limit);
        ssb.query(tqbContent);
        sr.source(ssb);
        try {
            SearchResponse searchResponse = rhlClient.search(sr, RequestOptions.DEFAULT);
            aggregationList= searchResponse.getAggregations().asList();
            for(Aggregation aggregation:aggregationList){
                System.out.println("-----------start of data---------");
                System.out.println( aggregation.getName());
                System.out.println( aggregation.getType());
                System.out.println(aggregation.getMetadata());
                System.out.println("------------end of data----------");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return aggregationList;
    }

    @Override
    public <T extends ManagedObject<String>> void createIndex(T data) {
        IndexQuery indexQuery = new IndexQueryBuilder()
                .withId(data.getMoId())
                .withObject(data)
                .build();
        IndexCoordinates indexCoordinates = IndexCoordinates.of(data.getMoReference());
        String documentId = elasticsearchOperations.index(indexQuery, indexCoordinates);


    }

    @Override
    public List<AnalyseData> search(EsSearchParameters params) {
        String keyWord = params.getKey_words();
        if (keyWord == null || keyWord.isEmpty()) {
            return new ArrayList<>(0);
        }
        List<String> wordsList = new ArrayList<>();
        wordsList = EkdsUtil.splitWord(keyWord);
        wordsList.add(keyWord);
        Set<String> setWords = new HashSet<>();
        setWords.addAll(wordsList);
        int offset = Integer.valueOf(params.getOffset());
        int limit = Integer.valueOf(params.getLimit());
        int count = fcppr.countDistinctByContentContainsOrFileNameContains(keyWord,keyWord);

        //5 20 150
        //count-offset
        if(count<=0){
            return new ArrayList<>();
        }
        int stPgae = offset/limit;
        int pageSize = limit;
        Pageable page = PageRequest.of(stPgae,pageSize);
        List<EkdsFileContent> efcList = fcppr.findByContentLike(keyWord,page);
        List<AnalyseData> analyseDataList = new ArrayList<>();
        for ( EkdsFileContent ffc:efcList) {
            AnalyseData data = new AnalyseData();
            data.setSummary(ffc.getContent());
            data.setDocMoId(ffc.getFileMoId());
            data.setDocName(ffc.getFileName());
            data.setDocType(ffc.getType());
            analyseDataList.add(data);
        }
        return analyseDataList;

    }

    @Override
    public void dropIndex(String indexUUID) {

    }

    @Override
    public void updateIndex(GenericDataHolder data) {

    }

    @Override
    public <T extends ManagedObject<String>> void updateIndex(T data) {

    }

    @Override
    public <T extends ManagedObject<String>> T findIndexById(String uuid) {
        return null;
    }

    @Override
    public List<DataDescriptor<ZdkFileInfo>> getDataDescriptorList() {

        return null;
    }
}
