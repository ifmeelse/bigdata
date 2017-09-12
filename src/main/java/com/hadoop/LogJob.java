package com.hadoop;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
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

import com.bean.LogMap;
import com.util.DateUtil;
import com.util.HdfsUtil;
import com.util.LogUtil;

public class LogJob {

	public static HashMap<String, Integer> phoneCookie = new HashMap<String, Integer>();

	public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable> {

		private final static IntWritable one = new IntWritable(1);
		private Text phoneCookieMap = new Text(); // 手机uniqueCookie

		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			LogMap logMap = LogUtil.strToLog(value.toString());
			if (logMap != null) {
				String url = logMap.getAccessUrl();
				if (url.indexOf(".json") == -1 || url.indexOf("mobile/") != -1) {
					Pattern pat = Pattern.compile("mobile/");
					Matcher mat = pat.matcher(url);
					if (mat.find()) {
						// -----------------------------------手机客户端访问.json-------------------------------
					} else if (LogUtil.getPhone(logMap.getAccessType()) == 1) {
						if (logMap.getFromIp() != null && !"".equals(logMap.getFromIp())) {
							if (logMap.getUniqueCookie() != null && !"".equals(logMap.getUniqueCookie())
									&& !"0".equals(logMap.getUniqueCookie())) {
								phoneCookieMap.set(logMap.getUniqueCookie());
								context.write(phoneCookieMap, one);
							}
						}
					}
				}
			}
		}
	}

	public static class IntSumReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
		public void reduce(Text key, Iterable<IntWritable> values, Context context)
				throws IOException, InterruptedException {
			int count = 0;
			Iterator<IntWritable> iter = values.iterator();
			while (iter.hasNext()) {
				count += iter.next().get();
			}
			LogJob.phoneCookie.put(key.toString(), count);
			System.out.println("cookie :" + phoneCookie.size());
			context.write(key, new IntWritable(count));
		}

	}

	public static class execute implements Runnable {
		public void run() {

			try {
				Job job = HdfsUtil.getJob(LogJob.class.getSimpleName(),null);
				Configuration conf=HdfsUtil.getConfiguration();
				SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
				String d = sd.format(DateUtil.getSpecifiedDayBefore());
//				String d ="2017-03-12";
				job.setJarByClass(LogJob.class);
				job.setMapperClass(TokenizerMapper.class);
				job.setReducerClass(IntSumReducer.class);
//				job.setCombinerClass(IntSumReducer.class);
				job.setOutputKeyClass(Text.class);
				job.setOutputValueClass(IntWritable.class);
				// job.setNumReduceTasks(1);
				//
				// MultipleOutputs.addNamedOutput(job, "newsMap",
				// TextOutputFormat.class, Text.class, IntWritable.class);
				// MultipleOutputs.addNamedOutput(job, "PcMap",
				// TextOutputFormat.class, Text.class, IntWritable.class);
				// MultipleOutputs.addNamedOutput(job, "AppMap",
				// TextOutputFormat.class, Text.class, IntWritable.class);
				// MultipleOutputs.addNamedOutput(job, "WapMap",
				// TextOutputFormat.class, Text.class, IntWritable.class);
				// MultipleOutputs.addNamedOutput(job, "StoreMap",
				// TextOutputFormat.class, Text.class, IntWritable.class);

				FileInputFormat.addInputPath(job, new Path("/data_log/web_all/web_access.log." + d));

				Path dstPath = new Path("/output_log/output_log_click1" + d);
				FileSystem dhfs = dstPath.getFileSystem(conf);
				if (dhfs.exists(dstPath)) {
					dhfs.delete(dstPath, true);
				}
				FileOutputFormat.setOutputPath(job, new Path("/output_log/output_log_click1" + d));
				job.waitForCompletion(true);
				// System.exit(job.waitForCompletion(true) ? 0 : 1);
			} catch (Exception e) {

				// TODO Auto-generated catch block
				e.printStackTrace();

			}

		}

		// }
	}
	public static void main(String[] args) {
//		LogJob logJob=new LogJob();
		Thread thread=new Thread(new execute());
		thread.start();
		
	}

}
