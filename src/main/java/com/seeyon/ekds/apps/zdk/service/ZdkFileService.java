package com.seeyon.ekds.apps.zdk.service;

import com.alibaba.fastjson.JSON;
import com.seeyon.ekds.apps.zdk.reader.ZdkFileReader;
import com.seeyon.ekds.apps.zdk.vo.ZdkFileInfo;
import com.seeyon.ekds.dao.es.repository.FileContentPerPageRepository;
import com.seeyon.ekds.dao.repository.ManagedFileRepository;
import com.seeyon.ekds.domain.po.ConfigItem;
import com.seeyon.ekds.domain.po.ManagedFile;
import com.seeyon.ekds.domain.po.es.EkdsFileContent;
import com.seeyon.ekds.platform.log.service.EkdsLogService;
import com.seeyon.ekds.service.ConfigService;
import com.seeyon.ekds.util.EkdsUtil;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * Created by shenzhiping on 2021/9/1.
 */
@Service
@PropertySource("classpath:${spring.profiles.active}/zdk.properties")
public class ZdkFileService {

    @Value("${zdk.fetch.gszd.file.url}")
    private String gszdFileFetchUrl;

    @Value("${zdk.fetch.gszd.count.url}")
    private String gszdFileCountUrl;

    @Value("${zdk.fetch.zcfg.file.url}")
    private String zcfgFileFetchUrl;

    @Value("${zdk.file.download.url}")
    private String fileDownloadUrl;

    @Autowired
    private ConfigService configService;

    @Autowired
    private ManagedFileRepository managedFileRepository;

    @Autowired
    private FileContentPerPageRepository fileContentPerPageRepository;

    @Autowired
    @Qualifier("logService")
    private EkdsLogService logService;

    private static final String ZDK_GSZD_FILE_DATE_CODE = "zdk_gszd_file_date";

    private static final String ZDK_ZCFG_FILE_DATE_CODE = "zdk_zcfg_file_date";

    private String getZdkGszdFileDateConfigValue() {
        ConfigItem item = configService.getItemByCode(ZDK_GSZD_FILE_DATE_CODE);
        if (item == null) {
            item = new ConfigItem();
            item.setCode(ZDK_GSZD_FILE_DATE_CODE);
            item.setValue("1960-01-01");
            item.setId(UUID.randomUUID().getLeastSignificantBits());
            configService.saveOrUpdateItem(item);
        }
        return item.getValue();

    }

    private void updateZdkDateConfigValue(String code, String fileDate) {
        ConfigItem item = configService.getItemByCode(code);
        if (item == null) {
            item = new ConfigItem();
            item.setCode(code);
        }
        item.setValue(fileDate);
        configService.saveOrUpdateItem(item);
    }

    private String getZdkZcfgFileDateConfigValue() {

        ConfigItem item = configService.getItemByCode(ZDK_ZCFG_FILE_DATE_CODE);
        if (item == null) {
            item = new ConfigItem();
            item.setId(UUID.randomUUID().getLeastSignificantBits());
            item.setCode(ZDK_ZCFG_FILE_DATE_CODE);
            item.setValue("1980-01-01");
            configService.saveOrUpdateItem(item);
        }
        return item.getValue();
    }

    private Integer zdkPageSize = 50;
    private boolean isFinished = true;

    private Integer fetchGszdFileInfo() {
        String fileDate = getZdkGszdFileDateConfigValue();
        logService.quickLog("[" + EkdsUtil.formatDate(new Date()) + "]抓取公司制度fileDate:" + fileDate);
        Integer count = countGszdFile(fileDate);
        System.out.println("count:" + count);
        if (count != null && count != 0) {
            int pageMax = count / zdkPageSize + 1;
            System.out.println("pageMax:" + pageMax);
            for (int page = 1; page <= pageMax; page++) {
                fetchFileInfoByPaging(fileDate, String.valueOf(page), String.valueOf(zdkPageSize));
            }
        }
        logService.quickLog("[" + EkdsUtil.formatDate(new Date()) + "]抓取公司制度结束共:" + count + "条");
        return count;
    }

    private void fetchFileInfoByPaging(String fileDate, String pageNo, String pageSize) {
        //getZcfgFileInfo(fileDate,pageNo,pageSize);
        List<ZdkFileInfo> zdkFileInfoList = getGszdFileList(fileDate, pageNo, pageSize);
        saveManagedFile(ZDK_GSZD_FILE_DATE_CODE, fileDate, zdkFileInfoList);
    }

    private Integer fetchZcfgManagedFile() {

        String fileDate = getZdkZcfgFileDateConfigValue();
        logService.quickLog("[" + EkdsUtil.formatDate(new Date()) + "]抓取政策法规fileDate:" + fileDate);
        List<ZdkFileInfo> zdkFileInfoList = getZcfgFileInfo(fileDate, "1", "200000");
        int count = 0;
        if (!CollectionUtils.isEmpty(zdkFileInfoList)) {
            saveManagedFile(ZDK_ZCFG_FILE_DATE_CODE, fileDate, zdkFileInfoList);
            count = zdkFileInfoList.size();
        }
        logService.quickLog("[" + EkdsUtil.formatDate(new Date()) + "]抓取政策法规结束共:" + count + "条");
        return count;
    }

    private boolean processExistManagedFiles(Collection<ManagedFile> mfList, ManagedFile mf) {
        String fileNo = mf.getFileNo();

        String isoStatus = mf.getIsoStatus();
        List<ManagedFile> existMfList = new ArrayList<>();
        if("101".equals(isoStatus)){
            existMfList = managedFileRepository.findAllByFileNoAndIsoStatus(fileNo, isoStatus);
        }
        //没有重复的直接入库
        if (CollectionUtils.isEmpty(existMfList)) {
            mfList.add(mf);
            return true;
        }
        String fileDate = mf.getFileDate();
        if(EkdsUtil.isEmptyString(fileDate)){
            return false;
        }
        String maxDate = "1970-01-01";
        for(ManagedFile emf:existMfList){
            //注意:ManagedFile 重写了hashcode 和equals方法,否者这里不能这么写
            //已有的并且编号，日期一致的直接返回
            if(emf.equals(mf)){
                return false;
            }
            //不等才需要处理
            String emFileDate = emf.getFileDate();
            //已有的文件没有文件日期，直接删了
            if(EkdsUtil.isEmptyString(fileDate)){
                deleteEMF(emf);
                continue;
            }

            //存在的文件的文件日期小于新增的需要更新
            if(emFileDate.compareTo(fileDate)<0){
                deleteEMF(emf);
            }

            if(maxDate.compareTo(emFileDate)<0){
                maxDate = emFileDate;
            }
        }
        //当前的文件时间要大于已经存在的才添加
        if(fileDate.compareTo(maxDate)>0){
            mfList.add(mf);
            return true;
        }
        return false;
    }

    private void deleteEMF(ManagedFile emf){
        managedFileRepository.deleteById(emf.getId());
        String esId = emf.getMoReference();
        if(EkdsUtil.isNotEmptyString(esId)){
            fileContentPerPageRepository.deleteById(esId);
        }

    }

    private void saveManagedFile(String code, String fileDate, List<ZdkFileInfo> zdkFileInfoList) {
        Date maxDate = EkdsUtil.parseDateBySimple(fileDate);
        long max = -1;
        if (maxDate != null) {
            max = maxDate.getTime();
        }
        Set<ManagedFile> mfList = new HashSet<>();
        for (ZdkFileInfo zdkFileInfo : zdkFileInfoList) {
            String date = zdkFileInfo.getFileDate();
            Date curDate = EkdsUtil.parseDateBySimple(date);
            if (curDate != null) {
                if (curDate.getTime() > max) {
                    max = curDate.getTime();
                }
            }
            ManagedFile mf = zdkFileInfo.toNoIndexedManagedFile();
            mf.setDefaultValue();
            mf.setId(UUID.randomUUID().toString());
            if (ZDK_ZCFG_FILE_DATE_CODE.equals(code)) {
                mf.setFileType("zcfg");
            } else {
                mf.setFileType("gszd");
            }
            //mf.getFileName()+mf.getFileNo();
            //
            processExistManagedFiles(mfList,mf);

        }
        System.out.println("maxDate:" + EkdsUtil.formatDateSimple(new Date(max)));
        if(!CollectionUtils.isEmpty(mfList)){
            managedFileRepository.saveAll(mfList);
        }
        //这里有可能第二页什么都没有或者file_date比较小
        if (ZDK_GSZD_FILE_DATE_CODE.equals(code)) {
            String fileD = getZdkGszdFileDateConfigValue();
            Date dt = EkdsUtil.parseDateBySimple(fileD);
            if (dt != null) {
                if (dt.getTime() > new Date(max).getTime()) {
                    max = dt.getTime();
                }
            }
        }
        updateZdkDateConfigValue(code, EkdsUtil.formatDateSimple(new Date(max)));
    }

    /**
     * 自动获取文件信息
     *
     * @return
     */
    public Integer autoFetchFileInfo() {

        return fetchZcfgManagedFile() + fetchGszdFileInfo();

    }

    /**
     * 自动下载文件（ManagedFile的moStatus）
     */
    public void autoDownloadFile() {
        //正有
        if (!isFinished) {
            return;
        }
        isFinished = false;
        try {
            Integer count = managedFileRepository.countAllByIndexed(false);
            Integer pageSize = 50;
            int pages = count / pageSize;
            if (count % pageSize > 0) {
                pages++;
            }
            for (int page = 0; page < pages; page++) {
                Pageable pb = PageRequest.of(page, pageSize, Sort.Direction.DESC, "createDate");
                List<ManagedFile> managedFileList = managedFileRepository.findByIndexedEquals(false, pb);
                for (ManagedFile mf : managedFileList) {
                    autoIndexFile(mf);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            isFinished = true;
        }


    }

    //Pageable pb = new PageRequest();
//        List<ManagedFile> managedFileList = managedFileRepository.findByIndexedEquals(false);


    /**
     * 自动索引文件
     */
    public void autoIndexFile(ManagedFile mFile) {
        File file = downloadFile(mFile);
        try {
            //EkdsFileContent
            String content = "";
            if ("pdf".equals(mFile.getMoDesc())) {
                PDDocument document = PDDocument.load(new FileInputStream(file));
                try {
                    int pages = document.getNumberOfPages();
                    PDFTextStripper stripper = new PDFTextStripper();
                    // 设置按顺序输出
                    stripper.setSortByPosition(true);
                    stripper.setStartPage(1);
                    stripper.setEndPage(pages + 1);
                    content = stripper.getText(document);
                } finally {
                    if (document != null) {
                        document.close();
                    }
                }
            } else {
                content = ZdkFileReader.getContentByPoi(file);
            }
            content = mFile.getFileName() + content;
            EkdsFileContent efc = new EkdsFileContent();
            efc.setContent(content);
            efc.buildByManagedFile(mFile);
            fileContentPerPageRepository.save(efc);
            mFile.setIndexed(true);
            mFile.setMoReference(efc.getId());
            managedFileRepository.save(mFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Integer countGszdFile(String fileDate) {
        String countUrl = injectParam(gszdFileCountUrl, fileDate);
        System.out.println("countUrl:" + countUrl);
        String ret = EkdsUtil.executeHttpGetMethodRequest(countUrl);
        Map data = JSON.parseObject(ret, HashMap.class);
        Object val = data.get("fileCount");
        if (val == null || "".equals(String.valueOf(val).trim())) {
            return 0;
        }
        return Integer.valueOf(String.valueOf(val));
    }

    public List<ZdkFileInfo> getGszdFileList(String fileDate, String pageNo, String pageSize) {
        String fetchUrl = injectParam(gszdFileFetchUrl, fileDate, pageNo, pageSize);
        logService.quickLog("[" + EkdsUtil.formatDate(new Date()) + "]获取公司制度URL:" + fetchUrl);
        return fetchZdkFileInfoList(fetchUrl);
    }

    public List<ZdkFileInfo> getZcfgFileInfo(String fileDate, String pageNo, String pageSize) {
        String fetchUrl = injectParam(zcfgFileFetchUrl, fileDate, pageNo, pageSize);
        logService.quickLog("[" + EkdsUtil.formatDate(new Date()) + "]获取政策法规URL:" + fetchUrl);
        return fetchZdkFileInfoList(fetchUrl);
    }

    public File downloadFile(ManagedFile file) {
        String fileId = file.getMoId();
        String url = injectParam(fileDownloadUrl, null, null, null, fileId);
        logService.quickLog("[" + EkdsUtil.formatDate(new Date()) + "]下载URL:" + url);

        String name = file.getId() + "." + file.getMoDesc();
        ConfigItem item = configService.getItemByCode("temp_file_dir");
        String basePath = this.getClass().getResource("").getPath() + File.separator;
        if (item != null) {
            String val = item.getValue();
            File dir = new File(val);
            basePath = item.getValue();
            if (!dir.exists()) {
                try {
                    dir.mkdirs();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        File f = new File(basePath + fileId.hashCode());
        if (!f.exists()) {
            f.mkdirs();
        }
        String downloadFilePath = basePath + fileId.hashCode() + File.separator + name;
        File outputFile = new File(downloadFilePath);
        if (outputFile.exists()) {
            outputFile.delete();
        }
        try {
            outputFile.createNewFile();
        } catch (IOException e) {
            System.out.println("file is error:" + outputFile);
            e.printStackTrace();
            return null;
        }
        try {
            URL downloadUrl = new URL(url);
            URLConnection conn = downloadUrl.openConnection();
            InputStream ins = conn.getInputStream();
            FileOutputStream fout = new FileOutputStream(outputFile);
            EkdsUtil.copyStream(ins, fout, true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return outputFile;

    }

    private List<ZdkFileInfo> fetchZdkFileInfoList(String fetchUrl) {
        String ret = EkdsUtil.executeHttpGetMethodRequest(fetchUrl);
        System.out.println("返回结果:" + ret);
        logService.quickLog(fetchUrl);
        List<ZdkFileInfo> dataMapList = JSON.parseArray(ret, ZdkFileInfo.class);
        return dataMapList;
    }

    private static String injectParam(String url, String fileDate, String pageNo, String pageSize) {
        return injectParam(url, fileDate, pageNo, pageSize, null);
    }

    private static String injectParam(String url, String fileDate) {
        return injectParam(url, fileDate, null, null, null);
    }

    private static String injectParam(String url, String fileDate, String pageNo, String pageSize, String idSeq) {
        String ret = url;
        if (EkdsUtil.isEmptyString(url)) {
            return "";
        }
        if (EkdsUtil.isNotEmptyString(fileDate)) {
            ret = ret.replace("#file_date", fileDate);
        }
        if (EkdsUtil.isNotEmptyString(pageNo)) {
            ret = ret.replace("#page_no", pageNo);
        }
        if (EkdsUtil.isNotEmptyString(pageSize)) {
            ret = ret.replace("#page_size", pageSize);
        }
        if (EkdsUtil.isNotEmptyString(idSeq)) {
            ret = ret.replace("#id_seq", idSeq);
        }
        return ret;
    }

    public static void main(String[] args) {
        System.out.println("2020-06-09".compareTo("2020-12-01"));
        //System.out.println(injectParam("http://100.16.14.88:8071/gisoWeb/isoFileIndexService?method=queryData&fileDate=${file_date}&pageNo=${page_no}&pageSize=${page_size}", "2021-09-01", "1", "50", null));
    }

}
