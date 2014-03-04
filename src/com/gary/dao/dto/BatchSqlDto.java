package com.gary.dao.dto;

public class BatchSqlDto {
	private String sql;
	private Object[] params;

	public BatchSqlDto(String sql, Object[] params) {
		this.params = params;
		this.sql = sql;
	}
	
	public BatchSqlDto() {
		// TODO Auto-generated constructor stub
	}
	
	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public Object[] getParams() {
		return params;
	}

	public void setParams(Object[] params) {
		this.params = params;
	}

	public String getParamsSql() {
		if (params != null && params.length > 0)
			for (Object param : params) {
				String val = "";
				if(param == null){
					val = "null";
				}else if(param instanceof String){
					val = "'" + param + "'";
				}else{
					val = param.toString();
				}
				sql = sql.replaceFirst("\\?", val);
			}
		return sql;
	}
}
