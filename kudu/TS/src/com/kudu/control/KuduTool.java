package com.kudu.control;

import java.util.LinkedList;
import java.util.List;

import org.apache.kudu.ColumnSchema;
import org.apache.kudu.Schema;
import org.apache.kudu.Type;
import org.apache.kudu.ColumnSchema.ColumnSchemaBuilder;
import org.apache.kudu.client.CreateTableOptions;
import org.apache.kudu.client.Insert;
import org.apache.kudu.client.KuduClient;
import org.apache.kudu.client.KuduException;
import org.apache.kudu.client.KuduPredicate;
import org.apache.kudu.client.KuduScanner;
import org.apache.kudu.client.KuduSession;
import org.apache.kudu.client.KuduTable;
import org.apache.kudu.client.RowResult;
import org.apache.kudu.client.RowResultIterator;
import org.apache.kudu.client.KuduPredicate.ComparisonOp;
import org.apache.kudu.client.KuduScanner.KuduScannerBuilder;
import org.apache.kudu.client.SessionConfiguration.FlushMode;

public class KuduTool {

	private static final String KUDU_MASTER = "15.17.10.114:7051";
    private static ColumnSchema newColumn(String name, Type type, boolean iskey) {
        ColumnSchemaBuilder column = new ColumnSchema.ColumnSchemaBuilder(name, type);
        column.key(iskey);
        return column.build();
    }
    public static void main(String[] args) throws KuduException {
    	//kuduSearch();
    	//InsertKudu();
    	delTable();
    	//kuduCreateTable2();
    	//kuduCreateTable();
    }
    
    
    
    public static void kuduSearch() {
		KuduClient client = new KuduClient.KuduClientBuilder(KUDU_MASTER).build();

		try {
			KuduTable table = client.openTable("t_ex1");
			  KuduScannerBuilder builder = client.newScannerBuilder(table);
		        //newComparisonPredicate 在一个整数或时间戳列上创建一个新的比较谓词。
		        KuduPredicate predicate = KuduPredicate.newComparisonPredicate(table.getSchema().getColumn("id"),
		                ComparisonOp.EQUAL, 4000001);
		        builder.addPredicate(predicate);


		        // 开始扫描
		        KuduScanner scaner = builder.build();
		        while (scaner.hasMoreRows()) {
		            RowResultIterator iterator = scaner.nextRows();
		            while (iterator.hasNext()) {
		                RowResult result = iterator.next();
		                /**
		                 * 输出行
		                 */
		                System.out.println("id:" + result.getInt("id"));
		                System.out.println("Name:" + result.getString("name"));
		            }
		        }

		        scaner.close();
		        client.close();
		}catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
    
    
    
    
    
    public static void kuduCreateTable() {
    	
    	 // master地址
        // 创建kudu的数据库链接
        KuduClient client = new KuduClient.KuduClientBuilder(KUDU_MASTER).defaultSocketReadTimeoutMs(600000).build();

        // 设置表的schema
        List<ColumnSchema> columns = new LinkedList<ColumnSchema>();
        columns.add(newColumn("id", Type.INT32, true));
        columns.add(newColumn("name", Type.STRING, false));
        Schema schema = new Schema(columns);
        //创建表时提供的所有选项
        CreateTableOptions options = new CreateTableOptions();
        // 设置表的replica备份和分区规则
        List<String> parcols = new LinkedList<String>();
        parcols.add("id");
        // 一个replica
        options.setNumReplicas(1);
        // 用列companyid做为分区的参照
        options.setRangePartitionColumns(parcols);
        // 添加key的hash分区
        //options.addHashPartitions(parcols, 3);
        try {
            client.createTable("t_ex1", schema, options);
        } catch (KuduException e) {
            e.printStackTrace();
        }
        try {
			client.close();
		} catch (KuduException e) {
			e.printStackTrace();
		}
    	
    }
    
    
    public static void kuduCreateTable2() {
    	
   	 // master地址
       // 创建kudu的数据库链接
       KuduClient client = new KuduClient.KuduClientBuilder(KUDU_MASTER).defaultSocketReadTimeoutMs(600000).build();

       // 设置表的schema
       List<ColumnSchema> columns = new LinkedList<ColumnSchema>();
       columns.add(newColumn("CAPTURE_TIME", Type.STRING, true));
       columns.add(newColumn("MAC", Type.STRING, false));
       columns.add(newColumn("BRAND", Type.STRING, false));
       columns.add(newColumn("CACHE_SSID", Type.STRING, false));
       columns.add(newColumn("TERMINAL_FIELD_STRENGTH", Type.STRING, false));
       columns.add(newColumn("IDENTIFICATION_TYPE", Type.STRING, false));
       columns.add(newColumn("CERTIFICATE_CODE", Type.STRING, false));
       columns.add(newColumn("SSID_POSITION", Type.STRING, false));
       columns.add(newColumn("ACCESS_AP_MAC", Type.STRING, false));
       columns.add(newColumn("ACCESS_AP_CHANNEL", Type.STRING, false));
       columns.add(newColumn("ACCESS_AP_ENCRYPTION_TYPE", Type.STRING, false));
       columns.add(newColumn("X_COORDINATE", Type.STRING, false));
       columns.add(newColumn("Y_COORDINATE", Type.STRING, false));
       columns.add(newColumn("NETBAR_WACODE", Type.STRING, false));
       columns.add(newColumn("COLLECTION_EQUIPMENT_ID", Type.STRING, false));
       columns.add(newColumn("COLLECTION_EQUIPMENT_LONGITUDE", Type.STRING, false));
       columns.add(newColumn("COLLECTION_EQUIPMENT", Type.STRING, false));
       Schema schema = new Schema(columns);
       //创建表时提供的所有选项
       CreateTableOptions options = new CreateTableOptions();
       // 设置表的replica备份和分区规则
       List<String> parcols = new LinkedList<String>();
       //parcols.add("MAC");
       parcols.add("CAPTURE_TIME");

       // 一个replica
       options.setNumReplicas(1);
       // 用列companyid做为分区的参照
       options.setRangePartitionColumns(parcols);
       // 添加key的hash分区
       //options.addHashPartitions(parcols, 3);
       try {
           client.createTable("t_source_fj_1001", schema, options);
       } catch (KuduException e) {
           e.printStackTrace();
       }
       try {
			client.close();
		} catch (KuduException e) {
			e.printStackTrace();
		}
   	
   }
    
    public static void InsertKudu() throws KuduException {
        // 创建kudu的数据库链接
       KuduClient client = new KuduClient.KuduClientBuilder(KUDU_MASTER).build();
        // 打开表
        KuduTable table = client.openTable("t_ex1");
        // 创建写session,kudu必须通过session写入
        KuduSession session = client.newSession();
        // 采取Flush方式 手动刷新
        session.setFlushMode(FlushMode.MANUAL_FLUSH);
        session.setMutationBufferSpace(3000);
        
        System.out.println("-------start--------" + System.currentTimeMillis());
        for (int i = 4000001; i < 4000002; i++) {
        Insert insert = table.newInsert();
            // 设置字段内容
            insert.getRow().addInt("id", i);
            insert.getRow().addString("name", "雄人班长"+ i);
            session.flush();
            session.apply(insert);
        }
        System.out.println("-------end--------" + System.currentTimeMillis());
        session.close();
        client.close();
    }
    
    
    
    public static void InsertKudu2(String tempValues) throws KuduException {
    	
		
    	String[]  values = tempValues.split("\t", -1);
    	
        // 创建kudu的数据库链接
       KuduClient client = new KuduClient.KuduClientBuilder(KUDU_MASTER).build();
        // 打开表
        KuduTable table = client.openTable("WA_SOURCE_FJ_1001");
        // 创建写session,kudu必须通过session写入
        KuduSession session = client.newSession();
        // 采取Flush方式 手动刷新
        session.setFlushMode(FlushMode.MANUAL_FLUSH);
        session.setMutationBufferSpace(3000);
        
        Insert insert = table.newInsert();
            // 设置字段内容
        	insert.getRow().addString("CAPTURE_TIME", values[3]);
            insert.getRow().addString("MAC", values[0]);
            insert.getRow().addString("BRAND", values[1]);
            insert.getRow().addString("CACHE_SSID", values[2]);
            insert.getRow().addString("TERMINAL_FIELD_STRENGTH", values[4]);
            insert.getRow().addString("IDENTIFICATION_TYPE", values[5]);
            insert.getRow().addString("CERTIFICATE_CODE", values[6]);
            insert.getRow().addString("SSID_POSITION", values[7]);
            insert.getRow().addString("ACCESS_AP_MAC", values[8]);
            insert.getRow().addString("ACCESS_AP_CHANNEL", values[9]);
            insert.getRow().addString("ACCESS_AP_ENCRYPTION_TYPE", values[10]);
            insert.getRow().addString("X_COORDINATE", values[11]);
            insert.getRow().addString("Y_COORDINATE", values[12]);
            insert.getRow().addString("NETBAR_WACODE", values[13]);
            insert.getRow().addString("COLLECTION_EQUIPMENT_ID", values[14]);
            insert.getRow().addString("COLLECTION_EQUIPMENT_LONGITUDE", values[15]);
            insert.getRow().addString("COLLECTION_EQUIPMENT", values[16]);
           
            session.flush();
            session.apply(insert);
        session.close();
        client.close();
    }
    




    public static void delTable() {
    	

		KuduClient client = new KuduClient.KuduClientBuilder(KUDU_MASTER).build();
		try {
			client.deleteTable("t_ex1");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				client.shutdown();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
    	
    }


}
