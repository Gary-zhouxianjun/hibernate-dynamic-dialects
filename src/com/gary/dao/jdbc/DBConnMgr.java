package com.gary.dao.jdbc;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.gary.dao.dto.DBConfigDto;

/**
 * @author Gary 数据库连接池管理类
 */
public class DBConnMgr {
	private Logger log = Logger.getLogger(DBConnMgr.class);
	private static DBConnMgr instance;// 唯一数据库连接池管理实例类
	private Vector<DBConfigDto> drivers = new Vector<DBConfigDto>();// 驱动信息
	private Hashtable<String, DBConnPool> pools = new Hashtable<String, DBConnPool>();// 连接池
	private static String path;
	private JdbcTransactionProxy tp = new JdbcTransactionProxy();
	
	@SuppressWarnings("unchecked")
	public <T> T getTransaction(Object dao){
		return (T) tp.bind(dao);
	}
	@SuppressWarnings("unchecked")
	public <T> T getTransaction(Object dao, String dataSource){
		return (T) tp.bind(dao, dataSource);
	}
	/**
	 * 实例化管理类
	 */
	private DBConnMgr() {
		// TODO Auto-generated constructor stub
		this.init();
	}

	/**
	 * 得到唯一实例管理类
	 * 
	 * @return
	 */
	static synchronized public DBConnMgr getInstance(String path) {
		if (instance == null) {
			DBConnMgr.path = path;
			instance = new DBConnMgr();
		}
		return instance;

	}
	static synchronized public DBConnMgr getInstance() {
		if (instance == null) {
			instance = new DBConnMgr();
		}
		return instance;

	}

	/**
	 * 释放连接
	 * 
	 * @param name
	 * @param con
	 */
	public void freeConnection(String name, DBConn con) {
		DBConnPool pool = (DBConnPool) pools.get(name);// 根据关键名字得到连接池
		if (pool != null)
			pool.freeConnection(con);// 释放连接
	}

	/**
	 * 得到一个连接根据连接池的名字name
	 * 
	 * @param name
	 * @return
	 */
	public DBConn getConnection(String name) {
		DBConnPool pool = null;
		DBConn con = null;
		pool = (DBConnPool) pools.get(name);// 从名字中获取连接池
		if(pool != null)
			con = pool.getConnection();// 从选定的连接池中获得连接
		else
			log.error("获取连接池失败!没有["+name+"]此连接!");
		return con;
	}

	/**
	 * 得到一个连接，根据连接池的名字和等待时间
	 * 
	 * @param name
	 * @param time
	 * @return
	 */
	public DBConn getConnection(String name, long timeout) {
		DBConnPool pool = null;
		DBConn con = null;
		pool = (DBConnPool) pools.get(name);// 从名字中获取连接池
		con = pool.getConnection(timeout);// 从选定的连接池中获得连接
		return con;
	}

	/**
	 * 释放所有连接
	 */
	public synchronized void release() {
		Enumeration<?> allpools = pools.elements();
		while (allpools.hasMoreElements()) {
			DBConnPool pool = (DBConnPool) allpools.nextElement();
			if (pool != null)
				pool.release();
		}
		pools.clear();
	}

	/**
	 * 创建连接池
	 * 
	 * @param props
	 */
	private void createPools(DBConfigDto dsb) {
		log.info("创建连接池:\n"+dsb.toString());
		DBConnPool dbpool = new DBConnPool();
		dbpool.setName(dsb.getName());
		dbpool.setDriver(dsb.getDriver());
		dbpool.setUrl(dsb.getUrl());
		dbpool.setUser(dsb.getUsername());
		dbpool.setPassword(dsb.getPassword());
		dbpool.setMaxConn(dsb.getMaxconn());
		dbpool.setMaxSize(dsb.getMaxsize());
		DBConn con = dbpool.getConnection();
		if(con != null){
			pools.put(dsb.getName(), dbpool);
			dbpool.freeConnection(con);
		}else{
			log.error("["+dsb.getName()+"]连接池创建失败!");
		}
	}

	/**
	 * 初始化连接池的参数
	 */
	@SuppressWarnings("rawtypes")
	private void init() {
		// 加载驱动程序
		this.loadDrivers();
		// 创建连接池
		Iterator alldriver = drivers.iterator();
		while (alldriver.hasNext()) {
			this.createPools((DBConfigDto) alldriver.next());
		}
		log.info("创建连接池完毕!");
	}

	/**
	 * 加载驱动程序
	 * 
	 * @param props
	 */
	private void loadDrivers() {
		// 读取数据库配置文件
		drivers = ParseDBConfig.readConfigInfo(DBConnMgr.path);
		log.info("加载驱动程序..." + DBConnMgr.path);
	}
	/**
	 * 加载一个驱动
	 * @param config
	 */
	public void addDrivers(DBConfigDto config){
		drivers.add(config);
	}
	/**
	 * 删除一个驱动
	 * @param name
	 */
	public void delDrivers(String name){
		for (DBConfigDto config : drivers) {
			if(config.getName().equals(name)){
				drivers.remove(config);
				return;
			}
		}
	}
}
