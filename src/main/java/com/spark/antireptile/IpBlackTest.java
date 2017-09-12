package com.spark.antireptile;

import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.CanCommitOffsets;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.HasOffsetRanges;
//import org.apache.spark.streaming.kafka.KafkaUtils;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import org.apache.spark.streaming.kafka010.OffsetRange;

import com.bean.LogMap;
import com.util.DateUtil;
import com.util.LogFilter;
import com.util.LogUtil;
import com.util.StringUtil;

import scala.Tuple2;

public final class IpBlackTest {
	private static final Pattern SPACE = Pattern.compile("	");
	private static final Pattern SPACE2 = Pattern.compile(",");

	public static void main(String[] args) throws Exception {

		String brokers = "10.90.60.206:9092,10.90.60.205:9092,10.90.60.204:9092";
		String groupid = "webaccess_IpBlackReferer";
		Collection<String> topics = Arrays.asList("webaccess");

		SparkConf sparkConf = new SparkConf();
		sparkConf.setAppName("IpBlackReferer");
		sparkConf.setMaster("spark://10.90.60.206:7077");
		sparkConf.set("spark.submit.deployMode", "cluster");
		// sparkConf.set("spark.cores.max", "2");
		// sparkConf.set(" spark.executor.memenory", "1g");
		sparkConf.set("spark.scheduler.mode", "FAIR");
		JavaSparkContext conf = new JavaSparkContext(sparkConf);

		JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(60));

		Map<String, Object> kafkaParams = new HashMap<String, Object>();
		kafkaParams.put("bootstrap.servers", brokers);
		kafkaParams.put("key.deserializer", StringDeserializer.class);
		kafkaParams.put("value.deserializer", StringDeserializer.class);
		kafkaParams.put("group.id", groupid);
		kafkaParams.put("auto.offset.reset", "latest");
		kafkaParams.put("auto.commit.interval.ms", "1000");
		kafkaParams.put("enable.auto.commit", true);
		kafkaParams.put("partition.assignment.strategy", "org.apache.kafka.clients.consumer.RangeAssignor");
		// kafkaParams.put("request.timeout.ms", "305000");
		// kafkaParams.put("heartbeat.interval.ms", "85000");
		// kafkaParams.put("session.timeout.ms", "90000");
		// Create direct kafka stream with brokers and topics
		final JavaInputDStream<ConsumerRecord<String, String>> stream = KafkaUtils.createDirectStream(jssc,
				LocationStrategies.PreferConsistent(),
				ConsumerStrategies.<String, String> Subscribe(topics, kafkaParams));

		// stream.foreachRDD(new VoidFunction<JavaRDD<ConsumerRecord<String,
		// String>>>() {
		// public void call(JavaRDD<ConsumerRecord<String, String>> rdd) {
		// OffsetRange[] offsetRanges = ((HasOffsetRanges)
		// rdd.rdd()).offsetRanges();
		//
		// // some time later, after outputs have completed
		// ((CanCommitOffsets) stream.inputDStream()).commitAsync(offsetRanges);
		// }
		// });
		JavaDStream<String> words = stream.flatMap(new FlatMapFunction<ConsumerRecord<String, String>, String>() {
			@Override
			public Iterator<String> call(ConsumerRecord<String, String> x) throws Exception {
				// TODO Auto-generated method stub
				return Arrays.asList(SPACE.split(x.value())).iterator();
			}
		});

		// 过滤
		JavaDStream<String> accesslogs = words.filter(new Function<String, Boolean>() {

			@Override
			public Boolean call(String line) throws Exception {
				boolean falg = false;

				try {
					if (StringUtil.isEmpty(line)) {
						falg = false;
					} else {
						String[] lines = line.split("\\|");
						if (lines.length != 14) {
							falg = false;// 放弃该条数据
						}
						LogMap logMap = new LogMap();

						if (lines[7] == null || lines[7].indexOf("getuploadPic.json") > 0)
							falg = false;
						logMap.setAccessUrl(lines[7]);
						if (lines[9] == null)
							falg = false;
						logMap.setAccessType(lines[9]);

						if (!"".equals(logMap.getAccessUrl()) && LogFilter.getBot(logMap.getAccessType()) == 0) {
							// 生成过滤后的日志
							falg = true;
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

				}

				return falg;
			}

		});

		JavaDStream<LogMap> LogMapList = accesslogs.map(new Function<String, LogMap>() {

			@Override
			public LogMap call(String line) throws Exception {

				LogMap map = LogUtil.strToLog(line);
				return map;
			}
		});

		LogMapList.cache();
		// //30分钟同一个ip请求数超100，referer都是为空或者一个固定值的情况
		JavaPairDStream<String, String> ipCounts = LogMapList.mapToPair(new PairFunction<LogMap, String, String>() {
			@Override
			public Tuple2<String, String> call(LogMap map) {
				// String[] lines = line.split("\\|");

				// LogMap map = LogUtil.strToLog(line);
				if (map != null) {

					try {
						String ip = map.getFromIp();
						if (ip != null && !ip.isEmpty()) {
//							if (StringUtil.isEmpty(map.getFromUrl())) {
								return new Tuple2<>(ip, map.getFromUrl().trim());
//							}
						}
					} catch (Exception e) {

						// TODO Auto-generated catch block
						e.printStackTrace();

					}
				}
				return new Tuple2<>("", "");
			}
		});

		JavaPairDStream<String, Iterable<String>> ipCountsreduce = ipCounts
				.groupByKeyAndWindow(Durations.seconds(60 * 30))
				.filter(new Function<Tuple2<String, Iterable<String>>, Boolean>() {

					int ipcount = 0;
					int ipcount1 = 0;
					String acc = "";

					public Boolean call(Tuple2<String, Iterable<String>> arg0) throws Exception {

						
						if (StringUtil.isEmpty(arg0._1)) {
							return false;
						}
						
						arg0._2.forEach((entry) -> {

							if (StringUtil.isEmpty(entry)) {
								ipcount++;
							} else {
								ipcount = 0;
							}
							
							if (acc.equals(entry)) {
								ipcount1++;
							} else {
								acc = entry;
								ipcount1 = 0;
							}

						});
						if (ipcount >= 100||ipcount1 >= 100) {
							 ipcount = 0;
							 ipcount1 = 0;
							 acc = "";
							return true;
						}
						 ipcount = 0;
						 ipcount1 = 0;
						 acc = "";
						return false;

					}
				});

		ipCountsreduce.print();
		jssc.start();// 开始计算
		jssc.awaitTermination();// 等待计算结束
		// jssc.stop();
	}
}