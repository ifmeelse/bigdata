package com.hadoop;

import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import com.bean.LogMap;
import com.util.DateUtil;
import com.util.HdfsUtil;
import com.util.LogUtil;


/**
 * 统计用户搜索关键词
 * date: 2017年4月20日 上午9:15:05 
 * @author geyang
 */
public class keywordCount {
	public void run() {
		Thread thread = new Thread(new execute());
		thread.start();
		// logger.error("MrUrl0");
	}

	private static String index = "/search/index.htm";
	private static String search = "/car/search.htm";
	public static class TokenizerMapper extends Mapper<Object, Text, Text,Text> {
		private Text word = new Text();
		 private Text text = new Text();
		//
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			try {
				LogMap map = LogUtil.strToLog(value.toString());
				if (map == null) {
					return;
				}
				if (map!=null) {
					String uniqueCookie=map.getUserId().substring(17, map.getUserId().length());
					if (uniqueCookie==null||"".equals(uniqueCookie)||"null".equals(uniqueCookie)) {
						uniqueCookie =map.getUniqueCookie();
					}
					if (uniqueCookie != null && !"".equals(uniqueCookie)&& !"0".equals(uniqueCookie)) {
						String line = map.getBuildOriginalURL();
						if (line != null && line.contains(search) || line.contains(index)) {
							word.set(uniqueCookie.trim());
							String keyword = null;
							if (line.contains(search)) {
								keyword = StringUtils.substringAfter(line, "more=");
								if (keyword.indexOf("&") > -1) {
									keyword = StringUtils.substringBefore(keyword, "&");
								}
							} else {
								keyword = StringUtils.substringAfter(line, "?keyword=");
							}

							 if (keyword!=null&&!"".equals(keyword)) {

							text.set(keyword);
							context.write(word, text);
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

	public static class IntSumReducer extends Reducer<Text, Text, Text, Text> {
		
		public void reduce(Text key, Text values, Context context)
				throws IOException, InterruptedException {
			try {
//				StringBuffer s=new StringBuffer();
//				for (Text text : values) {
//					s.append(text);
//					s.append(",");
//				}
//				
				 context.write(key, new Text(values));
			} catch (Exception e) {
				
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
			
		}

		
	}

	public class execute implements Runnable {
		public void run() {

			try {
				Job job = HdfsUtil.getJob(keywordCount.class.getSimpleName(),null);
				Configuration conf=HdfsUtil.getConfiguration();
				SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
				String d = sd.format(DateUtil.getSpecifiedDayBefore());
				String d2 = sd.format(DateUtil.getSpecifiedDayBefore(-2));
				String d3 = sd.format(DateUtil.getSpecifiedDayBefore(-3));
				job.setJarByClass(keywordCount.class);
//				job.setJar("/app/soft/hadoop-2.6.0/hadoop.jar");
				job.setMapperClass(TokenizerMapper.class);
				job.setReducerClass(IntSumReducer.class);
//				job.setCombinerClass(IntSumReducer.class);
				job.setOutputKeyClass(Text.class);
				job.setOutputValueClass(Text.class);
				FileInputFormat.addInputPath(job,
						new Path("/data_log/web_all/web_access.log." + d));
				FileInputFormat.addInputPath(job,
						new Path("/data_log/web_all/web_access.log." + d2));
				FileInputFormat.addInputPath(job,
						new Path("/data_log/web_all/web_access.log." + d3));
				
				Path dstPath = new Path("/output_log/output_log_search1" + d);
				FileSystem dhfs = dstPath.getFileSystem(conf);
				if (dhfs.exists(dstPath)) {
					dhfs.delete(dstPath, true);
				}
				FileOutputFormat.setOutputPath(job,
						new Path("/output_log/output_log_search1" + d));
				job.waitForCompletion(true);
//				job.submit();
				// System.exit(job.waitForCompletion(true) ? 0 : 1);
			} catch (Exception e) {

				// TODO Auto-generated catch block
				e.printStackTrace();

			}

		}

		// }
	}
	public static void main(String[] args) {
		keywordCount keywordCount=new keywordCount();
		keywordCount.run();
	}
}
