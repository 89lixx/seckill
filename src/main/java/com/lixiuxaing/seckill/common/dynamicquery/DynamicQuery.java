package com.lixiuxaing.seckill.common.dynamicquery;

import java.util.List;

/**
 * 扩展SpringDataJpa, 支持动态jpql/nativesql查询并支持分页查询
 */
public interface DynamicQuery {

    void save(Object entity);

    void update(Object entity);

    <T> void delete(Class<T> entityClass, Object entityId);
    <T> void delete(Class<T> entityClass, Object[] entityIds);
    /**
     * 查询对象列表，返回list
     * @param nativeSql:
     * @param params:
     * @return List<T>
     */
    <T> List<T> nativeQueryList(String nativeSql, Object... params);

    /**
     * 查询对象列表，返回list<Object>
     * @param nativeSql:
     * @param resultClass:
     * @param params:
     * @return List<T>
     */
    <T> List<T> nativeQueryListModel(Class<T> resultClass, String nativeSql, Object... params);

    /**
     * 查询对象列表，返回list<Map<key,value>>
     * @param nativeSql:
     * @param params:
     * @return List<T>
     */
    <T> List<T> nativeQueryListMap(String nativeSql, Object... params);

    /**
     * 执行nativeSql统计查询
     * @param nativeSql:
     * @param params: 占位符参数(例如?1)绑定的参数值
     * @return Object
     */
    Object nativeQueryObject(String nativeSql, Object... params);

    /**
     * 执行nativeSql统计查询
     * @param nativeSql:
     * @param params: 占位符参数(例如?1)绑定的参数值
     * @return Object
     */
    Object[] nativeQueryArray(String nativeSql, Object... params);

    /**
     * 执行nativeSql的update,delete操作
     * @param nativeSql:
     * @param params:
     * @return int
     */
    int nativeExecuteUpdate(String nativeSql, Object... params);
}
