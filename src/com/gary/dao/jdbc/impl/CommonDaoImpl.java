package com.gary.dao.jdbc.impl;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.gary.dao.dto.BatchSqlDto;
import com.gary.dao.jdbc.CommonDao;
import com.gary.dao.jdbc.DBConn;
import com.gary.dao.jdbc.DBConnMgr;

public abstract class CommonDaoImpl implements CommonDao {
	abstract protected String getDataSource();
	private String defaultPath = "db.xml";
	private DBConnMgr dbConnMgr;
	private DBConn con;
	protected String CfgPath(){
		return defaultPath;
	}
	private String getClassPath(){
		URL url = getClass().getClassLoader().getResource("");
		String path = url.getPath();
		return fix(path);
	}
	private static String fix(String path){
		if(path.startsWith("/"))
			path = path.substring(1);
		return path.replace("\\", "/");
	}
	public DBConn getDBConn() throws SQLException{
		if(con == null)
			con = dbConnMgr.getConnection(getDataSource());
		con.getConnection().setAutoCommit(false);
		return con;
	}
	protected void initDBConnMgr(){
		if(dbConnMgr == null)
			dbConnMgr = DBConnMgr.getInstance(getClassPath() + CfgPath());
	}
	@Override
	public Map<String, List<Map<String, Object>>> batchCacheQuery(
			List<BatchSqlDto> sqls) throws SQLException {
		// TODO Auto-generated method stub
		return getDBConn().batchCacheQuery(sqls);
	}
	@Override
	public int[] batchExecuet(List<BatchSqlDto> sqls) throws SQLException {
		// TODO Auto-generated method stub
		return getDBConn().batchExecuet(sqls);
	}
	@Override
	public int[] batchExecuet(String sql, List<Object[]> list)
			throws SQLException {
		// TODO Auto-generated method stub
		return getDBConn().batchExecuet(sql, list);
	}
	@Override
	public Map<String, List<Map<String, Object>>> batchQuery(
			List<BatchSqlDto> sqls) throws SQLException {
		// TODO Auto-generated method stub
		return getDBConn().batchQuery(sqls);
	}
	@Override
	public List<Map<String, Object>> cacheQuery(String sql, Object[] params)
			throws SQLException {
		// TODO Auto-generated method stub
		return getDBConn().cacheQuery(sql, params);
	}
	@Override
	public int execuet(String sql, Object[] params) throws SQLException {
		// TODO Auto-generated method stub
		return getDBConn().execuet(sql, params);
	}
	@Override
	public List<Map<String, Object>> query(String sql, Object[] params)
			throws SQLException {
		// TODO Auto-generated method stub
		return getDBConn().query(sql, params);
	}
	public <T> T transaction(){
		initDBConnMgr();
		return dbConnMgr.getTransaction(this, getDataSource());
	}
	public DBConnMgr getDbConnMgr() {
		return dbConnMgr;
	}
}
