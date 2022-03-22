package com.seeyon.ekds.apps.zdk;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.seeyon.ekds.apps.zdk.reader.ZdkFileReader;
import com.seeyon.ekds.apps.zdk.service.ZdkFileService;
import com.seeyon.ekds.apps.zdk.vo.EsSearchParameters;
import com.seeyon.ekds.apps.zdk.vo.ZdkFileInfo;
import com.seeyon.ekds.dao.es.repository.FileContentPerPageRepository;
import com.seeyon.ekds.dao.handler.JdbcHandler;
import com.seeyon.ekds.dao.repository.ManagedFileRepository;
import com.seeyon.ekds.data.descriptor.DataDescriptor;
import com.seeyon.ekds.domain.po.es.EkdsFileContent;
import com.seeyon.ekds.mock.MockDataUtils;
import com.seeyon.ekds.service.EsGenericService;
import com.seeyon.ekds.service.EsOperationNativeService;
import com.seeyon.ekds.util.EkdsUtil;
import com.seeyon.ekds.util.WebUtil;
import com.seeyon.ekds.web.EkdsJSONResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by liuwenping on 2021/7/26.
 */
@RestController
@RequestMapping("/zdk/api/v0.1/")
public class ZidukuController {
    private static final Logger log = LoggerFactory.getLogger(ZidukuController.class);

    @Autowired
    private EsGenericService esGenericService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ZdkFileReader zdkFileReader;

    @Autowired
    private FileContentPerPageRepository fileContentPerPageRepository;

    @Autowired
    private ManagedFileRepository managedFileRepository;

    @Autowired
    private ZdkFileService zdkFileService;

    @Autowired
    @Qualifier("esOperationNativeService")
    private EsOperationNativeService esOperationNativeService;


    @RequestMapping(method = RequestMethod.GET, value = "list")
    public List<ZdkFileInfo> list() {
        List<ZdkFileInfo> dataList = MockDataUtils.getMockFileInfoList();
        return dataList;

    }

    @RequestMapping(method = RequestMethod.GET, value = "mock_get_list")
    public EkdsJSONResponse mockGetList() {
        Integer afi = zdkFileService.autoFetchFileInfo();
        System.out.println("afi:" + afi);
        zdkFileService.autoDownloadFile();
        return WebUtil.responseSuccessWhithData("ok");

    }

    @RequestMapping(method = RequestMethod.GET, value = "gszd/count")
    public Object gszdCount(HttpServletRequest request) {
        String fileDate = request.getParameter("file_date");
        if (EkdsUtil.isEmptyString(fileDate)) {
            fileDate = EkdsUtil.formatDateSimple(new Date(new Date().getTime() - 50 * 365 * 24 * 3600 * 1000L));
        }
        return zdkFileService.countGszdFile(fileDate);
    }

    @RequestMapping(method = RequestMethod.GET, value = "finfo/list/{file_type}")
    public Object gszdFileList(@PathVariable("file_type") String fileType, HttpServletRequest request) {
        String fileDate = request.getParameter("file_date");
        if (EkdsUtil.isEmptyString(fileDate)) {
            fileDate = EkdsUtil.formatDateSimple(new Date(new Date().getTime() - 50 * 365 * 24 * 3600 * 1000L));
        }
        String pageNo = request.getParameter("page_no");
        if (EkdsUtil.isEmptyString(pageNo)) {
            pageNo = "1";
        }
        String pageSize = request.getParameter("page_size");
        if (EkdsUtil.isEmptyString(pageSize)) {
            pageSize = "100";
        }
        if ("zcfg".equals(fileType)) {
            return zdkFileService.getZcfgFileInfo(fileDate, pageNo, pageSize);
        } else {
            return zdkFileService.getGszdFileList(fileDate, pageNo, pageSize);
        }

    }

    //ZdkFileService
    @RequestMapping(value = "searchraw")
    public EkdsJSONResponse searchRaw(EsSearchParameters params, HttpServletResponse response) {
        List<Aggregation> analyseData = esGenericService.searchNative(params);
        EkdsJSONResponse resp = new EkdsJSONResponse();
        resp.setMessage("success");
        resp.setItems(analyseData);
        resp.setSuccess(true);
        resp.setData(params);
        response.setHeader("contentType", "text/json;charset=utf-8");
        return resp;

    }

    List<Map> executeByQueryBuilder(SearchSourceBuilder ssb) {
        SearchRequest sr = new SearchRequest();
        sr.indices("es_file_content");
        sr.source(ssb);
        SearchResponse resp = esOperationNativeService.search(sr);
        SearchHits hits = resp.getHits();
        List<Map> dataMapList = new ArrayList<>();
        if (hits != null) {
            Iterator<SearchHit> shIt = hits.iterator();
            while (shIt.hasNext()) {
                SearchHit hit = shIt.next();
                Map temp = hit.getSourceAsMap();
                temp.put("_score", hit.getScore());
                dataMapList.add(temp);
            }
        }
        return dataMapList;
    }

    //esOperationNativeService
    @RequestMapping(value = "searchv3")
    public EkdsJSONResponse searchv3(EsSearchParameters params, HttpServletResponse response) {
        System.out.println("params.formatKeyWords:" + params.formatKeyWords());
        MatchQueryBuilder mqb = new MatchQueryBuilder("file_name", params.formatKeyWords());
        mqb.fuzziness(Fuzziness.AUTO);
        mqb.prefixLength(1);
        mqb.maxExpansions(100);
        WildcardQueryBuilder wqb = QueryBuilders.wildcardQuery("file_name", "*" + params.formatKeyWords() + "*");
//        List<String> terms = EkdsUtil.splitWord(params.formatKeyWords());
//        Set<String> keys = new HashSet<>();
//        keys.add(params.formatKeyWords());
//        keys.addAll(terms);

        SearchSourceBuilder ssb = new SearchSourceBuilder();
        ssb.from(0);
        ssb.size(5);

        ssb.query(wqb);
        SearchRequest sr = new SearchRequest();
        sr.indices("es_file_content");
        sr.source(ssb);
        SearchResponse resp = esOperationNativeService.search(sr);
        response.setHeader("contentType", "text/json;charset=utf-8");
        EkdsJSONResponse respData = new EkdsJSONResponse();
        SearchHits hits = resp.getHits();
        Map data = new HashMap();
        if (hits == null) {
            data.put("size", 0);
        }
        Long count = 0L;
        count = hits.getTotalHits().value;
        if (count == null) {
            data.put("size", 0);
        } else {
            data.put("size", count);
        }
        respData.setData(data);
        List<Map> dataMapList = new ArrayList<>();
        if (hits != null) {
            Iterator<SearchHit> shIt = hits.iterator();
            while (shIt.hasNext()) {
                SearchHit hit = shIt.next();
                Map temp = hit.getSourceAsMap();
                data.put("_score", hit.getScore());
                dataMapList.add(temp);
            }
        }
        JSON.parseObject("",HashMap.class);
        respData.setItems(dataMapList);
        return respData;

    }

    //esOperationNativeService
    @RequestMapping(value = "search")
    public EkdsJSONResponse search(EsSearchParameters params, HttpServletResponse response) {
        params.setIso_type(EkdsUtil.parseIsoTypeOrder(params.getIso_type()));
        Map data = countingByFileNameAndContent(params);
        Integer fileNameSize = (Integer) data.get("file_name_count");
        Integer contentSize = (Integer) data.get("content_count");
        System.out.println("data:" + data);
        Integer countAll = fileNameSize + contentSize;
        Map<String, Integer> pages = params.page();

        int from = pages.get("from");
        int size = pages.get("size");
        int fnFrom = 0, fnSize = 0, cFrom = 0, cSize = 0;
        if (from < fileNameSize) {
            fnFrom = from;
            if ((fnFrom + size) < fileNameSize) {
                fnSize = size;
            } else {
                fnSize = fileNameSize - fnFrom;
            }
        }
        //需要补content的
        if (fnSize < size) {
            //大小
            cSize = size - fnSize;
            cFrom = (from - fileNameSize) < 0 ? 0 : (from - fileNameSize);// - size+fileNameSize;
        }
        List<Map> items = new ArrayList<>();
        MatchQueryBuilder wqb = new MatchQueryBuilder("file_name", params.formatKeyWords());
        wqb.fuzziness(Fuzziness.AUTO);
        wqb.prefixLength(2);
        wqb.maxExpansions(10);
        //WildcardQueryBuilder wqb = QueryBuilders.wildcardQuery("file_name", params.formatKeyWords());
        if (fnSize > 0) {

            SearchSourceBuilder ssb = generateSearchBuilder(params, wqb, null);
            ssb.fetchSource(true);
            ssb.from(fnFrom);
            ssb.size(fnSize);
            ssb.trackScores(true);
            ssb.sort("iso_type", SortOrder.ASC);
            ssb.sort("_score",SortOrder.DESC);
            List<Map> fList = executeByQueryBuilder(ssb);
            items.addAll(fList);
        }
        if (cSize > 0) {
            SearchSourceBuilder ssbContentBuilder = generateSearchBuilder(params, "content", wqb);
            ssbContentBuilder.fetchSource(true);
            ssbContentBuilder.from(cFrom);
            ssbContentBuilder.size(cSize);
            ssbContentBuilder.trackScores(true);
            ssbContentBuilder.sort("iso_type", SortOrder.ASC);
            ssbContentBuilder.sort("_score",SortOrder.DESC);
            List<Map> cList = executeByQueryBuilder(ssbContentBuilder);
            items.addAll(cList);
        }
        response.setHeader("contentType", "text/json;charset=utf-8");
        EkdsJSONResponse respData = new EkdsJSONResponse();
        params.setCount(countAll + "");
        respData.setData(params);
        respData.setItems(items);
        respData.setSuccess(true);
        respData.setStatus("200");
        return respData;

    }

    private Map countingByFileNameAndContent(EsSearchParameters params) {
        Map ret = new HashMap();
        //WildcardQueryBuilder wqb = QueryBuilders.wildcardQuery("file_name",  params.formatKeyWords());
        MatchQueryBuilder wqb = new MatchQueryBuilder("file_name", params.formatKeyWords());
        wqb.fuzziness(Fuzziness.AUTO);
        wqb.prefixLength(2);
        wqb.maxExpansions(10);
        SearchSourceBuilder ssb = generateSearchBuilder(params, wqb, null);
        ssb.fetchSource(false);
        SearchSourceBuilder ssbContentBuilder = generateSearchBuilder(params, "content", wqb);
        ssbContentBuilder.fetchSource(false);
        int fSize = countByRequestBuilder(ssb);
        int cSize = countByRequestBuilder(ssbContentBuilder);
        ret.put("file_name_count", fSize);
        ret.put("content_count", cSize);
        ret.put("count", fSize + cSize);
        return ret;
    }

    private int countByRequestBuilder(SearchSourceBuilder ssb) {
        ssb.from(0);
        ssb.from(1);
        ssb.fetchSource(false);
        SearchRequest sr = new SearchRequest();
        sr.indices("es_file_content");
        sr.source(ssb);
        SearchResponse resp = esOperationNativeService.search(sr);
        SearchHits hits = resp.getHits();
        if (hits == null) {
            return 0;
        }
        Long count = 0L;
        count = hits.getTotalHits().value;
        if (count == null) {
            return 0;
        }
        return count.intValue();

    }

    private SearchSourceBuilder generateSearchBuilder(EsSearchParameters params, String field) {
        return generateSearchBuilder(params, field, null);
    }

    private SearchSourceBuilder generateSearchBuilder(EsSearchParameters params, QueryBuilder mqb, QueryBuilder excludesBuilder) {
        SearchSourceBuilder ssb = new SearchSourceBuilder();
        BoolQueryBuilder bqb = new BoolQueryBuilder();
        bqb.must(mqb);
        if (excludesBuilder != null) {
            bqb.mustNot(excludesBuilder);
        }
        if (!StringUtils.isEmpty(params.getFile_type())) {
            if (!StringUtils.isEmpty(params.getFile_type())) {
                if (params.getFile_type().indexOf(",") >= 0) {
                    String[] types = params.getFile_type().split(",");
                    TermsQueryBuilder tqbs = QueryBuilders.termsQuery("file_type", types);
                    bqb.must(tqbs);
                } else {
                    TermQueryBuilder tqb = QueryBuilders.termQuery("file_type", params.getFile_type());
                    bqb.must(tqb);
                }

            }
        }
        if (!StringUtils.isEmpty(params.getIso_status())) {

            if (!StringUtils.isEmpty(params.getIso_status())) {
                if (params.getIso_status().indexOf(",") >= 0) {
                    String[] types = params.getIso_status().split(",");
                    TermsQueryBuilder tqbs = QueryBuilders.termsQuery("iso_status", types);
                    bqb.must(tqbs);
                } else {
                    TermQueryBuilder tqb = QueryBuilders.termQuery("iso_status", params.getIso_status());
                    bqb.must(tqb);
                }

            }
        }
        if (!StringUtils.isEmpty(params.getIso_type())) {
            if (!StringUtils.isEmpty(params.getIso_type())) {
                if (params.getIso_type().indexOf(",") >= 0) {
                    String[] types = params.getIso_type().split(",");
                    List<String> typeList = new ArrayList<>();
                    for(String tp:types){
                        typeList.add(EkdsUtil.parseIsoTypeOrder(tp));
                    }
                    TermsQueryBuilder tqbs = QueryBuilders.termsQuery("iso_type", typeList);
                    bqb.must(tqbs);
                } else {
                    TermQueryBuilder tqb = QueryBuilders.termQuery("iso_type",  EkdsUtil.parseIsoTypeOrder(params.getIso_type()));
                    bqb.must(tqb);
                }

            }
        }
        ssb.query(bqb);
        return ssb;
    }

    private SearchSourceBuilder generateSearchBuilder(EsSearchParameters params, String fieldName, QueryBuilder excludesBuilder) {
        String keyWords = params.formatKeyWords();
        MatchQueryBuilder mqb = new MatchQueryBuilder(fieldName, keyWords);
        mqb.fuzziness(Fuzziness.AUTO);
        mqb.prefixLength(1);
        mqb.maxExpansions(5);
        return generateSearchBuilder(params, mqb, excludesBuilder);
    }

    @RequestMapping(value = "searchMulti")
    public EkdsJSONResponse searchMulti(EsSearchParameters params, HttpServletResponse response) {
        response.setHeader("contentType", "text/json;charset=utf-8");
        EkdsJSONResponse respData = new EkdsJSONResponse();
        Map<String, Integer> pages = params.page();
        SearchSourceBuilder ssb = generateSearchBuilder(params, "file_name");
        ssb.from(0);
        ssb.size(1);
        ssb.fetchSource(true);
        SearchRequest sr = new SearchRequest();
        sr.indices("es_file_content");
        sr.source(ssb);
        SearchResponse resp = esOperationNativeService.search(sr);
        SearchHits hits = resp.getHits();
        long count = hits.getTotalHits().value;
        List<Map> dataMapList = new ArrayList<>();
        if (hits != null) {
            Iterator<SearchHit> shIt = hits.iterator();
            while (shIt.hasNext()) {
                SearchHit hit = shIt.next();
                Map data = hit.getSourceAsMap();
                data.put("_score", hit.getScore());
                dataMapList.add(data);
            }
        }
        respData.setSuccess(true);
        params.setCount("" + count);
        respData.setData(params);
        respData.setItems(dataMapList);
        return respData;
    }

    @RequestMapping(value = "searchv5")
    public EkdsJSONResponse searchNative(EsSearchParameters params, HttpServletResponse response) {
        SearchRequest sr = new SearchRequest();
        sr.indices("es_file_content");
        SearchSourceBuilder ssb = new SearchSourceBuilder();
        Map<String, Integer> pages = params.page();
        ssb.from(pages.get("from"));
        ssb.size(pages.get("size"));
        ssb.fetchSource(true);

        String keyWords = params.getKey_words();
        if (keyWords == null) {
            keyWords = "";
        }
        keyWords = keyWords.replaceAll(" ", "");
        BoolQueryBuilder wordsQuery = new BoolQueryBuilder();
        MatchQueryBuilder mqb = new MatchQueryBuilder("content", keyWords);
        mqb.fuzziness(Fuzziness.AUTO);
        mqb.prefixLength(2);
        mqb.maxExpansions(5);
        MatchQueryBuilder mqbFileName = new MatchQueryBuilder("file_name", "\"" + keyWords + "\"");
        mqbFileName.fuzziness(Fuzziness.AUTO);
        mqbFileName.prefixLength(2);
        mqbFileName.maxExpansions(5);
        wordsQuery.mustNot(mqbFileName);
        wordsQuery.must(mqb);

        BoolQueryBuilder bqb = new BoolQueryBuilder();
        bqb.must(wordsQuery);

        if (!StringUtils.isEmpty(params.getFile_type())) {
            TermQueryBuilder tqb = QueryBuilders.termQuery("file_type", params.getFile_type());
            bqb.must(tqb);
        }
        if (!StringUtils.isEmpty(params.getIso_status())) {
            TermQueryBuilder tqb = QueryBuilders.termQuery("iso_status", params.getIso_status());
            bqb.must(tqb);
        }
        if (!StringUtils.isEmpty(params.getIso_type())) {
            if (params.getIso_type().indexOf(",") >= 0) {
                String[] types = params.getIso_type().split(",");
                TermsQueryBuilder tqbs = QueryBuilders.termsQuery("iso_type", types);
                bqb.must(tqbs);
            } else {

                TermQueryBuilder tqb = QueryBuilders.termQuery("iso_type", params.getIso_type());
                bqb.must(tqb);
            }

        }
        ssb.query(bqb);
        sr.source(ssb);
        SearchResponse resp = esOperationNativeService.search(sr);
        SearchHits hits = resp.getHits();
        long count = hits.getTotalHits().value;
        response.setHeader("contentType", "text/json;charset=utf-8");
        EkdsJSONResponse respData = new EkdsJSONResponse();
        List<Map> dataMapList = new ArrayList<>();
        if (hits != null) {
            Iterator<SearchHit> shIt = hits.iterator();
            while (shIt.hasNext()) {
                SearchHit hit = shIt.next();
                Map data = hit.getSourceAsMap();
                data.put("_score", hit.getScore());
                dataMapList.add(data);
            }
        }
        respData.setSuccess(true);
        params.setCount("" + count);
        respData.setData(params);
        respData.setItems(dataMapList);
        return respData;

    }
    @RequestMapping(value = "getAll")
    public EkdsJSONResponse getAll() {
        EkdsJSONResponse resp = new EkdsJSONResponse();
        resp.setSuccess(true);
        resp.setMessage("success");
        resp.setStatus("200");
        List list = new ArrayList();
        Iterable it = fileContentPerPageRepository.findAll();
        Iterator iterator = it.iterator();
        while (iterator.hasNext()) {
            Object obj = iterator.next();
            list.add(obj);
        }
        resp.setItems(list);
        return resp;

    }

    @RequestMapping(value = "inject")
    public EkdsJSONResponse inject() {
        EkdsJSONResponse resp = new EkdsJSONResponse();
        resp.setSuccess(true);
        resp.setMessage("success");
        resp.setStatus("200");
        List<DataDescriptor<ZdkFileInfo>> ddList = esGenericService.getDataDescriptorList();
        if (CollectionUtils.isEmpty(ddList)) {
            resp.setItems(new ArrayList());
        } else {
            List<EkdsFileContent> retList = new ArrayList<>();
            for (DataDescriptor<ZdkFileInfo> dds : ddList) {
                EkdsFileContent efc = zdkFileReader.read(dds);
                retList.add(efc);
            }
            fileContentPerPageRepository.saveAll(retList);
        }
        return resp;

    }

    @RequestMapping(method = RequestMethod.DELETE, value = "delete/and/run/away")
    public EkdsJSONResponse deleteData() {
        EkdsJSONResponse resp = new EkdsJSONResponse();
        resp.setSuccess(true);
        resp.setStatus("200");
        fileContentPerPageRepository.deleteAll();
        managedFileRepository.deleteAll();
        resp.setMessage("全部删完了 跑路吧！！！！！快跑");
        return resp;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "add/and/run/away")
    public EkdsJSONResponse createData() {
        EkdsJSONResponse resp = new EkdsJSONResponse();
        resp.setSuccess(true);
        resp.setStatus("200");
        zdkFileService.autoFetchFileInfo();
        resp.setMessage("数据加完了 欢呼吧！！！！！");
        return resp;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "download/and/run/away")
    public EkdsJSONResponse downloadData() {
        EkdsJSONResponse resp = new EkdsJSONResponse();
        resp.setSuccess(true);
        resp.setStatus("200");
        zdkFileService.autoDownloadFile();
        resp.setMessage("下载数据完了 欢呼吧！！！！！");
        return resp;

    }
    @RequestMapping(method = RequestMethod.POST, value = "checkstatus")
    public EkdsJSONResponse checkStatus() {
        EkdsJSONResponse resp = new EkdsJSONResponse();
        resp.setSuccess(true);
        resp.setStatus("200");
        List list = JdbcHandler.findRawDataBySql("select count(1) from mo_ekds_file");
        Map data = new HashMap();
        data.put("all",list);
        List list2 = JdbcHandler.findRawDataBySql("select count(1) from mo_ekds_file where is_indexed=0");
        data.put("indexed",list2);
        resp.setData(data);
        resp.setMessage("checkStatus finished!");
        return resp;

    }
    public static void main(String[] args) {
        // Map data = countingByFileNameAndContent(params);
        Integer fileNameSize = 22;
        Integer contentSize = 9418;
        Integer countAll = fileNameSize + contentSize;
        int from = 51;
        int size = 28;
        int fnFrom = 0, fnSize = 0, cFrom = 0, cSize = 0;
        if (from < fileNameSize) {
            fnFrom = from;
            if ((fnFrom + size) < fileNameSize) {
                fnSize = size;
            } else {
                fnSize = fileNameSize - fnFrom;
            }
        }
        //需要补content的
        if (fnSize < size) {
            //大小
            cSize = size - fnSize;
            cFrom = (from - fileNameSize) < 0 ? 0 : (from - fileNameSize);// - size+fileNameSize;
        }
        System.out.println(fnFrom + "," + fnSize + "," + cFrom + "," + cSize);

    }


}
