// **********************************************************************
//
// Copyright (c) 2003-2006 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

// Ice version 3.1.0

package com.common;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.apache.hadoop.mapred.lib.db.DBWritable;

public final class DDBUser implements  DBWritable 
{
    public long id;

    public int star;

    public int credit;

    public int flag;
    
    public int userType;

    public int otherType;

    public String loginName;

    public String company;

//    public String password;

    public String problem;

    public String answer;

    public String username;

//    public String photo;

//    public String signature;

//    public String memo;

    public int gender;

    public long birthday;

    public String areaCode;

    public String address;

    public String mapurl;

    public String longitude;

    public String latitude;

    public String post;

    public String mobile;

    public String phone;

    public String email;

    public String fax;

    public String website;

    public long managerId;

    public String regIp;

    public int clickcount;

//    public String message;

    public String regDate;

    public String modifiedTime;

//    public String shotIntroduction;
    
    
  


    
    

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getStar() {
		return star;
	}

	public void setStar(int star) {
		this.star = star;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}
	/**
	 * 0:个人
	 * 1：商家
	 * 4：轿车中介
	 * 5：货车中介
	 * 6：营运车
	 * 7：抵押车
	 * 8：网站
	 * @return
	 */
	public int getUserType() {
		return userType;
	}

	public void setUserType(int userType) {
		this.userType = userType;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}


	/**
	 * 密码提问
	 * @return
	 */
	public String getProblem() {
		return problem;
	}

	public void setProblem(String problem) {
		this.problem = problem;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	



	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public Date getBirthday() {
		return new Date(birthday);
	}
	
	public void setBirthday(Date birthday) {
		this.birthday = birthday.getTime();
	}
	
	public void setBirthday(long birthday) {
		this.birthday = birthday;
	}
	
	
	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getMapurl() {
		return mapurl;
	}

	public void setMapurl(String mapurl) {
		this.mapurl = mapurl;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getPost() {
		if(post!=null&&post.length()>15){
			return post.substring(0,15);
		}
		return post;
	}

	public void setPost(String post) {
		if(post!=null&&post.length()>15){
			this.post = post.substring(0,15);
		}else{
			this.post = post;
		}
		
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getRegIp() {
		if(regIp!=null&&regIp.length()>15){
			return regIp.substring(0,15);
		}
		return regIp;
	}

	public void setRegIp(String regIp) {
		if(regIp!=null&&regIp.length()>15){
			this.regIp = regIp.substring(0,15);
		}else{
			this.regIp = regIp;
		}
	}

	

	
	
	
    public String getRegDate() {
		return regDate;
	}

	public void setRegDate(String regDate) {
		this.regDate = regDate;
	}

	public String getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(String modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

	public int getClickcount() {
		return clickcount;
	}

	public void setClickcount(int clickcount) {
		this.clickcount = clickcount;
	}
    
    public int getCredit() {
		return credit;
	}

	public void setCredit(int credit) {
		this.credit = credit;
	}
	
    public int getOtherType() {
		return otherType;
	}

	public void setOtherType(int otherType) {
		this.otherType = otherType;
	}
	
    public long getManagerId() {
		return managerId;
	}

	public void setManagerId(long managerId) {
		this.managerId = managerId;
	}
    
    
   



	
	/**
	 * TODO 简单描述该方法的实现功能（可选）.
	 * @see org.apache.hadoop.mapreduce.lib.db.DBWritable#readFields(java.sql.ResultSet)
	 */
	@Override
	public void readFields(ResultSet resultSet) throws SQLException {
		int index = 1;
		this.id = resultSet.getLong(index++);
		this.star = resultSet.getInt(index++);
		this.credit = resultSet.getInt(index++);
		this.flag = resultSet.getInt(index++);
		this.userType = resultSet.getInt(index++);
		this.otherType = resultSet.getInt(index++);
		this.loginName = resultSet.getString(index++);
		this.company = resultSet.getString(index++);
		this.problem = resultSet.getString(index++);
		this.answer = resultSet.getString(index++);
		this.username = resultSet.getString(index++);
		this.gender = resultSet.getInt(index++);
		this.birthday = resultSet.getLong(index++);
		this.areaCode = resultSet.getString(index++);
		this.address = resultSet.getString(index++);
		this.mapurl = resultSet.getString(index++);
		this.longitude = resultSet.getString(index++);
		this.latitude = resultSet.getString(index++);
		this.post = resultSet.getString(index++);
		this.mobile = resultSet.getString(index++);
		this.phone = resultSet.getString(index++);
		this.email = resultSet.getString(index++);
		this.fax = resultSet.getString(index++);
		this.website = resultSet.getString(index++);
		this.managerId = resultSet.getLong(index++);
		this.regIp = resultSet.getString(index++);
		this.clickcount = resultSet.getInt(index++);
		this.regDate = resultSet.getString(index++);
		this.modifiedTime = resultSet.getString(index++);
		
	}

	/**
	 * TODO 简单描述该方法的实现功能（可选）.
	 * @see org.apache.hadoop.mapreduce.lib.db.DBWritable#write(java.sql.PreparedStatement)
	 */
	@Override
	public void write(PreparedStatement preparedStatement) throws SQLException {
		int index = 1;
		preparedStatement.setLong(index++, this.id);
		preparedStatement.setInt(index++, this.star);
		preparedStatement.setInt(index++, this.credit);
		preparedStatement.setInt(index++, this.flag);
		preparedStatement.setLong(index++, this.userType);
		preparedStatement.setInt(index++, this.otherType);
		preparedStatement.setString(index++, this.loginName);
		preparedStatement.setString(index++, this.company);
		preparedStatement.setString(index++, this.answer);
		preparedStatement.setString(index++, this.problem);
		preparedStatement.setString(index++, this.username);
		preparedStatement.setInt(index++, this.gender);
		preparedStatement.setLong(index++, this.birthday);
		preparedStatement.setString(index++, this.areaCode);
		preparedStatement.setString(index++, this.address);
		preparedStatement.setString(index++, this.mapurl);
		preparedStatement.setString(index++, this.longitude);
		preparedStatement.setString(index++, this.latitude);
		preparedStatement.setString(index++, this.post);
		preparedStatement.setString(index++, this.mobile);
		preparedStatement.setString(index++, this.phone);
		preparedStatement.setString(index++, this.email);
		preparedStatement.setString(index++, this.fax);
		preparedStatement.setString(index++, this.website);
		preparedStatement.setLong(index++, this.managerId);
		preparedStatement.setString(index++, this.regIp);
		preparedStatement.setInt(index++, this.clickcount);
		preparedStatement.setString(index++, this.regDate);
		preparedStatement.setString(index++, this.modifiedTime);
	}

	/**
	 * TODO 简单描述该方法的实现功能（可选）.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "" + id + "|" + star + "|" + credit + "|" + flag + "|"
				+ userType + "|" + otherType + "|" + loginName + "|" + company
				+ "|" + problem + "|" + answer + "|" + username + "|" + gender
				+ "|" + birthday + "|" + areaCode + "|" + address + "|" + mapurl
				+ "|" + longitude + "|" + latitude + "|" + post + "|" + mobile
				+ "|" + phone + "|" + email + "|" + fax + "|" + website + "|"
				+ managerId + "|" + regIp + "|" + clickcount + "|" + regDate
				+ "|" + modifiedTime;
	}




	
}
