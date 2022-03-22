package com.seeyon.ekds.gateway.controller;

import com.seeyon.ekds.apps.zdk.reader.ZdkFileReader;
import com.seeyon.ekds.apps.zdk.vo.ZdkFileInfo;
import com.seeyon.ekds.dao.es.repository.FileContentPerPageRepository;
import com.seeyon.ekds.dao.handler.JdbcHandler;
import com.seeyon.ekds.dao.repository.ManagedFileRepository;
import com.seeyon.ekds.data.descriptor.DataDescriptor;
import com.seeyon.ekds.data.vo.GenericDataHolder;
import com.seeyon.ekds.domain.po.ConfigItem;
import com.seeyon.ekds.domain.po.ManagedFile;
import com.seeyon.ekds.domain.po.es.EkdsFileContent;
import com.seeyon.ekds.service.ConfigService;
import com.seeyon.ekds.service.DataGenerateService;
import com.seeyon.ekds.service.EsGenericService;
import com.seeyon.ekds.util.AppContextUtil;
import com.seeyon.ekds.util.EkdsUtil;
import com.seeyon.ekds.web.EkdsJSONResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by liuwenping on 2021/6/24.
 *
 * @author liuwenping
 */
@RestController
@RequestMapping("/ekds/api/v0.1/")
public class EkdsController {

    @Autowired
    private RestHighLevelClient highLevelClient;

    @Autowired
    private ConfigService configService;

    @Autowired
    private EsGenericService esGenericService;

    @Autowired
    private ManagedFileRepository managedFileRepository;

    @Autowired
    private ZdkFileReader fileEkdsReader;

    @Autowired
    private FileContentPerPageRepository fileContentPerPageRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DataGenerateService dataGenerateService;

    @RequestMapping(method = RequestMethod.POST, value = "config/item")
    public EkdsJSONResponse saveConfigItem(ConfigItem item) {
        EkdsJSONResponse res = new EkdsJSONResponse();
        res.setSuccess(true);
        res.setData(item);
        res.setMessage("success");
        res.setStatus("200");
        if (item.getId() != null) {
            if (item.getCode() == null) {
                res.setSuccess(false);
                res.setMessage("code不能为空，保存失败");
            } else {
                ConfigItem itemExist = configService.getItemByCode(item.getCode());
                if (itemExist == null) {
                    configService.saveOrUpdateItem(item);
                } else {
                    if (itemExist.getId().equals(item.getId())) {
                        configService.saveOrUpdateItem(item);
                    } else {
                        res.setSuccess(false);
                        res.setMessage("code同现有数据冲突，保存失败");
                    }

                }
            }
        } else {
            if (item.getCode() != null) {
                ConfigItem itemExist = configService.getItemByCode(item.getCode());
                if (itemExist == null) {
                    item.setId(UUID.randomUUID().getLeastSignificantBits());
                    configService.saveOrUpdateItem(item);
                } else {
                    res.setSuccess(false);
                    res.setMessage("code同现有数据冲突，保存失败");
                }
            } else {
                res.setSuccess(false);
                res.setMessage("code不能为空，保存失败");
            }
        }

        return res;

    }
    @RequestMapping(method = RequestMethod.POST, value = "dlconsole")
    public EkdsJSONResponse dlconsole(String sqlcode){
        EkdsJSONResponse res = new EkdsJSONResponse();
        res.setSuccess(true);
        if(StringUtils.isEmpty(sqlcode)){
            res.setSuccess(false);
            res.setMessage("sql为空");
            return res;
        }
        String sql2 = sqlcode.toLowerCase();
        try {
            if (sql2.trim().startsWith("select")) {
                List dataList = JdbcHandler.findRawDataBySql(sqlcode);
                res.setItems(dataList);
            }else{
                int code = JdbcHandler.executeUpdate(sqlcode);
                res.setData("影响:"+code);
            }
        }catch(Exception|Error e){
            res.setMessage(e.getMessage());
            res.setSuccess(false);
            e.printStackTrace();
        }

        return res;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "config/item/{code}")
    public EkdsJSONResponse updateConfigItem(@PathVariable(name = "code") String code,String value) {
        EkdsJSONResponse res = new EkdsJSONResponse();
        ConfigItem itemExist = configService.getItemByCode(code);
        if (itemExist != null) {
            res.setSuccess(true);
            itemExist.setValue(value);
            configService.saveOrUpdateItem(itemExist);
            res.setData(itemExist);
            res.setMessage("success");
        } else {
            res.setStatus("404");
            res.setSuccess(false);
            res.setMessage("NOT FOUND");
        }
        return res;

    }
    @RequestMapping(method = RequestMethod.GET, value = "config/item/{code}")
    public EkdsJSONResponse getConfigItem(@PathVariable(name = "code") String code) {
        EkdsJSONResponse res = new EkdsJSONResponse();
        ConfigItem itemExist = configService.getItemByCode(code);
        if (itemExist != null) {
            res.setSuccess(true);
            res.setData(itemExist);
            res.setMessage("success");
        } else {
            res.setStatus("404");
            res.setSuccess(false);
            res.setMessage("NOT FOUND");
        }
        return res;

    }

    @RequestMapping(method = RequestMethod.GET, value = "config/list")
    public EkdsJSONResponse getConfigItems() {
        EkdsJSONResponse res = new EkdsJSONResponse();
        res.setSuccess(true);
        res.setMessage("success");
        res.setStatus("200");
        res.setItems(configService.findAll());
        return res;

    }

    @RequestMapping(method = RequestMethod.GET, value = "fetch")
    public List<DataDescriptor<ZdkFileInfo>> fetchFileInfoList() {
        return dataGenerateService.fetchData();
    }

    @PostMapping(value = "content/raw")
    public int queryRaw(HttpServletRequest request) {
        List<EkdsFileContent> retList = new ArrayList<>();
        String keyWord = request.getParameter("key_word");

        if (keyWord == null || keyWord.isEmpty()) {
            return 0;
        }
        keyWord = keyWord.trim();
        keyWord = keyWord.replaceAll(" ", "");
        int size = fileContentPerPageRepository.findAllByContentLike(keyWord).size();

        return size;
    }

    @PostMapping(value = "content/strict")
    public List<EkdsFileContent> query(HttpServletRequest request) {

        String keyWord = request.getParameter("key_word");

        if (keyWord == null || keyWord.isEmpty()) {
            return new ArrayList<>(0);
        }
        List<EkdsFileContent> retList = new ArrayList<>();
//        List<String> words = EkdsUtil.splitWord(keyWord);
//        List<String> notIdInList = new ArrayList<>();
//        notIdInList.add("sentry_data");
//        for (String word : words) {
//            List<EkdsFileContent> fcppList = fileContentPerPageRepository.findAllByContentLikeAndIdNotIn(word, notIdInList);
//            if (!CollectionUtils.isEmpty(fcppList)) {
//                for (EkdsFileContent fcpp : fcppList) {
//                    System.out.println(fcpp.getId());
//                    notIdInList.add(fcpp.getId());
//                }
//                retList.addAll(fcppList);
//            }
//        }

        return retList;

    }

    @RequestMapping(method = RequestMethod.GET, value = "hello/{user}")
    public String hello(@PathVariable("user") String user) {
        return "Hello " + user + "!";
    }

    @RequestMapping(value = "fire")
    public EkdsJSONResponse fire() {
        EkdsJSONResponse ekdsJSONResponse = new EkdsJSONResponse();
        ekdsJSONResponse.setItems(esGenericService.getDataDescriptorList());
        ekdsJSONResponse.setSuccess(true);
        ekdsJSONResponse.setStatus("200");
        ekdsJSONResponse.setMessage("success");
        return ekdsJSONResponse;
    }

    private Map singletonMap(String dk, String dv) {
        Map data = new HashMap();
        data.put(dk, dv);
        return data;
    }

    @RequestMapping(method = RequestMethod.GET, value = "es/test")
    public IndexResponse restes() throws IOException {
        IndexRequest request = new IndexRequest("EKDS-DATA")
                .id(UUID.randomUUID().toString())
                .source(singletonMap("feature", "high-level-rest-client"))
                .setRefreshPolicy(RefreshPolicy.IMMEDIATE);
        IndexResponse response = highLevelClient.index(request, RequestOptions.DEFAULT);
        return response;
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "mo/file")
    public Map deleteFiles() {

        managedFileRepository.deleteAll();
        return new GenericDataHolder().set("success",true);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "mo/es/file")
    public Map deleteEsFiles() {

        fileContentPerPageRepository.deleteAll();
        return new GenericDataHolder().set("success",true);
    }

    @RequestMapping(method = RequestMethod.GET, value = "data/inject")
    public Map fetchFile() {

        return null;
    }

    private static String getSuffix(String fileName) {

        if (fileName.lastIndexOf(".") > -1) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        return "unknown";


    }

    private static boolean matchProcessedFile(File file) {

        if (file.isDirectory()) {
            return false;
        }
        String name = file.getName();
        ConfigService configService = AppContextUtil.getBean("configService");
        if (configService != null) {

            ConfigItem item = configService.getItemByCode("files_suffix");
            if (item == null) {
                return false;
            }
            String[] suffixList = item.getValue().split(",");
            for (String suffix : suffixList) {
                if (name.toLowerCase().lastIndexOf(suffix) == (name.length() - suffix.length())) {
                    return true;

                }
            }

        }
        return false;
    }


    public static void main(String[] args) throws IOException {
        String name = "/G:/3001/";
         //--- ---//
         System.out.println(new File(name).exists());
         //--- System.out.println(new File(name).exists()) ---//
         //--- ElasticsearchTemplate template = new ElasticsearchTemplate() ---//
    }


}
