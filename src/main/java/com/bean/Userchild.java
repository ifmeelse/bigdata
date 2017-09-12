package com.bean;

public class Userchild{
private long id;
private String childlogin_name;
private Long childlogin_id;
private String fatherlogin_name;
private long traffic;

public void trafficAdd(){
	traffic++;
}

public long getId() {
	return id;
}
public void setId(long id) {
	this.id = id;
}
public long getTraffic() {
	return traffic;
}
public void setTraffic(long traffic) {
	this.traffic = traffic;
}
public Long getChildlogin_id() {
	return childlogin_id;
}
public void setChildlogin_id(Long childlogin_id) {
	this.childlogin_id = childlogin_id;
}
public String getChildlogin_name() {
	return childlogin_name;
}
public void setChildlogin_name(String childlogin_name) {
	this.childlogin_name = childlogin_name;
}
public String getFatherlogin_name() {
	return fatherlogin_name;
}
public void setFatherlogin_name(String fatherlogin_name) {
	this.fatherlogin_name = fatherlogin_name;
}
}
