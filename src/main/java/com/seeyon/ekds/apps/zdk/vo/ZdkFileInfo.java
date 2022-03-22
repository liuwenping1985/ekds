package com.seeyon.ekds.apps.zdk.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.seeyon.ekds.domain.po.ManagedFile;
import com.seeyon.ekds.util.EkdsUtil;
import lombok.Data;
import org.elasticsearch.common.Strings;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * "IDSEQ":"9388498",
 * "FILENO":"信总发[2018]34号",
 * "FILENAME":"关于加强重大突发事件管理工作的通知",
 * "EXT":"pdf",
 * "BANBEN2":"A",
 * "FILEGRADE":"三级",
 * "FILEDATE":"2018-03-23",
 * "LOCATION":"OA发文中规范性文件暂存",
 * "ISOTYPE":"1",
 * "ISOSTATUS":"101"
 * Created by liuwenping on 2021/7/27.
 */
@Data
public class ZdkFileInfo {

    @JSONField(name = "IDSEQ")
    private String idSeq;

    @JSONField(name = "FILENO")
    private String fileNo;

    @JSONField(name = "FILENAME")
    private String fileName;

    @JSONField(name = "BANBEN2")
    private String fileVersion;

    @JSONField(name = "FILEGRADE")
    private String fileGrade;

    @JSONField(name = "EXT")
    private String ext;

    @JSONField(name = "FILEDATE")
    private String fileDate;

    @JSONField(name = "LOCATION")
    private String location;

    @JSONField(name = "ISOTYPE")
    private String isoType;

    @JSONField(name = "ISOSTATUS")
    private String isoStatus;

    /**
     *   文件类型优先级：1、2、4、8、3、5
     中文参考：正文1、内部支持文件2、外来支持文件4、附件8、相关记录3、改版说明5
     * @return
     */
    public ManagedFile toNoIndexedManagedFile() {
        ManagedFile mf = new ManagedFile();
        EkdsUtil.genValues4BaseManagedObjectDomain(mf);
        mf.setIndexed(false);
        String fileDate = this.getFileDate();
        if(fileDate!=null){
            fileDate = fileDate.trim();
        }
        if (Strings.isEmpty(fileDate)) {
            fileDate = "2000-01-01";
        }else{
            if(fileDate.length()<"2000-01-01".length()){
                fileDate = "2000-01-01";
            }
        }
        mf.setFileDate(fileDate);
        mf.setFileName(this.getFileName());
        mf.setMoDesc(this.getExt());
        mf.setMoId(this.getIdSeq());
        mf.setFileNo(this.getFileNo());
        mf.setDefaultValue();
        mf.setFileLocation(this.getLocation());
        mf.setIsoStatus(this.getIsoStatus());

        mf.setIsoType(EkdsUtil.parseIsoTypeOrder(this.getIsoType()));
        mf.setFileVersion(this.getFileVersion());
        mf.setFileGrade(this.getFileGrade());
        return mf;
    }

}
