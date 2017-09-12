package com.logfilter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.util.DateUtil;
import com.util.IOUtil;
import com.util.StringUtil;


/**
 * @date 2015年3月10日
 * @author hq 日志过滤
 */
public class LogFilter {

	static ArrayList<String> botList = new ArrayList<String>();
	static {
		botList.add("spider");
		botList.add("bot");
		botList.add("ysearch");
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

	/**
	 * @date 2013年8月21日 读取日志 filePath:日志路径
	 */
	public static String readLog(String filePath) {
		String res = "1";
		String line;
		try {
			FileInputStream fis = new FileInputStream(filePath);
			InputStreamReader isr = new InputStreamReader(fis, "utf-8");
			BufferedReader br = new BufferedReader(isr);

			while ((line = br.readLine()) != null) {
				if (line.equals("")) {
					continue;
				} else {
					String[] lines = line.split("\\|");
					if (lines.length !=14) {
						continue;// 放弃该条数据
					}
//					LogMap logMap = new LogMap();

					if (StringUtil.isEmpty(lines[7]) || lines[7].indexOf("getuploadPic.json") > 0|| lines[7].indexOf("callback.json")>0)
						continue;
//					logMap.setAccessUrl(lines[7]);
					if (lines[9] == null)
						continue;
//					logMap.setAccessType(lines[9]);

					if ( LogFilter.getBot(lines[9]) == 0) {
						// 生成过滤后的日志
						try {
							IOUtil.wirteString("./web_access.log", line + "\r\n");
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				}
			}
			br.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return res;
	}

	public static void main(String[] args) {
//		String path = "./";
		String path = "/app/soft/hadoop-2.6.0/data_log/";
		String res = readLog(path + "access.log");
		System.out.println(res);
	}
}