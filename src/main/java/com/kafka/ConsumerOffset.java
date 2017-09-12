/**
 * Project Name:hxdata
 * File Name:T.java
 * Package Name:hxdata
 * Date:2017年3月1日下午1:49:51
 * Copyright (c) 2017, hx2car.com All Rights Reserved.
 *
*/
/**
 * Project Name:hxdata
 * File Name:T.java
 * Package Name:hxdata
 * Date:2017年3月1日下午1:49:51
 * Copyright (c) 2017, geyang All Rights Reserved.
 *
 */

package com.kafka;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.ConsumerStrategy;

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
public class ConsumerOffset {
	public static void main(String[] args) {
		String brokers = "10.90.60.206:9092,10.90.60.205:9092,10.90.60.204:9092";
		String groupid = "webaccess_hdfs";
		String topic = "webaccess";
		Collection<String> topics = Arrays.asList("webaccess");
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
		 kafkaParams.put("enable.auto.commit", true);
		kafkaParams.put("auto.commit.interval.ms", "1000");
		kafkaParams.put("partition.assignment.strategy", "org.apache.kafka.clients.consumer.RangeAssignor");
		// ConsumerStrategy<String, String> cc=ConsumerStrategies.<String,
		// String> Subscribe(topics, kafkaParams);
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(kafkaParams);

		consumer.subscribe(topics);
		Map<TopicPartition, Long> query = new HashMap<>();
		// query.put(
		// new TopicPartition("webaccess", 0),
		// 1488944071347L);
		// query.put(
		// new TopicPartition("webaccess", 1),
		// 1488944071347L);
		// query.put(
		// new TopicPartition("webaccess", 2),
		// 1488944071347L);

		// System.out.println(consumer.listTopics().get("webaccess"));

		List<PartitionInfo> partitionInfos = consumer.partitionsFor(topic);
		for (PartitionInfo partitionInfo : partitionInfos) {
			query.put(new TopicPartition(partitionInfo.topic(), partitionInfo.partition()), 0L);
			System.out.println(partitionInfo);
		}
		Map<TopicPartition, OffsetAndTimestamp> result = consumer.offsetsForTimes(query);
		System.out.println(result);

		// System.out.println(System.currentTimeMillis());

		//
		// try {
		// while (true) {
		// ConsumerRecords<String, String> records = consumer.poll(1000);
		// // 根据分区来遍历数据：
		//// for (TopicPartition partition : records.partitions()) {
		//// List<ConsumerRecord<String, String>> partitionRecords =
		// records.records(partition);
		//// // 数据处理
		//// for (ConsumerRecord<String, String> record : partitionRecords) {
		//// System.out.println(record.offset() + ": " + record.value());
		//// }
		//// // 取得当前读取到的最后一条记录的offset
		//// long lastOffset = partitionRecords.get(partitionRecords.size() -
		// 1).offset();
		//// // 提交offset，记得要 + 1
		//// consumer.commitSync(Collections.singletonMap(partition, new
		// OffsetAndMetadata(lastOffset + 1)));
		//// }
		// try {
		// consumer.commitSync();
		// } catch (CommitFailedException e) {
		// // application specific failure handling
		// }
		// for (ConsumerRecord<String, String> record : records){
		// Map<String, Object> data = new HashMap<>();
		// data.put("partition", record.partition());
		// data.put("offset", record.offset());
		//// data.put("value", record.value());
		// System.out.println(data);
		// }
		// }
		// } finally {
		// consumer.close();
		// }
	}
}
