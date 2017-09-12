package com.spark;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import org.apache.spark.streaming.kafka010.OffsetRange;

import com.bean.LogMap;
import com.kafka.ConsumerUtil;
import com.util.DateUtil;
import com.util.HdfsUtil;
import com.util.LogFilter;
import com.util.StringUtil;

public final class KafkaFilterHdfs {
	private static final Pattern SPACE = Pattern.compile("	");

	public static void main(String[] args) throws Exception {
		String path = "/data_log/web_log/" + DateUtil.date2Str(DateUtil.getBeforeDay()) + "/";

		if (HdfsUtil.isExists(path)) {
			HdfsUtil.deleteFile(path);
		}
		String brokers = "10.90.60.206:9092,10.90.60.205:9092,10.90.60.204:9092";
		String groupid = "webaccess_hdfs";
		// Create context with a 2 seconds batch interval
		SparkConf sparkConf = new SparkConf().setAppName("KafkaFilterHdfs")
				.setMaster("spark://10.90.60.205:7077,10.90.60.206:7077");
		String topic="webaccess";
		JavaSparkContext jssc = new JavaSparkContext(sparkConf);

		Map<String, Object> kafkaParams = new HashMap<String, Object>();
		kafkaParams.put("bootstrap.servers", brokers);
		kafkaParams.put("key.deserializer", StringDeserializer.class);
		kafkaParams.put("value.deserializer", StringDeserializer.class);
		kafkaParams.put("group.id", groupid);
		kafkaParams.put("auto.offset.reset", "earliest");//[latest, earliest, none]
		kafkaParams.put("enable.auto.commit",false);
		// Create direct kafka stream with brokers and topics
		final JavaRDD<ConsumerRecord<String, String>> stream = KafkaUtils.createRDD(jssc, kafkaParams, ConsumerUtil.getOffsetRange(topic), LocationStrategies.PreferConsistent());

		JavaRDD<String> words = stream.flatMap(new FlatMapFunction<ConsumerRecord<String, String>, String>() {
			@Override
			public Iterator<String> call(ConsumerRecord<String, String> x) throws Exception {

				// TODO Auto-generated method stub
				return Arrays.asList(SPACE.split(x.value())).iterator();
			}
		});

		// 过滤
		JavaRDD<String> logs = words.filter(new Function<String, Boolean>() {

			@Override
			public Boolean call(String line) throws Exception {
				boolean falg = false;

				try {
					if (line.equals("")) {
						return false;
					} else {
						String[] lines = line.split("\\|");
						if (lines.length !=14) {
							return false;// 放弃该条数据
						}
//						LogMap logMap = new LogMap();

						if (StringUtil.isEmpty(lines[7]) || lines[7].indexOf("getuploadPic.json") > 0|| lines[7].indexOf("callback.json")>0)
							return false;
//						logMap.setAccessUrl(lines[7]);
						if (lines[9] == null)
							return false;
						if (lines[0].indexOf(DateUtil.date2Str(DateUtil.getBeforeDay()))<0)
							return false;
//						logMap.setAccessType(lines[9]);

						if ( LogFilter.getBot(lines[9]) == 0) {
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
		
		logs.repartition(1).saveAsTextFile(path);
		jssc.stop();
		HdfsUtil.rename(path+"/part-00000", path+"/web_access.log");
	}
}