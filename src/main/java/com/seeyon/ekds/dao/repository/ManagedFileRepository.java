package com.seeyon.ekds.dao.repository;

import com.seeyon.ekds.domain.po.ManagedFile;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * Created by liuwenping on 2021/7/16.
 */
@Repository
public interface ManagedFileRepository extends BaseRepository<ManagedFile, String> {

    List<ManagedFile> findAllByIdIn(Collection<String> idList);
    ManagedFile findByMoId(String moId);
    List<ManagedFile>findByIndexedEquals(Boolean indexed, Pageable pageable);

    Integer countAllByIndexed(Boolean indexed);

    List<ManagedFile> findAllByFileNoAndIsoStatus(String fileNo,String isoStatus);

}
