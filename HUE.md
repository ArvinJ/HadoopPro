

Hue是一个开源的Apache Hadoop UI系统



通过使用Hue我们可以在浏览器端的Web控制台上与Hadoop集群进行交互来分析处理数据，例如操作HDFS上的数据，运行MapReduce Job，执行Hive的SQL语句，浏览HBase数据库等等。



Hue在数据库方面，默认使用的是SQLite数据库来管理自身的数据





HUE自身的功能包含有：

```doc
对HDFS的访问，通过浏览器来查阅HDFS的数据。
Hive编辑器：可以编写HQL和运行HQL脚本，以及查看运行结果等相关Hive功能。
提供Solr搜索应用，并对应相应的可视化数据视图以及DashBoard。
提供Impala的应用进行数据交互查询。
最新的版本集成了Spark编辑器和DashBoard
支持Pig编辑器，并能够运行编写的脚本任务。
Oozie调度器，可以通过DashBoard来提交和监控Workflow、Coordinator以及Bundle。
支持HBase对数据的查询修改以及可视化。
支持对Metastore的浏览，可以访问Hive的元数据以及对应的HCatalog。
另外，还有对Job的支持，Sqoop，ZooKeeper以及DB（MySQL，SQLite，Oracle等）的支持。
```



