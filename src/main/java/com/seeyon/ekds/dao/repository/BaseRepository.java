package com.seeyon.ekds.dao.repository;

import com.seeyon.ekds.domain.BaseDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * Created by liuwenping on 2019/8/27.
 */
@NoRepositoryBean
public interface BaseRepository<T extends BaseDomain, I extends Serializable> extends JpaRepository<T, I> {


}
