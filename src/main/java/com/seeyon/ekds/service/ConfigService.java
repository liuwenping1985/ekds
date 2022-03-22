package com.seeyon.ekds.service;

import com.seeyon.ekds.domain.po.ConfigItem;

import java.util.List;

/**
 * Created by liuwenping on 2021/7/12.
 */
public interface ConfigService {

    ConfigItem getItemByCode(String code);

    List<ConfigItem> findAll();

    void saveOrUpdateItem(ConfigItem item);

    ConfigItem deleteConfigItemById(Long id);

    List<ConfigItem> getItemsByGroupName(String groupName);

}
