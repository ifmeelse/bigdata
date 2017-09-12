package com.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bean.LogMap;

public class LogUtil {
	static ArrayList<String> phoneList = new ArrayList<String>();
	static {
		phoneList.add("iPhone");
		phoneList.add("Android");
	}

	public static LogMap strToLog(String line) {
		try {
			if (line == null || "".equals(line)) {
				return null;
			}
			String[] lines = line.split("\\|");
			
//			if (lines.length == 12) {
//				logMap.setUserId(lines[0]);
//				logMap.setFromIp(lines[1]);
//				logMap.setCountry(lines[2]);
//				logMap.setProvince(lines[3]);
//				logMap.setCity(lines[4]);
//				logMap.setArea(lines[5]);
//				logMap.setCompany(lines[6]);
//				logMap.setAccessUrl(lines[7]);
//				logMap.setFromUrl(lines[8]);
//				logMap.setAccessType(lines[9]);
//				logMap.setBuildOriginalURL(lines[10]);
//				logMap.setUniqueCookie(lines[11]);
//
//			} else
				if (lines.length == 14) {
				LogMap logMap = new LogMap();
				logMap.setTime(lines[0].trim());
				logMap.setFromIp(lines[1]);
				logMap.setCountry(lines[2]);
				logMap.setProvince(lines[3]);
				logMap.setCity(lines[4]);
				logMap.setArea(lines[5]);
				logMap.setCompany(lines[6]);
				logMap.setAccessUrl(lines[7]);
				logMap.setFromUrl(lines[8]);
				logMap.setAccessType(lines[9]);
				logMap.setBuildOriginalURL(lines[10]);
				logMap.setUniqueCookie(lines[11]);
				logMap.setMobile(lines[12]);
				logMap.setUserId(lines[13]);
				return logMap;
			} else {
				return null;
			}
//			if (!StringUtil.isNumeric(logMap.getUniqueCookie())) {
//				return null;
//			}
		
		} catch (Exception e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 匹配手机访问
	 * 
	 * @param anegt
	 * @return 0:非手机访问 1：手机访问
	 */
	public static int getPhone(String anegt) {
		for (int i = 0; i < phoneList.size(); i++) {
			Matcher m = Pattern.compile(phoneList.get(i), Pattern.CASE_INSENSITIVE).matcher(anegt);
			if (m.find()) {
				return 1;
			}
		}
		return 0;
	}
}
