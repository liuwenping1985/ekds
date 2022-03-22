package com.seeyon.ekds.domain.po;

import com.seeyon.ekds.domain.BaseTenantDomain;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by liuwenping on 2021/7/12.
 * @Author liuwenping
 */
@Data
@Entity
@Table(name="ekds_config_item")
public class ConfigItem extends BaseTenantDomain<Long>{

    private String code;

    @Column(name="group_name")
    private String groupName;

    private String name;

    private String value;

    private String ext1;

    private String ext2;

    private String ext3;

    private String ext4;

    private String ext5;
}
