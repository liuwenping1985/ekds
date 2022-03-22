package com.seeyon.ekds.platform.dao;

import com.seeyon.ekds.dao.repository.BaseRepository;
import com.seeyon.ekds.platform.domain.LogItem;
import org.springframework.stereotype.Repository;

/**
 * Created by shenzhiping on 2021/11/12.
 */
@Repository
public interface LogRepository extends BaseRepository<LogItem, String> {
}
