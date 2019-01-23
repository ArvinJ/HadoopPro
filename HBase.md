#### HBase 

##### HBase简介

HbaseHadoop Database，是一个高可靠性、高性能、面向列、可伸缩、实时读写的分布式数据库利用Hadoop HDFS作为其文件存储系统,利用Hadoop MapReduce来处理HBase中的海量数据,利用Zookeeper作为其分布式协同服务主要用来存储非结构化和半结构化的松散数据（列存 NoSQL 数据库），可以存储结构化数据。

##### HBase 数据模型

###### ROW  KEY

决定一行数据

按照字典顺序排序的。

Row key只能存储64k的字节数据

###### #Column Family列族 & qualifier列

HBase表中的每个列都归属于某个列族，列族必须作为表模式(schema)定义的一部分预先给出。如 create ‘test’, ‘course’；

列名以列族作为前缀，每个“列族”都可以有多个列成员(column)；如course:math, course:english, 新的列族成员（列）可以随后按需、动态加入；

权限控制、存储以及调优都是在列族层面进行的；

HBase把同一列族里面的数据存储在同一目录下，由几个文件保存。

###### #Timestamp时间戳  版本号

在HBase每个cell存储单元对同一份数据有多个版本，

根据唯一的时间戳来区分每个版本之间的差异，不同版本的数据按照时间倒序排序，最新的数据版本排在最前面。时间戳的类型是 64位整型。时间戳可以由HBase(在数据写入时自动)赋值，此时时间戳是精确到毫秒的当前系统时间。时间戳也可以由客户显式赋值，如果应用程序要避免数据版本冲突，就必须自己生成具有唯一性的时间戳。

###### Cell单元格

由行和列的坐标交叉决定；单元格是有版本的；单元格的内容是未解析的字节数组；

由{row key， column( =<family> +<qualifier>)， version} 唯一确定的单元。cell中的数据是没有类型的，全部是字节码形式存贮。

###### HLog(WAL log)

HLog文件就是一个普通的Hadoop Sequence File，Sequence File 的Key是HLogKey对象，HLogKey中记录了写入数据的归属信息，除了table和region名字外，同时还包括 sequence number和timestamp，timestamp是” 写入时间”，sequence number的起始值为0，或者是最近一次存入文件系统中sequence number。HLog SequeceFile的Value是HBase的KeyValue对象，即对应HFile中的KeyValue。



###### HBase 架构

###### Client

包含访问HBase的接口并维护cache来加快对HBase的访问

###### Zookeeper

保证任何时候，集群中只有一个master存贮所有Region的寻址入口。实时监控Region server的上线和下线信息。并实时通知Master存储HBase的schema和table元数据

###### Master

为Region server分配region负责Region server的负载均衡发现失效的Region server并重新分配其上的region管理用户对table的增删改操作

###### RegionServer

Region server维护region，处理对这些region的IO请求Region server负责切分在运行过程中变得过大的region

###### Region

HBase自动把表水平划分成多个区域(region)，

每个region会保存一个表里面某段连续的数据每个表一开始只有一个region，

随着数据不断插入表，region不断增大，当增大到一个阀值的时候，region就会等分会两个新的region（裂变）当table中的行不断增多，就会有越来越多的region。这样一张完整的表被保存在多个Regionserver 上。

###### Memstore 与 storefile

一个region由多个store组成，一个store对应一个CF（列族）

store包括位于内存中的memstore和位于磁盘的storefile

写操作先写入memstore，当memstore中的数据达到某个阈值，hregionserver会启动flashcache进程写入storefile，每次写入形成单独的一个storefile当storefile文件的数量增长到一定阈值后，系统会进行合并（minor、major compaction），在合并过程中会进行版本合并和删除工作（majar），形成更大的storefile当一个region所有storefile的大小和数量超过一定阈值后，会把当前的region分割为两个，并由hmaster分配到相应的regionserver服务器，实现负载均衡客户端检索数据，先在memstore找，找不到再找storefile

###### HRegion

HRegion是HBase中分布式存储和负载均衡的最小单元。

最小单元就表示不同的HRegion可以分布在不同的 HRegion server上。HRegion由一个或者多个Store组成，每个store保存一个columns family(列族)。每个Strore又由一个memStore和0至多个StoreFile组成。如图：StoreFile以HFile格式保存在HDFS上。







```doc
Hadoop使用分布式文件系统，用于存储大数据，并使用MapReduce来处理。
Hadoop的限制 Hadoop只能执行批量处理，并且只以顺序方式访问数据。当处理结果在另一个庞大的数据集，也是按顺序处理一个巨大的数据集。在这一点上，一个新的解决方案，需要访问数据中的任何点（随机访问）单元。
Hadoop随机存取数据库 应用程序，如HBase, Cassandra, couchDB, Dynamo 和 MongoDB 都是一些存储大量数据和以随机方式访问数据的数据库。

```

##### 扩展

Scale Out（横向扩展）向外扩展，指的是采购新的设备，和现有设备一起提供更强的负载能力。

Scale Up(纵向扩展)向上扩展，指的是替换掉已经不能满足需求的硬件设备、采购更高性能的硬件设备，从而提升系统的负载能力。

### 1.什么是HBase?

HBase是建立在Hadoop文件系统之上的分布式面向列的数据库。它是一个开源项目，是横向扩展的。

HBase是一个数据模型，类似谷歌的大表设计，可以提供快速随机访问海量结构化数据。它利用了Hadoop的文件系统HDFS提供的容错能力。

HBase是Hadoop的生态系统，提供对数据的随机实时读、写访问，是Hadoop 文件系统的一部分。

人们可以直接或通过HBase的存储HDFS数据。使用HBase在HDFS读取消费/随机访问数据。

HBase在Hadoop的文件系统之上，提供了读写访问。



### 2.HBase和HDFS

​          

HDFS是适于 存储大容量文件的分布式文件系统。

HBase是建立在HDFS之上的数据库。

HDFS 不支持快速单独记录查找。

HBase提供在较大的表快速查找

HDFS提供了高延迟批量处理;没有批处理概念。

HBase提供了数据 十亿条记录低延迟访问单个行记录（随机存取）。

HDFS提供数据只能顺序

HBase内部使用哈希表和提供随即接入 ，并且其存储索引，可将在HDFS文件中的数据进行快速查找

### 3.HBase的存储机制



HBase是一个面向列的数据库，在表中它由行排序。

表模式定义只能列族，也就是键值对。一个表有多个列族以及每一个列族可以有任意数量的列。

表是行的集合。 行是列族的集合。 列族是列的集合。 列是键值对的集合。

面向列和面向行

面向列的数据库是存储数据表作为数据列的部分，而不是作为行数据。

行式数据库  ，它适用于联机事务处理（OLTP）,这样的数据库被设计为小数目的行和列。

列式数据库， 它适用于在线分析处理（OLAP）,面向列的数据库设计的巨大表。

HBase 和RDBMS

```doc
HBase 无模式，它不具有固定列模式的概念;仅定义列族。
它专门创建为宽表。HBase是横向扩展。
没有任何事务存在于HBase。
它反规范化的数据。
它用于半结构以及结构化数据是非常好的。

RDBMS  有它的模式，描述表的整体结构的约束。
这些都是细而专为小表。很难形成规模。
RDBMS是事务性的。
它具有规范化的数据。
用于结构化数据非常好。
```

HBase的特点

```doc
HBase线性可扩展。
它具有自动故障支持。
它提供了一致的读取和写入。
它集成了Hadoop,作为源和目的地。
客户端方便的Java API。
它提供了跨集群数据复制。
```



```doc
在哪里可以使用HBase？
 Apache HBase曾经是随机，实时的读/写访问大数据。 
 它承载在集群普通硬件的顶端是非常大的表。
 Apache HBase是此前谷歌Bigtable模拟非关系型数据库。
 Bigtable对谷歌文件系统操作，同样类似Apache HBase工作在Hadoop HDFS的顶部。
 HBase的应用 
 它是用来当有需要写重的应用程序。
 HBase使用于当我们需要提供快速随机访问的数据。
 很多公司，如Facebook，Twitter，雅虎，和Adobe内部都在使用HBase。
```



## HBase架构

在HBase中，表被分割成区域，并由区域服务器提供服务。区域被列族垂直分为“Stores”。Stores被保存在HDFS文件。下面显示的是HBase的结构。

术语“store”是用于区域来解释存储结构。



HBase有三个主要组成部分：客户端库，主服务器和区域服务器。区域服务器可以按要求添加或删除。



主服务器 主服务器是 - 分配区域给区域服务器并在Apache ZooKeeper的帮助下完成这个任务。 处理跨区域的服务器区域的负载均衡。它卸载繁忙的服务器和转移区域较少占用的服务器。 通过判定负载均衡以维护集群的状态。 负责模式变化和其他元数据操作，如创建表和列。

区域
 区域只不过是表被拆分，并分布在区域服务器。

区域服务器

 区域服务器拥有区域如下

- 与客户端进行通信并处理数据相关的操作。

- 句柄读写的所有地区的请求。

- 由以下的区域大小的阈值决定的区域的大小。

   ​

    需要深入探讨区域服务器：包含区域和存储

存储包含内存存储和HFiles。memstore就像一个高速缓存。在这里开始进入了HBase存储。数据被传送并保存在Hfiles作为块并且memstore刷新。





Zookeeper

 Zookeeper管理是一个开源项目，提供服务，如维护配置信息，命名，提供分布式同步等 

Zookeeper代表不同区域的服务器短暂节点。主服务器使用这些节点来发现可用的服务器。 除了可用性，该节点也用于追踪服务器故障或网络分区。 客户端通过与zookeeper区域服务器进行通信。 在模拟和独立模式，HBase由zookeeper来管理。

#### HBase解压安装配置

tar -zxvf hbase1.4.9.bin.tar.gz

vim /etc/profile

```xml
export HBASE_HOME=/root/hbase-1.4.9
export PATH=$PATH:$JAVA_HOME/bin:$HADOOP_HOME/bin:$HADOOP_HOME/sbin:$HBASE_HOME/bin
```

source /etc/profile

echo $JAVA_HOME
/root/jdk1.8/jdk1.8.0_162

cd conf/

vim hbase-env.sh

export JAVA_HOME=/root/jdk1.8/jdk1.8.0_162



vim hbase-site.xml

```xml
 <property>
    <name>hbase.rootdir</name>
    <value>file:///home/testuser/hbase</value>
  </property>
  <property>
    <name>hbase.zookeeper.property.dataDir</name>
    <value>/home/testuser/zookeeper</value>
  </property>
  <property>
    <name>hbase.unsafe.stream.capability.enforce</name>
    <value>false</value>
    <description>
      Controls whether HBase will check for stream capabilities (hflush/hsync).

      Disable this if you intend to run on LocalFileSystem, denoted by a rootdir
      with the 'file://' scheme, but be mindful of the NOTE below.

      WARNING: Setting this to false blinds you to potential data loss and
      inconsistent system state in the event of process and/or node failures. If
      HBase is complaining of an inability to use hsync or hflush it's most
      likely not a false positive.
    </description>
  </property>
```



Hbase 内部自带zookeeper

启动Hbase    

start-hbase.sh

jps

```doc
HMaster
```

hbase shell

help 查看当下hbase 的命令

输入错误 后 按Ctrl+Backspace删除, 或者光标往前面定位，它会删除光标后面的值

list  查看当前有那些表

删除表时，要先  把表置为disable 状态  在删除，否则删除不了。

describe 查看表描述

disable 'tbl'   所tbl表置 为禁用状态

drop 'tbl'      删除tbl表

create 'tbl1','cf1'  创建tbl1表 ，cf1列族。

describe 'tbl1'  查看tbl1表描述详情

##### 插入数据

put 'tabl1','1111','cf1:name','xiaoming' 向tabl1表中 cf1列族 name列 rowkey为1111 数据为xiaoming

put 'tab1','1','cf1:name','wen'

put 'tab1','1','cf1:age','18'

##### 取数据

get 'tab1','1'    取出表tab1 中rowkey =1   的数据

```doc
COLUMN                        CELL                                                        
 cf1:age                      timestamp=1547608827864, value=18                          
 cf1:name                     timestamp=1547608799501, value=wen                          
1 row(s) in 0.0590 seconds
```

查看所有数据,以下是一条记录

scan 'tab1'

```doc
ROW                           COLUMN+CELL                                                
 1                            column=cf1:age, timestamp=1547608827864, value=18          
 1                            column=cf1:name, timestamp=1547608799501, value=wen        
1 row(s) in 0.0270 seconds

```



#### 修改数据，直接用put  在次创建同样的rowkey,列族，列 并覆盖

put 'tab1','1','cf1:name','hua'

##### 查看表数据本地磁盘位置

cd  /home/testuser/hbase/data/default



##### 删除数据

delete 'tab1','1','cf1:age'

truncate 'tab1'  删除表里面所有的数据



##### 命名空间

list_namespace



#### 完全分布式安装

1. hosts设置,iptables关闭,网络互通，

   1. ssh 免密登录 

       ssh-keygen

      ssh-copy-id  -i  /root/.ssh/id_rsa.pub node2 

2. 安装ntp服务 yum install -y ntp

   时间服务器时间同步  ntpdate  ntp1.aliyun.com

3. jdk 安装

4. hadoop 集群启动 start-dfs.sh

5. 上传解压HBase配置相关配置文件

   scp /etc/profile root@node3:/etc/

   ​

   vi conf/hbase-env.sh   (jdk_home, zk)

   export JAVA_HOME=/root/jdk1.8/jdk1.8.0_162

   export HBASE_MANAGES_ZK=false



​	

​      *conf/hbase-site.xml*	

​	vi   hbase-site.xml

```xml
<configuration>
  <property>
    <!-- 特别注意的是 hbase.rootdir 里面的 HDFS 地址是要跟 Hadoop 的 core-site.xml 里面的 fs.defaultFS 的 HDFS 的 IP 地址或者域名、端口必须一致。 -->
    <name>hbase.rootdir</name>
    <value>hdfs://node1:9000/hbase</value>
  </property>
  <property>
    <!--  为 false 表示单机模式，为 true 表示分布式模式。 -->
    <name>hbase.cluster.distributed</name>
    <value>true</value>
  </property>
  <property>
    <name>hbase.zookeeper.quorum</name>
    <value>node1,node2,node3</value>
  </property>
</configuration>
```

 vi conf/regionservers	

输入	

node2

node3



vi *conf/backup-masters*

node4



把hadoop 的hdfs-site.xml 复制到本地HBase  conf/文件下

cp /root/hadoop/etc/hadoop/hdfs-site.xml    ./  



scp -r hbase-1.4.9/  root@node2:/root/

scp -r hbase-1.4.9/  root@node3:/root/



#### 启动

HBase  start-hbase.sh



 ## HBaseHA 部署

1.hbase-env.sh

```xml
export JAVA_HOME=/root/jdk1.8/jdk1.8.0_162
export HADOOP_HOME=/root/hadoop/hadoop-2.7.7
export HBASE_LOG_DIR=/root/hbase-1.4.9/logs
export HBASE_MANAGES_ZK=false
```



2.hbase-site.xml

```xml
<configuration>

<!-- 设置HRegionServers共享目录 -->
 	<property>
    		<name>hbase.rootdir</name>
    		<value>hdfs://wenjin/hbase</value>
  	</property>

 <!-- 开启分布式模式 -->
  	<property>
            <name>hbase.cluster.distributed</name>
            <value>true</value>
        </property>

        <property>
            <name>hbase.master</name>
            <value>60000</value>
        </property>

 	<!-- 指定缓存文件存储的路径 -->
        <property>
            <name>hbase.tmp.dir</name>
            <value>/root/hbase-1.4.9/tmp</value>
        </property>

        <property>
            <name>hbase.zookeeper.quorum</name>
            <value>node-5,node-6,node-7</value>
        </property>

        <!--指定Zookeeper数据目录，需要与ZooKeeper集群上配置相一致 -->

	<property>
            <name>hbase.zookeeper.property.dataDir</name>
            <value>/tmp/zookeeper/data</value>
        </property>

 	<!-- 指定ZooKeeper集群端口 -->
        <property>
            <name>hbase.zookeeper.property.clientPort</name>
            <value>2181</value>
        </property>

        <property>
            <name>zookeeper.session.timeout</name>
            <value>120000</value>
        </property>

        <property>
            <name>hbase.regionserver.restart.on.zk.expire</name>
            <value>true</value>
        </property>



</configuration>
```





2.regionservers

vim  regionservers

```xml
node-5
node-6
node-7
```

3.新建 backup-masters

vim backup-masters

```xml
node-5
```



创建hbase的缓存文件目录
`cd /root/hbase-1.4.9/`
`mkdir tmp`

创建hbase的日志文件目录
`mkdir logs`

改一下权限

chmod -R 777 tmp

chmod -R 777 logs



将hbase工作目录同步到集群其它节点

 scp -r /root/hbase-1.4.9/  root@node-5:/root/

 scp -r /root/hbase-1.4.9/  root@node-6:/root/

 scp -r /root/hbase-1.4.9/  root@node-7:/root/





在集群各节点上修改用户环境变量

```
export HBASE_HOME=/usr/local/hbase-1.2.6
export PATH=$PATH:$HBASE_HOME/bin
```



source /etc/profile



删除hbase的slf4j-log4j12-1.7.5.jar，解决hbase和hadoop的LSF4J包冲突



## 启动



node-5,node-6,node-7 分别执行 zkServer.sh start



node-4 start-dfs.sh



node-4 start-yarn.sh



node-5 yarn-daemon.sh start resourcemanager



node-4 start-hbase.sh

此命令分别在master1/master2节点启动了HMaster，分别在slave1/slave2/slave3节点启动了HRegionServer。

