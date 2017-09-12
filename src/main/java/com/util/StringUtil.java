package com.util;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class StringUtil {
	private static Properties props = new Properties();

	static {
		try {
//			 String path =StringUtil.class.getClassLoader().getResource("").getPath();
			InputStream ips = Config.class.getClassLoader().getResourceAsStream("mapping.txt");
			props.load(ips);
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}
	/**
	 * 把控字符转换成空串
	 * @param str
	 * @return
	 */
	public String transnull(String str){
		if(str!=null){
			return str;
		}else{
			return "";
		}
	}
	
	/**
	 * 把控字符转换成空串
	 * @param str
	 * @return
	 */
	public static String blanknull(String str){
		if(str!=null){
			return str;
		}else{
			return "";
		}
	}
	
	/**
	 * 特殊字符反处理
	 * @param str
	 * @return
	 */
	public static String convertChars(String str){
		String ret = "";
		if(str!=null && !"".equals(str)){
			ret = str.replaceAll("&apos;", "'")
					.replaceAll("&quot;", "\"")
					.replaceAll("&lt;", "<")
					.replaceAll("&gt;", ">")
					.replaceAll("&amp;nbsp;", " ")
					.replaceAll("<br>", "\n");
			return ret;
		}else{
			return "";
		}
	}

	
	/**
	 * 字符串变成Long数组
	 * @param s 字符串
	 * @param regex 分隔符
	 * @return
	 */
	public static Long[] parseLongs(String s , String regex){
		String[] str = split(s,regex);
		String parse;
		Long[] strLongs = new Long[str.length]; 
		for (int i = 0; i < str.length ; i++){
			parse = str[i];
			if (null == parse || "".equals(parse)){
				strLongs[i] = 0l;
			}else{
				try{
					strLongs[i] = Long.parseLong(parse);
				}catch(Exception e){
					strLongs[i] = 0l;
				}
			}
		}
		return strLongs;
	}
	
	/**
	 * 字符串变成long数组
	 * @param s 字符串
	 * @param regex 分隔符
	 * @return
	 */
	public static long[] parselongs(String s , String regex){
		String[] str = split(s,regex);
		String parse;
		long[] strlongs = new long[str.length]; 
		for (int i = 0; i < str.length ; i++){
			parse = str[i];
			if (null == parse || "".equals(parse)){
				strlongs[i] = 0l;
			}else{
				try{
					strlongs[i] = Long.parseLong(parse);
				}catch(Exception e){
					strlongs[i] = 0l;
				}
			}
		}
		return strlongs;
	}
	
	
	/**
	 * 把字符串根据分隔符生成数组。
	 * 
	 * @param line
	 *            字符串
	 * @param delim
	 *            分隔符
	 * @return String[] 字符串数组
	 */
	public static String[] split(String line, String delim) {
		if (line == null) {
			return new String[0];
		}
		List list = new ArrayList();
		StringTokenizer t = new StringTokenizer(line, delim);

		while (t.hasMoreTokens()) {
			list.add(t.nextToken());
		}

		return (String[]) list.toArray(new String[list.size()]);
	}

	/**
	 * 转化日志的编码
	 * 
	 * @param logInfo
	 *            日志信息
	 * @return String 转化编码后的日志信息
	 */
	public static String transLog(String logInfo) {
		return StringUtil.transChiTo8859(logInfo);
	}

	/**
	 * 解决中文的乱码问题
	 * 
	 * @param chi
	 *            为输入的要汉化的字符串
	 * @return String 经过转换后的字符串
	 */
	public static String transChiTo8859(String chi) {

		if (StringUtil.isEmpty(chi))
			return "";

		String result = null;
		byte temp[];
		try {
			temp = chi.getBytes("GBK");
			result = new String(temp, "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
		}

		return result;

	}

	public static String parseEnter(String html) {
		String reg = "[\r\n]";
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(html);
		String s = m.replaceAll("\3\2\1");
		return s;
	}

	public static String parseHtml(String html) {
		if (html != null) {
			return html.replaceAll("\r", "<br>");
		}
		return "";
	}

	/**
	 * 取得某个数值是否被选择中,值=1? 支持最多10位
	 * 
	 * @author huajun.luohj
	 * @param num
	 *            数字
	 * @param idx
	 *            位置
	 * @return
	 */
	public static boolean getBinIsOne(int num, int idx) {
		String str = "0000000000" + Integer.toBinaryString(num);
		str = str.substring(str.length() - 10);
		if (str.substring(10 - idx, 11 - idx).equalsIgnoreCase("1")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断多选的是否选中
	 * 
	 * @param str
	 * @param i
	 * @return
	 */
	public static boolean checkSelected(String str, int i) {
		if (str == null) {
			return false;
		}
		try {
			String[] splits = split(str, ",");
			for (String s_num : splits) {
				int num = Integer.parseInt(s_num);
				if (num == i) {
					return true;
				}
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	/**
	 * 城市值
	 * 
	 * @author huajun.luohj
	 * @param toAreaIds
	 * @return
	 */
	public String getCityByArea(String area) {
		if (area != null && area.length() > 1) {
			return area.substring(0, area.length());
		} else {
			return "";
		}
	}

	/**
	 * 
	 * @author huajun.luohj
	 * @param list
	 * @param index
	 * @return
	 */
	public Object getObjByList(List list, int index) {
		if (list == null || list.isEmpty()) {
			return new Object();
		} else {
			return list.get(index);
		}
	}

	public static boolean isEmpty(String str) {
		if (str != null && !str.trim().equalsIgnoreCase("")) {
			return false;
		}
		return true;
	}

	/**
	 * key1 \1 value1 \0 key2 \1 value2
	 * 
	 * @param richText
	 * @param key
	 * @return
	 */
	public String getRichTextValue(String richText, String key) {
		if (richText != null) {
			String[] arr = StringUtil.split(richText, "\0");
			for (String str : arr) {
				String[] obj = StringUtil.split(str, "\1");
				if (obj != null && obj.length == 2) {
					if (key.equalsIgnoreCase(obj[0])) {
						return obj[1];
					}
				}
			}

		}
		return "";
	}

	/**
	 * key1=value1 \1 key2=value2 \2 key1=value1 \1 key2=value2 取得酒店房型信息
	 * 
	 * @author huajun.luohj
	 * @param richText
	 * @return
	 */
	public String getHotelHouseInfo(String richText) {
		if (richText == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		String[] arr = StringUtil.split(richText, "\2");
		for (String str : arr) {
			String[] obj = StringUtil.split(str, "\1");
			for (String fieldStr : obj) {
				String[] fields = fieldStr.split("=");
				if (fields[0].equalsIgnoreCase("houseType")) {
					sb.append(fields[1]).append(":");
				} else if (fields[0].equalsIgnoreCase("housePrice")) {
					sb.append(fields[1]).append(" * ");
				} else if (fields[0].equalsIgnoreCase("houseNum")) {
					sb.append(fields[1]);
				}
			}
			sb.append("<br>");
		}
		return sb.toString();
	}

	/**
	 * 取得某个用户的增减费用 type=1 增 type=-1 减
	 * 
	 * @param feeText
	 * @param type
	 * @return
	 */
	public int getTouristFee(String feeText, int type) {
		int addRet = 0;
		int subRet = 0;
		if (feeText != null) {
			String[] arr = StringUtil.split(feeText, "\0");
			for (String str : arr) {
				String[] obj = StringUtil.split(str, "\1");
				if (obj != null && obj.length == 2) {
					if ("price10".equalsIgnoreCase(obj[0])
							|| "price11".equalsIgnoreCase(obj[0])
							|| "price12".equalsIgnoreCase(obj[0])
							|| "price13".equalsIgnoreCase(obj[0])
							|| "price14".equalsIgnoreCase(obj[0])
							|| "price15".equalsIgnoreCase(obj[0])) {
						try {
							int i = Integer.parseInt(obj[1]);
							subRet += i;
						} catch (Exception e) {
						}
					} else {
						try {
							int i = Integer.parseInt(obj[1]);
							addRet += i;
						} catch (Exception e) {
						}
					}
				}
			}
		}

		if (type == 1) {
			return addRet;
		} else {
			return subRet;
		}
	}

	public static String getReceiveStat(int receiveStat) {
		String returnStr = "";
		switch (receiveStat) {
		case 0:
			returnStr = "收客中";
			break;
		case 1:
			returnStr = "取消";
			break;
		case 2:
			returnStr = "停售";
			break;
		case 3:
			returnStr = "代理停售";
			break;
		}
		return returnStr;
	}

	public static String getNotice(String name, Timestamp createTime,
			int length, String tourIds) {
		StringBuffer ret = new StringBuffer();

		try {
			if (name.getBytes("GBK").length > length) {
				ret.append("<span title='").append(name).append("'>").append(
						dochar(name, length)).append("...").append("</span>");
			} else {
				ret.append(name);
			}
		} catch (Exception e) {

		}
		// 当线路分类不为null时,在线路之后加上线路分类
		if (tourIds != null && tourIds.trim() != "") {
			ret.append("<span class=\"tourIds\">");
			for (int i = 1; i <= 11; i++) {
				if (checkSelected(tourIds, i)) {
					if (i == 1) {
						ret.append("春节&nbsp;");
					}
					if (i == 2) {
						ret.append("国庆&nbsp;");
					}
					if (i == 3) {
						ret.append("五一&nbsp;");
					}
					if (i == 4) {
						ret.append("中秋&nbsp;");
					}
					if (i == 5) {
						ret.append("元旦&nbsp;");
					}
					if (i == 6) {
						ret.append("特价&nbsp;");
					}
					if (i == 7) {
						ret.append("控位&nbsp;");
					}
					if (i == 8) {
						ret.append("推荐&nbsp;");
					}
					if (i == 9) {
						ret.append("金假期&nbsp;");
					}
					if (i == 10) {
						ret.append("醉夕阳&nbsp;");
					}
					if (i == 11) {
						ret.append("小脚丫&nbsp;");
					}
				}
			}
			ret.append("</span>");
		}
		Date date = new Date();
		if (createTime != null
				&& (date.getTime() - createTime.getTime()) / (1000 * 60 * 60) < 24) {
			ret.append("<span class=\"new\">NEW</span>");
		} else {
			ret.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\r\t");
		}
		return ret.toString();
	}

	public static String getNotice(String name, Timestamp createTime) {
		int length = 12;
		return getNotice(name, createTime, length, null);
	}

	/**
	 * 截取字符串 包括中文
	 * 
	 * @param str
	 * @param count
	 * @return
	 */
	public static String dochar(String str, int count) {
		try {
			byte[] temp = str.getBytes("GBK");
			byte[] bArray = new byte[count * 2];

			int i;
			int ii = 0;// 用于判断最后一个是不是一半汉字
			String strc = "full";

			for (i = 0; i < count; i++) {
				bArray[i] = temp[i];
			}
			for (i = 0; i < count; i++) {
				if (bArray[i] < 0) {
					ii++;
				}
			}
			if (ii % 2 != 0) {
				strc = "hard";
			}

			// 截下去为完全的时候
			if (strc.equals("full") && bArray[i] < 0) {
				bArray[i] = ' ';
			}
			// 截下去为一半的时候
			if (strc.equals("hard") && bArray[i - 1] < 0) {
				bArray[i - 1] = ' ';
			}
			// String ret = new String(bArray,"utf8");
			String ret = new String(bArray, "gbk");
			// String ret2 = new String(bArray,"iso8859-1");
			return ret.trim();
		} catch (Exception e) {
			return str;
		}
	}

	public static String getChnmoney(double number_i) {
		String strNum = String.valueOf(number_i);
		int n, m, k, i, j, q, p, r, s = 0;
		int length, subLength, pstn;
		String change, output, subInput, input = strNum;
		output = "";
		if (strNum.equals(""))
			return null;
		else {
			length = input.length();
			pstn = input.indexOf('.'); // 小数点的位置

			if (pstn == -1) {
				subLength = length;// 获得小数点前的数字
				subInput = input;
			} else {
				subLength = pstn;
				subInput = input.substring(0, subLength);
			}

			char[] array = new char[4];
			char[] array2 = { '仟', '佰', '拾' };
			char[] array3 = { '亿', '万', '元', '角', '分' };

			n = subLength / 4;// 以千为单位
			m = subLength % 4;

			if (m != 0) {
				for (i = 0; i < (4 - m); i++) {
					subInput = '0' + subInput;// 补充首位的零以便处理
				}
				n = n + 1;
			}
			k = n;

			for (i = 0; i < n; i++) {
				p = 0;
				change = subInput.substring(4 * i, 4 * (i + 1));
				array = change.toCharArray();// 转换成数组处理

				for (j = 0; j < 4; j++) {
					output += formatC(array[j]);// 转换成中文
					if (j < 3) {
						output += array2[j];// 补进单位，当为零是不补（千百十）
					}
					p++;
				}

				if (p != 0)
					output += array3[3 - k];// 补进进制（亿万元分角）
				// 把多余的零去掉

				String[] str = { "零仟", "零佰", "零拾" };
				for (s = 0; s < 3; s++) {
					while (true) {
						q = output.indexOf(str[s]);
						if (q != -1)
							output = output.substring(0, q) + "零"
									+ output.substring(q + str[s].length());
						else
							break;
					}
				}
				while (true) {
					q = output.indexOf("零零");
					if (q != -1)
						output = output.substring(0, q) + "零"
								+ output.substring(q + 2);
					else
						break;
				}
				String[] str1 = { "零亿", "零万", "零元" };
				for (s = 0; s < 3; s++) {
					while (true) {
						q = output.indexOf(str1[s]);
						if (q != -1)
							output = output.substring(0, q)
									+ output.substring(q + 1);
						else
							break;
					}
				}
				k--;
			}

			if (pstn != -1)// 小数部分处理
			{
				for (i = 1; i < length - pstn; i++) {
					if (input.charAt(pstn + i) != '0') {
						output += formatC(input.charAt(pstn + i));
						output += array3[2 + i];
					} else if (i < 2)
						output += "零";
					else
						output += "";
				}
			}
			if (output.substring(0, 1).equals("零"))
				output = output.substring(1);
			if (output.substring(output.length() - 1, output.length()).equals(
					"零"))
				output = output.substring(0, output.length() - 1);
			return output += "整";
		}
	}

	public static String formatC(char x) {
		String a = "";
		switch (x) {
		case '0':
			a = "零";
			break;
		case '1':
			a = "壹";
			break;
		case '2':
			a = "贰";
			break;
		case '3':
			a = "叁";
			break;
		case '4':
			a = "肆";
			break;
		case '5':
			a = "伍";
			break;
		case '6':
			a = "陆";
			break;
		case '7':
			a = "柒";
			break;
		case '8':
			a = "捌";
			break;
		case '9':
			a = "玖";
			break;
		}
		return a;
	}

	
	public static String getHourMinitus(String hours, String minitues) {
		String str = "";
		if (hours == null || hours.equalsIgnoreCase("")) {
			return "";
		}
		str = " " + hours;
		if (minitues != null && !minitues.trim().equalsIgnoreCase("")) {
			str += ":" + minitues;
		}
		return str;
	}

	/**
	 * 取得两个时间之间的差数（天）
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static String getTime(Date startDate, Date endDate) {
		if (startDate != null && endDate != null) {
			int date = (int) (endDate.getTime() - startDate.getTime())
					/ (1000 * 60 * 60 * 24);
			return String.valueOf(date);
		}
		return "";
	}

	/**
	 * 取得两个时间之间的差数（天）
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static String getTimeIncludeHead(Date startDate, Date endDate) {
		if (startDate != null && endDate != null) {
			int date = (int) (endDate.getTime() - startDate.getTime())
					/ (1000 * 60 * 60 * 24) + 1;
			return String.valueOf(date);
		}
		return "";
	}

	

	public static String readByURL(String url) {

		StringBuffer sb = new StringBuffer();

		try {

			String sCurrentLine = "";
			java.io.InputStream l_urlStream;
			java.net.URL l_url = new java.net.URL(url);
			java.net.HttpURLConnection l_connection = (java.net.HttpURLConnection) l_url
					.openConnection();

			l_connection.connect();

			l_urlStream = l_connection.getInputStream();

			java.io.BufferedReader l_reader = new java.io.BufferedReader(
					new java.io.InputStreamReader(l_urlStream, "UTF8"));

			while ((sCurrentLine = l_reader.readLine()) != null) {
				sb.append(sCurrentLine).append("\r\n");
			}
			l_reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return sb.toString();

	}

	/**
	 * @param data
	 * @return
	 */
	public static String decodeUrlByUtf8(String data) {
		try {
			return java.net.URLDecoder.decode(data, "utf8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * encodeurl，使用Utf8编码
	 * 
	 * @param name
	 *            要编码的字符
	 * @return String 编码后的字符
	 */
	public static String encodeUrlByUtf8(String name) {
		if (name == null) {
			return "";
		}
		try {
			name = java.net.URLEncoder.encode(name, "UTF-8");
		} catch (Exception e) {
		}
		return name;
	}

	public static int parseInt(String s) {
		if (s == null) {
			return 0;
		}
		try {
			int i = Integer.parseInt(s);
			return i;
		} catch (Exception e) { 
		}
		return 0;
	}

	public static double parseDouble(String s){
		if(s == null){
			return 0.0;
		}
		try{
			double i = Double.parseDouble(s);
			return i;
		}catch(Exception e){
			
		}
		return 0.0;
	}
	
	
	public static long parseStrToLong(String s) {
		try {
			return Long.parseLong(s);
		} catch (Exception e) {
			return 0l;
		}
	}
	
	public static int checkURI(String uri) {
		if (uri == null) {
			return -1;
		}

		if (uri.contains("index.action")) {
			return 1; // 首页
		} else if (uri.contains("chujing.action")) {
			return 4; // 出境
		} else if (uri.contains("guonei.action")) {
			return 5; // 国内
		} else if (uri.contains("buy.action")) {
			return 6; // 旅游团购
		} else if (uri.contains("gift.action")) {
			return 7; // 礼品区域
		} else if (uri.contains("elong.action")) {
			return 8; // 酒店
		} else if (uri.contains("taobaoke.action")) {
			return 9; // 机票
		} else if (uri.contains("web/user/")) {
			return 10; // 个人中心
		}

		return 0;
	}

	private static String getFileName(String fileName) {
		if (fileName != null && !fileName.equalsIgnoreCase("")) {
			int index = fileName.indexOf(".");
			return fileName.substring(0, index);
		}
		return null;
	}

	/**
	 * 将helloWorld转hello_world
	 * 
	 * @param input
	 * @return
	 */
	public static String getSplitString(String input) {
		StringBuffer str = new StringBuffer();
		if (input != null) {
			for (int i = 0; i < input.length(); i++) {
				char a = input.charAt(i);
				if (i != 0 && (a >= 'A') && (a <= 'Z')) {
					a += 32;
					str.append("_");
				}
				str.append(a);
			}
		}
		return str.toString();
	}

	// GENERAL_PUNCTUATION 判断中文的“号

	// CJK_SYMBOLS_AND_PUNCTUATION 判断中文的。号

	// HALFWIDTH_AND_FULLWIDTH_FORMS 判断中文的，号

	public static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
		|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
		|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
		|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
		|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
		|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;

	}

	public static String replaceChinese(String strName) {
		String ret = blanknull(strName);
		char[] ch = ret.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			char c = ch[i];
			if (isChinese(c) == true) {
				ret = ret.replace(String.valueOf(c), "");
			}
		}
		return ret;
	}

	public static String getNumber(String str){
		String data = blanknull(str);
		//Pattern pt=Pattern.compile("([0-9]|\\-|\\s)*");
		Pattern pt=Pattern.compile("([0-9])*");
	    Matcher m=pt.matcher(data);
	    String result = "";
	    while (m.find()) { 
	    	result+=m.group();
	    }
	    return result;
	}
	
	/**
	 * 2014年3月12日
	 * 注释：判断是否为emoji表情
	 */
	private static boolean isNotEmojiCharacter(char codePoint) {
		return  (codePoint == 0x0) ||
				(codePoint == 0x9) ||
				(codePoint == 0xA) ||
				(codePoint == 0xD) ||
				((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
				((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
				((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
    }
	
	/**
	 * 2014年3月13日
	 * 注释：过滤emoji表情
	 */
	public static String replaceEmoji(String str){
		if(str==null)return null;
		if(str.equals(""))return "";
		int len = str.length();
		StringBuilder buf = null;
		for(int i=0;i<len;i++){
			char point = str.charAt(i);
			if(isNotEmojiCharacter(point)){
				if(buf==null){
					buf = new StringBuilder(len);
				}
				buf.append(point);
			}else{
				//过滤emoji表情
			}
		}
		if(buf!=null){
			return buf.toString();
		}else{
			return "";
		}
	}
	
	public static void main(String[] args) {
		String str = StringUtil.convertChars("恍恍惚惚恍恍惚惚恍恍惚惚恍恍惚惚");
		System.out.println(str);
	}

	public static String toHexString(String s) 
	{ 
	String str=""; 
	for (int i=0;i<s.length();i++) 
	{ 
	int ch = (int)s.charAt(i); 
	String s4 = Integer.toHexString(ch); 
	str = str + s4; 
	} 
	
	
	return str; 
	} 
	
	
	/**
	 * 验证邮箱
	 * @param email
	 * @return
	 */
	public static boolean checkEmail(String email){
		boolean flag = false;
		try{
				String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
				Pattern regex = Pattern.compile(check);
				Matcher matcher = regex.matcher(email);
				flag = matcher.matches();
			}catch(Exception e){
				flag = false;
			}
		return flag;
	}
	
	/**
	 * 验证手机号码
	 * @param mobiles
	 * @return
	 */
	public static boolean checkMobileNumber(String mobileNumber){
		boolean flag = false;
		if(mobileNumber != null && !mobileNumber.equals("")){
			try{
					Pattern regex = Pattern.compile("^(((13[0-9])|(17[0-9])|(14[0-9])|(15([0-3]|[5-9]))|(18([0-3]|[5-9])))\\d{8})|(0\\d{2}-\\d{8})|(0\\d{3}-\\d{7})$");
					Matcher matcher = regex.matcher(mobileNumber);
					flag = matcher.matches();
				}catch(Exception e){
					flag = false;
				}
		}
		return flag;
	}
	
	/**
	 * 验证中国移动手机号码
	 * @param mobiles
	 * @return
	 */
	public static boolean checkChinaMobileNumber(String mobileNumber){
		boolean flag = false;
		if(mobileNumber != null && !mobileNumber.equals("")){
			try{
					Pattern regex = Pattern.compile("^((139|138|137|136|135|134|159|150|151|158|157|188|187|152|182|147)\\d{8})|(0\\d{2}-\\d{8})|(0\\d{3}-\\d{7})$");
					Matcher matcher = regex.matcher(mobileNumber);
					flag = matcher.matches();
				}catch(Exception e){
					flag = false;
				}
		}
		return flag;
	}
	
	public static String trim(String str){
		     int j = 0;
		     StringBuffer sb  = new StringBuffer();
		     String[] strs=str.split("");
		     for(int i=0;i<=str.length();i++)
		     {
		         if(strs[i].equals(" ") && j==0)
		         {
		        	 j++;
		         }else if(strs[i].equals(" ")){
		        	 strs[i] = "";
		        	 
		         }else if(strs[i].equals("·")){
		        	 strs[i] = "";
		         }else{
		        	 j=0;
		         }
		         sb.append(strs[i]);
		       
		         
		     }
		    return sb.toString();
	}

	public static String removeRepeat(String[] tempStr) {

		Set<String> set = new TreeSet<String>();
		for (String str : tempStr) {
		    set.add(str);
		}
		StringBuffer sb = new StringBuffer();
		int i = 0;
		for (String str : set) {
			i++;
			if(i==set.size()){
				 sb.append(str);
			}else{
				 sb.append(str+",");
			}
		}
		
		return sb.toString();
	}
	
	/**
	 * 获取6位验证码
	 * @return
	 */
	public static String getSixCode(){
		String re = "";
		for(int i = 0;i<6;i++){
			 Random r = new Random(); 
			 int n = r.nextInt()%10;
			 int nz = Math.abs(n);
			 re+=nz;
		}
		return re;
	}
	
	/**
	 * 获取6位验证码
	 * @return
	 */
	public static String getFourCode(){
		String re = "";
		for(int i = 0;i<4;i++){
			 Random r = new Random(); 
			 int n = r.nextInt()%10;
			 int nz = Math.abs(n);
			 re+=nz;
		}
		return re;
	}
	

	
	/**
	 * 判断是否手机号
	 * @param phone
	 * @return
	 */
	public static boolean checkPhone(String phone){
		String r1 = "^(((13[0-9])|(17[0-9])|(14[0-9])|(15([0-9]))|(18([0-9])))\\d{8})|(0\\d{2}-\\d{8})|(0\\d{3}-\\d{7})$";
        Pattern pattern = Pattern.compile(r1);
        Matcher matcher = pattern.matcher(phone);
        
        if (matcher.matches()){
            return true;
        }
        return false;
    }
	
	/**
	 * 保留指定小数   by hq
	 * @param str
	 * @param r
	 * @return
	 */
	public static String decimalFormat(String str , String r){
		if(str == null || r == null){
			return "";
		}
		DecimalFormat df = new DecimalFormat(r);
		try {
			Double d = Double.parseDouble(str);
			String res = df.format(d);
			return res;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}
	
	public static String formatArea(String area){
		if(area!=null){
			area = area.replaceAll("市辖区", "");
			if(area.indexOf("市") != -1){
				area = area.substring(0,area.indexOf("市"));
			}
			area = area.replaceAll("省", "");
			area = area.replaceAll("市", "");
		}
		return area;
	}
	
	/**
	 * 获取400规则的手机号
	 * @param phones
	 * @return
	 */
	public static String get400Phones(String phones){
		if(phones!=null){
			phones = phones.replaceAll("(^[,]*|[,]*$|(?<=[,])[,]+)", ""); 
		}
		return phones;
	}
	public static String getString(String key, String defaultValue) {

		try {
			String tmp = new String(getString(key).getBytes("iso8859-1"),
					"utf8");
			return tmp;
		} catch (Exception e) {
			return "";
		}
	}
	public static String getString(String key) {
		String tmp = getRealValue(props.getProperty(key));
		return tmp;
	}
	private static String getRealValue(String valueStr) {
		// 修改了空指针异常
		if (valueStr != null) {
			String rtnValue = valueStr;
			while (valueStr.indexOf("${") >= 0 && valueStr.indexOf("}") >= 0) {
				String keyName = valueStr.substring(valueStr.indexOf("{") + 1,
						valueStr.indexOf("}"));
				rtnValue = getString(keyName, "")
						+ valueStr.substring(valueStr.indexOf("}") + 1);
			}
			return rtnValue;
		} else
			return null;
	}
	/**
	 * 获取url中从start到之后第一个end之间的string
	 */
	public static String getChildId(String start, String end, String url) {
		int startLength = start.length();
		int iStart = url.indexOf(start) + startLength;
		int iEnd = 0;
		if (end != null) {
			int i = 0;
			while (iStart > iEnd) {
				iEnd = url.indexOf(end, i);
				if (iEnd == -1)
					break;
				i++;
			}
		}
		if (iStart < iEnd) {
			return url.substring(iStart, iEnd);
		}
		return null;
	}
	/**
	 * 
	 * 是否是数字
	 *
	 * @param str
	 * @return
	 * @since JDK 1.6
	 */
	public static boolean isNumeric(String str){ 
		   Pattern pattern = Pattern.compile("[0-9]*"); 
		   Matcher isNum = pattern.matcher(str);
		   if( !isNum.matches() ){
		       return false; 
		   } 
		   return true; 
		}
}
