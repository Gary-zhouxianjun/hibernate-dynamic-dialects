package com.gary.dao.jdbc;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.gary.dao.dto.BatchSqlDto;

public abstract interface CommonDao {
	/**
     * 缓存查询
     * @param sql SQL语句
     * @param params 传入参数(可为NULL)
     * @return 结果集
     * @throws SQLException
     */
	public List<Map<String, Object>> cacheQuery(String sql, Object[] params) throws SQLException;
    /**
     * 查询 
     * @param sql SQL语句
     * @param params 传入参数(可为NULL)
     * @return 结果集
     * @throws SQLException
     */
	public List<Map<String, Object>> query(String sql, Object[] params) throws SQLException;
    /**
     * 执行增删改
     * @param sql SQL语句
     * @param params 传入参数(可为NULL)
     * @return 成功条数
     * @throws SQLException
     */
	public int execuet(String sql, Object[] params) throws SQLException;
    /**
     * 批量执行查询
     * @param sqls 批量集合<BatchSqlDto>
     * @return Map集合key=sql，value=result
     * @throws SQLException
     */
    public Map<String, List<Map<String, Object>>> batchQuery(List<BatchSqlDto> sqls) throws SQLException;
    /**
     * 缓存批量执行查询
     * @param sqls sqls 批量集合<BatchSqlDto>
     * @return Map集合key=sql，value=result
     * @throws SQLException
     */
    public Map<String, List<Map<String, Object>>> batchCacheQuery(List<BatchSqlDto> sqls) throws SQLException;
    /**
     * 批量执行增删改(多条SQL语句的批量处理)
     * @param sqls sql集合
     * @return 成功集合
     * @throws SQLException
     */
    public int[] batchExecuet(List<BatchSqlDto> sqls) throws SQLException;
    /**
     * 批量执行增删改(一个SQL语句的批量传参)
     * @param sql SQL语句
     * @param list 批量参数
     * @return 成功集合
     * @throws SQLException
     */
    public int[] batchExecuet(String sql,List<Object[]> list) throws SQLException;
}
