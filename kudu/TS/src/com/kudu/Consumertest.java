package com.kudu;

import org.apache.kafka.clients.consumer.*;
import org.apache.kudu.client.KuduException;

import java.util.*;

/**
 * Created by on 2017/8/11.
 * 伪分布式集群
 */
public class Consumertest extends Thread{
    private final KafkaConsumer<String,String>  consumer;
    private final String topic;
    public Consumertest(String topic) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "15.17.10.111:9092,15.17.10.112:9092,15.17.10.113:9092,15.17.10.114:9092,15.17.10.115:9092");
        props.put("group.id", "test5");
        props.put("enable.auto.commit", "true");
        props.put("auto.offset.reset", "earliest");
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumer = new KafkaConsumer<String, String>(props);
        this.topic =topic;
    }
    @Override
    public void run(){
        //设置Topic=>Thread Num映射关系, 构建具体的流
        Map<String,Integer> topickMap = new HashMap<String, Integer>();
        topickMap.put(topic, 1);
        List<String> list=new ArrayList<String>();
        list.add(topic);
        consumer.subscribe(list);
        System.out.println("*********Results********");
        while(true){
            ConsumerRecords<String,String> records=consumer.poll(10000);
            Iterator<ConsumerRecord<String,String>> it=records.iterator();
            while (it.hasNext()) {
                ConsumerRecord<String,String> ss=it.next();
                System.err.println(ss.value());
                
                try {
					CreateTable.InsertKudu2(ss.value());
				} catch (KuduException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
               // System.out.println("get data:" + ss.key() + "," + ss.value() + "," + ss.toString());

                /*try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
            }
        }
    }
    public static void main(String[] args) {
        Consumertest consumerThread = new Consumertest("WA_SOURCE_FJ_1001");
        consumerThread.start();
    }
}
