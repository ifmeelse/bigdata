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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.lib.db.DBWritable;

public final class DDBCar implements  DBWritable {
	public long id;

	public String orderedr;

	public String firstlist;

	public String xujia;

	public int flag;

	public String brandStr;

	public String typeStr;

	public String money;

	public int bigType;

	public int carSerial;

	public int carType;

	public int carKind;

	public String license;

	public String priceRegion;

	public String carSource;

	public String carForuse;

	public String seats;

	public String oilWear;

	public String transfer;

	public String mortgage;

	public String journey;

	public String carAuto;

	public String color;

//	public String memo;

	public String zhiliao;

	public String carLoginname;

	public long userId;

//	public String picUrl;

	public int clickCount;

	public String usedate;

	public String saleStatus;

	public String areaCode;

	public long newId;

	public String createIp;

	public String createTime;

	public String modifiedTime;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getOrderedr() {
		return orderedr;
	}

	public void setOrderedr(String orderedr) {
		this.orderedr = orderedr;
	}

	public String getFirstlist() {
		return firstlist;
	}

	public void setFirstlist(String firstlist) {
		this.firstlist = firstlist;
	}

	public String getXujia() {
		return xujia;
	}

	public void setXujia(String xujia) {
		this.xujia = xujia;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public String getBrandStr() {
		return brandStr;
	}

	public void setBrandStr(String brandStr) {
		this.brandStr = brandStr;
	}

	public String getTypeStr() {
		return typeStr;
	}

	public void setTypeStr(String typeStr) {
		this.typeStr = typeStr;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public int getBigType() {
		return bigType;
	}

	public void setBigType(int bigType) {
		this.bigType = bigType;
	}

	public int getCarSerial() {
		return carSerial;
	}

	public void setCarSerial(int carSerial) {
		this.carSerial = carSerial;
	}

	public int getCarType() {
		return carType;
	}

	public void setCarType(int carType) {
		this.carType = carType;
	}

	public int getCarKind() {
		return carKind;
	}

	public void setCarKind(int carKind) {
		this.carKind = carKind;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public String getPriceRegion() {
		return priceRegion;
	}

	public void setPriceRegion(String priceRegion) {
		this.priceRegion = priceRegion;
	}

	public String getCarSource() {
		return carSource;
	}

	public void setCarSource(String carSource) {
		this.carSource = carSource;
	}

	public String getCarForuse() {
		return carForuse;
	}

	public void setCarForuse(String carForuse) {
		this.carForuse = carForuse;
	}

	public String getSeats() {
		return seats;
	}

	public void setSeats(String seats) {
		this.seats = seats;
	}

	public String getOilWear() {
		return oilWear;
	}

	public void setOilWear(String oilWear) {
		this.oilWear = oilWear;
	}

	public String getTransfer() {
		return transfer;
	}

	public void setTransfer(String transfer) {
		this.transfer = transfer;
	}

	public String getMortgage() {
		return mortgage;
	}

	public void setMortgage(String mortgage) {
		this.mortgage = mortgage;
	}

	public String getJourney() {
		return journey;
	}

	public void setJourney(String journey) {
		this.journey = journey;
	}

	public String getCarAuto() {
		return carAuto;
	}

	public void setCarAuto(String carAuto) {
		this.carAuto = carAuto;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	

	public String getZhiliao() {
		return zhiliao;
	}

	public void setZhiliao(String zhiliao) {
		this.zhiliao = zhiliao;
	}

	public String getCarLoginname() {
		return carLoginname;
	}

	public void setCarLoginname(String carLoginname) {
		this.carLoginname = carLoginname;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	

	public int getClickCount() {
		return clickCount;
	}

	public void setClickCount(int clickCount) {
		this.clickCount = clickCount;
	}

	public String getUsedate() {
		return usedate;
	}

	public void setUsedate(String usedate) {
		this.usedate = usedate;
	}

	public String getSaleStatus() {
		return saleStatus;
	}

	public void setSaleStatus(String saleStatus) {
		this.saleStatus = saleStatus;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public long getNewId() {
		return newId;
	}

	public void setNewId(long newId) {
		this.newId = newId;
	}

	public String getCreateIp() {
		return createIp;
	}

	public void setCreateIp(String createIp) {
		this.createIp = createIp;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(String modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

	



	/**
	 * TODO 简单描述该方法的实现功能（可选）.
	 * 
	 * @see org.apache.hadoop.mapreduce.lib.db.DBWritable#readFields(java.sql.ResultSet)
	 */
	@Override
	public void readFields(ResultSet resultSet) throws SQLException {
		int index = 1;
		this.id = resultSet.getLong(index++);
		this.orderedr = resultSet.getString(index++);
		this.firstlist = resultSet.getString(index++);
		this.xujia = resultSet.getString(index++);
		this.flag = resultSet.getInt(index++);
		this.brandStr = resultSet.getString(index++);
		this.typeStr = resultSet.getString(index++);
		this.money = resultSet.getString(index++);
		this.bigType = resultSet.getInt(index++);
		this.carSerial = resultSet.getInt(index++);
		this.carType = resultSet.getInt(index++);
		this.carKind = resultSet.getInt(index++);
		this.license = resultSet.getString(index++);
		this.priceRegion = resultSet.getString(index++);
		this.carSource = resultSet.getString(index++);
		this.carForuse = resultSet.getString(index++);
		this.seats = resultSet.getString(index++);
		this.oilWear = resultSet.getString(index++);
		this.transfer = resultSet.getString(index++);
		this.mortgage = resultSet.getString(index++);
		this.journey = resultSet.getString(index++);
		this.carAuto = resultSet.getString(index++);
		this.color = resultSet.getString(index++);
		this.zhiliao = resultSet.getString(index++);
		this.carLoginname = resultSet.getString(index++);
		this.userId = resultSet.getLong(index++);
		this.clickCount = resultSet.getInt(index++);
		this.usedate = resultSet.getString(index++);
		this.saleStatus = resultSet.getString(index++);
		this.areaCode = resultSet.getString(index++);
		this.newId = resultSet.getLong(index++);
		this.createIp = resultSet.getString(index++);
		this.createTime = resultSet.getString(index++);
		this.modifiedTime = resultSet.getString(index++);

	}

	/**
	 * TODO 简单描述该方法的实现功能（可选）.
	 * 
	 * @see org.apache.hadoop.mapreduce.lib.db.DBWritable#write(java.sql.PreparedStatement)
	 */
	@Override
	public void write(PreparedStatement preparedStatement) throws SQLException {
		int index = 1;
		preparedStatement.setLong(index++, this.id);
		preparedStatement.setString(index++, this.orderedr);
		preparedStatement.setString(index++, this.firstlist);
		preparedStatement.setString(index++, this.xujia);
		preparedStatement.setLong(index++, this.flag);
		preparedStatement.setString(index++, this.brandStr);
		preparedStatement.setString(index++, this.typeStr);
		preparedStatement.setString(index++, this.money);
		preparedStatement.setLong(index++, this.bigType);
		preparedStatement.setInt(index++, this.carSerial);
		preparedStatement.setLong(index++, this.carType);
		preparedStatement.setInt(index++, this.carKind);
		preparedStatement.setString(index++, this.license);
		preparedStatement.setString(index++, this.priceRegion);
		preparedStatement.setString(index++, this.carSource);
		preparedStatement.setString(index++, this.carForuse);
		preparedStatement.setString(index++, this.seats);
		preparedStatement.setString(index++, this.oilWear);
		preparedStatement.setString(index++, this.transfer);
		preparedStatement.setString(index++, this.mortgage);
		preparedStatement.setString(index++, this.journey);
		preparedStatement.setString(index++, this.carAuto);
		preparedStatement.setString(index++, this.color);
		preparedStatement.setString(index++, this.zhiliao);
		preparedStatement.setString(index++, this.carLoginname);
		preparedStatement.setLong(index++, this.userId);
		preparedStatement.setLong(index++, this.clickCount);
		preparedStatement.setString(index++, this.usedate);
		preparedStatement.setString(index++, this.saleStatus);
		preparedStatement.setString(index++, this.areaCode);
		preparedStatement.setLong(index++, this.newId);
		preparedStatement.setString(index++, this.createIp);
		preparedStatement.setString(index++, this.createTime);
		preparedStatement.setString(index++, this.modifiedTime);
	}

	/**
	 * TODO 简单描述该方法的实现功能（可选）.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "" + id + "|" + orderedr + "|" + firstlist + "|" + xujia
				+ "|" + flag + "|" + brandStr + "|" + typeStr + "|" + money
				+ "|" + bigType + "|" + carSerial + "|" + carType + "|" + carKind
				+ "|" + license + "|" + priceRegion + "|" + carSource + "|"
				+ carForuse + "|" + seats + "|" + oilWear + "|" + transfer + "|"
				+ mortgage + "|" + journey + "|" + carAuto + "|" + color + "|"
				+ zhiliao + "|" + carLoginname + "|" + userId + "|" + clickCount
				+ "|" + usedate + "|" + saleStatus + "|" + areaCode + "|" + newId
				+ "|" + createIp + "|" + createTime + "|" + modifiedTime + "";
	}

	
}
