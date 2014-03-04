package com.gary.dao.result;

import java.util.List;


public class Page<T> {
	//当前页
	private int pageNum;
	
	private int pageSize;
	//总记录数
	private int count;
	
	private List<T> items;
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public List<T> getItems() {
		return items;
	}
	public void setItems(List<T> items) {
		this.items = items;
	}
	public int getTotalPage() {
		int totalPage = count / pageSize;
		if (totalPage == 0 || count % pageSize != 0) {
			totalPage++;
		}
		return totalPage;
	}
	public int getPageNum() {
		return pageNum;
	}
	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
}
