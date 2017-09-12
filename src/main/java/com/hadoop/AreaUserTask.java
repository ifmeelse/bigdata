package com.hadoop;

import java.io.IOException;
import java.text.SimpleDateFormat;

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

import com.util.DateUtil;
import com.util.HdfsUtil;

/**
 * rizhi date: 2017年4月20日 上午9:17:52
 * 
 * @author geyang
 */
public class AreaUserTask {

	private static final Logger logger = Logger.getLogger(AreaUserTask.class);

	// 统计品牌搜索量
	public void run() {
		Thread thread = new Thread(new execute());
		thread.start();
		// logger.error("MrUrl0");
	}

	public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable> {
		// private Text word = new Text();
		// private Text text = new Text();
		// private final static IntWritable one = new IntWritable(1);
		// HashMap<String, String> ipmap = new HashMap<String, String>();

		//
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

			try {
				String uline = value.toString();
				System.out.println(uline);
				String[] lines = uline.split("\\|");
				if (lines.length == 29) {
					String usertype = lines[4];
					String areacode = lines[13];
					context.write(new Text(areacode + "_" + usertype + "_"), new IntWritable(1));
				}

			} catch (Exception e) {

				// TODO Auto-generated catch block
				e.printStackTrace();

			}
		}
	}

	public static class IntSumReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
		// private MultipleOutputs<Text, IntWritable> out;
		//
		// protected void setup(Context context) throws IOException,
		// InterruptedException {
		// out = new MultipleOutputs<Text, IntWritable>(context);
		// }

		public void reduce(Text key, Iterable<IntWritable> values, Context context)
				throws IOException, InterruptedException {
			String k = key.toString();
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			context.write(key, new IntWritable(sum));

		}

		// protected void cleanup(Context context) throws IOException,
		// InterruptedException {
		// out.close();
		// }
	}

	public class execute implements Runnable {
		public void run() {

			try {
				// Job job=HdfsUtil.getJob("area_log");
//				Configuration conf = new Configuration();
//				conf.set("fs.defaultFS", "hdfs://mycluster");
//				conf.set("dfs.nameservices", "mycluster");
//				conf.set("dfs.ha.namenodes.mycluster", "nn1,nn2");
//				conf.set("dfs.namenode.rpc-address.mycluster.nn1", "app206:9000");
//				conf.set("dfs.namenode.rpc-address.mycluster.nn2", "app205:9000");
//				conf.set("dfs.client.failover.proxy.provider.mycluster",
//						"org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider");
//				conf.set("mapreduce.reduce.shuffle.input.buffer.percent", "0.01");
//				conf.set("mapreduce.reduce.shuffle.parallelcopies", "1");// Reduce
//																			// Task启动的并发拷贝数据的线程数目
//				conf.set("mapreduce.reduce.shuffle.memory.limit.percent", "0.01");
//				// conf.set("yarn.app.mapreduce.am.staging-dir",
//				// "/app/soft/hadoop-2.6.0/hadoop-yarn/staging");
//				Job job = Job.getInstance(conf, "area_log");
				
				Configuration conf=HdfsUtil.getConfiguration();
				conf.set("mapreduce.reduce.shuffle.input.buffer.percent", "0.01");
				conf.set("mapreduce.reduce.shuffle.parallelcopies", "1");// Reduce
//																			// Task启动的并发拷贝数据的线程数目
				conf.set("mapreduce.reduce.shuffle.memory.limit.percent", "0.01");
				Job job = HdfsUtil.getJob(AreaUserTask.class.getSimpleName(),conf);
				SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
				String d = sd.format(DateUtil.getSpecifiedDayBefore());
				// String d ="2017-03-12";
				job.setJarByClass(AreaUserTask.class);
				job.setMapperClass(TokenizerMapper.class);
				job.setReducerClass(IntSumReducer.class);
				// job.setCombinerClass(IntSumReducer.class);
				job.setOutputKeyClass(Text.class);
				job.setOutputValueClass(IntWritable.class);
				// job.setNumReduceTasks(1);
				// FileInputFormat.addInputPaths(job,
				// "/carmulti/cars/cars0000/part-m-00000,/carmulti/cars/cars0001/part-m-00000,/carmulti/cars/cars0002/part-m-00000,/carmulti/cars/cars0003/part-m-00000");;
				// MultipleOutputs.addNamedOutput(job, "areacodepv",
				// TextOutputFormat.class,
				// Text.class, Text.class);
				// MultipleOutputs.addNamedOutput(job, "ip",
				// TextOutputFormat.class,
				// Text.class, Text.class);
				// MultipleOutputs.addNamedOutput(job, "areacodeuv",
				// TextOutputFormat.class,
				// Text.class, Text.class);
				//
				// MultipleOutputs.addNamedOutput(job, "loginCount",
				// TextOutputFormat.class,
				// Text.class, Text.class);
				FileInputFormat.addInputPath(job, new Path("/carmulti/users/users0000/part-m-00000"));
				FileInputFormat.addInputPath(job, new Path("/carmulti/users/users0001/part-m-00000"));
				FileInputFormat.addInputPath(job, new Path("/carmulti/users/users0002/part-m-00000"));
				FileInputFormat.addInputPath(job, new Path("/carmulti/users/users0003/part-m-00000"));

				Path dstPath = new Path("/output_log/output_user_month" + d);
				FileSystem dhfs = dstPath.getFileSystem(HdfsUtil.getConfiguration());
				if (dhfs.exists(dstPath)) {
					dhfs.delete(dstPath, true);
				}
				FileOutputFormat.setOutputPath(job, new Path("/output_log/output_user_month" + d));
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
		AreaUserTask starCar = new AreaUserTask();
		starCar.run();

	}
}
