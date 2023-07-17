package com.lixiuxaing.seckill.common.dynamicquery;

import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Objects;

/**
 * @Author: lixiuxiang3
 * @Date: 2023/7/17 18:18
 * @Version: 1.0
 */
@Repository
public class DynamicQueryImpl implements DynamicQuery{

    @PersistenceContext
    private EntityManager em;

    public EntityManager getEntityManager() {
        return em;
    }
    @Override
    public void save(Object entity) {
        em.persist(entity);
    }

    @Override
    public void update(Object entity) {
        em.merge(entity);
    }

    @Override
    public <T> void delete(Class<T> entityClass, Object entityId) {
        delete(entityClass, new Object[] {entityId});
    }

    @Override
    public <T> void delete(Class<T> entityClass, Object[] entityIds) {
        for (Object obj : entityIds) {
            em.remove(em.getReference(entityClass, obj));
        }
    }

    private Query createQuery(String nativeSql, Object... params) {
        Query q = em.createQuery(nativeSql);
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; ++ i) {
                q.setParameter(i + 1, params[i]);
            }
        }
        return q;
    }
    @Override
    public <T> List<T> nativeQueryList(String nativeSql, Object... params) {
        Query q = createQuery(nativeSql, params);
        q.unwrap(SQLQuery.class).setResultTransformer(Transformers.TO_LIST);
        return q.getResultList();
    }

    @Override
    public <T> List<T> nativeQueryListModel(Class<T> resultClass, String nativeSql, Object... params) {
        Query q = createQuery(nativeSql, params);
        q.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(resultClass));
        return q.getResultList();
    }

    @Override
    public <T> List<T> nativeQueryListMap(String nativeSql, Object... params) {
        Query q = createQuery(nativeSql, params);
        q.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return q.getResultList();
    }

    @Override
    public Object nativeQueryObject(String nativeSql, Object... params) {
        return createQuery(nativeSql, params).getSingleResult();
    }

    @Override
    public Object[] nativeQueryArray(String nativeSql, Object... params) {
        return (Object[]) createQuery(nativeSql, params).getSingleResult();
    }

    @Override
    public int nativeExecuteUpdate(String nativeSql, Object... params) {
        return createQuery(nativeSql, params).executeUpdate();
    }
}
