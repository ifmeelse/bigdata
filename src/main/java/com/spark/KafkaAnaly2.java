package com.spark;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
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
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
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

import kafka.serializer.StringDecoder;
import scala.Tuple2;
import scala.collection.Seq;

public final class KafkaAnaly2 {
	private static final Pattern SPACE = Pattern.compile(" ");

	public static void main(String[] args) throws Exception {

		String brokers = "10.90.60.206:9092,10.90.60.205:9092,10.90.60.204:9092";
		String groupid = "webaccess_IpBlack";
		String topic="webaccess";
		Collection<String> topics = Arrays.asList(topic);
		// Create context with a 2 seconds batch interval
		// SparkConf sparkConf = new
		// SparkConf().setAppName("KafkaFilter").setMaster("spark://10.90.60.206:7077");
//		SparkSession sparkSession = SparkSession.builder().master("spark://10.90.60.206:7077").appName("KafkaFilter2")
//				.config("spark.submit.deployMode", "cluster").getOrCreate();
		 SparkConf sparkConf = new SparkConf();
		 sparkConf.setAppName("KafkaFilter");
		 sparkConf.setMaster("spark://10.90.60.206:7077");
		 sparkConf.set("spark.submit.deployMode", "cluster");
		 sparkConf.set("spark.cores.max", "2");
		 sparkConf.set(" spark.executor.memenory", "1g");
//		 Seq<String> jars = null;
//		sparkConf.setJars(jars);
		JavaSparkContext conf = new JavaSparkContext(sparkConf);
		conf.hadoopConfiguration().set("fs.defaultFS", "hdfs://mycluster");
		conf.hadoopConfiguration().set("dfs.nameservices", "mycluster");
		conf.hadoopConfiguration().set("dfs.ha.namenodes.mycluster", "nn1,nn2");
		conf.hadoopConfiguration().set("dfs.namenode.rpc-address.mycluster.nn1", "app206:9000");
		conf.hadoopConfiguration().set("dfs.namenode.rpc-address.mycluster.nn2", "app205:9000");
		conf.hadoopConfiguration().set("dfs.client.failover.proxy.provider.mycluster",
				"org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider");

		JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(60));

		Map<String, Object> kafkaParams = new HashMap<String, Object>();
		// kafkaParams.put("metadata.broker.list", brokers);
		// kafkaParams.put("serializer.class",
		// "kafka.serializer.StringEncoder");
		// kafkaParams.put("auto.offset.reset", "smallest");// smallest and
		// largest
		kafkaParams.put("bootstrap.servers", brokers);
		kafkaParams.put("key.deserializer", StringDeserializer.class);
		kafkaParams.put("value.deserializer", StringDeserializer.class);
		kafkaParams.put("group.id", groupid);
		kafkaParams.put("auto.offset.reset", "latest");
		// kafkaParams.put("enable.auto.commit", true);
		 kafkaParams.put("auto.commit.interval.ms", "1000");
		kafkaParams.put("enable.auto.commit", false);
		kafkaParams.put("partition.assignment.strategy", "org.apache.kafka.clients.consumer.RangeAssignor");
//		kafkaParams.put("request.timeout.ms", "305000");
//		kafkaParams.put("heartbeat.interval.ms", "85000");
//		kafkaParams.put("session.timeout.ms", "90000");
		// Create direct kafka stream with brokers and topics
		final JavaInputDStream<ConsumerRecord<String, String>> stream = KafkaUtils.createDirectStream(jssc,
				LocationStrategies.PreferConsistent(),
				ConsumerStrategies.<String, String> Subscribe(topics, kafkaParams));
//		TopicPartition partition0 = new TopicPartition(topic, 0);
//		TopicPartition partition1 = new TopicPartition(topic, 1);
//		TopicPartition partition2 = new TopicPartition(topic, 2);
//		final JavaInputDStream<ConsumerRecord<Object, Object>> stream = KafkaUtils.createDirectStream(jssc,
//				LocationStrategies.PreferConsistent(),
//				ConsumerStrategies.Assign(Arrays.asList(partition0, partition1,partition2), kafkaParams));
		stream.foreachRDD(new VoidFunction<JavaRDD<ConsumerRecord<String, String>>>() {
			@Override
			public void call(JavaRDD<ConsumerRecord<String, String>> rdd) {
				OffsetRange[] offsetRanges = ((HasOffsetRanges) rdd.rdd()).offsetRanges();

				// some time later, after outputs have completed
				((CanCommitOffsets) stream.inputDStream()).commitAsync(offsetRanges);
			}
		});
		JavaDStream<String> words = stream.flatMap(new FlatMapFunction<ConsumerRecord<String, String>, String>() {
			@Override
			public Iterator<String> call(ConsumerRecord<String, String> x) throws Exception {
				// TODO Auto-generated method stub
//				OffsetRange[] offsetRanges = ((HasOffsetRanges) x.offset()).offsetRanges();
				return Arrays.asList(x.partition()+":"+x.offset()).iterator();
			}
		});
//		 String suffix = "";
//		 String prefix = "/data_log/web_log/"+DateUtil.date2Str(new Date())+"/";
//		 words.repartition(1).dstream().saveAsTextFiles(prefix, suffix);
		words.print();
//
//		// 过滤
//		JavaDStream<String> logs = words.filter(new Function<String, Boolean>() {
//
//			@Override
//			public Boolean call(String line) throws Exception {
//				boolean falg = false;
//
//				try {
//					if (line.equals("")) {
//						falg = false;
//					} else {
//						String[] lines = line.split("\\|");
//						if (lines.length < 12) {
//							falg = false;// 放弃该条数据
//						}
//						LogMap logMap = new LogMap();
//
//						if (lines[7] == null || lines[7].indexOf("getuploadPic.json") > 0)
//							falg = false;
//						logMap.setAccessUrl(lines[7]);
//						if (lines[9] == null)
//							falg = false;
//						logMap.setAccessType(lines[9]);
//
//						if (!"".equals(logMap.getAccessUrl()) && LogFilter.getBot(logMap.getAccessType()) == 0) {
//							// 生成过滤后的日志
//							falg = true;
//						}
//					}
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//
//				}
//
//				return falg;
//			}
//
//		});
//
//		// logs.cache();
//
//		// String suffix = "";
//		// logs.repartition(1).dstream().saveAsTextFiles(prefix, suffix);
//
//		JavaPairDStream<String, Integer> wordCounts = logs.mapToPair(new PairFunction<String, String, Integer>() {
//			@Override
//			public Tuple2<String, Integer> call(String line) {
//				String[] lines = line.split("\\|");
//
//				String s = lines[0];
//				if (s != null && !s.isEmpty()) {
//
//					return new Tuple2<>(s, 1);
//				}
//				return null;
//			}
//		});
//		JavaPairDStream<String, Integer> reduce = wordCounts.reduceByKey(new Function2<Integer, Integer, Integer>() {
//			@Override
//			public Integer call(Integer i1, Integer i2) {
//				return i1 + i2;
//			}
//		});
//		reduce.print();

		// Start the computation
		jssc.start();// 开始计算
		jssc.awaitTermination();// 等待计算结束
		// jssc.stop();
	}
}