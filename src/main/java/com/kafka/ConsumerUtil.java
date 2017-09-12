/**
 * Project Name:hxdata
 * File Name:T.java
 * Package Name:hxdata
 * Date:2017年3月1日下午1:49:51
 * Copyright (c) 2017, hx2car.com All Rights Reserved.
 *
*/

package com.kafka;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.streaming.kafka010.OffsetRange;

import com.util.DateUtil;

/**
 * ClassName:T 
 * Function: TODO ADD FUNCTION 
 * Reason:	 TODO ADD REASON
 * Date:     2017年3月1日 下午1:49:51 
 * @author   geyang
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
/**
 * date: 2017年3月1日 下午1:49:51
 * 
 * @author geyang
 */
public class ConsumerUtil {

	public static KafkaConsumer<String, String> getKafkaConsumer() {

		String brokers = "10.90.60.206:9092,10.90.60.205:9092,10.90.60.204:9092";
		// Collection<String> topics = Arrays.asList(topic);
		Map<String, Object> kafkaParams = new HashMap<String, Object>();
		kafkaParams.put("bootstrap.servers", brokers);
		kafkaParams.put("key.deserializer", StringDeserializer.class);
		kafkaParams.put("value.deserializer", StringDeserializer.class);
//		kafkaParams.put("group.id", groupid);
//		kafkaParams.put("auto.offset.reset", "latest");
		// kafkaParams.put("enable.auto.commit", true);
		// kafkaParams.put("auto.commit.interval.ms", "1000");
//		kafkaParams.put("enable.auto.commit", false);
		kafkaParams.put("partition.assignment.strategy", "org.apache.kafka.clients.consumer.RangeAssignor");
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(kafkaParams);

		// consumer.subscribe(topics);
		return consumer;

	}

	public static OffsetRange[] getOffsetRange(String topic) {

		KafkaConsumer<String, String> consumer = getKafkaConsumer();
		Collection<TopicPartition> query = new ArrayList<TopicPartition>();
		List<PartitionInfo> partitionInfos = consumer.partitionsFor(topic);
		for (PartitionInfo partitionInfo : partitionInfos) {
			query.add(new TopicPartition(partitionInfo.topic(), partitionInfo.partition()));
		}
		Map<Integer, Map<String, Long>> offMap = new HashMap<>();
		Map<TopicPartition, Long> start = consumer.beginningOffsets(query);
		for (Entry<TopicPartition, Long> entry : start.entrySet()) {
			int partition = entry.getKey().partition();
			Map<String, Long> m = new HashMap<>();
			if (offMap.containsKey(partition)) {
				m = offMap.get(partition);
			}
			m.put("start", entry.getValue());
			offMap.put(partition, m);
		}
		Map<TopicPartition, Long> end = consumer.endOffsets(query);
		for (Entry<TopicPartition, Long> entry : end.entrySet()) {

			int partition = entry.getKey().partition();
			Map<String, Long> m = new HashMap<>();
			if (offMap.containsKey(partition)) {
				m = offMap.get(partition);
			}
			m.put("end", entry.getValue());
			offMap.put(partition, m);

		}

		OffsetRange[] offsetRanges = new OffsetRange[offMap.size()];
		for (Entry<Integer, Map<String, Long>> entry : offMap.entrySet()) {

			// topic, partition, inclusive starting offset, exclusive
			// ending offset
			offsetRanges[entry.getKey()] = OffsetRange.create(topic, entry.getKey(), entry.getValue().get("start"),
					entry.getValue().get("end"));

		}
		;
		System.out.println(offMap);
		return offsetRanges;

	}

	public static void main(String[] args) {
//		String topic = "webaccess";
//		KafkaConsumer<String, String> consumer = getKafkaConsumer("webaccess_hdfs");
//		Collection<TopicPartition> query = new ArrayList<TopicPartition>();
//		Map<TopicPartition, Long> query2 = new HashMap<>();
//		List<PartitionInfo> partitionInfos = consumer.partitionsFor(topic);
//		for (PartitionInfo partitionInfo : partitionInfos) {
//			query.add(new TopicPartition(partitionInfo.topic(), partitionInfo.partition()));
//			query2.put(new TopicPartition(partitionInfo.topic(), partitionInfo.partition()), 0l);
//		}
		// Map<TopicPartition, OffsetAndTimestamp> result =
		// consumer.offsetsForTimes(query);
		try {
			// System.out.println(consumer.beginningOffsets(query));
			// System.out.println(consumer.endOffsets(query));
			getOffsetRange("webaccess");
//			getOffsetRange("webaccess","webaccess_hdfs1");
			// System.out.println(consumer.offsetsForTimes(query2));
		} catch (Exception e) {

			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
//			consumer.close();
		}

	}
}
