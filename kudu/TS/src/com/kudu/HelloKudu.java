package com.kudu;

import java.util.LinkedList;
import java.util.List;

import org.apache.kudu.ColumnSchema;
import org.apache.kudu.Schema;
import org.apache.kudu.Type;
import org.apache.kudu.client.CreateTableOptions;
import org.apache.kudu.client.KuduClient;
import org.apache.kudu.client.KuduException;

public class HelloKudu {

	
	
	public static void main(String[] args) throws KuduException {
		// master地址
		final String masteraddr = "15.17.10.114:7051";
		// 创建kudu的数据库链接
		KuduClient client = new KuduClient.KuduClientBuilder(masteraddr).defaultSocketReadTimeoutMs(6000).build();

		// 设置表的schema
		List<ColumnSchema> columns = new LinkedList<ColumnSchema>();
		columns.add(new ColumnSchema.ColumnSchemaBuilder("key", Type.STRING)
                .key(true)
                .build());
		columns.add(new ColumnSchema.ColumnSchemaBuilder("value", Type.STRING)
                .key(false)
                .build());
		;
		Schema schema = new Schema(columns);
		// 创建表时提供的所有选项
		CreateTableOptions options = new CreateTableOptions();
		// 设置表的replica备份和分区规则
		List<String> parcols = new LinkedList<>();
		parcols.add("CompanyId");

		// 一个replica
		options.setNumReplicas(1);
		// 用列companyid做为分区的参照
		options.setRangePartitionColumns(parcols);
		// 添加key的hash分区
		// options.addHashPartitions(parcols, 3);
		try {
			client.createTable("PERSON", schema, options);
		} catch (KuduException e) {
			e.printStackTrace();
		}
		client.close();
	}
}
