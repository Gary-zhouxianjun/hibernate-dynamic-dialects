package com.gary.dao.dto;

/**
 * @author Gary 配置数据库文件Bean类
 */
public class DBConfigDto {
	private String type = ""; // 数据库类型
	private String name = ""; // 连接池名字
	private String driver = ""; // 数据库驱动
	private String url = ""; // 数据库url
	private String username = ""; // 用户名
	private String password = ""; // 密码
	private int maxsize = 0; // 最大缓存
	private int maxconn = 0; // 最大连接数

	/** 
  * 
  */
	public DBConfigDto() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
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
	 * @return the maxconn
	 */
	public int getMaxconn() {
		return maxconn;
	}

	/**
	 * @param maxconn
	 *            the maxconn to set
	 */
	public void setMaxconn(int maxconn) {
		this.maxconn = maxconn;
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
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
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
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Type: ").append(this.getType()).append("\n").append("Name: ").append(this.getName())
		.append("\n").append("Driver: ").append(this.getDriver()).append("\n").append("MaxConn: ")
		.append(this.getMaxconn()).append("\n").append("MaxSize: ").append(this.getMaxsize())
		.append("\n").append("Url: ").append(this.getUrl());
		return sb.toString();
	}

	public int getMaxsize() {
		return maxsize;
	}

	public void setMaxsize(int maxsize) {
		this.maxsize = maxsize;
	}
}
