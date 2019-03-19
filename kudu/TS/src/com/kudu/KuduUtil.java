package com.kudu;

import java.util.ArrayList;
import java.util.List;

import org.apache.kudu.ColumnSchema;
import org.apache.kudu.Schema;
import org.apache.kudu.Type;
import org.apache.kudu.client.CreateTableOptions;
import org.apache.kudu.client.Insert;
import org.apache.kudu.client.KuduClient;
import org.apache.kudu.client.KuduScanner;
import org.apache.kudu.client.KuduSession;
import org.apache.kudu.client.KuduTable;
import org.apache.kudu.client.OperationResponse;
import org.apache.kudu.client.PartialRow;
import org.apache.kudu.client.RowResult;
import org.apache.kudu.client.RowResultIterator;
import org.apache.kudu.client.Update;
import org.junit.Test;
 
/**
 * @ClassName: KuduUtil
 */
public class KuduUtil {
    private static final String KUDU_MASTER = "15.17.10.114:7051";
    private static String tableName = "TestKudu";
 
    @Test
    public void kuduCreateTableTest(){
    	
        KuduClient client = new KuduClient.KuduClientBuilder(KUDU_MASTER).defaultAdminOperationTimeoutMs(6000).build();
    	//KuduClient client = new KuduClient.KuduClientBuilder(KUDU_MASTER).build();
    	
        try {
            List<ColumnSchema> columns = new ArrayList<ColumnSchema>(2);
            columns.add(new ColumnSchema.ColumnSchemaBuilder("key", Type.STRING)
                    .key(true)
                    .build());
            columns.add(new ColumnSchema.ColumnSchemaBuilder("value", Type.STRING)
                    .build());
            List<String> rangeKeys = new ArrayList<String>(1);
            rangeKeys.add("key");
            int numBuckets =8;
            CreateTableOptions cto = new CreateTableOptions();
            cto.addHashPartitions(rangeKeys,numBuckets);
            Schema schema = new Schema(columns);
            // new CreateTableOptions().setRangePartitionColumns(rangeKeys)
            client.createTable(tableName, schema,cto
                    );
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                client.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        System.err.println(111); 
    }
 
    @Test
    public void kuduSaveTest(){
        KuduClient client = new KuduClient.KuduClientBuilder(KUDU_MASTER).build();
        try{
            KuduTable table = client.openTable(tableName);
            KuduSession session = client.newSession();
            System.out.println("-------start--------"+System.currentTimeMillis());
            for (int i = 30000; i < 31000; i++) {
                Insert insert = table.newInsert();
                PartialRow row = insert.getRow();
                row.addString(0, i+"");
                row.addString(1, "aaa");
                OperationResponse operationResponse =  session.apply(insert);
            }
            System.out.println("-------end--------"+System.currentTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                client.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
 
    @Test
    public void kuduUpdateTest(){
 
        KuduClient client = new KuduClient.KuduClientBuilder(KUDU_MASTER).build();
        try {
        KuduTable table = client.openTable(tableName);
            KuduSession session = client.newSession();
                Update update = table.newUpdate();
                PartialRow row = update.getRow();
                row.addString("key", 4+"");
                row.addString("value", "value " + 10);
            OperationResponse operationResponse =  session.apply(update);
 
           System.out.print(operationResponse.getRowError());
 
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                client.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
 
    }
 
    @Test
    public void kuduSearchTest(){
        KuduClient client = new KuduClient.KuduClientBuilder(KUDU_MASTER).build();
 
        try {
            KuduTable table = client.openTable(tableName);
        List<String> projectColumns = new ArrayList<String>(1);
        projectColumns.add("value");
        KuduScanner scanner = client.newScannerBuilder(table)
                .setProjectedColumnNames(projectColumns)
                .build();
        while (scanner.hasMoreRows()) {
            RowResultIterator results = scanner.nextRows();
            while (results.hasNext()) {
                RowResult result = results.next();
                System.out.println(result.getString(0));
            }
        }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                client.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
 
    @Test
    public void kuduDelTabletest(){
        KuduClient client = new KuduClient.KuduClientBuilder(KUDU_MASTER).build();
        try {
            client.deleteTable(tableName);
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
 
  /*  @Test
    public void searchBysparkSql(){
        SparkSession sparkSession = getSparkSession();
        List<StructField> fields = Arrays.asList(
                DataTypes.createStructField("key", DataTypes.StringType, true),
                DataTypes.createStructField("value", DataTypes.StringType, true));
        StructType schema = DataTypes.createStructType(fields);
        Dataset ds =  sparkSession.read().format("org.apache.kudu.spark.kudu").
                schema(schema).option("kudu.master","10.1.0.20:7051").option("kudu.table","TestKudu").load();
        ds.registerTempTable("abc");
        sparkSession.sql("select * from abc").show();
    }
 
    @Test
    public void checkTableExistByKuduContext(){
        SparkSession sparkSession = getSparkSession();
        KuduContext context = new KuduContext("10.1.0.20:7051",sparkSession.sparkContext());
        System.out.println(tableName +" is exist = "context.tableExists(tableName));
    }
 
    public SparkSession getSparkSession(){
        SparkConf conf = new SparkConf().setAppName("test")
                .setMaster("local[*]")
                .set("spark.driver.userClassPathFirst", "true");
 
        conf.set("spark.sql.crossJoin.enabled", "true");
        SparkContext sparkContext = new SparkContext(conf);
        SparkSession sparkSession = SparkSession.builder().sparkContext(sparkContext).getOrCreate();
        return sparkSession;
    }*/
}
