package com.kudu.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kudu.client.Insert;
import org.apache.kudu.client.KuduClient;
import org.apache.kudu.client.KuduException;
import org.apache.kudu.client.KuduSession;
import org.apache.kudu.client.KuduTable;
import org.apache.kudu.client.PartialRow;
import org.apache.kudu.client.SessionConfiguration;

public class KafKaToKudu extends Thread {
	private final static int OPERATION_BATCH = 5000;
	private final KafkaConsumer<String, String> consumer;
	private final String topic;
	KuduTable table;

	public KafKaToKudu(String topic) {
		Properties props = new Properties();
		props.put("bootstrap.servers",
				"15.17.10.111:9092,15.17.10.112:9092,15.17.10.113:9092,15.17.10.114:9092,15.17.10.115:9092");
		props.put("group.id", "test11");
		
		props.put("enable.auto.commit", "true");
		props.put("auto.offset.reset", "earliest");
		props.put("auto.commit.interval.ms", "1000");
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		consumer = new KafkaConsumer<String, String>(props);
		this.topic = topic;
	}

	@Override
	public void run() {
		// 设置Topic=>Thread Num映射关系, 构建具体的流
		Map<String, Integer> topickMap = new HashMap<String, Integer>();
		topickMap.put(topic, 1);
		List<String> list = new ArrayList<String>();
		list.add(topic);
		consumer.subscribe(list);
		System.out.println("*********Results********"+System.currentTimeMillis());
		

		KuduClient client = new KuduClient.KuduClientBuilder("15.17.10.114:7051").build();
		KuduSession session = client.newSession();

		try {
			table = client.openTable("impala::default.t_sources_fj_1001");
		} catch (KuduException e2) {
			e2.printStackTrace();
		}
		
		

		SessionConfiguration.FlushMode mode;
		mode = SessionConfiguration.FlushMode.MANUAL_FLUSH;
		session.setFlushMode(mode);
		session.setMutationBufferSpace(OPERATION_BATCH);
		
		//UUID uuid = UUID.randomUUID();
		int target =1;
		while (true) {
			
			int uncommit = 0;
			ConsumerRecords<String, String> records = consumer.poll(100);
			for (ConsumerRecord<String, String> record : records) {
				String[] values = record.value().split("\t", -1);

				if (values.length != 17 || values[0] == null || values[3] == null || values[0].equals("") || values[3].equals("")
						|| values[3].length() != 10 || values[0].equals("0") || values[0].length() != 17
						|| values[13].equals("") || values[14].equals("") || values[14].equals("0")
						|| values[14].equals(0) || values[0].equals("00-00-00-00-00-00")
						|| values[0].equals("FF-FF-FF-FF-FF-FF") || values[13].equals("0") || values[13].equals(0)
						|| values[0].equals(0) || values[13].length() != 14) {

				}else {
					try {
						Insert insert = table.newInsert();
						PartialRow row = insert.getRow();
						
						UUID uuid = UUID.randomUUID();
						row.addString("id", values[3]+"||"+values[0]+"||"+ values[13]);
						row.addString("capture_time", values[3]);
						row.addString("mac", values[0]);
						row.addString("brand", values[1]);
						row.addString("cache_ssid", values[2]);
						row.addString("terminal_fieldstrength", values[4]);
						row.addString("identification_type", values[5]);
						row.addString("capture_time", values[6]);
						row.addString("ssid_position", values[7]);
						row.addString("access_ap_mac", values[8]);
						row.addString("access_ap_channel", values[9]);
						row.addString("access_ap_encryption_type", values[10]);
						row.addString("x_coordinate", values[11]);
						row.addString("y_coordinate", values[12]);
						row.addString("netbar_wacode", values[13]);
						row.addString("collection_equipment_id", values[14]);
						row.addString("collection_equipment_longitude", values[15]);
						row.addString("collection_equipment_latitude", values[16]);

						session.apply(insert);

						// 对于手工提交, 需要buffer在未满的时候flush,这里采用了buffer一半时即提交
						uncommit = uncommit + 1;
						if (uncommit > OPERATION_BATCH / 2) {
							session.flush();
							uncommit = 0;
						}

					} catch (KuduException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
				
				
				
				System.err.println("插入了："+target++);
				
			}
			
			
			// 对于手工提交, 保证完成最后的提交
			if (uncommit > 0) {
				try {
					session.flush();
				} catch (KuduException e) {
					e.printStackTrace();
				}
			}

				
			
		}
		
		
	}

	public static void main(String[] args) {
		KafKaToKudu kafKaToKudu = new KafKaToKudu("WA_SOURCE_FJ_1001");
		kafKaToKudu.start();
	}

}