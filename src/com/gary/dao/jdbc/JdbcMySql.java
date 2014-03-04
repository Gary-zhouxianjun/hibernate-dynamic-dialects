package com.gary.dao.jdbc;

import java.sql.*;
import java.util.*;

public class JdbcMySql {
	private Connection conn;

	private PreparedStatement ps;

	private String ip;
	
	private String name;
	
	private String password;
	
	private String code;
	
	private int port;
	private String db;
	ResultSet rs;
	public JdbcMySql(String ip, String name,String password,int port, String db,String code) {
		this.db = db;
		this.ip = ip;
		this.name = name;
		this.port = port;
		this.password = password;
		this.code = code;
		if(this.port == 0)
			this.port = 3306;
		if(this.code == null)
			this.code = "UTF-8";
	}
	public JdbcMySql(String ip, String name,String password, String db,String code) {
		this.db = db;
		this.ip = ip;
		this.name = name;
		this.port = 3306;
		this.password = password;
		this.code = code;
		if(this.code == null)
			this.code = "UTF-8";
	}
	public JdbcMySql(String ip, String name,String password, String db) {
		this.db = db;
		this.ip = ip;
		this.name = name;
		this.port = 3306;
		this.password = password;
		this.code = "UTF-8";
	}
	private void getConnection() throws SQLException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://"+ip+":"+port+"/"+db+"?autoReconnect=true&useUnicode=true&characterEncoding="+code+"&user="+name+"&password="+password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public int execuetUpdate(String sql, List<?> list) throws Exception {
		getConnection();
		int n = 0;
		ps = conn.prepareStatement(sql);
		for (int i = 0; list != null && i < list.size(); i++) {
			ps.setObject(i + 1, list.get(i));
		}
		n = ps.executeUpdate();
		return n;
	}
	public ResultSet execuetQuery(String sql, List<?> list) throws Exception {
		getConnection();
		ps = conn.prepareStatement(sql);
		for (int i = 0; list != null && i < list.size(); i++) {
			ps.setObject(i + 1, list.get(i));
		}
		rs = ps.executeQuery();
		return rs;
	}
	public void closedAll() throws SQLException {

		if (rs != null) {
			rs.close();
		}
		if (ps != null) {
			ps.close();
		}
		if (conn != null) {
			conn.close();
		}

	}
	public static String limit(String sql,int page,int h){
		return (sql.endsWith(";") ? sql.substring(0, sql.length() - 1) : sql) + " limit " + (page != -1 ? (page-1)*h + "," : "") + h + ";";
	}
	public static String count(String sql){
		StringBuffer sb = new StringBuffer(sql);
		sb.insert(sb.indexOf("select")+7, "count(*) ");
		sb.delete(sb.indexOf("count(*)")+9, sb.indexOf("from"));
		return sb.toString();
	}
}
