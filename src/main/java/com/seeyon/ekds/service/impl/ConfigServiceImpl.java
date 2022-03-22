package com.seeyon.ekds.service.impl;

import com.seeyon.ekds.dao.repository.ConfigItemRepository;
import com.seeyon.ekds.domain.po.ConfigItem;
import com.seeyon.ekds.exception.EkdsRuntimeException;
import com.seeyon.ekds.service.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by liuwenping on 2021/7/12.
 */
@Service("configService")
public class ConfigServiceImpl implements ConfigService {

    private static final Logger log = LoggerFactory.getLogger(ConfigServiceImpl.class);

    private Map<String, ConfigItem> cacheData = new HashMap<>();

    @Autowired
    private ConfigItemRepository configItemRepository;

    @Override
    public ConfigItem getItemByCode(String code) {

        ConfigItem item = cacheData.get(code);
        if (item == null) {
            item = configItemRepository.findByCode(code);
        }
        return item;
    }

    @Override
    public List<ConfigItem> findAll() {


        return configItemRepository.findAll();
    }

    @Override
    public void saveOrUpdateItem(ConfigItem item) {
        Long id =                                                                                                                                                                                                                                                                                                                                     item.getId();
        if (id == null) {
            item.setId(UUID.randomUUID().getMostSignificantBits());
        }
        String code = item.getCode();
        if (isEmpty(code)) {
            throw new EkdsRuntimeException("code is not presented!");
        }

        item = configItemRepository.save(item);
        cacheData.remove(code);
        cacheData.put(code, item);
    }

    @Override
    public ConfigItem deleteConfigItemById(Long id) {
        Optional<ConfigItem> optionalItem = configItemRepository.findById(id);
        ConfigItem item = optionalItem.get();
        if (item != null) {
            configItemRepository.delete(item);
            String code = item.getCode();
            cacheData.remove(code);
        }
        return item;
    }

    @Override
    public List<ConfigItem> getItemsByGroupName(String groupName) {
        return configItemRepository.findAllByGroupName(groupName);
    }

    private static boolean isEmpty(@Nullable Object str) {
        return str == null || "".equals(str);
    }
}
