package com.hadoop;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
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

/**
 * 搜索指数统计
 * date: 2017年4月20日 上午9:17:02 
 * @author geyang
 */
public class SearchJob {
//	public void run() {
//		Thread thread = new Thread(new execute());
//		thread.start();
//		// logger.error("MrUrl0");
//	}
   public static void main(String[] args) {
	   Thread thread = new Thread(new execute());
		thread.start();
}
   static Pattern mpattern = Pattern.compile("search2016.htm|carlist.htm|filteData.json");
	public static class TokenizerMapper extends Mapper<Object, Text, Text,Text> {
		private Text word = new Text();
		 private Text text = new Text();
		private final static IntWritable one = new IntWritable(1);
		  HashMap<String, String> ipmap=new HashMap<String, String>();
		//
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			try {
				LogMap map = LogUtil.strToLog(value.toString());
				if (map!=null) {
					
					String line = map.getBuildOriginalURL();
					if (line!=null&&mpattern.matcher(line).find()) {
						word.set(map.getFromIp()+"|");
						text.set(line);
						context.write(word, text);			
					}
				}
			} catch (Exception e) {
				
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
		
			
		}
	}

	public static class IntSumReducer extends Reducer<Text, Text, Text, Text> {
		
		public void reduce(Text key, Text value, Context context)
				throws IOException, InterruptedException {
			 context.write(key, value);
			
		}

		
	}

	public static class execute implements Runnable {
		public void run() {

			try {
				
				Job job = HdfsUtil.getJob(SearchJob.class.getSimpleName(),null);
				Configuration conf=HdfsUtil.getConfiguration();
				
				
				SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
				String d = sd.format(DateUtil.getSpecifiedDayBefore());
				job.setJarByClass(SearchJob.class);
				job.setMapperClass(TokenizerMapper.class);
				job.setReducerClass(IntSumReducer.class);
//				job.setCombinerClass(IntSumReducer.class);
				job.setOutputKeyClass(Text.class);
				job.setOutputValueClass(Text.class);
//				FileInputFormat.addInputPath(job,
//						new Path("/data_log/web_all/web_access.log." + d));
				ArrayList<String> urList = HdfsUtil.getLogAccessByDay(1);
				for (String url : urList) {
					FileInputFormat.addInputPath(job, new Path(url));
				}
				Path dstPath = new Path("/output_log/output_log_search" + d);
				FileSystem dhfs = dstPath.getFileSystem(conf);
				if (dhfs.exists(dstPath)) {
					dhfs.delete(dstPath, true);
				}
				FileOutputFormat.setOutputPath(job,
						new Path("/output_log/output_log_search" + d));
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
