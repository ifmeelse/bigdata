package com.hadoop;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
import com.constants.Constant;
import com.util.DateUtil;
import com.util.HdfsUtil;
import com.util.LogUtil;
import com.util.StringUtil;

/**
 * 用户浏览车辆统计 date: 2017年4月20日 上午9:17:52
 * 
 * @author geyang
 */
public class MonthTask {

	private static final Logger logger = Logger.getLogger(MonthTask.class);

	public static void main(String[] args) {
		 Thread thread = new Thread(new execute());
		 thread.start();
	}

	public static class TokenizerMapper extends Mapper<Object, Text, Text, Text> {
		private Text word = new Text();
		private Text text = new Text();
		private IntWritable one = new IntWritable(1);

		//
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

			LogMap map = LogUtil.strToLog(value.toString());
			if (map == null) {
				return;
			}

//			String loginName = map.getUserId();
//			if (loginName != null && !"".equals(loginName)) {
//				loginName = loginName.substring(17, loginName.length());
//			}
			// 用户浏览车辆统计
			String uniqueCookie = map.getUniqueCookie();
			String mobile=map.getMobile();
			if (map.getBuildOriginalURL().contains(Constant.wap_url)
					|| map.getBuildOriginalURL().contains(Constant.app_url)
					|| map.getBuildOriginalURL().contains(Constant.pc_url)) {

				if (map.getBuildOriginalURL().contains("id=")) {

					String idurl = map.getBuildOriginalURL().substring(map.getBuildOriginalURL().indexOf("id=") + 3,
							map.getBuildOriginalURL().length());
					if (idurl.contains("&")) {
						idurl = idurl.substring(0, idurl.indexOf("&"));
					}
					if (!StringUtil.isEmpty(idurl)&&StringUtil.isNumeric(idurl)&&!"0".equals(uniqueCookie)) {
						context.write(new Text("visitcars_"  + idurl+"|"), new Text(uniqueCookie));
					}
				}

			}
			//--------------------------------
			if (!"0".equals(uniqueCookie)&&mobile!=null&&!"".equals(mobile)) {
				
				context.write(new Text("uniqueCookie_"  + uniqueCookie+"|"), new Text(mobile));
			}
			
			
			
		}
	}

	public static class IntSumReducer extends Reducer<Text, Text, Text, Text> {
		private MultipleOutputs<Text, Text> out;

		protected void setup(Context context) throws IOException, InterruptedException {
			out = new MultipleOutputs<Text, Text>(context);
		}

		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			String k = key.toString();
			String v = "";
			StringBuffer str = new StringBuffer();
			for (Text val : values) {
				str.append(val);
				str.append("|");
				if (!StringUtil.isEmpty(val.toString())) {
					v=val.toString();
				}
			}
			if (k.indexOf("visitcars_") != -1) {
				out.write("visitcars",new Text(k.replace("visitcars_", "")), str.toString());
			}
			if (k.indexOf("uniqueCookie_") != -1) {
				out.write("uniqueCookie",new Text(k.replace("uniqueCookie_", "")), v);
			}

		}

		protected void cleanup(Context context) throws IOException, InterruptedException {
			out.close();
		}
	}

	public static class execute implements Runnable {
		public void run() {

			try {
				Date date = DateUtil.getSpecifiedDayBefore();
				String yesterday = DateUtil.dateToStr(date, "yyyy-MM-dd");
				Job job = HdfsUtil.getJob(MonthTask.class.getSimpleName(), null);
				SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
//				String d = sd.format(DateUtil.getSpecifiedDayBefore());
				job.setJarByClass(MonthTask.class);
				job.setMapperClass(TokenizerMapper.class);
				job.setReducerClass(IntSumReducer.class);
				// job.setCombinerClass(IntSumReducer.class);
				job.setOutputKeyClass(Text.class);
				job.setOutputValueClass(Text.class);
				MultipleOutputs.addNamedOutput(job, "visitcars", TextOutputFormat.class, Text.class, IntWritable.class);
				MultipleOutputs.addNamedOutput(job, "uniqueCookie", TextOutputFormat.class, Text.class, IntWritable.class);

				ArrayList<String> urList = HdfsUtil.getLogAccessByDay(30);
				for (String url : urList) {
					FileInputFormat.addInputPath(job, new Path(url));
				}
				Path dstPath = new Path("/output_log/output_log_monthtask"+yesterday);
				FileSystem dhfs = dstPath.getFileSystem(HdfsUtil.getConfiguration());
				if (dhfs.exists(dstPath)) {
					dhfs.delete(dstPath, true);
				}
				FileOutputFormat.setOutputPath(job, new Path("/output_log/output_log_monthtask"+yesterday));
				job.waitForCompletion(true);
				// System.exit(job.waitForCompletion(true) ? 0 : 1);
			} catch (Exception e) {

				// TODO Auto-generated catch block
				e.printStackTrace();

			}

		}

		// }
	}
}
