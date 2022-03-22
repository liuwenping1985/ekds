package com.seeyon.ekds.domain;

import com.seeyon.ekds.domain.mo.ManagedObject;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * 基础管理对象,考虑多节点，多租户的情况，分布式数据更新的问题
 * 作为基础PO的属性，但不是必须被继承
 * Created by liuwenping on 2019/8/27.
 * @Author liuwenping
 */
@MappedSuperclass
@Data
public abstract class BaseManagedObjectDomain<I extends Serializable> extends BaseTenantDomain <I> implements ManagedObject<I> {
    @Column(name = "uuid")
    private String uuid;

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "update_date")
    private Date updateDate;

    @Column(name = "mo_id")
    private String moId;

    @Column(name = "mo_reference")
    private String moReference;

    @Column(name = "mo_data_version")
    private String moDataVersion;

    @Column(name = "mo_data")
    private String moData;

    @Column(name = "mo_status")
    private String moStatus;

    @Column(name = "mo_lock_status")
    private String moLockStatus;

    @Column(name = "mo_desc")
    private String moDesc;

    public void setDefaultValue(){

        createDate = new Date();
        if(uuid==null||uuid.isEmpty()){
            uuid= UUID.randomUUID().toString();
        }

    }


}
