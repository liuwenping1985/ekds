package com.seeyon.ekds.apps.zdk.reader;

import com.seeyon.ekds.apps.zdk.ZdkConstant;
import com.seeyon.ekds.apps.zdk.vo.ZdkFileInfo;
import com.seeyon.ekds.data.descriptor.DataDescriptor;
import com.seeyon.ekds.data.locator.impl.InputStreamDataLocator;
import com.seeyon.ekds.data.locator.impl.ZdkFileDataLocator;
import com.seeyon.ekds.domain.po.ConfigItem;
import com.seeyon.ekds.domain.po.es.EkdsFileContent;
import com.seeyon.ekds.engine.reader.EsContentReader;
import com.seeyon.ekds.service.ConfigService;
import com.seeyon.ekds.util.EkdsUtil;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.extractor.ExtractorFactory;
import org.apache.poi.extractor.POITextExtractor;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.ooxml.extractor.POIXMLTextExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

/**
 * 工具人
 * Created by liuwenping on 2021/6/28.
 *
 * @author liuwenping
 */
@Component("fileReader")
public class ZdkFileReader implements EsContentReader<ZdkFileInfo> {

    @Autowired
    private ConfigService configService;

    private String getZdkFileDownloadURL(String fileId) {
        ConfigItem item = configService.getItemByCode("zdk_file_url");
        String url = "";
        if (item != null) {
            url = item.getValue();
        }
        return url;

    }


    @Override
    public EkdsFileContent read(DataDescriptor<ZdkFileInfo> descriptor) {
        //this is a filter method for data flush
        try {
            return readContent(descriptor);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private EkdsFileContent readByPdfBox(DataDescriptor<ZdkFileInfo> file) {


        return null;

    }

    private EkdsFileContent readContent(DataDescriptor<ZdkFileInfo> descriptor) throws IOException {
        ZdkFileInfo info = descriptor.getValue();
        String typeRaw = info.getExt();
        String type = "";
        FileInputStream headerIns = null;
        if ("doc".equals(typeRaw) || "docx".equals(typeRaw)) {
            try {
                headerIns = new FileInputStream(new File(info.getLocation()));
                byte[] header = new byte[4];
                headerIns.read(header);
                type = EkdsUtil.getFileType(header);
            } catch (Exception e) {
                e.printStackTrace();
                type = typeRaw;
            } finally {
                if (headerIns != null) {
                    try {
                        headerIns.close();
                    } catch (IOException e) {

                    }
                }
            }
        }

        if (ZdkConstant.PDF.equals(type)) {
            return readByPdfBox(descriptor);
        }
        String contentString = "";
        if (ZdkConstant.DOC.equals(type) || ZdkConstant.DOCX.equals(type)) {
            contentString = getContentByPoi(new File(info.getLocation()));
        }
//        if (ZdkConstant.DOC.equals(type)) {
//            InputStream is = null;
//            try {
//                is = new FileInputStream(new File(info.getLocation()));
//                WordExtractor ex = new WordExtractor(is);
//                contentString = ex.getText();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                if(is!=null){
//                    try {
//                        is.close();
//                    } catch (Exception e) {
//
//                    }
//                }
//            }
//
//        }
//        if (ZdkConstant.DOCX.equals(type)) {
//            OPCPackage opcPackage = null;
//            try {
//                opcPackage = POIXMLDocument.openPackage(info.getLocation());
//                POIXMLTextExtractor extractor = new XWPFWordExtractor(opcPackage);
//                contentString = extractor.getText();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }finally {
//                if(opcPackage!=null){
//                    try {
//                        opcPackage.close();
//                    } catch (Exception e) {
//
//                    }
//                }
//            }
//        }
        if ("unKnown".equals(type)) {
            try {

                contentString = EkdsUtil.getFilePlainContent(new File(info.getLocation()));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        EkdsFileContent content = new EkdsFileContent();
        content.setContent(contentString);
        content.setFileName(info.getFileName());
        content.setCreateDate(new Date());
        content.setFileMoId(info.getIdSeq());
        content.setType(info.getExt());
//        content.setMoId(info.getIdSeq());
//        content.setId(content.getMoId());
        return content;
    }

    public static String getContentByPoi(File f) throws IOException {
        if (f == null) {

            return "";
        }
        POITextExtractor extractor =null;
        try {
             extractor = ExtractorFactory.createExtractor(f);
            String text = extractor.getText();
            return docTextFilter(text);

        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if(extractor!=null){
                try {
                    extractor.setCloseFilesystem(true);
                    extractor.close();
                }catch (Exception e){

                }
            }
        }
        return "";
    }

    public static void main(String[] args) throws Exception {
        String filePath = "C:\\Users\\shenzhiping\\Desktop\\上线签字文档集合\\文档\\协同办公平台_生产发布影响分析报告.doc";
        System.out.println(getContentByPoi(new File(filePath)));
//        InputStream is = new FileInputStream(new File(filePath));
//        WordExtractor ex = new WordExtractor(is);
//        String s = ex.getText();
//        System.out.println(s);

    }

    public static String getCurrentOperatingSystem() {
        String os = System.getProperty("os.name").toLowerCase();
        return os;
    }

    private static String docTextFilter(String str) {
        if (str == null || str.trim().isEmpty()) {
            return "";
        } else {
            str = str.replace("\u0013 TOC \\o \"1-3\" \\h \\z \\u \u0014", "").replace("TOC \\o \"1-2\" \\t \"", "").replaceAll("\u0013 HYPERLINK .. \"_Toc\\w*\" \u0001\u0014", "").replaceAll("\u0013 PAGEREF _Toc\\d{0,20} .{0,2} ", "").replaceAll("\u0013 HYPERLINK \".*\" \u0001\u0014", "").replaceAll("\u0013 PAGEREF _Toc\\d{0,20} ", "").replaceAll("\u0013 SHAPE  .\\* MERGEFORMAT ", "").replaceAll("\u0013 PAGE   .. MERGEFORMAT .*", "").replaceAll("\u0014", "").replaceAll("\u0007", "").replaceAll("\u000b", "").replaceAll("\u0015", "").replaceAll("\b", "").replaceAll("\u0001", "").replaceAll("\u0013", "").replaceAll("EMBED PBrush", "").replaceAll("null", "").replaceAll("[\\t\\n\\x0B\\f\\r]", "");
            return str;
        }
    }

}