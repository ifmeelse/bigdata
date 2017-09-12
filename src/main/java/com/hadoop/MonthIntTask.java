package com.hadoop;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
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

import com.bean.LogMap;
import com.util.DateUtil;
import com.util.HdfsUtil;
import com.util.LogUtil;
/**
 * 车辆展示数据获得
 * date: 2017年4月20日 上午9:17:52 
 * @author geyang
 */
public class MonthIntTask {


     public static void main(String[] args) {
    	 Thread thread = new Thread(new execute());
 		thread.start();
	}
	public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable> {
		private final static IntWritable one = new IntWritable(1);
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			
			LogMap map = LogUtil.strToLog(value.toString());
			if (map == null) {
				return;
			}
			if (map.getAccessUrl().contains("carlog.json")) {
				
				String ids=StringUtils.substringAfter(map.getBuildOriginalURL(), "ids=");
				
				ids=StringUtils.substringBefore(ids, "&");
				
				String[] idStrings=ids.split(",");
				for (int i = 0; i < idStrings.length; i++) {
					if (StringUtils.isNotEmpty(idStrings[i])) {
						context.write(new Text(idStrings[i]+"|"), one);
					}
				}
				
				
			}
			
			
			
		}
	}

	public static class IntSumReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
		 private MultipleOutputs<Text, IntWritable> out;

		 protected void setup(Context context) throws IOException,
		 InterruptedException {
		 out = new MultipleOutputs<Text, IntWritable>(context);
		 }

		public void reduce(Text key, Iterable<IntWritable> values, Context context)
				throws IOException, InterruptedException {
			String k = key.toString();
				int sum = 0;
				for (IntWritable val : values) {
					sum += val.get();
				}
				out.write("carshow", key, sum);

		}

		 protected void cleanup(Context context) throws IOException,
		 InterruptedException {
		 out.close();
		 }
	}
	
	public static class execute implements Runnable {
		public void run() {

			try {
				Job job = HdfsUtil.getJob(MonthIntTask.class.getSimpleName(),null);
				SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
				String d = sd.format(DateUtil.getSpecifiedDayBefore());
				
				job.setMapOutputValueClass(IntWritable.class);
				job.setJarByClass(MonthIntTask.class);
				job.setMapperClass(TokenizerMapper.class);
				job.setReducerClass(IntSumReducer.class);
//				job.setCombinerClass(IntSumReducer.class);
				job.setOutputKeyClass(Text.class);
				job.setOutputValueClass(IntWritable.class);
				 MultipleOutputs.addNamedOutput(job, "carshow",TextOutputFormat.class, Text.class, IntWritable.class);

				ArrayList<String> urList = HdfsUtil.getLogAccessByDay(30);
				for (String url : urList) {
					FileInputFormat.addInputPath(job, new Path(url));
				}
				Path dstPath = new Path("/output_log/output_log_monthinttask" + d);
				FileSystem dhfs = dstPath.getFileSystem(HdfsUtil.getConfiguration());
				if (dhfs.exists(dstPath)) {
					dhfs.delete(dstPath, true);
				}
				FileOutputFormat.setOutputPath(job, new Path("/output_log/output_log_monthinttask" + d));
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
