package com.hadoop;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.log4j.Logger;

import com.bean.LogMap;
import com.util.DBUtil;
import com.util.DateUtil;
import com.util.HdfsUtil;
import com.util.LogUtil;
import com.util.StringUtil;

public class AnalyseJob {
	private Logger log = Logger.getLogger(AnalyseJob.class);

	// public void run() {
	// Thread thread = new Thread(new execute());
	// thread.start();
	// // logger.error("MrUrl0");
	// }
	public static void main(String[] args) {
		Thread thread = new Thread(new execute());
		thread.start();
	}

	static ArrayList<String> phoneList = new ArrayList<String>();
	static ArrayList<String> botList = new ArrayList<String>();
	static long pageRequestTotle = 0; // 电脑访问总数
	static long clientRequestTotle = 0;// 客户端访问总数
	static long phoneRequestTotle = 0; // 手机访问总数
	static long searchTotle = 0;// 电脑检索次数
	static long phoneSearchTotle = 0;// 手机检索次数
	static ArrayList<String> arealist = null;
	static ArrayList<String> urllist = null;
	static ArrayList<String> phone_arealist = null;
	static ArrayList<String> phone_urllist = null;
	

	
	static {
		botList.add("spider");
		botList.add("bot");
		botList.add("ysearch");
		phoneList.add("iPhone");
		phoneList.add("Android");
	}
	static String regexPhone = "mobile/";
	static Pattern pat = Pattern.compile(regexPhone);
	static Pattern p = Pattern.compile("search|verify");

	public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable> {
		private final static IntWritable no = new IntWritable(1);
		private Text word = new Text();
//		String fstr ="杭州;";
//		String fstr1 = urlList;
//		String fstr2 =phoneAreaList;
//		String fstr3 = phoneUrlList;
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			try {
				LogMap map = LogUtil.strToLog(value.toString());
				String fstr = context.getConfiguration().get("AreaList");
				String fstr1 = context.getConfiguration().get("UrlList");
				String fstr2 = context.getConfiguration().get("phoneAreaList");
				String fstr3 = context.getConfiguration().get("phoneUrlList");
				System.out.println("areaList"+fstr);
				if (map == null) {
					return;
				}
				if (fstr == null) {
					fstr = "";
				}
				if (fstr1 == null) {
					fstr1 = "";
				}
				if (fstr2 == null) {
					fstr2 = "";
				}
				if (fstr3 == null) {
					fstr3 = "";
				}
				String url = map.getAccessUrl();
				Matcher mat = pat.matcher(url);
				String reqUrl = map.getBuildOriginalURL();
				String furl = map.getFromUrl();
				Matcher m = p.matcher(reqUrl);
				String uri = subString("?", null, reqUrl);
				String[] reqs = uri.split("&");
				String province = map.getProvince();
				String city = map.getCity();
				if (!"".equals(url) && getBot(map.getAccessType()) == 0) {
					if (mat.find()) {
						word.set("clientRequestTotle" + "|");
						context.write(word, no);
						if (map.getFromIp() != null && !"".equals(map.getFromIp())) {
							word.set("clientIpMap" + map.getFromIp() + "|");
							context.write(word, no);
						}
						word.set("clientRequestMap" + map.getAccessUrl() + "|");
						context.write(word, no);
						Pattern cartrade = Pattern.compile("tradeallcar.htm");
						Matcher cartrade1 = cartrade.matcher(reqUrl);
						if (cartrade1.find()) {
//							String[] line1 = map.getUserId().split(" ");
							if (!StringUtil.isEmpty(map.getUserId())) {
								word.set("tradeMap" + "mobile" + "|");
								context.write(word, no);
							}
						}
						Pattern pa0 = Pattern.compile("appmobile=");
						Matcher ma0 = pa0.matcher(reqUrl);
						if (ma0.find()) {
							String mobile = StringUtil.getChildId("appmobile=", "&", reqUrl);
							word.set("appAccessMap" + mobile + "|");
							context.write(word, no);
						}

					} else if (getPhone(map.getAccessType()) == 1) {
						Pattern cartrade = Pattern.compile("tradeallcar.htm");
						Matcher cartrade1 = cartrade.matcher(reqUrl);
						if (cartrade1.find()) {
							String[] line1 = map.getUserId().split(" ");
							if (line1.length == 3) {
								word.set("tradeMap" + "mobile" + "|");
								context.write(word, no);
							}
						}
						word.set("phoneRequestTotle" + "|");
						context.write(word, no);
						if (m.find()) {
							word.set("phoneSearchTotle" + "|");
							context.write(word, no);
						}
						if (map.getFromIp() != null && !"".equals(map.getFromIp())) {
							word.set("phoneIpMap" + map.getFromIp() + "|");
							context.write(word, no);
						}
						if (map.getFromIp() != null && !"".equals(map.getFromIp()) && map.getUniqueCookie() != null
								&& !"".equals(map.getUniqueCookie()) && !"0".equals(map.getUniqueCookie())) {
							word.set("phoneCookieMap" + map.getUniqueCookie() + "|");
							context.write(word, no);
						}
						word.set("phoneRequestMap" + map.getAccessUrl() + "|");
						context.write(word, no);
						if (reqs != null && reqs.length > 0) {
							for (int i = 0; i < reqs.length; i++) {
								Pattern pa = Pattern.compile("keyword");
								Matcher ma = pa.matcher(reqs[i]);
								if (ma.find()) {
									String keyword = subString("keyword=", null, reqs[i]);
									if (keyword != null) {
										try {
											keyword = java.net.URLDecoder.decode(keyword, "utf-8");
										} catch (Exception e) {
										}
									}
									String newword = StringUtil.getString(keyword, null);
									if (newword != null && !"".equalsIgnoreCase(newword)) {
										keyword = newword;
									}
									if (keyword != null && !"".equals(keyword)) {
										word.set("phoneKeywordMap" + keyword + "|");
										context.write(word, no);
										String areaName = null;
										String[] al = fstr2.split(";");
										if (al != null && al.length > 0) {
											for (int j = 0; j < al.length; j++) {
												areaName = al[j];
												if (areaName != null && !areaName.equals("")) {
													Pattern p = Pattern.compile(areaName);
													Matcher m_p = p.matcher(province);
													Matcher m_c = p.matcher(city);
													if (m_p.find() || m_c.find()) {
														word.set("phoneareaKeywordMapK" + al[j] + "," + keyword + "|");
														context.write(word, no);
													}
												}
											}
										}
									}
								}
							}
						}
						Pattern carPa = Pattern.compile("car\\/cardetail.htm");
						Matcher carMa = carPa.matcher(reqUrl);
						if (carMa.find()) {
							String carId = StringUtil.getChildId("id=", "&", reqUrl);
							word.set("phoneCarDetailsMap" + carId + "|");
							context.write(word, no);
						}
						Pattern newsPa = Pattern.compile("car\\/newsdetails.htm");
						Matcher newsMa = newsPa.matcher(reqUrl);
						if (newsMa.find()) {
							String newsId = StringUtil.getChildId("id=", "&", reqUrl);
							word.set("zixunMap" + newsId + "|");
							context.write(word, no);
						}
						Pattern pa0 = Pattern.compile("childId|childLoginId");
						Matcher ma0 = pa0.matcher(reqUrl);
						if (ma0.find()) {
							String childId = StringUtil.getChildId("Id=", "&", reqUrl);
							word.set("childAccessMap" + childId + "|");
							context.write(word, no);
						}
						String areaName = "";
						String[] al = fstr2.split(";");
						for (int i = 0; i < al.length; i++) {
							areaName = al[i];
							if (areaName != null && !areaName.equals("")) {
								Pattern p = Pattern.compile(areaName);
								Matcher m_p = p.matcher(province);
								Matcher m_c = p.matcher(city);
								if (m_p.find() || m_c.find()) {
									word.set("phoneareaMapA" + al[i] + "|");
									context.write(word, no);
								}
							}
						}
						Matcher ms = p.matcher(reqUrl);
						if (ms.find()) {
							for (int i = 0; i < al.length; i++) {
								areaName = al[i];
								if (areaName != null && !areaName.equals("")) {
									Pattern p = Pattern.compile(areaName);
									Matcher m_p = p.matcher(province);
									Matcher m_c = p.matcher(city);
									if (m_p.find() || m_c.find()) {
										word.set("phoneareaMapSearchS" + al[i] + "|");
										context.write(word, no);
									}
								}
							}
						}
						String urlName = "";
						String[] ul = fstr3.split(";");
						if (ul != null && ul.length > 0) {
							for (int i = 0; i < ul.length; i++) {
								urlName = ul[i];
								String[] sul = urlName.split("\\$\\$");
								if (sul != null && sul.length > 0) {
									Pattern p1 = Pattern.compile(sul[0], Pattern.CASE_INSENSITIVE);
									Matcher m1 = p1.matcher(reqUrl);
									if (m1.find()) {
										word.set("phoneurlMapU" + ul[i] + "|");
										context.write(word, no);
									}
									if (sul.length == 1) {
										Pattern p2 = Pattern.compile(urlName, Pattern.CASE_INSENSITIVE);
										Matcher m2 = p2.matcher(furl);
										if (m2.find()) {
											word.set("phonefurlMapF" + ul[i] + "|");
											context.write(word, no);
										}
									} else {
										for (int j = 0; j < sul.length; j++) {
											if (sul[j] != null && sul[j].equals("http://www.hx2car.com/")
													&& "http://www.hx2car.com/".equals(furl)) {
												word.set("phonefurlMapF" + ul[i] + "|");
												context.write(word, no);
											} else {
												Pattern p2 = Pattern.compile(sul[j], Pattern.CASE_INSENSITIVE);
												Matcher m2 = p2.matcher(furl);
												if (m2.find()) {
													word.set("phonefurlMapF" + ul[i] + "|");
													context.write(word, no);
												}
											}
										}
									}
								}
							}
						}
					} else {
						Pattern cartrade = Pattern.compile("tradeallcar.htm");
						Matcher cartrade1 = cartrade.matcher(reqUrl);
						if (cartrade1.find()) {
							String[] line1 = map.getUserId().split(" ");
							if (line1.length == 3) {
								word.set("tradeMap" + "pc" + "|");
								context.write(word, no);
							}
						}
						word.set("pageRequestTotle" + "|");
						context.write(word, no);
						if (m.find()) {
							word.set("searchTotle" + "|");
							context.write(word, no);
						}
						if (map.getFromIp() != null && !"".equals(map.getFromIp())) {
							word.set("pageIpMap" + map.getFromIp() + "|");
							context.write(word, no);
						}
						if (map.getFromIp() != null && !"".equals(map.getFromIp()) && map.getUniqueCookie() != null
								&& !"".equals(map.getUniqueCookie()) && !"0".equals(map.getUniqueCookie())) {
							word.set("pageCookieMap" + map.getUniqueCookie() + "|");
							context.write(word, no);
						}
						word.set("pageRequestMap" + map.getAccessUrl() + "|");
						context.write(word, no);
						if (reqs != null && reqs.length > 0) {
							for (int i = 0; i < reqs.length; i++) {
								Pattern pa = Pattern.compile("keyword");
								Matcher ma = pa.matcher(reqs[i]);
								if (ma.find()) {
									String keyword = subString("keyword=", null, reqs[i]);
									if (keyword != null) {
										try {
											keyword = java.net.URLDecoder.decode(keyword, "utf-8");
										} catch (Exception e) {
										}
									}
									String newword = StringUtil.getString(keyword, null);
									if (newword != null && !"".equalsIgnoreCase(newword)) {
										keyword = newword;
									}
									if (keyword != null && !"".equals(keyword)) {
										word.set("keywordMap" + keyword + "|");
										context.write(word, no);
										String areaName = null;
										String[] al = fstr.split(";");
										for (int j = 0; j < al.length; j++) {
											areaName = al[j];
											Pattern p = Pattern.compile(areaName);
											Matcher m_p = p.matcher(province);
											Matcher m_c = p.matcher(city);
											if (m_p.find() || m_c.find()) {
												word.set("DareaKeywordMapK" + al[j] + "," + keyword + "|");
												context.write(word, no);
											
											}
										}
									}
								}
							}
						}
						String areaName = "";
						String[] al = fstr.split(";");
						for (int i = 0; i < al.length; i++) {
							areaName = al[i];
							Pattern p = Pattern.compile(areaName);
							Matcher m_p = p.matcher(province);
							Matcher m_c = p.matcher(city);
							if (m_p.find() || m_c.find()) {
								word.set("DareaMapA" + al[i] + "|");
								context.write(word, no);
							}
						}
						Matcher ms = p.matcher(reqUrl);
						if (ms.find()) {
							for (int i = 0; i < al.length; i++) {
								areaName = al[i];
								Pattern p = Pattern.compile(areaName);
								Matcher m_p = p.matcher(province);
								Matcher m_c = p.matcher(city);
								if (m_p.find() || m_c.find()) {
									word.set("DareaMapSearchS" + al[i] + "|");
									context.write(word, no);
								}
							}
						}
						String urlName = "";
						String[] ul = fstr1.split(";");
						for (int i = 0; i < ul.length; i++) {
							urlName = ul[i];
							String[] sul = urlName.split("\\$\\$");
							if (sul != null && sul.length > 0) {
								Pattern p1 = Pattern.compile(sul[0], Pattern.CASE_INSENSITIVE);
								Matcher m1 = p1.matcher(reqUrl);
								if (m1.find()) {
									word.set("DurlMapU" + ul[i] + "|");
									context.write(word, no);
								}
								if (sul.length == 1) {
									Pattern p2 = Pattern.compile(urlName, Pattern.CASE_INSENSITIVE);
									Matcher m2 = p2.matcher(furl);
									if (m2.find()) {
										word.set("DfurlMapF" + ul[i] + "|");
										context.write(word, no);
									}
								} else {
									for (int j = 0; j < sul.length; j++) {
										if (sul[j] != null && sul[j].equals("http://www.hx2car.com/")
												&& "http://www.hx2car.com/".equals(furl)) {
											word.set("DfurlMapF" + ul[i] + "|");
											context.write(word, no);
										} else {
											Pattern p2 = Pattern.compile(sul[j], Pattern.CASE_INSENSITIVE);
											Matcher m2 = p2.matcher(furl);
											if (m2.find()) {
												word.set("DfurlMapF" + ul[i] + "|");
												context.write(word, no);
											}
										}
									}
								}
							}
						}
					}
				}
			} catch (Exception e) {

				// TODO Auto-generated catch block
				e.printStackTrace();

			}
		}
	}

	public static class IntSumReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
		private MultipleOutputs<Text, IntWritable> out;

		protected void setup(Context context) throws IOException, InterruptedException {
			out = new MultipleOutputs<Text, IntWritable>(context);
		}

		public void reduce(Text key, Iterable<IntWritable> values, Context context)
				throws IOException, InterruptedException {
			try {
				IntWritable result = new IntWritable();
				int s = 0;
				for (IntWritable val : values) {
					s += val.get();
				}
				result.set(s);
				String k = key.toString();
				if (k.indexOf("phoneIpMap") != -1) {
					out.write("phoneIpMap", k.replace("phoneIpMap", ""), result);
				} else if (k.indexOf("phoneCookieMap") != -1) {
					out.write("phoneCookieMap", k.replace("phoneCookieMap", ""), result);
				} else if (k.indexOf("pageRequestTotle") != -1 || k.indexOf("clientRequestTotle") != -1
						|| k.indexOf("phoneRequestTotle") != -1 || k.indexOf("searchTotle") != -1
						|| k.indexOf("phoneSearchTotle") != -1) {
					out.write("requestTotle", key, result);
				} else if (k.indexOf("pageIpMap") != -1) {
					out.write("pageIpMap", k.replace("pageIpMap", ""), result);
				} else if (k.indexOf("pageCookieMap") != -1) {
					out.write("pageCookieMap", k.replace("pageCookieMap", ""), result);
				} else if (k.indexOf("pageRequestMap") != -1) {
					out.write("pageRequestMap", k.replace("pageRequestMap", ""), result);
				} else if (k.indexOf("phoneRequestMap") != -1) {
					out.write("phoneRequestMap", k.replace("phoneRequestMap", ""), result);
				} else if (k.indexOf("clientIpMap") != -1) {
					out.write("clientIpMap", k.replace("clientIpMap", ""), result);
				} else if (k.indexOf("clientRequestMap") != -1) {
					out.write("clientRequestMap", k.replace("clientRequestMap", ""), result);
				} else if (k.indexOf("keywordMap") != -1) {
					out.write("keywordMap", k.replace("keywordMap", ""), result);
				} else if (k.indexOf("phoneKeywordMap") != -1) {
					out.write("phoneKeywordMap", k.replace("phoneKeywordMap", ""), result);
				} else if (k.indexOf("phoneCarDetailsMap") != -1) {
					out.write("phoneCarDetailsMap", k.replace("phoneCarDetailsMap", ""), result);
				} else if (k.indexOf("zixunMap") != -1) {
					out.write("zixunMap", k.replace("zixunMap", ""), result);
				} else if (k.indexOf("childAccessMap") != -1) {
					out.write("childAccessMap", k.replace("childAccessMap", ""), result);
				} else if (k.indexOf("appAccessMap") != -1) {
					out.write("appAccessMap", k.replace("appAccessMap", ""), result);
				} else if (k.indexOf("tradeMap") != -1) {
					out.write("tradeMap", k.replace("tradeMap", ""), result);

				} else if (k.indexOf("DareaKeywordMap") != -1 || k.indexOf("DareaMap") != -1
						|| k.indexOf("DareaMapSearch") != -1) {
					out.write("areaMapTotal",
							k.replace("DareaKeywordMap", "").replace("DareaMapSearch", "").replace("DareaMap", ""),
							result);
				} else if (k.indexOf("DurlMap") != -1 || k.indexOf("DfurlMap") != -1) {
					out.write("urlMapTotal", k.replace("DfurlMap", "").replace("DurlMap", ""), result);
				} else if (k.indexOf("phoneareaKeywordMap") != -1 || k.indexOf("phoneareaMap") != -1
						|| k.indexOf("phoneareaMapSearch") != -1) {
					out.write("phoneareaMapTotal", k.replace("phoneareaKeywordMap", "")
							.replace("phoneareaMapSearch", "").replace("phoneareaMap", ""), result);
				} else if (k.indexOf("phoneurlMap") != -1 || k.indexOf("phonefurlMap") != -1) {
					out.write("phoneurlMapTotal", k.replace("phonefurlMap", "").replace("phoneurlMap", ""), result);
				}
			} catch (Exception e) {

				// TODO Auto-generated catch block
				e.printStackTrace();

			}
		}

		protected void cleanup(Context context) throws IOException, InterruptedException {
			out.close();
		}
	}

	public static class execute implements Runnable {
		public void run() {

			try {
				Configuration conf = HdfsUtil.getConfiguration();
			
				DBUtil db = new DBUtil();
				String sql = "select * from log_conf where flag=0";
				HashMap<Integer, ArrayList<String>> sqlmap = db.getList(sql);
//				System.out.println("数据库未正常关闭！"+sqlmap);
				// log.error("sqlmap:"+sqlmap);
				db.closeCon();
				arealist = sqlmap.get(0);
				urllist = sqlmap.get(1);
				phone_arealist = sqlmap.get(2);
				phone_urllist = sqlmap.get(3);
				 String areaList = "";
				 String urlList = "";
				 String phoneAreaList = "";
				 String phoneUrlList = "";
				for (String a : arealist) {
					areaList += a + ";";
				}
				for (String a : urllist) {
					urlList += a + ";";
				}
				for (String a : phone_arealist) {
					phoneAreaList += a + ";";
				}
				for (String a : phone_urllist) {
					phoneUrlList += a + ";";
				}
				
				conf.set("AreaList", areaList);
				conf.set("UrlList", urlList);
				conf.set("phoneAreaList", phoneAreaList);
				conf.set("phoneUrlList", phoneUrlList);
				Job job = HdfsUtil.getJob(AnalyseJob.class.getSimpleName(), conf);
				SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
				String d = sd.format(DateUtil.getSpecifiedDayBefore(-1));
				job.setJarByClass(AnalyseJob.class);
				job.setMapperClass(TokenizerMapper.class);
				job.setReducerClass(IntSumReducer.class);
				job.setOutputKeyClass(Text.class);
				job.setOutputValueClass(IntWritable.class);
				MultipleOutputs.addNamedOutput(job, "phoneIpMap", TextOutputFormat.class, Text.class, Text.class);
				MultipleOutputs.addNamedOutput(job, "phoneCookieMap", TextOutputFormat.class, Text.class, Text.class);
				MultipleOutputs.addNamedOutput(job, "pageIpMap", TextOutputFormat.class, Text.class, Text.class);
				MultipleOutputs.addNamedOutput(job, "pageCookieMap", TextOutputFormat.class, Text.class, Text.class);
				MultipleOutputs.addNamedOutput(job, "pageRequestMap", TextOutputFormat.class, Text.class, Text.class);
				MultipleOutputs.addNamedOutput(job, "phoneRequestMap", TextOutputFormat.class, Text.class, Text.class);
				MultipleOutputs.addNamedOutput(job, "clientIpMap", TextOutputFormat.class, Text.class, Text.class);
				MultipleOutputs.addNamedOutput(job, "clientRequestMap", TextOutputFormat.class, Text.class, Text.class);
				MultipleOutputs.addNamedOutput(job, "requestTotle", TextOutputFormat.class, Text.class, Text.class);
				MultipleOutputs.addNamedOutput(job, "keywordMap", TextOutputFormat.class, Text.class, Text.class);
				MultipleOutputs.addNamedOutput(job, "phoneKeywordMap", TextOutputFormat.class, Text.class, Text.class);
				MultipleOutputs.addNamedOutput(job, "phoneCarDetailsMap", TextOutputFormat.class, Text.class,
						Text.class);
				MultipleOutputs.addNamedOutput(job, "childAccessMap", TextOutputFormat.class, Text.class, Text.class);
				MultipleOutputs.addNamedOutput(job, "appAccessMap", TextOutputFormat.class, Text.class, Text.class);
				MultipleOutputs.addNamedOutput(job, "zixunMap", TextOutputFormat.class, Text.class, Text.class);
				MultipleOutputs.addNamedOutput(job, "areaMapTotal", TextOutputFormat.class, Text.class, Text.class);
				MultipleOutputs.addNamedOutput(job, "urlMapTotal", TextOutputFormat.class, Text.class, Text.class);
				MultipleOutputs.addNamedOutput(job, "phoneareaMapTotal", TextOutputFormat.class, Text.class,
						Text.class);
				MultipleOutputs.addNamedOutput(job, "phoneurlMapTotal", TextOutputFormat.class, Text.class, Text.class);
				MultipleOutputs.addNamedOutput(job, "tradeMap", TextOutputFormat.class, Text.class, Text.class);
				Path dstPath = new Path("/output_log/output_log" + d);
				FileSystem dhfs = dstPath.getFileSystem(conf);
				if (dhfs.exists(dstPath)) {
					dhfs.delete(dstPath, true);
				}
				ArrayList<String> urList = HdfsUtil.getLogAccessByDay(1);
				for (String url : urList) {
					FileInputFormat.addInputPath(job, new Path(url));
				}
				FileOutputFormat.setOutputPath(job, new Path("/output_log/output_log" + d));
				// System.exit(job.waitForCompletion(true) ? 0 : 1);

				job.waitForCompletion(true);

			} catch (Exception e) {

				// TODO Auto-generated catch block
				e.printStackTrace();

			}

		}

		// }
	}

	public static int getPhone(String anegt) {
		for (int i = 0; i < phoneList.size(); i++) {
			Matcher m = Pattern.compile(phoneList.get(i), Pattern.CASE_INSENSITIVE).matcher(anegt);
			if (m.find()) {
				return 1;
			}
		}
		return 0;
	}

	public static int getBot(String anegt) {
		for (int i = 0; i < botList.size(); i++) {
			Matcher m = Pattern.compile(botList.get(i), Pattern.CASE_INSENSITIVE).matcher(anegt);
			if (m.find()) {
				return 1;
			}
		}
		return 0;
	}

	public static String subString(String start, String end, String content) {
		int startLength = start.length();
		int iStart = content.indexOf(start) + startLength;
		int iEnd = content.length();
		if (end != null) {
			iEnd = content.indexOf(end);
		}
		if (iStart < iEnd) {
			return content.substring(iStart, iEnd);
		}
		return null;
	}

}
