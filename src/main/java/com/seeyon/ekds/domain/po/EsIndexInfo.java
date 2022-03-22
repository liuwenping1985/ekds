package com.seeyon.ekds.domain.po;

import com.seeyon.ekds.domain.BaseTenantDomain;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 索引信息，就是在查询的时候需要指定索引
 * Created by liuwenping on 2021/7/12.
 *
 * @Author liuwenping
 */
@Data
@Entity
@Table(name = "mo_es_index")
public class EsIndexInfo extends BaseTenantDomain<Long> {

    @Column(name = "es_index_name")
    private String esIndexName;


}
