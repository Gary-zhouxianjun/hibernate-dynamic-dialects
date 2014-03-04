package com.gary.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import com.gary.core.dto.LRUCache;
import com.gary.dao.dto.BatchSqlDto;

public class DBConn{
	private Logger log = Logger.getLogger(DBConn.class);
	private AtomicLong visitedCount = new AtomicLong(0);
    private AtomicLong hitCount = new AtomicLong(0);
    private LRUCache<String, List<Map<String, Object>>> cache = null;
    private Connection connection;
    public DBConn(Connection connection,int maxSize) {
		this.connection = connection;
		cache = maxSize < 1 ? new LRUCache<String, List<Map<String, Object>>>() : new LRUCache<String, List<Map<String, Object>>>(maxSize);
	}
    /**
     * 缓存查询
     * @param sql SQL语句
     * @param list 传入参数(可为NULL)
     * @return 结果集
     * @throws SQLException
     */
	public List<Map<String, Object>> cacheQuery(String sql,Object[] params) throws SQLException{
		visitedCount.incrementAndGet();
		if (cache.containsKey(sql)) {
            hitCount.incrementAndGet();
            return cache.get(sql);
        } else {
        	List<Map<String, Object>> result = query(sql, params);
            cache.put(sql, result);
            return result;
        }
	}
    /**
     * 查询 
     * @param sql SQL语句
     * @param list 传入参数(可为NULL)
     * @return 结果集
     * @throws SQLException
     */
	public List<Map<String, Object>> query(String sql,Object[] params) throws SQLException{
    	log.debug("[query]SQL:"+sql);
    	PreparedStatement ps = connection.prepareStatement(sql);
    	setParams(params, ps);
    	ResultSet rs = ps.executeQuery();
    	List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
    	ResultSetMetaData md = rs.getMetaData();
    	int columnCount = md.getColumnCount();
    	while (rs.next()) {
    		Map<String, Object> map = new HashMap<String, Object>();
    		for (int i = 1; i <= columnCount; i++) { 
    		     map.put(md.getColumnName(i), rs.getObject(i)); 
		    }
    		result.add(map);
		}
    	ps.close();
    	return result;
    }
	private void setParams(Object[] params, PreparedStatement ps)
			throws SQLException {
		for (int i = 0; params != null && i < params.length; i++) {
    		Object o = params[i];
			log.debug(i+"--"+o);
			ps.setObject(i + 1, o);
		}
	}
    /**
     * 执行增删改
     * @param sql SQL语句
     * @param list 传入参数(可为NULL)
     * @return 成功条数
     * @throws SQLException
     */
	public int execuet(String sql,Object[] params) throws SQLException{
    	int n = 0;
    	log.debug("[execuet]SQL:"+sql);
    	PreparedStatement ps = connection.prepareStatement(sql);
    	setParams(params, ps);
		n = ps.executeUpdate();
		ps.close();
		return n;
    }
    /**
     * 批量执行查询
     * @param sqls 批量集合<BatchSqlDto>
     * @param isCache 是否把查询结果集添加到缓存(单条SQL)
     * @return Map集合key=sql，value=result
     * @throws SQLException
     */
    public Map<String, List<Map<String, Object>>> batchQuery(List<BatchSqlDto> sqls) throws SQLException{
    	Map<String, List<Map<String, Object>>> rs = new HashMap<String, List<Map<String,Object>>>();
    	for (BatchSqlDto batchSqlDto : sqls) {
    		String sql = batchSqlDto.getSql();
    		log.debug("[batchQuery]SQL:"+sql);
    		rs.put(sql, query(sql, batchSqlDto.getParams()));
		}
    	return rs;
    }
    /**
     * 缓存批量执行查询
     * @param sqls sqls 批量集合<BatchSqlDto>
     * @return Map集合key=sql，value=result
     * @throws SQLException
     */
    public Map<String, List<Map<String, Object>>> batchCacheQuery(List<BatchSqlDto> sqls) throws SQLException{
    	Map<String, List<Map<String, Object>>> rs = new HashMap<String, List<Map<String,Object>>>();
    	for (BatchSqlDto batchSqlDto : sqls) {
    		String sql = batchSqlDto.getSql();
    		log.debug("[batchCacheQuery]SQL:"+sql);
    		rs.put(sql, cacheQuery(sql, batchSqlDto.getParams()));
		}
    	return rs;
    }
    /**
     * 批量执行增删改(多条SQL语句的批量处理)
     * @param sqls sql集合
     * @return 成功集合
     * @throws SQLException
     */
    public int[] batchExecuet(List<BatchSqlDto> sqls) throws SQLException{
    	Statement sm = connection.createStatement();
    	for (BatchSqlDto batchSqlDto : sqls) {
    		log.debug("[batchExecuet]SQL: " + batchSqlDto.getSql());
    		sm.addBatch(batchSqlDto.getParamsSql());
		}
    	int[] rs = sm.executeBatch();
    	sm.close();
    	return rs;
    }
    /**
     * 批量执行增删改(一个SQL语句的批量传参)
     * @param sql SQL语句
     * @param list 批量参数
     * @return 成功集合
     * @throws SQLException
     */
    public int[] batchExecuet(String sql,List<Object[]> list) throws SQLException{
    	PreparedStatement ps = connection.prepareStatement(sql);
    	log.debug("[batchExecuet]SQL:"+sql);
    	for (Object[] list2 : list) {
    		setParams(list2, ps);
    		ps.addBatch();
		}
    	int[] rs = ps.executeBatch();
    	ps.close();
    	return rs;
    }
	public Connection getConnection() {
		return connection;
	}
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
}
