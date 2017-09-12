package com.bean;

import java.io.Serializable;

/**
 * @date 2013年8月21日
 * @author lvjq 日志中每一行的日志对象 对应web工程中的LoggerFilter
 */
public class LogMap  implements Serializable{
	private String time;// 登陆用户的id
	private String fromIp;// 访问者ip
	private String country;// 访问者国家
	private String province;// 访问者省份
	private String city;// 访问者城市
	private String area;// 访问者区域
	private String company;// 访问者ip所属公司
	private String accessUrl;// 请求的url地址
	private String fromUrl;// 从哪里跳转的url地址
	private String accessType;//user-agent信息
	private String buildOriginalURL;// 请求的url地址（带参数）
	private String uniqueCookie; //用户标识cookie；
	private String userId;
	private String mobile;
	
	
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getUniqueCookie() {
		return uniqueCookie;
	}

	public void setUniqueCookie(String uniqueCookie) {
		this.uniqueCookie = uniqueCookie;
	}


	public String getFromIp() {
		return fromIp;
	}

	public void setFromIp(String fromIp) {
		this.fromIp = fromIp;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getAccessUrl() {
		return accessUrl;
	}

	public void setAccessUrl(String accessUrl) {
		this.accessUrl = accessUrl;
	}

	public String getFromUrl() {
		return fromUrl;
	}

	public void setFromUrl(String fromUrl) {
		this.fromUrl = fromUrl;
	}

	public String getAccessType() {
		return accessType;
	}

	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}

	public String getBuildOriginalURL() {
		return buildOriginalURL;
	}

	public void setBuildOriginalURL(String buildOriginalURL) {
		this.buildOriginalURL = buildOriginalURL;
	}
}
