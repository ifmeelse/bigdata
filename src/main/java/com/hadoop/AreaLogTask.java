package com.hadoop;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
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
import com.util.DateUtil;
import com.util.HdfsUtil;
import com.util.LogUtil;
import com.util.StringUtil;

/**
 * rizhi date: 2017年4月20日 上午9:17:52
 * 
 * @author geyang
 */
public class AreaLogTask {

	private static final Logger logger = Logger.getLogger(AreaLogTask.class);

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
//				String[] lines = uline.split("\\|");
				LogMap map = LogUtil.strToLog(value.toString());
				if (map == null) {
					return;
				}	
				    String line =map.getBuildOriginalURL();
					if (line.contains("search.htm")&&line.contains("more=")) {
//						 line=StringUtils.substringAfter(line, "more=");
//							area_code=area_code.substring(1, 7);
							if (line.contains("more=")) {
								int indexstart = line.lastIndexOf("more=") + 5;
								String parame = line.substring(indexstart, line.length());
								int indexend = parame.indexOf("&");
								if (parame.contains("/")) {
									int ind2 = parame.indexOf("/");
									if (ind2 < indexend) {
										indexend = ind2;
									}
								}
								String parametersmore = parame.substring(0, indexend);
								// System.out.println(parametersmore);
								List<String> volist = new ArrayList<String>();
								int t_i = 0;
								if (parametersmore != null && !"".equalsIgnoreCase(parametersmore)) {
									for (int i = 0; i < parametersmore.length(); i++) {
										String temp_i = String.valueOf(parametersmore.charAt(i));
										if (temp_i.matches("[a-zA-Z]*")) {
											if (i > 0) {
												volist.add(parametersmore.substring(t_i, i));
											}
											t_i = i;
										}
										if (i == parametersmore.length() - 1) {
											volist.add(parametersmore.substring(t_i, parametersmore.length()));
										}
									}

									for (int i = 0; i < volist.size(); i++) {
										String temp_i = String.valueOf(volist.get(i).charAt(0));
										 if (temp_i != null && temp_i.equalsIgnoreCase("l")
												&& !volist.get(i).equalsIgnoreCase("l")) {
					                     	context.write(new Text("areacode_search_"+volist.get(i).replace("l", "")), new IntWritable(1));
											
										} 
									}
								}
							}
							
						}
//					String areacode = map.getCity();
//					String ip = map.getFromIp();
//					String loginName = map.getUserId().substring(17, map.getUserId().length());
//					context.write(new Text("areacode_pv"+areacode), new IntWritable(1));
//					context.write(new Text("ip_"+ip), new IntWritable(1));
//					context.write(new Text("loginCount_"+loginName+"_"+areacode), new IntWritable(1));
//					context.write(new Text("areacode_uv_"+ip+"_"+areacode), new IntWritable(1));
					
					
			} catch (Exception e) {

				// TODO Auto-generated catch block
				e.printStackTrace();

			}
		}
	}

	public static class IntSumReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
		 private MultipleOutputs<Text, IntWritable> out;

		 protected void setup(Context context) throws IOException,
		 InterruptedException {
		 out = new MultipleOutputs<Text, IntWritable>(context);
		 }

		public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
			 String k = key.toString();
			 int sum=0;
				for (IntWritable val : values) {
					sum += val.get();
				}
				
				if (k != null && k.contains("areacode_search_")) {
					 out.write("areacodesearch",k.replace("areacode_search_", ""), sum);
				 }
//			 else if (k != null && k.contains("areacode_pv")) {
//				 out.write("areacodepv",k.replace("areacode_pv", ""), sum);
//			 }else if(k != null && k.contains("areacode_uv_")){
//				 out.write("areacodeuv",k.replace("areacode_uv_", ""), sum); 
//			}else if(k != null && k.contains("loginCount_")){
//				 out.write("loginCount",k.replace("loginCount_", ""), sum); 
//			}else{
//				 out.write("ip",k.replace("ip_", ""), sum); 
//			 }


		}

		 protected void cleanup(Context context) throws IOException,
		 InterruptedException {
		 out.close();
		 }
	}

	public class execute implements Runnable {
		public void run() {

			try {
//			Job	job=HdfsUtil.getJob("area_log");
//				Configuration conf = new Configuration();
//				conf.set("fs.defaultFS", "hdfs://mycluster");
//				conf.set("dfs.nameservices", "mycluster");
//				conf.set("dfs.ha.namenodes.mycluster", "nn1,nn2");
//				conf.set("dfs.namenode.rpc-address.mycluster.nn1", "app206:9000");
//				conf.set("dfs.namenode.rpc-address.mycluster.nn2", "app205:9000");
//				conf.set("dfs.client.failover.proxy.provider.mycluster",
//						"org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider");
//				conf.set("mapreduce.reduce.shuffle.input.buffer.percent", "0.01");
//				conf.set("mapreduce.reduce.shuffle.parallelcopies", "1");//	Reduce Task启动的并发拷贝数据的线程数目
//				conf.set("mapreduce.reduce.shuffle.memory.limit.percent", "0.01");
//				conf.set("yarn.app.mapreduce.am.staging-dir", "/app/soft/hadoop-2.6.0/hadoop-yarn/staging");
//				Job job = Job.getInstance(conf, "area_log");
				Configuration conf=HdfsUtil.getConfiguration();
				conf.set("mapreduce.reduce.shuffle.input.buffer.percent", "0.01");
				conf.set("mapreduce.reduce.shuffle.parallelcopies", "1");// Reduce
//																			// Task启动的并发拷贝数据的线程数目
				conf.set("mapreduce.reduce.shuffle.memory.limit.percent", "0.01");
				Job job = HdfsUtil.getJob(AreaLogTask.class.getSimpleName(),conf);
				SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
				String d = sd.format(DateUtil.getSpecifiedDayBefore());
				// String d ="2017-03-12";
				job.setJarByClass(AreaLogTask.class);
				job.setMapperClass(TokenizerMapper.class);
				job.setReducerClass(IntSumReducer.class);
				// job.setCombinerClass(IntSumReducer.class);
				job.setOutputKeyClass(Text.class);
				job.setOutputValueClass(IntWritable.class);
				// job.setNumReduceTasks(1);
				// FileInputFormat.addInputPaths(job,
				// "/carmulti/cars/cars0000/part-m-00000,/carmulti/cars/cars0001/part-m-00000,/carmulti/cars/cars0002/part-m-00000,/carmulti/cars/cars0003/part-m-00000");;
				for (String url : HdfsUtil.getLogAccessByDay(30)) {

					FileInputFormat.addInputPath(job, new Path(url));
				}
				MultipleOutputs.addNamedOutput(job, "areacodepv", TextOutputFormat.class,
						Text.class, Text.class);
				MultipleOutputs.addNamedOutput(job, "ip", TextOutputFormat.class,
						Text.class, Text.class);
				MultipleOutputs.addNamedOutput(job, "areacodeuv", TextOutputFormat.class,
						Text.class, Text.class);
				
				MultipleOutputs.addNamedOutput(job, "loginCount", TextOutputFormat.class,
						Text.class, Text.class);
				MultipleOutputs.addNamedOutput(job, "areacodesearch", TextOutputFormat.class,
						Text.class, Text.class);
				Path dstPath = new Path("/output_log/output_log_month" + d);
				FileSystem dhfs = dstPath.getFileSystem(HdfsUtil.getConfiguration());
				if (dhfs.exists(dstPath)) {
					dhfs.delete(dstPath, true);
				}
				FileOutputFormat.setOutputPath(job, new Path("/output_log/output_log_month" + d));
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
		AreaLogTask starCar = new AreaLogTask();
		starCar.run();

	}
}
