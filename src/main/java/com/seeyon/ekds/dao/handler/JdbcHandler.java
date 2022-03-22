package com.seeyon.ekds.dao.handler;

import com.seeyon.ekds.domain.BaseDomain;
import com.seeyon.ekds.util.AppContextUtil;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by liuwenping on 2021/6/25.
 * @author liuwenping
 */

public final class JdbcHandler {

    public static <T extends BaseDomain>  List<T> findBySql(String sql, Class<T> cls){

            return null;
    }
    public static int executeUpdate(String sql){
        EntityManager em = AppContextUtil.getBean("entityManager");

        Query query = em.createNativeQuery(sql);
        int  ret = query.executeUpdate();
        return ret;
    }
    public static List findRawDataBySql(String sql){
        EntityManager em = AppContextUtil.getBean("entityManager");
        Query query = em.createNativeQuery(sql);
        List list = query.getResultList();
        return list;
    }

}
