package com.gary.dao.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;

import org.apache.log4j.Logger;
/**
 * @author Gary 数据库连接池
 */
public class DBConnPool {
	private Logger log = Logger.getLogger(DBConnPool.class);
	private Connection con = null;
	private int inUsed = 0; // 使用的连接数
	private ArrayList<DBConn> freeConnections = new ArrayList<DBConn>();// 容器，空闲连接
	private int minConn; // 最小连接数
	private int maxConn; // 最大连接
	private int maxSize; // 最大缓存
	private String name; // 连接池名字
	private String password; // 密码
	private String url; // 数据库连接地址
	private String driver; // 驱动
	private String user; // 用户名
	private long timeout = 3000;
	public Timer timer; // 定时

	public DBConnPool() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 创建连接池
	 * 
	 * @param driver
	 * @param name
	 * @param URL
	 * @param user
	 * @param password
	 * @param maxConn
	 */
	public DBConnPool(String name, String driver, String URL, String user,
			String password, int maxConn, int maxSize) {
		this.name = name;
		this.driver = driver;
		this.url = URL;
		this.user = user;
		this.password = password;
		this.maxConn = maxConn;
		this.maxSize = maxSize;
	}

	/**
	 * 用完，释放连接
	 * 
	 * @param con
	 */
	public synchronized void freeConnection(DBConn con) {
		this.freeConnections.add(con);// 添加到空闲连接的末尾
		this.inUsed--;
		if(this.inUsed < 0)
			this.inUsed = 0;
		log.debug("释放连接["+this.name+"],已使用:"+inUsed+"个连接,空闲连接:"+freeConnections.size()+",最大连接:"+maxConn);
	}

	/**
	 * timeout 根据timeout得到连接
	 * 
	 * @param timeout
	 * @return
	 * @throws InterruptedException 
	 */
	public synchronized DBConn getConnection(long timeout){
		DBConn con = null;
		if (this.maxConn == 0 || this.maxConn <= this.inUsed) {
			con = waitConnection();// 达到最大连接数，暂时不能获得连接了。
		}else{
			if (this.freeConnections.size() > 0) {
				con = (DBConn) this.freeConnections.get(0);
				if (con == null)
					con = getConnection(timeout); // 继续获得连接
			} else {
				con = newConnection(); // 新建连接
			}
		}
		if (con != null) {
			this.inUsed++;
		}
		return con;
	}

	/**
	 * 
	 * 从连接池里得到连接
	 * 
	 * @return
	 * @throws InterruptedException 
	 */
	public synchronized DBConn getConnection() {
		DBConn con = null;
		if (this.maxConn == 0 || this.maxConn <= this.inUsed) {
			con = waitConnection();// 等待 超过最大连接时
		}else{
			if (this.freeConnections.size() > 0) {
				con = (DBConn) this.freeConnections.get(0);
				this.freeConnections.remove(0);// 如果连接分配出去了，就从空闲连接里删除
				if (con == null)
					con = getConnection(); // 继续获得连接
			} else {
				con = newConnection(); // 新建连接
			}
		}
		if (con != null) {
			this.inUsed++;
			log.debug("获取连接["+this.name+"],已使用:"+inUsed+"个连接,空闲连接:"+freeConnections.size()+",最大连接:"+maxConn);
		}
		return con;
	}

	private synchronized DBConn waitConnection(){
		try {
			log.debug("等待再次获取连接...");
			wait(timeout);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		DBConn con = null;
		if (this.freeConnections.size() > 0) {
			con = (DBConn) this.freeConnections.get(0);
			this.freeConnections.remove(0);// 如果连接分配出去了，就从空闲连接里删除
			if (con == null)
				con = getConnection(); // 继续获得连接
		} else {
			log.warn("没有可用的连接["+name+"],等待连接超时!");
		}
		return con;
	}
	
	/**
	 * 释放全部连接
	 * 
	 */
	public synchronized void release() {
		Iterator<DBConn> allConns = this.freeConnections.iterator();
		while (allConns.hasNext()) {
			DBConn con = (DBConn) allConns.next();
			try {
				con.getConnection().close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		this.freeConnections.clear();

	}

	/**
	 * 创建新连接
	 * 
	 * @return
	 */
	private DBConn newConnection() {
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, password);
			return new DBConn(con, maxSize);
		} catch (ClassNotFoundException e) {
			log.error("sorry can't find db driver!",e);
		} catch (SQLException e1) {
			log.error("sorry can't create Connection!",e1);
		}
		return null;
	}

	/**
	 * 定时处理函数
	 */
	public synchronized void TimerEvent() {
		// 暂时还没有实现以后会加上的
	}

	/**
	 * @return the driver
	 */
	public String getDriver() {
		return driver;
	}

	/**
	 * @param driver
	 *            the driver to set
	 */
	public void setDriver(String driver) {
		this.driver = driver;
	}

	/**
	 * @return the maxConn
	 */
	public int getMaxConn() {
		return maxConn;
	}

	/**
	 * @param maxConn
	 *            the maxConn to set
	 */
	public void setMaxConn(int maxConn) {
		this.maxConn = maxConn;
	}

	/**
	 * @return the minConn
	 */
	public int getMinConn() {
		return minConn;
	}

	/**
	 * @param minConn
	 *            the minConn to set
	 */
	public void setMinConn(int minConn) {
		this.minConn = minConn;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

	public int getInUsed() {
		return inUsed;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
}