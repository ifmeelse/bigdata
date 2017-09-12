package com.util;

import java.rmi.activation.ActivationException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.zip.DataFormatException;

/**
 * @date 2013年8月26日
 * @author lvjq 时间工具类
 */
public class DateUtil {
	
	private static final SimpleDateFormat formater = new SimpleDateFormat(
			"yyyy-MM-dd");
	/**
	 * 获取今天开始时间
	 * 
	 * @return
	 */
	public static Date getTodayStartTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Date start = calendar.getTime();
		return start;
	}

	/**
	 * @date 2013年8月26日 获取当前日期的前/后num天,
	 */
	public static String getDayOffset(int num) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, num);
		Date date = c.getTime();
		java.sql.Date date1 = new java.sql.Date(date.getTime());
		return dateToStr(date1, "yyyy-MM-dd");
	}
	
	/**
	 * @date 2015年12月17日 获取当前日期的前/后num天,
	 */
	public static Date getDateOffset(int num) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, num);
		Date date = c.getTime();
		java.sql.Date date1 = new java.sql.Date(date.getTime());
		return date1;
	}
	public static Date getSpecifiedDayBefore(int day) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, day);
		Date date = c.getTime();
		java.sql.Date date1 = new java.sql.Date(date.getTime());
		return date1;
		
	}
	/**
	 * @date 2013年8月26日 获取当前日期的前一天
	 */
	public static Date getSpecifiedDayBefore() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -1);
		Date date = c.getTime();
		java.sql.Date date1 = new java.sql.Date(date.getTime());
		return date1;
	}
	public static Date DateAdd(Date date, int datediff) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, datediff);
		Date times = calendar.getTime();
		return times;
	}
	/**
	 * @return 获取前一天日期(util类型)
	 */
	public static Date getBeforeDay() {
		Calendar date = Calendar.getInstance();
		date.set(Calendar.HOUR_OF_DAY, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.add(Calendar.DAY_OF_MONTH, -1);
		return new Date(date.getTimeInMillis());
	}
	public static Date getBeforeDay(int day) {
		Calendar date = Calendar.getInstance();
		date.set(Calendar.HOUR_OF_DAY, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.add(Calendar.DAY_OF_MONTH, day);
		return new Date(date.getTimeInMillis());
	}
	/**
	 * 获取当前日期前几天
	 * @return
	 */
	public static String getDayBefore(String datet,int day) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟  
		Calendar now = null;
		try {
			java.util.Date date=sdf.parse(datet);
			 now = Calendar.getInstance();      
			 now.setTime(date);      
			 now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}      
		
		String da=sdf.format(now.getTime());
		  return da;
		
	}
	
	/**
	 * 2016年5月30日
	 * lvjianqing
	 * 获取几分钟之前
	 */
	public static Date getMinutesBefore(int minute) {
		Calendar now = null;
		try {
			 Date date=new Date();
			 now = Calendar.getInstance();      
			 now.setTime(date);      
			 now.set(Calendar.MINUTE, now.get(Calendar.MINUTE) - minute);
		} catch (Exception e) {
			e.printStackTrace();
		}      
		return now.getTime();
	}

	/**
	 * 2016年5月30日
	 * lvjianqing
	 */
	public static Date getDayBefore(int day) {
		Calendar now = null;
		try {
			Date date=new Date();
			now = Calendar.getInstance();      
			now.setTime(date);      
			now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
		} catch (Exception e) {
			e.printStackTrace();
		}      
		return now.getTime();
	}
	/**
	 * 获取当前日期后几天
	 * @return
	 */
	public static String getDayAfter(String datet,int day) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟  
		Calendar now = null;
		try {
			java.util.Date date=sdf.parse(datet);
			 now = Calendar.getInstance();      
			 now.setTime(date);      
			 now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}      
		
		String da=sdf.format(now.getTime());
		  return da;
		
	}
	public static Timestamp getNowTime(){
		long now = System.currentTimeMillis();
		Timestamp time = new Timestamp(now);
		return time;
	}
	
	public static java.sql.Date getYestodayStartTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Date start = calendar.getTime();
		java.sql.Date res = new java.sql.Date(start.getTime()-1000*60*60*24);
		return res;
	}
	
	/**
	 * Date->String
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String dateToStr(Date date, String pattern) {
		SimpleDateFormat formater2 = new SimpleDateFormat(pattern);
		if (date == null) {
			return "";
		}
		return formater2.format(date);
	}
	public static String dateToStr(Date date) {
		SimpleDateFormat formater2=new SimpleDateFormat("yyyy-MM-dd");
		if (date == null) {
			return "";
		}
		return formater2.format(date);
	}
	/**
	 * 2015年10月21日
	 * lvjianqing
	 * 计算时间差（分钟）
	 */
	public static int getDiffMinute(Date d1,String time2,String format){
		int difftime;
		try {
			//时间差
			SimpleDateFormat simpleDate = new SimpleDateFormat(format);
//			Date d1 = null;
			Date d2 = null;
//			d1 = simpleDate.parse(time1);
			d2 = simpleDate.parse(time2);
			//毫秒ms
			long diff = d1.getTime() - d2.getTime();
			long diffHours = diff / 1000 / 60/60/24;
			difftime = new Long(diffHours).intValue();
			return difftime;
		} catch (Exception e) {
			System.out.println(time2);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return 0;
	}
	
	// 向后推时间(单位月)
		public static String getStringDiffDate(String formart,String date, int num) {
			String reStr = "";
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(formart);
				String str = date;
				Date dt = sdf.parse(str);
				Calendar rightNow = Calendar.getInstance();
				rightNow.setTime(dt);
				rightNow.add(Calendar.YEAR, 0);// 日期减1年
				rightNow.add(Calendar.MONTH, num);// 日期加3个月
				rightNow.add(Calendar.DAY_OF_YEAR, 0);// 日期加10天
				Date dt1 = rightNow.getTime();
				reStr = sdf.format(dt1);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return reStr;
		}
		/**
		 * 比较两个日期之间的大小
		 * 
		 * @param d1
		 * @param d2
		 * @return 前者大于后者返回true 反之false
		 * @throws ParseException
		 */
		public static boolean compareDate(String t1, String t2) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");// 小写的mm表示的是分钟
				Date d1 = null;
				Date d2 = null;
			
					d1 = sdf.parse(t1);
					d2 = sdf.parse(t2);
				
				Calendar c1 = Calendar.getInstance();
				Calendar c2 = Calendar.getInstance();
				c1.setTime(d1);
				c2.setTime(d2);

				int result = c1.compareTo(c2);
				if (result >= 0)
					return true;
				else
					return false;
			} catch (Exception e) {
				return false;
			}
		}

		public static boolean compareDate(Date t1, Date t2) {
			try {
				Calendar c1 = Calendar.getInstance();
				Calendar c2 = Calendar.getInstance();
				c1.setTime(t1);
				c2.setTime(t2);

				int result = c1.compareTo(c2);
				if (result >= 0)
					return true;
				else
					return false;
			} catch (Exception e) {
				return false;
			}
		}
		/**
		 * @author geyang 如何获得上周星期六的日期
		 * @return
		 */
		public static Date getLastWeekSunday(int week){

		    Calendar date=Calendar.getInstance(Locale.CHINA);

//		    date.setFirstDayOfWeek(Calendar.SATURDAY);//将每周第一天设为星期一，默认是星期天

		    date.add(Calendar.WEEK_OF_MONTH,week);//周数减一，即上周

		    date.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);//日子设为星期天

		    return date.getTime();

		}
     /**
      * 获取本月第一天
       * getMonthFirstDay
       * TODO
       * @Title: getMonthFirstDay
       * @Description: TODO
       * @param @return    
       * @return String    
       * @throws
      */
	public static String getMonthFirstDay() {
		 SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
		Calendar cal_1 = Calendar.getInstance();// 获取当前日期
		cal_1.add(Calendar.MONTH, -1);
		cal_1.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
		String firstDay = format.format(cal_1.getTime());
		return firstDay;
	}
	
	/**
	 * 
	 * @param formart
	 * @return
	 */
	public static String getStringDate(String formart) {
		SimpleDateFormat df = new SimpleDateFormat(formart);// 日期格式
		String dateFormart = df.format(new Date());
		return dateFormart;
	}
	
	
	
	/**
	 * convert date to string,with default pattern
	 * 
	 * @param date
	 * @return
	 */
	public static String date2Str(Date date) {
		if (date != null) {
			return formater.format(date);
		} else {
			return "";
		}

	}
	
	/**
	 * 获取当前年份
	 * 
	 * @return
	 */
	public static int getYear() {

		SimpleDateFormat df = new SimpleDateFormat("yyyy");// 日期格式
		int year = Integer.parseInt(df.format(new Date()));

		return year;

	}
	
	public static boolean getDiffTime(Date date, Date end_date) {

		if (date.getTime() + 10 * 1000 * 60 > end_date.getTime()) {
			return false;
		}
		return true;
	}
	
	/**
	 * convert date to string,with custom pattern,throws
	 * IllegalArgumentException
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static String date3Str(Date date, String format) {
		SimpleDateFormat formater = new SimpleDateFormat(format);
		if (date != null) {
			return formater.format(date);
		} else {
			return "";
		}
	}
	
	/**
	 * convert string to date ,with custom pattern,throws
	 * IllegalArgumentException
	 * 
	 * @param str
	 * @param format
	 * @return
	 */
	public static Date str2Date(String str, String format) {
		SimpleDateFormat formater = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = formater.parse(str);
		} catch (ParseException e) {
			//
		}
		return date;
	}
	
	public static long getDistanceDays(Date one, Date two) throws Exception {
		long days = 0;
		long time1 = one.getTime();
		long time2 = two.getTime();
		long diff;
		if (time1 < time2) {
			diff = time2 - time1;
		} else {
			diff = time1 - time2;
		}
		days = diff / (1000 * 60 * 60 * 24);
		return days;
	}
	
	/**
	 * 比较两个时间相差多少天
	 * @param nowdate
	 * @param inputdate
	 * @return
	 * @throws DataFormatException
	 * @throws ActivationException
	 */
	public static long DateDiff(Date nowdate, Date inputdate){
		long nowTime;
		long diffTime;
		long days = 0;
		if(nowdate == null || inputdate == null){
			return days;
		}
		try {
			nowTime = (nowdate.getTime() / 1000);
			diffTime = (inputdate.getTime() / 1000);
			if (nowTime > diffTime) {
				days = (nowTime - diffTime) / (1 * 60 * 60 * 24);
			} else {
				days = (diffTime - nowTime) / (1 * 60 * 60 * 24);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return days;

	}
	public static void main(String[] args) {
		System.out.println(DateUtil.date2Str(DateUtil.getBeforeDay(-1)));
	}
}
