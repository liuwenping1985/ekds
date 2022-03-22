package com.seeyon.ekds.domain.po.es;

import com.seeyon.ekds.domain.mo.EsManagedObject;
import com.seeyon.ekds.domain.mo.ManagedObject;
import com.seeyon.ekds.domain.po.ManagedFile;
import lombok.Data;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.Strings;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.UUID;

/**
 * Created by liuwenping on 2021/6/30.
 *
 * @AUTHOR LIUWENPING
 */
@Data
@Document(indexName = "es_file_content")
public class EkdsFileContent implements EsManagedObject {
    @Nullable
    @Id
    private String id;

    @Nullable
    @Field(name = "content")
    private String content;
    @Nullable
    @Field(name = "content_type",type=FieldType.Keyword)
    private String contentType;
    @Nullable
    @Field(name = "file_mo_id",type=FieldType.Keyword)
    private String fileMoId;
    @Nullable
    @Field(name = "file_name")
    private String fileName;
    @Nullable
    @Field(name = "file_type", type = FieldType.Keyword)
    private String fileType;
    @Nullable
    @Field(name = "file_date",type=FieldType.Keyword)
    private String fileDate;
    @Nullable
    @Field(name = "index_name",type=FieldType.Keyword)
    private String indexName;
    @Nullable
    @Field(name = "tenant_name",type=FieldType.Keyword)
    private String tenantName;
    @Nullable
    @Field(name = "iso_type", type =FieldType.Keyword)
    private String isoType;

    @Nullable
    @Field(name = "iso_status", type = FieldType.Keyword)
    private String isoStatus;

    @Nullable
    @Field(name = "type",type=FieldType.Keyword)
    private String type;
    @Nullable
    @Field(name = "total_page",type=FieldType.Keyword)
    private Integer totalPage;
    @Nullable
    @Field(name = "create_date",type=FieldType.Date, pattern="dd/MM/uuuu")
    private Date createDate;
    @Nullable
    @Field(name = "update_date",type=FieldType.Date, pattern="dd/MM/uuuu")
    private Date updateDate;

    @Nullable
    @Field(name = "file_version", type = FieldType.Keyword)
    private String fileVersion;

    @Nullable
    @Field(name = "file_grade", type = FieldType.Keyword)
    private String fileGrade;

    @Nullable
    @Field(name = "file_no", type = FieldType.Keyword)
    private String fileNO;

    @Nullable
    @Field(name = "file_id",type=FieldType.Keyword)
    private String fileId;




    public EkdsFileContent(){
        this.id = UUID.randomUUID().toString();

    }

    public EkdsFileContent buildByManagedFile(ManagedFile mf){

        this.setType(mf.getMoDesc());
        this.setFileName(mf.getFileName());
        this.setFileMoId(mf.getMoId());
        this.setFileId(mf.getId());
        this.setCreateDate(new Date());
        this.setUpdateDate(new Date());
        String fileDate = mf.getFileDate();
        if(Strings.isEmpty(fileDate)){
            fileDate="2000-01-01";
        }
        this.setFileDate(fileDate);
        this.setFileType(mf.getFileType());
        this.setContentType(mf.getFileLocation());
        this.setIsoStatus(mf.getIsoStatus());
        this.setIsoType(mf.getIsoType());
        this.setIndexName("es_file_content");
        this.setTenantName("cinda");
        this.setFileVersion(mf.getFileVersion());
        this.setFileGrade(mf.getFileGrade());
        return this;
    }


}
