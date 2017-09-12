package com.spark.antireptile;

import java.io.FileWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
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
import org.apache.spark.sql.functions;
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
import org.apache.twill.internal.json.ArgumentsCodec;

import com.bean.LogMap;
import com.kafka.KafkaUtil;
import com.util.DateUtil;
import com.util.LogFilter;
import com.util.LogUtil;
import com.util.StringUtil;

import scala.Tuple2;

public final class IpBlack {
	private static final Pattern SPACE = Pattern.compile("	");

	public static void main(String[] args) throws Exception {

		String brokers = "10.90.60.206:9092,10.90.60.205:9092,10.90.60.204:9092";
		String groupid = "webaccess_IpBlack";
		Collection<String> topics = Arrays.asList("webaccess");
		final String ipblack_topic = "ipblack";
		SparkConf sparkConf = new SparkConf();
		sparkConf.setAppName("IpBlack");
		sparkConf.setMaster("spark://10.90.60.205:7077,10.90.60.206:7077");
		sparkConf.set("spark.submit.deployMode", "cluster");
		 sparkConf.set("spark.cores.max", "2");
		 sparkConf.set("spark.executor.memenory", "1g");
		sparkConf.set("spark.scheduler.mode", "FAIR");

		JavaSparkContext conf = new JavaSparkContext(sparkConf);

		JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(60));

		Map<String, Object> kafkaParams = new HashMap<String, Object>();
		kafkaParams.put("bootstrap.servers", brokers);
		kafkaParams.put("key.deserializer", StringDeserializer.class);
		kafkaParams.put("value.deserializer", StringDeserializer.class);
		kafkaParams.put("group.id", groupid);
		kafkaParams.put("auto.offset.reset", "latest");
		// kafkaParams.put("auto.commit.interval.ms", "1000");
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

		JavaDStream<LogMap> LogMapList = words.map(new Function<String, LogMap>() {

			@Override
			public LogMap call(String line) throws Exception {

				LogMap map = LogUtil.strToLog(line);
				return map;
			}
		}).filter(new Function<LogMap, Boolean>() {

			@Override
			public Boolean call(LogMap map) throws Exception {
//				boolean falg = false;

				try {
						if (map.getAccessUrl() == null || map.getAccessUrl().indexOf(".json") > 0
								|| map.getAccessUrl().indexOf("upload.htm") > 0|| map.getAccessUrl().indexOf("carlog.json") > 0)
							return  false;
						if (!"".equals(map.getAccessUrl()) && LogFilter.getBot2(map.getAccessType()) == 0) {
							// 生成过滤后的日志
							return true;
						}
//					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

				}

				return false;
			}

		});

		LogMapList.cache();
		// ----------------------------------------------------------------------------------
		// //同一ip10分钟内请求数超500且频率比较固定：
		JavaPairDStream<String, String> wordCounts = LogMapList.mapToPair(new PairFunction<LogMap, String, String>() {
			@Override
			public Tuple2<String, String> call(LogMap map) {
				// String[] lines = line.split("\\|");

				// LogMap map = LogUtil.strToLog(line);
				if (map != null) {
					try {
						String ip = map.getFromIp();
						if (ip != null && !ip.isEmpty()) {

							return new Tuple2<>(ip,map.getBuildOriginalURL());
						}
					} catch (Exception e) {

						// TODO Auto-generated catch block
						e.printStackTrace();

					}
				}
				return new Tuple2<>(" ", "");
			}
		}).transformToPair(new Function<JavaPairRDD<String,String>, JavaPairRDD<String,String>>() {

			@Override
			public JavaPairRDD<String,String> call(JavaPairRDD<String, String> arg0) throws Exception {
				
				// TODO Auto-generated method stub
				return arg0.distinct();
			}
		});
		JavaPairDStream<String, Iterable<String>> reduce = wordCounts
				.groupByKeyAndWindow( Durations.seconds(60 * 10))
				.filter(new Function<Tuple2<String,  Iterable<String>>, Boolean>() {

					int i=0;
					public Boolean call(Tuple2<String,  Iterable<String>> arg0) throws Exception {
						if (StringUtil.isEmpty(arg0._1)) {
							return false;
						}
					
						arg0._2.forEach((k)->{
							i++;
						});
						if (i >= 500)
						{
							i=0;
							return true;
						}
						i=0;
						return false;
					}
				});
//		reduce.print();
		reduce.foreachRDD(new VoidFunction<JavaPairRDD<String, Iterable<String>>>() {

			@Override
			public void call(JavaPairRDD<String, Iterable<String>> arg0) throws Exception {

				arg0.collect().parallelStream().forEach((entry) -> {
					KafkaUtil.sendMessage("ip_1:" + entry._1, ipblack_topic);
				});

			}
		});
		// 相同agent在10分钟内请求超过500次 存储forbid_agent表
		JavaPairDStream<String, Integer> ipagents = LogMapList.mapToPair(new PairFunction<LogMap, String, Integer>() {
			@Override
			public Tuple2<String, Integer> call(LogMap map) {
				// String[] lines = line.split("\\|");

				// LogMap map = LogUtil.strToLog(line);
				if (map != null) {

					try {
						String agent = map.getAccessType();
						if (!StringUtil.isEmpty(agent)) {
							return new Tuple2<>(agent, 1);
						}
					} catch (Exception e) {

						// TODO Auto-generated catch block
						e.printStackTrace();

					}
				}
				return new Tuple2<>("", 1);
			}
		});

		JavaPairDStream<String, Integer> reduceipagents = ipagents
				.reduceByKeyAndWindow(new Function2<Integer, Integer, Integer>() {
					@Override
					public Integer call(Integer i1, Integer i2) {
						return i1 + i2;
					}
				}, Durations.seconds(60 * 10)).filter(new Function<Tuple2<String, Integer>, Boolean>() {

					@Override
					public Boolean call(Tuple2<String, Integer> arg0) throws Exception {
						if (StringUtil.isEmpty(arg0._1)) {
							return false;
						}
						if (arg0._2.intValue() >=1000) {
							return true;
						}
						return false;

					}
				});
		;
//		reduceipagents.print();
		reduceipagents.foreachRDD(new VoidFunction<JavaPairRDD<String, Integer>>() {

			@Override
			public void call(JavaPairRDD<String, Integer> arg0) throws Exception {
				arg0.collect().forEach((entry)->{
					KafkaUtil.sendMessage("agent_1:" + entry._1, ipblack_topic);
				});

			}
		});
		// --------------------------------------------------------------------
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
		}).transformToPair(new Function<JavaPairRDD<String,String>, JavaPairRDD<String,String>>() {

			@Override
			public JavaPairRDD<String,String> call(JavaPairRDD<String, String> arg0) throws Exception {
				
				// TODO Auto-generated method stub
				return arg0.distinct();
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

//		ipCountsreduce.print();
		ipCountsreduce.foreachRDD(new VoidFunction<JavaPairRDD<String, Iterable<String>>>() {

			@Override
			public void call(JavaPairRDD<String, Iterable<String>> arg0) throws Exception {

				arg0.collect().parallelStream().forEach((entry) -> {
					KafkaUtil.sendMessage("ip_2:" + entry._1, ipblack_topic);
				});

			}
		});
		// String prefix1 = "/ipblack/100/" + DateUtil.date2Str(new Date()) +
		// "/";
		// ipCountsreduce.dstream().repartition(1).saveAsTextFiles(prefix1,
		// suffix);
		// -----------------------------------------------------------------------------------
		// 30分钟内同一个ip登录10个以上的公司账号type=6 forbid_time=当前时间+30天

		JavaPairDStream<String, String> ipLoginNames = LogMapList.mapToPair(new PairFunction<LogMap, String, String>() {
			@Override
			public Tuple2<String, String> call(LogMap map) {
				// String[] lines = line.split("\\|");

				// LogMap map = LogUtil.strToLog(line);
				if (map != null) {

					try {
						if (!"杭州市".equals(map.getCity().trim())) {
							String ip = map.getFromIp();
							if (!StringUtil.isEmpty(map.getUserId()) && !"0".equals(map.getUserId())
									&& !StringUtil.isEmpty(ip)) {
								return new Tuple2<>(ip, map.getUserId().trim());
							}
						}
					} catch (Exception e) {

						// TODO Auto-generated catch block
						e.printStackTrace();

					}
				}
				return new Tuple2<>("", "");
			}
		});

		JavaPairDStream<String, Iterable<String>> reducelogs = ipLoginNames
				.groupByKeyAndWindow(Durations.seconds(60 * 30))
				.filter(new Function<Tuple2<String, Iterable<String>>, Boolean>() {
					public Boolean call(Tuple2<String, Iterable<String>> arg) throws Exception {

						try {
							if (StringUtil.isEmpty(arg._1)) {
								return false;
							}
						} catch (Exception e) {

							// TODO Auto-generated catch block
							e.printStackTrace();

						}
						return true;
					}
				});
		//
//		reducelogs.print();
		reducelogs.foreachRDD(new VoidFunction<JavaPairRDD<String, Iterable<String>>>() {

			@Override
			public void call(JavaPairRDD<String, Iterable<String>> arg0) throws Exception {
				arg0.collect().parallelStream().forEach((entry) -> {
					Iterator<String> v = entry._2.iterator();
					HashSet<String> set = new HashSet<>();
					while (v.hasNext()) {
						String next = v.next().trim();
						if (!StringUtil.isEmpty(next)) {
							set.add(next);
						}
						if (set.size() >= 10) {
							try {
								// FileWriter file = new
								// FileWriter("/app/y2.txt", true);
								// file.write(key + ":" + set.toString() +
								// "\r\n");
								// file.flush();
								// file.close();
								KafkaUtil.sendMessage("ip_3:" + entry._1, ipblack_topic);
								break;
							} catch (Exception e) {

								// TODO Auto-generated catch block
								e.printStackTrace();

							}
						}
					}

				});

			}
		});
		// String prefix2 = "/ipblack/10/" + DateUtil.date2Str(new Date()) +
		// "/";
		// reducelogs.dstream().repartition(1).saveAsTextFiles(prefix2, suffix);
		// ---------------------------------------------------------------------------------------
		// 同一ip连续10次请求未带uniquecookie的情况:type=2 forbid_time=当前时间+6小时

		JavaPairDStream<String, String> uniquecookies = LogMapList
				.mapToPair(new PairFunction<LogMap, String, String>() {
					@Override
					public Tuple2<String, String> call(LogMap map) {
						// String[] lines = line.split("\\|");

						// LogMap map = LogUtil.strToLog(line);
						if (map != null) {

							try {
								String ucookie = map.getUniqueCookie();
								String ip = map.getFromIp();
								if (!StringUtil.isEmpty(ucookie) && !StringUtil.isEmpty(ip)) {
									return new Tuple2<>(ip, ucookie.trim());
								}
							} catch (Exception e) {

								// TODO Auto-generated catch block
								e.printStackTrace();

							}
						}
						return new Tuple2<>("", "");
					}
				});

		JavaPairDStream<String, Iterable<String>> uniquecookieslogs = uniquecookies
				.groupByKeyAndWindow(Durations.seconds(60 * 60))
				.filter(new Function<Tuple2<String, Iterable<String>>, Boolean>() {
					public Boolean call(Tuple2<String, Iterable<String>> arg) throws Exception {

						try {
							if (StringUtil.isEmpty(arg._1)) {
								return false;
							}
						} catch (Exception e) {

							// TODO Auto-generated catch block
							e.printStackTrace();

						}
						return true;
					}
				});
		//
//		uniquecookieslogs.print();

		uniquecookieslogs.foreachRDD(new VoidFunction<JavaPairRDD<String, Iterable<String>>>() {

			@Override
			public void call(JavaPairRDD<String, Iterable<String>> arg0) throws Exception {
				arg0.collect().parallelStream().forEach((entry) -> {
					Iterator<String> v = entry._2.iterator();
					int count = 0;
					// StringBuffer s = new StringBuffer();
					while (v.hasNext()) {
						String next = v.next().trim();
						if ("0".equals(next)) {
							count++;
						} else {
							count = 0;
						}
						if (count >= 300) {
							try {
								KafkaUtil.sendMessage("ip_4:" + entry._1, ipblack_topic);
								break;
							} catch (Exception e) {
								e.printStackTrace();

							}
						}
					}

				});

			}
		});

		// --------------------------------------------------------------------------
		// 连续请求同一个接口超过100次：type=3 forbid_time=当前时间+12小时
		JavaPairDStream<String, String> interfacelist = LogMapList
				.mapToPair(new PairFunction<LogMap, String, String>() {
					@Override
					public Tuple2<String, String> call(LogMap map) {
						// String[] lines = line.split("\\|");

						// LogMap map = LogUtil.strToLog(line);
						if (map != null) {

							try {
								String accessurl = StringUtils.substringAfterLast(map.getAccessUrl(), "/");
								String ip = map.getFromIp();
								if (!StringUtil.isEmpty(accessurl) && !StringUtil.isEmpty(ip)) {
									return new Tuple2<>(ip, accessurl.trim());
								}
							} catch (Exception e) {

								// TODO Auto-generated catch block
								e.printStackTrace();

							}
						}
						return new Tuple2<>("", "");
					}
				});

		JavaPairDStream<String, Iterable<String>> interfacelistlogs = interfacelist
				.groupByKeyAndWindow(Durations.seconds(60 * 60))
				.filter(new Function<Tuple2<String, Iterable<String>>, Boolean>() {
					public Boolean call(Tuple2<String, Iterable<String>> arg) throws Exception {

						try {
							if (StringUtil.isEmpty(arg._1)) {
								return false;
							}

						} catch (Exception e) {

							// TODO Auto-generated catch block
							e.printStackTrace();

						}
						return true;
					}
				});
//		interfacelistlogs.print();
		interfacelistlogs.foreachRDD(new VoidFunction<JavaPairRDD<String, Iterable<String>>>() {

			@Override
			public void call(JavaPairRDD<String, Iterable<String>> arg0) throws Exception {
				arg0.collect().parallelStream().forEach((entry) -> {
					Iterator<String> v = entry._2.iterator();
					String interface1 = "";
					int count = 0;
					// StringBuffer s = new StringBuffer();
					while (v.hasNext()) {
						String next = v.next().trim();
						if (interface1.equals("")) {
							interface1 = next;
						}
						if (interface1.equals(next)) {
							count++;

						} else {
							count = 0;
							interface1 = next;
						}
						if (count >= 100) {
							try {
								// FileWriter file = new
								// FileWriter("/app/y.txt", true);
								// file.write(key + ":" + count + ":" +
								// s.toString() + "\r\n");
								// file.flush();
								// file.close();
//								KafkaUtil.sendMessage("ip_5:" + entry._1, ipblack_topic);
								break;
							} catch (Exception e) {

								// TODO Auto-generated catch block
								e.printStackTrace();

							}
						}
					}
				});
				// }

			}
		});
		// String prefix_interfacelistlogs = "/ipblack/i10/" +
		// DateUtil.date2Str(new Date()) + "/";
		// interfacelistlogs.dstream().repartition(1).saveAsTextFiles(prefix_interfacelistlogs,
		// suffix);
		// Start the computation
		jssc.start();// 开始计算
		jssc.awaitTermination();// 等待计算结束
		// jssc.stop();
	}
}