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
import com.constants.Constant;
import com.util.DateUtil;
import com.util.HdfsUtil;
import com.util.LogUtil;
import com.util.StringUtil;

/**
 * 车辆展示数据获得 date: 2017年4月20日 上午9:17:52
 * 
 * @author geyang
 */
public class DayTask {

	public static void main(String[] args) {
		Thread thread = new Thread(new execute());
		thread.start();
	}

	public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable> {
		private Text word = new Text();
		private final static IntWritable one = new IntWritable(1);

		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

			LogMap map = LogUtil.strToLog(value.toString());
			if (map == null) {
				return;
			}

			String line = map.getBuildOriginalURL();
			String ip = map.getFromIp();
			String appmobile = map.getMobile();
			String access = map.getAccessUrl();

			if (line.contains("carlog.json")&&line.contains("ids=")) {

				String ids = StringUtils.substringAfter(map.getBuildOriginalURL(), "ids=");

				ids = StringUtils.substringBefore(ids, "&");

				String[] idStrings = ids.split(",");
				for (int i = 0; i < idStrings.length; i++) {
					if (StringUtils.isNotEmpty(idStrings[i])) {
						context.write(new Text("carlog_"+idStrings[i] + "|"), one);
					}
				}

			} else if (line.contains("carlog.json") && line.contains("carid")) {

				try {
					String para = StringUtils.substringAfter(map.getBuildOriginalURL(), "carlog.json?");
					para = StringUtils.substringBefore(para, "=&");

					if (StringUtils.isNoneBlank(para)) {
						context.write(new Text("carlog_para|"+para), one);

					}
				} catch (Exception e) {
					
					// TODO Auto-generated catch block
					e.printStackTrace();
					
				}

			}
			//wap
               if (access.contains("m.hx2car.com")&&StringUtils.isNoneBlank(appmobile)) {
            	   word.set("wappc_wap_" + appmobile+"&");
					context.write(word, one);
            	   //pc
			   }else if (!line.contains("appmobile=")&&StringUtils.isNoneBlank(appmobile)) {
				   word.set("wappc_pc_" + appmobile+"&");
					context.write(word, one);
			}
			// 分享资讯---------------------------------------------------------------
			if (line.contains(Constant.news_url)) {

				try {
					String mobile = line.substring(line.indexOf("actMobile=") + 10, line.indexOf("actMobile=") + 21);
					if (mobile != null && StringUtil.checkPhone(mobile) && ip != null && !"".equals(ip)) {
						// if (!newsMap.containsKey(mobile + "|" + ip)) {
						// newsMap.put(mobile + "|" + ip, mobile);
						// }
						word.set(mobile + "|" + ip + "_news_url&");
						context.write(word, one);
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
				}
				// 详情点击量----------------------------------------------------------------
			} else if (line.contains(Constant.pc_url) && line.contains("id=")) {

				String idurl = line.substring(line.indexOf("id=") + 3, line.length());
				if (idurl.contains("&")) {
					idurl = idurl.substring(0, idurl.indexOf("&"));
				}
				if (idurl != null && !"".equals(idurl)) {
					word.set("pc_url_" + idurl + "&");
					context.write(word, one);
					if (StringUtils.isNoneBlank(appmobile)) {
						word.set("appvisit_" + appmobile + "&"+ idurl + "&");
						context.write(word, one);
					}
				}
			} else if (line.contains(Constant.app_url) && line.contains("id=")) {

				String idurl = line.substring(line.indexOf("id=") + 3, line.length());
				if (idurl.contains("&")) {
					idurl = idurl.substring(0, idurl.indexOf("&"));
				}
				if (idurl != null && !"".equals(idurl)) {
					word.set("app_url_" + idurl + "&");
					context.write(word, one);
					if (StringUtils.isNoneBlank(appmobile)) {
						word.set("appvisit_" + appmobile + "&"+ idurl + "&");
						context.write(word, one);
					}
				}

			} else if (line.contains(Constant.wap_url) && line.contains("id=")) {
				String idurl = line.substring(line.indexOf("id=") + 3, line.length());
				if (idurl.contains("&")) {
					idurl = idurl.substring(0, idurl.indexOf("&"));
				}
				if (idurl != null && !"".equals(idurl)) {
					word.set("wap_url_" + idurl + "&");
					context.write(word, one);
					if (StringUtils.isNoneBlank(appmobile)) {
						word.set("appvisit_" + appmobile + "&"+ idurl + "&");
						context.write(word, one);
					}
				}

				// 门店----------------------------------------------------------------
			} else if (line.contains(Constant.store_url)) {
				try {
					String idurl = "";
					if (line.contains("oname=")) {
						idurl = line.substring(line.indexOf("oname=") + 6, line.length());
						if (idurl.contains("&")) {
							idurl = idurl.substring(0, idurl.indexOf("&"));
						}
					} else {
						idurl = line.substring(line.indexOf("vip.hx2car.com/") + 15, line.length());
						if (idurl.contains("/")) {
							idurl = idurl.substring(0, idurl.indexOf("/"));
						}
					}

					if (idurl != null && !"".equals(idurl)) {
						word.set("store_url_" + idurl + "&");
						context.write(word, one);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
	}

	public static class IntSumReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
		private MultipleOutputs<Text, IntWritable> out;
		protected void setup(Context context) throws IOException, InterruptedException {
			out = new MultipleOutputs<Text, IntWritable>(context);
		}

		public void reduce(Text key, Iterable<IntWritable> values, Context context)
				throws IOException, InterruptedException {
			String k = key.toString();
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			 if (k.contains("carlog_para")) {
				out.write("carlogpara", k.replace("carlog_para|", ""),"");
			}else 
				if (k.contains("carlog_")) {
				out.write("carshow", k.replace("carlog_", ""), sum);
			}else if (k.contains("appvisit_")) {
				out.write("appvisit", k.replace("appvisit_", ""), sum);
			}else if (k.contains("wappc_")) {
				out.write("wappc", k, sum);
			}else {
				out.write("visitcount", k, sum);
			}

		}

		protected void cleanup(Context context) throws IOException, InterruptedException {
			out.close();
		}
	}

	public static class execute implements Runnable {
		public void run() {

			try {
				Job job = HdfsUtil.getJob(DayTask.class.getSimpleName(), null);
				SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
				String d = sd.format(DateUtil.getSpecifiedDayBefore());

				job.setMapOutputValueClass(IntWritable.class);
				job.setJarByClass(DayTask.class);
				job.setMapperClass(TokenizerMapper.class);
				job.setReducerClass(IntSumReducer.class);
				// job.setCombinerClass(IntSumReducer.class);
				job.setOutputKeyClass(Text.class);
				job.setOutputValueClass(IntWritable.class);
				 MultipleOutputs.addNamedOutput(job, "carshow",TextOutputFormat.class, Text.class, IntWritable.class);
				 MultipleOutputs.addNamedOutput(job, "visitcount",TextOutputFormat.class, Text.class, IntWritable.class);
				 MultipleOutputs.addNamedOutput(job, "appvisit",TextOutputFormat.class, Text.class, IntWritable.class);
				 MultipleOutputs.addNamedOutput(job, "carlogpara",TextOutputFormat.class, Text.class, Text.class);
				 MultipleOutputs.addNamedOutput(job, "wappc",TextOutputFormat.class, Text.class, Text.class);

				 
				 ArrayList<String> urList = HdfsUtil.getLogAccessByDay(1);
				for (String url : urList) {
					FileInputFormat.addInputPath(job, new Path(url));
				}
				Path dstPath = new Path("/output_log/output_log_daytask" + d);
				FileSystem dhfs = dstPath.getFileSystem(HdfsUtil.getConfiguration());
				if (dhfs.exists(dstPath)) {
					dhfs.delete(dstPath, true);
				}
				FileOutputFormat.setOutputPath(job, new Path("/output_log/output_log_daytask" + d));
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
