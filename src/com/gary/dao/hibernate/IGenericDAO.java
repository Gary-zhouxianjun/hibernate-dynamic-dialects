package com.gary.dao.hibernate;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;

import com.gary.dao.dto.SqlOrderBy;
import com.gary.dao.dto.SqlWhere;
import com.gary.dao.result.Page;

public abstract interface IGenericDAO<T> {
	/**
	 * 保存一个实体
	 * @param t
	 */
	Serializable save(T t);  
	
	Session getSession();
	
	void saveOrUpdate(T t);
	
	/**
	 * 删除一个实体
	 * @param t
	 */
    void delete(T t);  
    /**
     * 更新一个实体
     * @param t
     */
    void update(T t);  
    /**
     * 根据ID获取一个实体
     * @param id
     * @return
     */
    T get(Integer id);  
    /**
     * SQL返回一个DTO
     * @param sql
     * @param names
     * @param resultClass
     * @return
     */
    List<?> transformerQuery(String sql, String[] names, Class<?> resultClass);
    /**
     * 通过命名查询
     * @param name
     * @param params
     * @return
     */
    List<?> namedQuery(String name, Map<String, Object> params);
    /**
     * 根据映射实体类查询
     * @param entity
     * @return
     */
    List<?> findByEntity(Object entity);
    Criteria getCriteria();
    /**
     * 获取当前所有实体
     * @return
     */
    List<T> list();
    
    /**
     * 获取当前所有实体
     * @param order 排序
     * @return
     */
    List<T> list(SqlOrderBy[] order);
    /**
     * 获取当前所有实体
     * @param where 查询参数
     * @return
     */
    List<T> list(Map<SqlWhere, Object> where);
    /**
     * 获取当前所有实体
     * @param where 查询参数
     * @param order 排序
     * @return
     */
    List<T> list(Map<SqlWhere, Object> where, SqlOrderBy[] order);
    /**
     * 获取实体
     * @param where 查询参数
     * @return
     */
    T get(Map<SqlWhere, Object> where);
    /**
     * 获取实体
     * @param hql HQL查询语句
     * @param params
     * @return
     */
    T get(String hql, Map<String, Object> params);
    /**
     * 获取第一个实体
     * @param hql HQL查询语句
     * @param params 参数
     * @return
     */
    T getFirst(String hql, Map<String, Object> params);
    /**
     * 获取所有实体
     * @param hql HQL查询语句
     * @param params 参数
     * @return
     */
    List<?> list(String hql, Map<String, Object> params);
    /**
     * 获取所有实体
     * @param hql HQL查询语句
     * @param params 参数
     * @param order 排序
     * @return
     */
    List<?> list(String hql, Map<String, Object> params, SqlOrderBy[] order);
    /**
     * 获取指定个数实体
     * @param hql HQL查询语句
     * @param params 参数
     * @param recordNum 返回多少个实体
     * @return
     */
    List<?> list(String hql, Map<String, Object> params, int recordNum);
    /**
     * 获取指定个数实体
     * @param hql HQL查询语句
     * @param params 参数
     * @param recordNum 返回多少个实体
     * @param order 排序
     * @return
     */
    List<?> list(String hql, Map<String, Object> params, int recordNum, SqlOrderBy[] order);
    /**
     * 分页获取实体
     * @param hql HQL查询语句
     * @param params 参数
     * @param pageSize 每页条数
     * @param page 第几页
     * @return
     */
    Page<?> list(String hql, Map<String, Object> params, int pageSize, int page);
    /**
     * 分页获取实体
     * @param hql HQL查询语句
     * @param params 参数
     * @param pageSize 每页条数
     * @param page 第几页
     * @param order 排序
     * @return
     */
    Page<?> list(String hql, Map<String, Object> params, int pageSize, int page, SqlOrderBy[] order);
    /**
     * 分页获取当前实体
     * @param where 查询条件
     * @param pageSize 每页条数
     * @param page 第几页
     * @return
     */
    Page<T> list(Map<SqlWhere, Object> where, int pageSize, int page);
    /**
     * 分页获取当前实体
     * @param pageSize 每页条数
     * @param page 第几页
     * @return
     */
    Page<T> list(int pageSize, int page);
    /**
     * 分页获取当前实体
     * @param where 查询条件
     * @param pageSize 每页条数
     * @param page 第几页
     * @param order 排序
     * @return
     */
    Page<T> list(Map<SqlWhere, Object> where, int pageSize, int page, SqlOrderBy[] order);
    /**
     * 获取总条数
     * @param hql HQL查询语句
     * @return
     */
    int getCount(String hql);
    
    /**
     * 获取总条数
     * @param hql HQL查询语句
     * @param params 参数
     * @return
     */
    int getCount(String hql, Map<String, Object> params);
}
