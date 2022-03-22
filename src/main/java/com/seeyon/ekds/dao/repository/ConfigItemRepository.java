package com.seeyon.ekds.dao.repository;

import com.seeyon.ekds.domain.po.ConfigItem;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by liuwenping on 2021/7/12.
 */
@Repository
public interface ConfigItemRepository extends BaseRepository<ConfigItem, Long> {

    ConfigItem findByCode(String code);

    List<ConfigItem> findAllByGroupName(String groupName);

}
