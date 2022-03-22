package com.seeyon.ekds.platform.domain;

import com.seeyon.ekds.domain.BaseManagedObjectDomain;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by shenzhiping on 2021/11/12.
 */
@Data
@Entity
@Table(name = "mo_ekds_log")
public class LogItem extends BaseManagedObjectDomain<String> {

    @Column(name="log_level")
    private String level;
    @Column(name="log_type")
    private String type;
    @Column(name="create_date")
    private Date createDate;
    @Column(name="log_content")
    private String content;
    @Column(name="ext1")
    private String ext1;
    @Column(name="ext2")
    private String ext2;
    @Column(name="ext3")
    private String ext3;

}
