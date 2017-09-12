package com.spark.antireptile;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;

import com.bean.LogMap;
import com.kafka.KafkaUtil;
import com.util.DateUtil;
import com.util.LogFilter;
import com.util.LogUtil;
import com.util.StringUtil;

import scala.Tuple2;

public final class IpblackOffLine {
	private static final Pattern SPACE = Pattern.compile(" ");
	final static String ipblack_topic = "ipblack";

	public static void main(String[] args) throws Exception {
		SparkConf sparkConf = new SparkConf();
//		 sparkConf.set("spark.cores.max", "6");
//		 sparkConf.set("spark.executor.memenory", "2g");
		sparkConf.setMaster("spark://10.90.60.206:7077");
//		sparkConf.set("spark.submit.deployMode", "cluster");
		sparkConf.setAppName("IpblackOffLine");
		SparkSession spark = SparkSession.builder().config(sparkConf).getOrCreate();
		
		String hdfsurl = "hdfs://mycluster/data_log/web_all/" + DateUtil.date2Str(DateUtil.getDayBefore(1))
				+ "/web_access.log";

		JavaRDD<String> lines = spark.read().textFile(hdfsurl).javaRDD();
//				.flatMap(new FlatMapFunction<String, String>() {
//
//			@Override
//			public Iterator<String> call(String arg0) throws Exception {
//				return Arrays.asList(SPACE.split(arg0)).iterator();
//			}
//		});

		// 过滤

		JavaRDD<LogMap> LogMapList = lines.map(new Function<String, LogMap>() {

			@Override
			public LogMap call(String line) throws Exception {

				LogMap map = LogUtil.strToLog(line);
				return map;
			}
		}).filter(new Function<LogMap, Boolean>() {

			@Override
			public Boolean call(LogMap map) throws Exception {
				// boolean falg = false;

				try {
					if (map.getAccessUrl() == null || map.getAccessUrl().indexOf(".json") > 0
							|| map.getAccessUrl().indexOf("upload.htm") > 0)
						return false;
					if (!"".equals(map.getAccessUrl()) && LogFilter.getBot2(map.getAccessType()) == 0) {
						// 生成过滤后的日志
						return true;
					}
					// }
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

				}

				return false;
			}

		});

		LogMapList.cache();
		// ----------------------------------------------------------------------------------
		JavaPairRDD<String, Integer> wordCounts = LogMapList.mapToPair(new PairFunction<LogMap, String, Integer>() {
			@Override
			public Tuple2<String, Integer> call(LogMap map) {
				// String[] lines = line.split("\\|");

				// LogMap map = LogUtil.strToLog(line);
				if (map != null) {
					try {
						String ip = map.getFromIp();
						if (ip != null && !ip.isEmpty()) {

							return new Tuple2<>(ip, 1);
						}
					} catch (Exception e) {

						// TODO Auto-generated catch block
						e.printStackTrace();

					}
				}
				return new Tuple2<>("", 1);
			}
		});
		JavaPairRDD<String, Integer> reduce = wordCounts.reduceByKey(new Function2<Integer, Integer, Integer>() {
			@Override
			public Integer call(Integer i1, Integer i2) {
				return i1 + i2;
			}
		}).filter(new Function<Tuple2<String, Integer>, Boolean>() {

			@Override
			public Boolean call(Tuple2<String, Integer> arg0) throws Exception {
				if (StringUtil.isEmpty(arg0._1)) {
					return false;
				}
				if (arg0._2 >= 10000)
					return true;
				return false;
			}
		});
		// reduce.print();
//		String suffix = "log";
//		String prefix = "/ipblack/500/" + DateUtil.date2Str(new Date()) + "/";
//		reduce.saveAsTextFile(prefix);
		reduce.foreachAsync(new VoidFunction<Tuple2<String, Integer>>() {
			@Override
			public void call(Tuple2<String, Integer> arg0) throws Exception {
				if (arg0._2 >= 10000) {
					 KafkaUtil.sendMessage("ip_0:" + arg0._1+","+arg0._2(), ipblack_topic);
//					System.out.println(arg0._1() + ": " + arg0._2());
				}
			}
		});
		spark.stop();// 开始计算
	}
}