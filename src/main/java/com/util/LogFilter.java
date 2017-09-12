/**
 * Project Name:spark
 * File Name:LogFilter.java
 * Package Name:org.spark.util
 * Date:2016年12月16日下午2:45:55
 * Copyright (c) 2016, hx2car.com All Rights Reserved.
 *
*/
/**
 * Project Name:spark
 * File Name:LogFilter.java
 * Package Name:org.spark.util
 * Date:2016年12月16日下午2:45:55
 * Copyright (c) 2016, hx2car.com All Rights Reserved.
 *
 */

package com.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ClassName:LogFilter 
 * Function: TODO ADD FUNCTION 
 * Reason:	 TODO ADD REASON
 * Date:     2016年12月16日 下午2:45:55 
 * @author   geyang
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */

public class LogFilter {
	static ArrayList<String> botList = new ArrayList<String>();
	static {
		botList.add("spider");
		botList.add("bot");
		botList.add("ysearch");
		botList.add("Googlebot");
	}
	static ArrayList<String> botList2 = new ArrayList<String>();
	static {
		botList2.add("Googlebot");
		botList2.add("spider");
	}
	public static int getBot(String anegt) {
		for (int i = 0; i < botList.size(); i++) {
			Matcher m = Pattern.compile(botList.get(i),Pattern.CASE_INSENSITIVE).matcher(anegt);
			if (m.find()) {
				return 1;
			}
		}
		return 0;
	}
	public static int getBot2(String anegt) {
		for (int i = 0; i < botList2.size(); i++) {
			Matcher m = Pattern.compile(botList2.get(i),Pattern.CASE_INSENSITIVE).matcher(anegt);
			if (m.find()) {
				return 1;
			}
		}
		return 0;
	}
	public static void main(String[] args) {
		System.out.println(getBot("uuSpider"));
	}
}

