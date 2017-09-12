package com.kafka;

import java.util.Properties;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.util.Config;

public class KafkaUtil {
	static KafkaProducer<String, String> producer = null;

//	static {
//		// 修改kafka日志输出级别
//		Logger.getLogger("org.apache.kafka").setLevel(Level.OFF);
//	}
	   public static KafkaProducer<String, String> getProducer() {  
	        if (producer == null) {
	        Properties properties = new Properties();
			properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
			properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
			properties.put("bootstrap.servers", Config.getString("bootstrap.servers"));// 声明kafka
			properties.put(ProducerConfig.ACKS_CONFIG, "1");// 声明kafka
			properties.put("retries", 1);// 请求失败重试的次数
//			properties.put("client.id", 0);
			producer = new KafkaProducer<String, String>(properties);
			}  
	        return producer;  
	    }  
	public static void sendMessage(String mes,String topic) {
		try {
//			KafkaProducer<String, String> kafkaProducer=KafkaUtil.producer;
//			if (kafkaProducer!=null) {
				ProducerRecord<String, String> record = new ProducerRecord<String, String>(
						topic, mes);
				Future<RecordMetadata> future = getProducer().send(record);
//				System.out.println(future.get().offset() + "   " + future.get().partition());
//			}

			// }
		} catch (Exception e) {
			e.printStackTrace();
			producer.close();
			producer=null;
		} finally {
//			producer=null;
		}
	}

	public static void main(String[] args) {
		while (true) {

//			KafkaUtil.sendMessage(
//					"null|127.0.0.1|国外|||||http://backserver/car/historyKeyAndHotKey.json|http://bd.2schome.net/|Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.19 Safari/537.36|http://backserver/car/historyKeyAndHotKey.json?keytype=0&|1470189803366185158");
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//
//			}
			System.err.println(1);
		}

	}

}
