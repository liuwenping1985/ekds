package com.seeyon.ekds.apps.zdk;

import com.seeyon.ekds.service.EsGenericService;
import com.seeyon.ekds.service.EsOperationNativeService;
import com.seeyon.ekds.web.EkdsJSONResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 改造controller
 * Created by liuwenping on 2021/10/20.
 *
 * @author liuwenping
 */
@RestController
@RequestMapping("/zdok/api/")
public class ZidukuOmegaController {

    @Autowired
    private EsGenericService esGenericService;

    @Autowired
    private EsOperationNativeService esOperationNativeService;
//
//    @RequestMapping(method = RequestMethod.POST, value = "index")
//    public EkdsJSONResponse createIndex(@RequestParam(name = "index_name") String indexName, @RequestParam(name = "document_name") String documentName, @RequestParam(name = "data", required = false) String data, @RequestParam(name = "items", required = false) String items) {
//        EkdsJSONResponse resp = new EkdsJSONResponse();
//        GenericDataHolder holder = new GenericDataHolder();
//        holder.put("indexName", indexName);
//        holder.put("documentName", documentName);
//        try {
//            Map dj = JSON.parseObject(data);
//            holder.put("data", dj);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        try {
//            if (items != null) {
//                List<HashMap> datas = JSON.parseArray(items, HashMap.class);
//                holder.put("items", datas);
//            }
//            IndexResponse indexResponse = esOperationNativeService.createIndex(holder);
//            resp.setSuccess(true);
//            resp.setData(indexResponse.toString());
//            resp.setMessage("create index success");
//        } catch (Exception e) {
//            e.printStackTrace();
//            resp.setMessage(e.getMessage());
//            resp.setSuccess(false);
//        }
//        return resp;
//    }
//
//    @RequestMapping(method = RequestMethod.GET, value = "index/{index_name}/{document_name}")
//    public EkdsJSONResponse getIndexData(@PathVariable("index_name") String indexName, @PathVariable("document_name") String documentName) {
//        EkdsJSONResponse resp = new EkdsJSONResponse();
//        GetResponse mo = esGenericService.findIndex(indexName, documentName);
//        resp.setData(mo.toString());
//        resp.setSuccess(true);
//        return resp;
//    }
    @RequestMapping(method = RequestMethod.OPTIONS, value = "index/{index_name}/{document_name}")
    public EkdsJSONResponse checkIndexData(@PathVariable("index_name") String indexName, @PathVariable("document_name") String documentName) {
        EkdsJSONResponse resp = new EkdsJSONResponse();
        Boolean moData =  esOperationNativeService.existCheck(indexName,documentName);
        resp.setData(moData==null?false:moData);
        resp.setSuccess(true);
        return resp;
    }
    @RequestMapping(method = RequestMethod.GET, value = "data/{index_name}/{document_name}")
    public EkdsJSONResponse fetchData(@PathVariable("index_name") String indexName, @PathVariable("document_name") String documentName) {
        EkdsJSONResponse resp = new EkdsJSONResponse();
        GetResponse moData = esOperationNativeService.getData(indexName,documentName);
        resp.setData(moData);
        resp.setSuccess(true);
        return resp;
    }
//    @RequestMapping(value = "search/{index_name}/{field_name}/{page}/{page_size}")
//    public EkdsJSONResponse searchData(HttpServletRequest hsr,@PathVariable("index_name") String indexName, @PathVariable("field_name") String fieldName, @PathVariable("page") String page, @PathVariable("page_size") String pageSize) {
//        EkdsJSONResponse resp = new EkdsJSONResponse();
//        String fieldValue = hsr.getParameter("field_value");
//        if(Strings.isEmpty(fieldValue)){
//            resp.setSuccess(false);
//            resp.setMessage("fieldVale is not present!");
//            return resp;
//        }
//        Pageable pageParam = PageRequest.of(Integer.valueOf(page),Integer.valueOf(pageSize));
//        SearchResponse srp = esOperationNativeService.search(EsRequestBuilder.searchEq(indexName,fieldName,fieldValue,pageParam));
//        resp.setData(srp);
//        resp.setSuccess(true);
//        return resp;
//
//    }
    @RequestMapping(value = "index/{index_name}/{document_name}")
    public EkdsJSONResponse mockData() {
        EkdsJSONResponse resp = new EkdsJSONResponse();
        Map data = new HashMap();
        data.put("test_data","test_data");
        data.put("holy_data","holy_data");
        data.put("flag","uid");

        // IndexResponse indexResponse = esOperationNativeService.createIndex("temp_index001","test002",data);

        return resp;

    }
    @RequestMapping(value = "test/index")
    public EkdsJSONResponse testData() {
        EkdsJSONResponse resp = new EkdsJSONResponse();
        Map data = new HashMap();
        data.put("test_data","test_data");
        data.put("holy_data","holy_data");
        data.put("flag","uid");
        GetRequest request = new GetRequest();
        request.id("test002");
        request.index("temp_index001");
        request.storedFields("test_data");
        request.fetchSourceContext(FetchSourceContext.DO_NOT_FETCH_SOURCE);
        Boolean getResponse = esOperationNativeService.existCheck(request);
       // IndexResponse indexResponse = esOperationNativeService.createIndex("temp_index001","test002",data);
        resp.setData(getResponse.toString());
        resp.setSuccess(true);
        return resp;

    }
}
