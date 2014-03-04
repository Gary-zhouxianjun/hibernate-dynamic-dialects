package com.gary.dao.jdbc;

import java.sql.*;
import java.util.*;

public class JdbcSqlServer {
	private Connection conn;

	private PreparedStatement ps;

	private String ip;
	
	private String name;
	
	private String password;
	
	private int port;
	private String db;
	ResultSet rs;
	public JdbcSqlServer(String ip, String name,String password,int port, String db) {
		this.db = db;
		this.ip = ip;
		this.name = name;
		this.port = port;
		this.password = password;
	}
	private void getConnection() throws SQLException {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			conn = DriverManager.getConnection("jdbc:sqlserver://"+ip+":"+port+";user="+name+";password="+password+";database="+db);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		/*try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			conn = DriverManager.getConnection("jdbc:sqlserver://125.208.9.167;user=sa;password=v1kcmcc1mm;database=TicketDB");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}*/
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
}
