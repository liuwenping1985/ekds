package com.seeyon.ekds.domain.po;

import com.seeyon.ekds.domain.BaseManagedObjectDomain;
import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by liuwenping on 2021/7/16.
 */
@Data
@Entity
@Table(name = "mo_ekds_file")
public class ManagedFile extends BaseManagedObjectDomain<String> {

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_no")
    private String fileNo;

    @Column(name = "file_location")
    private String fileLocation;

    @Column(name = "page_count")
    private Integer pageCount;

    @Column(name = "is_indexed")
    private Boolean indexed;

    @Column(name = "es_data_id")
    private String esDataId;

    @Column(name = "file_date")
    private String fileDate;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "iso_status")
    private String isoStatus;

    @Column(name = "iso_type")
    private String isoType;

    @Column(name = "file_version")
    private String fileVersion;

    @Column(name = "file_grade")
    private String fileGrade;

    @Override
    public boolean equals(Object mf){
        if(mf instanceof ManagedFile){
            ManagedFile tmf = (ManagedFile)mf;
            return (""+tmf.getFileNo()+"@"+tmf.getFileDate()).equals(""+this.getFileNo()+"@"+this.getFileDate());
        }
        return false;
    }
    @Override
    public int hashCode(){
       return (""+this.getFileNo()+"@"+this.getFileDate()).hashCode();
    }


}
