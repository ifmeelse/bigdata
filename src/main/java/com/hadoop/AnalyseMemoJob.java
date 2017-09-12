package com.hadoop;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
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



public class AnalyseMemoJob {
//	public void run() {
//		Thread thread = new Thread(new execute());
//		thread.start();
//		// logger.error("MrUrl0");
//	}
	static ArrayList<String> botList = new ArrayList<String>();
	static String regexPhone = "mobile/";
	static Pattern pat = Pattern.compile(regexPhone);
	static ArrayList<String> phoneList = new ArrayList<String>();

	public static class MemoMap extends Mapper<Object, Text, Text, Text> {
		private Text word = new Text();
		private Text text = new Text();

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			LogMap map = LogUtil.strToLog(value.toString());
			if (map == null) {
				return;
			}
			String loginName = map.getUserId();
			
//			if (loginName!=null&&!"".equals(loginName)) {
//				loginName=loginName.substring(17, loginName.length());
//			}
			//用户登陆统计
			if (loginName!=null&&!"".equals(loginName)) {
				word.set("loginCount_"+loginName);
				text.set(loginName);
				context.write(word, text);
			}
			String url = map.getAccessUrl();
			Matcher mat = pat.matcher(url);
			if (!"".equals(url) && AnalyseJob.getBot(map.getAccessType()) == 0) {
				if (!mat.find()
						&& AnalyseJob.getPhone(map.getAccessType()) != 1) {
					if (map.getFromIp() != null && !"".equals(map.getFromIp())) {
						word.set("Memo" + map.getFromIp() + "$$");
						text.set(map.getCountry() + "|" + map.getProvince()
								+ "|" + map.getCity() + "|" + map.getArea()
								+ "|" + map.getCompany() + "|"
								+ map.getAccessType());
						context.write(word, text);
					}
					if (map.getUniqueCookie() != null
							&& !"".equals(map.getUniqueCookie())
							&& !"0".equals(map.getUniqueCookie())) {
						word.set("normalPageIp");
						text.set(map.getFromIp());
						context.write(word, text);
					}
				} else if (AnalyseJob.getPhone(map.getAccessType()) == 1) {
					if (map.getUniqueCookie() != null
							&& !"".equals(map.getUniqueCookie())
							&& !"0".equals(map.getUniqueCookie())) {
						word.set("normalphoneIp");
						text.set(map.getFromIp());
						context.write(word, text);
					}
				}
			}
		}
	}

	public static class IntSummemoReducer extends
			Reducer<Text, Text, Text, Text> {
		private MultipleOutputs<Text, Text> out;

		protected void setup(Context context) throws IOException,
				InterruptedException {
			out = new MultipleOutputs<Text, Text>(context);
		}

		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			Text text = new Text();
			Set<String> setlist = new HashSet<String>();
			String s = null;
			int a = 0;
			for (Text val : values) {
				s = val.toString();
				a++;
				setlist.add(val.toString());
			}
			if (key.toString().indexOf("loginCount_") != -1) {
					out.write("loginCount", key.toString().replace("loginCount_", ""),
							new Text(""));
			}
			if (key.toString().indexOf("Memo") != -1) {
				text.set(s);
				if (a > 500) {
					out.write("ipMemo", key.toString().replace("Memo", ""),
							text);
				}
			} else if (key.toString().indexOf("normalPageIp") != -1
					|| key.toString().indexOf("normalphoneIp") != -1) {
				String str = "";
				Iterator<String> it = setlist.iterator();
				while (it.hasNext()) {
					str += "\n" + it.next();
				}
				text.set(str);
				out.write("nomalTotal", key, text);
			}
		}

		protected void cleanup(Context context) throws IOException,
				InterruptedException {
			out.close();
		}
	}


	public static class execute implements Runnable {
		public void run() {

			try {
//				Configuration conf = new Configuration();
//				conf.set("fs.defaultFS", "hdfs://mycluster");
//			    conf.set("dfs.nameservices", "mycluster");
//			    conf.set("dfs.ha.namenodes.mycluster", "nn1,nn2");
//				conf.set("dfs.namenode.rpc-address.mycluster.nn1", "app206:9000");
//				conf.set("dfs.namenode.rpc-address.mycluster.nn2", "app205:9000");
//				conf.set("dfs.client.failover.proxy.provider.mycluster", "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider");
//			
//				Job job = Job.getInstance(conf, "AnalyseMemoJob");
				Configuration conf=HdfsUtil.getConfiguration();
				Job job = HdfsUtil.getJob(AnalyseMemoJob.class.getSimpleName(),conf);
				SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
				String d = sd.format(DateUtil.getSpecifiedDayBefore(-1));
				job.setJarByClass(AnalyseMemoJob.class);
				job.setMapperClass(MemoMap.class);
				job.setReducerClass(IntSummemoReducer.class);
				job.setOutputKeyClass(Text.class);
				job.setOutputValueClass(Text.class);
				Path dstPath = new Path("/output_log/memo_output_log" + d);
				FileSystem dhfs = dstPath.getFileSystem(conf);
				if (dhfs.exists(dstPath)) {
					dhfs.delete(dstPath, true);
				}
				MultipleOutputs.addNamedOutput(job, "ipMemo", TextOutputFormat.class,
						Text.class, Text.class);
				MultipleOutputs.addNamedOutput(job, "nomalTotal",
						TextOutputFormat.class, Text.class, Text.class);
				MultipleOutputs.addNamedOutput(job, "loginCount",
						TextOutputFormat.class, Text.class, Text.class);
//				FileInputFormat.addInputPath(job, new Path(
//						"/data_log/web_today/web_access.log"));
				ArrayList<String> urList = HdfsUtil.getLogAccessByDay(1);
				for (String url : urList) {
					FileInputFormat.addInputPath(job, new Path(url));
				}
				FileOutputFormat.setOutputPath(job, new Path(
						"/output_log/memo_output_log" + d));
//				System.exit(job.waitForCompletion(true) ? 0 : 1);
				job.waitForCompletion(true);
			} catch (Exception e) {

				// TODO Auto-generated catch block
				e.printStackTrace();

			}

		}

		// }
	}
	public static void main(String[] args) {
		Thread thread = new Thread(new execute());
		thread.start();
	}
}
