package com.hadoop;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

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
import org.apache.log4j.Logger;
import org.netlib.util.intW;

import com.util.DateUtil;
import com.util.HdfsUtil;
import com.util.StringUtil;
/**
 * 用户点击率统计
 * date: 2017年4月20日 上午9:17:52 
 * @author geyang
 */
public class StarCar {

	private static final Logger logger = Logger.getLogger(StarCar.class);


	// 统计品牌搜索量
	public void run() {
		Thread thread = new Thread(new execute());
		thread.start();
		// logger.error("MrUrl0");
	}

	public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable> {
//		private Text word = new Text();
		// private Text text = new Text();
		private final static IntWritable one = new IntWritable(1);
		HashMap<String, String> ipmap = new HashMap<String, String>();

		//
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

			try {
				String uline = value.toString();
				System.out.println(uline);
				String[] lines = uline.split("\\|");
				if (lines.length == 34) {
					String loginName=lines[24];
					if (!StringUtil.isEmpty(loginName.trim())) {
						context.write(new Text(loginName+"|"), new IntWritable(1));
					}
				}
			} catch (Exception e) {
				
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
		}
	}

	public static class IntSumReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
		// private MultipleOutputs<Text, IntWritable> out;

		// protected void setup(Context context) throws IOException,
		// InterruptedException {
		// out = new MultipleOutputs<Text, IntWritable>(context);
		// }

		public void reduce(Text key, Iterable<IntWritable> values, Context context)
				throws IOException, InterruptedException {
//			String k = key.toString();
//			if (k != null && k.contains("_news_url")) {
				int sum = 0;
				for (IntWritable val : values) {
					// String s = val.toString();
					// if (k != null && k.contains("|")) {
					sum += val.get();
					//
					// }else {
					// context.write(key, val);
					// }
				}
				context.write(key, new IntWritable(sum));
//			} else {
//				context.write(key, new IntWritable());
//			}

			// if (k.indexOf("newsMap") != -1) {
			// k = "newsMap";
			// } else if (k.indexOf("PcMap") != -1) {
			// k = "PcMap";
			// } else if (k.indexOf("AppMap") != -1) {
			// k = "AppMap";
			// } else if (k.indexOf("WapMap") != -1) {
			// k = "WapMap";
			// } else if (k.indexOf("StoreMap") != -1) {
			// k = "StoreMap";
			// }

		}

		// protected void cleanup(Context context) throws IOException,
		// InterruptedException {
		// out.close();
		// }
	}

	public class execute implements Runnable {
		public void run() {

			try {
				Job job = HdfsUtil.getJob(StarCar.class.getSimpleName(),null);
				SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
				String d = sd.format(DateUtil.getSpecifiedDayBefore());
//				String d ="2017-03-12";
				job.setJarByClass(StarCar.class);
				job.setMapperClass(TokenizerMapper.class);
				job.setReducerClass(IntSumReducer.class);
//				job.setCombinerClass(IntSumReducer.class);
				job.setOutputKeyClass(Text.class);
				job.setOutputValueClass(IntWritable.class);
//				job.setNumReduceTasks(1);
//				FileInputFormat.addInputPaths(job, "/carmulti/cars/cars0000/part-m-00000,/carmulti/cars/cars0001/part-m-00000,/carmulti/cars/cars0002/part-m-00000,/carmulti/cars/cars0003/part-m-00000");;
				FileInputFormat.addInputPath(job, new Path("/carmulti/cars/cars0000/part-m-00000"));
				FileInputFormat.addInputPath(job, new Path("/carmulti/cars/cars0001/part-m-00000"));
				FileInputFormat.addInputPath(job, new Path("/carmulti/cars/cars0002/part-m-00000"));
				FileInputFormat.addInputPath(job, new Path("/carmulti/cars/cars0003/part-m-00000"));
				Path dstPath = new Path("/output_log/output_log_carmulti" + d);
				FileSystem dhfs = dstPath.getFileSystem(HdfsUtil.getConfiguration());
				if (dhfs.exists(dstPath)) {
					dhfs.delete(dstPath, true);
				}
				FileOutputFormat.setOutputPath(job, new Path("/output_log/output_log_carmulti" + d));
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
		StarCar starCar=new StarCar();
		starCar.run();
		
	}
}
