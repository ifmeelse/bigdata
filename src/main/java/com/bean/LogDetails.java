package com.bean;

import java.util.HashMap;

/**
 * @date 2013年9月4日
 * @author lvjq 
 * 数据库对象实体类
 */
public class LogDetails {
	private String date;
	private Integer type;
	private String name;
	private Long count = 0l;
	private Long continueCount = 0l; //从指定URL跳转的请求个数
	private String rankKeyword;
	private Long searchCount = 0l;
	private HashMap<String, Integer> keywordMap;
	public LogDetails(){
		this.keywordMap=new HashMap<String, Integer>();
	}
	public HashMap<String, Integer> getKeywordMap() {
		return keywordMap;
	}
	public void setKeywordMap(HashMap<String, Integer> keywordMap) {
		this.keywordMap = keywordMap;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getCount() {
		return count;
	}
	public void setCount(Long count) {
		this.count = count;
	}
	public Long getContinueCount() {
		return continueCount;
	}
	public void setContinueCount(Long continueCount) {
		this.continueCount = continueCount;
	}
	public String getRankKeyword() {
		return rankKeyword;
	}
	public void setRankKeyword(String rankKeyword) {
		this.rankKeyword = rankKeyword;
	}
	public Long getSearchCount() {
		return searchCount;
	}
	public void setSearchCount(Long searchCount) {
		this.searchCount = searchCount;
	}
	public void addCount(){
		this.count++;
	}
	public void addContinueCount(){
		this.continueCount++;
	}
	public void addSearchCount(){
		this.searchCount++;
	}
}
