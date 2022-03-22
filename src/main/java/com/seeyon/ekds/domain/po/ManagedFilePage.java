package com.seeyon.ekds.domain.po;

import com.seeyon.ekds.domain.BaseManagedObjectDomain;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by liuwenping on 2021/7/22.
 */
@Data
@Entity
@Table(name = "mo_ekds_file_page")
public class ManagedFilePage extends BaseManagedObjectDomain<String> {

    @Column(name="page_number")
    private String pageNumber;

    @Column(name="file_id")
    private String fileId;

    @Column(name="content_es_id")
    private String contentEsId;


}
