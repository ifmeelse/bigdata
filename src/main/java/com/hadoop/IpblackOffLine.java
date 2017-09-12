package com.hadoop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

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
import com.kafka.KafkaUtil;
import com.util.HdfsUtil;
import com.util.LogFilter;
import com.util.LogUtil;

public class IpblackOffLine implements Runnable {
	public void run() {
		Thread thread = new Thread(new execute());
		thread.start();
	}

	public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable> {

		
		protected void map(Object key, Text value, Mapper<Object, Text, Text, IntWritable>.Context context)
				throws IOException, InterruptedException {

			
			LogMap map = LogUtil.strToLog(value.toString());
			if (map==null) {
				return;
			}
			if (map.getAccessUrl() == null || map.getAccessUrl().indexOf(".json") > 0
					|| map.getAccessUrl().indexOf("upload.htm") > 0)
				return;
			if (!"".equals(map.getAccessUrl()) && LogFilter.getBot2(map.getAccessType()) == 0) {
				// 生成过滤后的日志
				
				context.write(new Text(map.getFromIp()), new IntWritable(1));
				
			}
			
			
		
		}
	}

	public static class IntSumReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

		protected void reduce(Text key, Iterable<IntWritable> arg1, Context context)
				throws IOException, InterruptedException {

			int count = 0;
			Iterator<IntWritable> iter = arg1.iterator();
			while (iter.hasNext()) {
				count += iter.next().get();
			}
			
			if (count >= 10000) {
				 KafkaUtil.sendMessage("ip_0:" + key.toString()+","+count, "ipblack");
//				System.out.println(arg0._1() + ": " + arg0._2());
			}
			
		}
	}

	public class execute implements Runnable {
		public void run() {

			try {
				Job job = HdfsUtil.getJob(IpblackOffLine.class.getSimpleName(),null);
//				Configuration conf=HdfsUtil.getConfiguration();
//				String hdfsurl = "hdfs://mycluster/data_log/web_all/" + DateUtil.date2Str(DateUtil.getDayBefore(1))
//				+ "/web_access.log";
				job.setJarByClass(IpblackOffLine.class);
				job.setMapperClass(TokenizerMapper.class);
				job.setReducerClass(IntSumReducer.class);
				// job.setCombinerClass(IntSumReducer.class);
				job.setOutputKeyClass(Text.class);
				job.setOutputValueClass(IntWritable.class);

				ArrayList<String> urList = HdfsUtil.getLogAccessByDay(1);
				for (String url : urList) {
					FileInputFormat.addInputPath(job, new Path(url));
				}
				Path dstPath = new Path("/output_log/tmp");
				Configuration conf=HdfsUtil.getConfiguration();
				FileSystem dhfs = dstPath.getFileSystem(conf);
				if (dhfs.exists(dstPath)) {
					dhfs.delete(dstPath, true);
				}
				FileOutputFormat.setOutputPath(job, new Path("/output_log/tmp" ));

				boolean res = job.waitForCompletion(true);
				// System.exit(res ? 0 : 1);
			} catch (Exception e) {

				// TODO Auto-generated catch block
				e.printStackTrace();

			}

		}

		// }
	}

	public static void main(String[] args) {
		Thread thread = new Thread(new IpblackOffLine());
		thread.start();

	}
}
