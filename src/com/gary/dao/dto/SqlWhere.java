package com.gary.dao.dto;

public class SqlWhere {
	public static final String AND = " and ";
	public static final String OR = " or ";
	private String field;
	private String compare;
	private String way;
	public SqlWhere(String field) {
		this.compare = SqlFieldBean.EQUAL;
		this.field = field;
		this.way = AND;
	}
	public SqlWhere(String field,String compare) {
		this.compare = compare;
		this.field = field;
		this.way = AND;
	}
	public SqlWhere(String field,String compare, String way) {
		this.compare = compare;
		this.field = field;
		this.way = way;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getCompare() {
		return compare;
	}
	public void setCompare(String compare) {
		this.compare = compare;
	}
	public String getWay() {
		return way;
	}
	public void setWay(String way) {
		this.way = way;
	}
}
