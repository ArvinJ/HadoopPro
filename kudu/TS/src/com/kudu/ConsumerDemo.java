package com.kudu;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class ConsumerDemo {
	public static void main(String[] args) {
		Properties properties = new Properties();
		//properties.put("bootstrap.servers", "172.16.53.23:9092,172.16.53.29:9092,172.16.53.30:9092");
		properties.put("bootstrap.servers", "15.17.10.111:9092,15.17.10.112:9092,15.17.10.113:9092,15.17.10.114:9092,15.17.10.115:9092");
		//properties.put("borker.list", "15.17.10.111:9092,15.17.10.112:9092,15.17.10.113:9092,15.17.10.114:9092,15.17.10.115:9092");
		// group.id Consumer分组ID
		properties.put("group.id", "g11");
		properties.put("enable.auto.commit", "true");
		properties.put("auto.commit.interval.ms", "1000");
		properties.put("auto.offset.reset", "earliest");
		properties.put("session.timeout.ms", "30000");
		properties.put("zookeeper", "15.17.10.106:2181,15.17.10.107:2181,15.17.10.108:2181,15.17.10.109:2181,15.17.10.110:2181");
		// 。Consumer把来自Kafka集群的二进制消息反序列化为指定的类型。因本例中的Producer使用的是String类型，所以调用StringDeserializer来反序列化
		properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

		@SuppressWarnings("resource")
		KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<String, String>(properties);
		// Consumer订阅了Topic为AmyTopic的消息，
		kafkaConsumer.subscribe(Arrays.asList("WA_SOURCE_FJ_1001"));
		int target =1;
		while (true) {
			// Consumer调用poll方法来轮循Kafka集群的消息，其中的参数100是超时时间（Consumer等待直到Kafka集群中没有消息为止）
			ConsumerRecords<String, String> records = kafkaConsumer.poll(100);
			
			for (ConsumerRecord<String, String> record : records) {
				//System.out.printf("offset = %d, value = %s", record.offset(), record.value());
				System.out.println(target++);
			}
		}

	}
}
