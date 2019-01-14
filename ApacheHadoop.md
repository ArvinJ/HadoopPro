# Apache Hadoop(思想:化整为零,并行计算)

#### 简介

```doc
用于开发和运行处理大规模数据的软件平台。允许使用简单的编程模型在大量计算机集群上对大型数据进行分布式处理。
Hadoop是Apache旗下的一款软件，更广泛的说是Hadoop生态圈，下面是Hadoop2.0 的核心组件
HDFS  （hadoop分布式文件系统）：解决海量数据存储
MapReduce（分布式运算编程框架）：解决海量数据计算处理
YARN(作业调度和集群资源管理的框架)：解决资源任务调度资源管理
Common  常用工具


Hadoop 是Apache Lucene 创始人 Doug Cutting 创建的。
基于谷歌的GFS   MapReduce  BigTable 三篇论文思想 开发出来了。
Hadoop特点
扩容能力强
成本低
高效率
可靠性


Hadoop 集群包含两个集群：HDFS集群和YARN集群，两都逻辑上分离，物理上常在一起。
HDFS 集群负责海量数据的存储，集群中的角色有NameNode、DataNode、SecondaryNameNode。
YARN集群负责海量数据运算时的资源调度，集群中的角色有ResourceManager、NodeManager
mapreduce 是一个分布式运算编程框架，是应用程序开发包，由用户按照编辑规范进行程序开发，后打包运行在HDFS集群上，并且受到YARN集群的资源调试管理。
```



#### 存储模型

```
按字节存储
文件线性切割成块（Block）
Block 分散存储在集群节点中
单一文件Block大小一致，文件与文件可以不一样
Block 分散在无序的节点上，Block可以设置副本数，副本数不要超过节点数。
文件上传可以设置Block大小和副本数。
已经上传的文件Block副本数可以调整，大小不变。
只支持一次写入多次读取，同一时刻只有一个写入者。
可以append追加数据，不能修改数据。
```

#### Block 的放置策略

```doc
第一个副本：放置在上传文件的DataNode;如果是集群外提交，则随机挑选一台磁盘不太满，CPU不太忙的节点点。
第二个副本：放置在第一个副本不同的机架的节点上。
第三个副本：放置在与第二个副本相同机架不同节点上。
更多副本：随机节点。
```



#### 架构模型

```doc
文件元数据MetaData，文件数据包括了	元数据 和 数据本身 组成。
（主）NameNode节点保存文件元数据：单节点   posix
（从）DataNode节点保存文件Block数据：多节点
DataNode与NameNode保持心跳，提交Block列表
HdfsClient与NameNode交互元数据信息
HdfsClient与DataNode交互文件Block数据
DataNode利用服务器本地文件系统存储数据块
```

1.NameNode(NN)

```doc
基于内存存储 ：不会和磁盘发生交换
只存在内存中
持久化
NameNode主要功能：
接受客户端的读写服务
收集DataNode汇报的Block列表信息
NameNode保存metadata信息包括
文件owership和permissions
文件大小，时间
（Block列表：Block偏移量），位置信息
Block每副本位置（由DataNode上报）

NameNode持久化
NameNode的metadate信息在启动后会加载到内存
metadata存储到磁盘文件名为”fsimage”
Block的位置信息不会保存到fsimage
edits记录对metadata的操作日志。。。redis
```

2.DataNode（DN）

```doc
本地磁盘目录存储数据（Block），文件形式
同时存储Block的元数据信息文件
启动DN时会向NN汇报block信息
通过向NN发送心跳保持与其联系（3秒一次），如果NN 10分钟没有收到DN的心跳，则认为其已经lost，并copy其上的block到其它DN
```

3.SecondaryNameNode（SNN）

```doc
它不是NN的备份（但可以做备份），它的主要工作是帮助NN合并edits log，减少NN启动时间。
SNN执行合并时机
根据配置文件设置的时间间隔fs.checkpoint.period  默认3600秒
•  根据配置文件设置edits log大小 fs.checkpoint.size 规定edits文件的最大值默认是64MB	

```



#### Hadoop版本

```doc

GA  和 Release 都 是官方正式发布版
Beta 对外测试版
Alpha 内部测试版

Hadoop2系列中最稳定版本：Apache Hadoop2.7.4
现在已经到达  Hadoop3 了

```

#### Hadoop3 新特征

```doc



```



#### HDFS优点

```doc
高容错性
数据自动保存多个副本
 副本丢失后，自动恢复
适合批处理
移动计算而非数据
数据位置暴露给计算框架（Block偏移量）
适合大数据处理
GB 、TB 、甚至PB 级数据
百万规模以上的文件数量
10K+ 节点
可构建在廉价机器上
通过多副本提高可靠性
提供了容错和恢复 机制

```

#### HDFS缺点

````doc

低延迟数据访问
比如毫秒级
低延迟与高吞吐率
小文件存取
占用NameNode 大量内存
寻道时间超过读取时间
并发写入、文件随机修改
一个文件只能有一个写者
仅支持append

````



#### HDFS 写流程

```doc
Client：
切分文件Block
按Block线性和NN获取DN列表（副本数）
验证DN列表后以更小的单位流式传输数据
各节点，两两通信确定可用
Block传输结束后：
DN向NN汇报Block信息
DN向Client汇报完成
Client向NN汇报完成
获取下一个Block存放的DN列表
。。。。。。
最终Client汇报完成
NN会在写流程更新文件状态

```

#### HDFS读流程

```doc
Client：
和NN获取一部分Block副本位置列表
线性和DN获取Block，最终合并为一个文件
在Block副本列表中按距离择优选取

```

#### 安全模式

```doc
namenode启动的时候，首先将映像文件(fsimage)载入内存，并执行编辑日志(edits)中的各项操作。
一旦在内存中成功建立文件系统元数据的映射，则创建一个新的fsimage文件(这个操作不需要SecondaryNameNode)和一个空的编辑日志。
此刻namenode运行在安全模式。即namenode的文件系统对于客服端来说是只读的。(显示目录，显示文件内容等。写、删除、重命名都会失败)。
在此阶段Namenode收集各个datanode的报告，当数据块达到最小副本数以上时，会被认为是“安全”的， 在一定比例（可设置）的数据块被确定为“安全”后，再过若干时间，安全模式结束
当检测到副本数不足的数据块时，该块会被复制直到达到最小副本数，系统中数据块的位置并不是由namenode维护的，而是以块列表形式存储在datanode中。
```



#### Hadoop - hdfs

````doc
集群
角色==进程
namenode
数据元数据
内存存储，不会有磁盘交换
持久化（fsimage，eidts log）
不会持久化block的位置信息
block：偏移量，因为block不可以调整大小，hdfs，不支持修改文件
偏移量不会改变
datanode
block块
磁盘
面向文件，大小一样，不能调整
副本数，调整，（备份，高可用，容错/可以调整很多个，为了计算向数据移动）
SN
NN&DN
心跳机制
DN向NN汇报block信息
安全模式
client

````







#### Hadoop 集群搭建

前期准备

centos 7 64bit



网络环境要调通,确保各节点时间 同步

手动同步集群各机器时间

date -s "2018-11-27 11:10:20"

yum install ntpdate

网络同步时间

ntpdate cn.pool.ntp.org



设置主机名

vi /etc/sysconfig/network

NETWORKING=yes

HOSTNAME=node-1

然后执行设置当前的hostname(立即生效） 

hostname node-1   

编辑hosts文件，给127.0.0.1添加hostname

cat /etc/hosts                                       检查
127.0.0.1 localhost localhost.localdomain localhost4 localhost4.localdomain4 node-1
::1 localhost localhost.localdomain localhost6 localhost6.localdomain6

vim /etc/hosts





```doc
修改hostname
hostnamectl set-hostname node-1             # 使用这个命令会立即生效且重启也生效
vi /etc/hosts
编辑下hosts文件， 给127.0.0.1添加hostname
cat /etc/hosts                                           # 检查
127.0.0.1   localhost localhost.localdomain localhost4 localhost4.localdomain4 node-1
::1         localhost localhost.localdomain localhost6 localhost6.localdomain6
```



配置IP、主机名映射

vi /etc/hosts

192.168.1.11     node-1

192.168.1.12     node-2

192.168.1.13     node-3

查看

cat   /etc/hosts



配置ssh免密登陆

生成ssh免登录密钥

ssh-keygen -t rsa  (四个回车)

执行完这个命令后，会生成id_rsa(私钥)、id_rsa.pub(公钥)

将公钥拷贝到要免密登陆的目标机器 上

ssh-copy-id node-02



ssh node-3

无需密码直接登录上

exit 

退出当前登录的node-3





配置防火墙

查看防火墙状态

service iptables status

关闭防火wall

service iptables stop

查看防火墙开机启动状态

chkconfig iptables --list

关闭防火墙开机启动

chkconfig  iptables off

这里关闭防火墙。常规思路内网中一般关闭，外网时 把需要开放的端口开放出来。

```doc
一、.对于centos7自带的防火墙的相关指令 
systemctl stop firewalld.service #停止firewall 
systemctl disable firewalld.service #禁止firewall开机启动 
systemctl status firewalld.service #查看firewall的状态

二、iptables防火墙的相关状态 
关闭虚拟机防火墙： 
关闭命令： service iptables stop 
永久关闭防火墙：chkconfig iptables off 
两个命令同时运行，运行完成后查看防火墙关闭状态 
service iptables status 
1 关闭防火墙—–service iptables stop 
2 启动防火墙—–service iptables start 
3 重启防火墙—–service iptables restart 
4 查看防火墙状态–service iptables status 
5 永久关闭防火墙–chkconfig iptables off 
6 永久关闭后启用–chkconfig iptables on
```







 



JDK环境安装1.8

上传jdk安装包

解压安装包

tar -zxvf  jdk-....

配置环境变量

vi /etc/profile

export JAVA_HOME=/root/jdk1.8.0_65

exprot PATH=$PATH:$JAVA_HOME/bin

exprot  CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar

刷新配置

source   /etc/profile

 

进入http://hadoop.apache.org/  官网

下载hadoop

tar zxvf hadoop-2.7.7.tar.gz

解压



hadoop 配置 文件修改

一般在主节点进行修改，完毕后scp下发给其他各个从节点机器。

vi hadoop-env.sh

export JAVA_HOME=/root/jdk1.8/jdk1.8.0_65

export HDFS_NAMENODE_USER=root

export HDFS_DATANODE_USER=root

export HDFS_SECONDARYNAMENODE_USER=root

 which java

查看本地java 安装路径
/root/jdk1.8/jdk1.8.0_162/bin/java



vi core-site.xml



```xml
<property>
  <name>fs.defaultFS</name>
  <value>hdfs://node-1:9000</value>
  </property>
<property>
  <name>hadoop.tmp.dir</name>
<value>/root/hadoop/hadoop-2.7.7/tmp</value>
  </property>
```



 hdfs-site.xml



```xml
 <!-- 指定HDFS副本的数量，默认备份为3，这里我们指定为2 -->
<property>
  <name>dfs.replication</name>
  <value>2</value>
</property>
<property>
	<name>dfs.namenode.secondary.http-address</name>
  <value>node-2:50090</value>
</property>

```



mapred-site.xml

mv mapred-site.xml.template mapred-site.xml

vi mapred-site.xml 

```xml
<!-- 指定mr运行时框架，这里指定在yarn上，默认是local -->

<property>
  <name>mapreduce.framework.name </name>
  <value>yarn</value>
</property>
```





yarn-site.xml

```xml
<!-- 指定YARN的老大（ResourceManager）的地址 -->
<property>
<name>yarn.resourcemanager.hostname</name>
<value>node-1</value>
</property>
<!-- NodeManager 上运行的附属服务。需配置成mapreduce_shuffle,才可运行MapReduce程序默认值 -->

<property>
<name>yarn.nodemanager.aux-services</name>
 <value>mapreduce_shuffle</value>
</property>

```





slaves文件，里面写上从节点所在的主机名字

vi slaves

node-1

node-2

node-3

-------------------------------------------------

在hadoop 3的情况下  没有了slaves 文件  要修改 workers ,输入同样的内容。



vi /etc/profile

```doc

export HADOOP_HOME=/root/hadoop/hadoop-2.7.7/

export PATH=$PATH:$JAVA_HOME/bin:$HADOOP_HOME/bin:$HADOOP_HOME/sbin
```

source /etc/profile



hadoop 环境分发到从节点

scp -r  /root/hadoop/hadoop-2.7.7/  root@node-2: /root/hadoop/ 

scp -r /etc/profile  root@node-2:/etc/

在从节点上也把环境变量加载

执行source /etc/profile命令





关于hadoop 的配置文件：

```doc
***-default.xml 这里面配置了hadoop默认的配置选项
如果用户没有更改    那么这里面的选项将会生效
***-site.xml 这里面配置了用户需要 自定义的配置选项
site中配置选项做优先级>Default中的，如果有配置的话，就会覆盖默认的配置选项
```



关于hdfs 的格式化：

1首次启动需要进行格式 化。

2格式 化本质是进行文字系统的初始化操作，创建 一些自己所需要 的文件。

3格式化之后   集群启动成功   后续再也不会进行格式化。

4格式化的操作在hdfs集群的主角色（namenode）所在机器上操作

格式化命令

hdfs namenode -format 或者 hadoop namenode  -format





集群一键启动

前提配置好了 etc/hadoop/slaves 和ssh免密登录 ，则可以使用程序脚本启动所有Hadoop两个集群的相关进程，在主节点所设定的机器 上执行。

进入hadoop 的安装目录   /sbin 目录下

执行    start-dfs.sh

starrt-yarn.sh

停止集群  stop-dfs.sh    ,   stop-yarn.sh

 

单节点逐个启动

在主节点上使用以下命令启动HDFS NameNode:

hadoop-daemon.sh start namenode

在每个从节点上使用以下命令启动HDFS DateNode:

hadoop-daemon.sh start datanode

在主节点上使用以下命令启动YARN ResourceManager:

yarn-daemon.sh start resourcemanager

在每个从节点上使用以下命令启动 YARN nodemanager:

yarn-daemon.sh start nodemanager

以上脚本位于hadoop的安装目录/sbin下  如果想停止某个节点某个角色，只需要把命令中的start改为stop即可。



命令  jps   查看当前 node节点的角色



集群web-ui

一旦Hadoop集群启动并运行，可以通过web-ui进行集群查看

NameNode http://node-1:50070

http://192.168.13.136:50070

ResourceManager http://node-1:8088



查看hdfs资源中有什么 

hdfs dfs  -ls  /

在hdfs下创建一个hello文件

hdfs  dfs  -mkdir  /hello

给hello文件夹中上传一个文件

hdfs dfs -put  test.txt   /hello



运行mapreduce程序

cd  /hadoop2.7.7/share/hadoop/mapreduce下有官方自带的mapreduce程序

计算圆周率

hadoop jar  hadoop-mapreduce-examples-2.7.7.jar  pi  20 50





HDFS入门

HDFS是Hadoop Distribute File System .

Hadoop分布式文件系统，作为最底层的分布式存储服务而存在。

分布式文件系统解决的问题就是大数据存储。





 master/slave架构

HDFS 采用 master/slave架构。一般一个HDFS集群是有一个Namenode和一定数目 的Datanode组成。

Namenode是HDFS集群主节点，Datanode是HDFS集群从节点。

分块存储

HDFS中的文件在物理上是分块存储（block）的。块的大小可以配置，默认大小 是128M。



名字空间（NameSpace）

HDFS支持传统的目录树层次型文件组织结构。

Namenode 负责维护文件系统的名字空间。

 

Namenode 元数据管理

目录结构及文件分块位置信息叫做元数据。

Namenode 负责维护整个hdfs文件系统的目录树结构，以及每一个文件所对应的block块信息（block的id,以及所在的datanode服务器）。



Datanode数据存储

文件的各个block的具体存储管理由datanode节点承担。每一个block 都可以在多个datanode上。Datanode需要定时向Namenode 汇报自己持有的block信息。

存储多个副本  可以通过参数设置 dfs.replication,默认为3。



副本机制

为了容错，文件的所有block都会有副本。 防止单点故障。



一次写入，多次读出

HDFS 是设计成适应一次写入，多次读出的场景，且不支持文件的修改。

HDFS适合用来做大数据分析的底层存储服务，不适合用来做网盘。因为，修改不方便，延迟大，网络开销大，成本高。





HDFS 基本操作

Shell 命令行客户端

查看 hdfs 下的资源

hadoop fs -ls hdfs://node-1:9000  或 hadoop fs -ls /

hadoop1  命令如下

hdfs dfs -ls  /



查看本地root目录下的资源

hadoop fs -ls  file:///root   



Shell常用命令介绍

-ls  查看

hadoop fs -ls /hello



hadoop fs -ls -h  -R /hello   

-h 显示具体tb,gb,mb,kb单位

-R  递归向下的子文件



-put  上传

把  本地的intall.log上传到hdfs 的hello 文件夹下

hadoop fs -put  /root/install.log    /hello

第二次上传报  文件已经存在

hadoop fs -put   /root/install.log    /hello

加 -f    覆盖已经存丰

hadoop fs -put -f  /root/install.log /hello



-get  下载

hadoop fs -get  /hello/install.log   /root

hadoop fs -get /hello/test  /root/wen

前面是hdfs的文件路径，后面是本地路径。



-cat  查看

hadoop  fs -cat /hello.1.txt



-appendToFile    追加

hadoop 不支持修改，支持加文件的尾追加

hadoop fs -put  /root/1.txt  /hello

hadoop fs -appendToFile  /root/2.txt   /hello/1.txt



-tail   文件特别大的，可以查看文件的最后一千字节内容。

hadoop fs -tail  /hello/1.txt



修改权限

hadoop -fs  -chmod 777  /hello/1.txt



-getmerge  合并多个文件

hadoop fs  -getmerge /hello/1.txt  /hello/2.txt



-df   查看 hdfs 可用空间

hadoop fs -df  -h



-setrep设置指定文件的副本 数设置

hadoop fs  -setrep  -w  4   -R    /hello/1.txt





## 二、HDFS 基本原理

#### 1.NameNode

NameNode 是HDFS的核心。

NameNode 也称为Master（带头大哥）。

```doc
NameNode 仅存储HDFS的元数据：文件系统中所有文件的目录树，并跟踪整个集群的文件。

NameNode 不存储实际数据或数据集。数据本身实际存储在DataNodes 中。

NameNode 知道HDFS中任何给定 文件的块列表及位置。使用此信息NameNode知道如何从块中构建文件。

NameNode并不持久化存储每个文件中各个块所在的DataNode 的位置信息，这信息会在系统启动时从数据节点重建。

NameNode 对于HDFS 到关重要，当NameNode关闭时，HDFS/Hadoop集群无法访问。

NameNode 是Hadoop集群中的单点故障。

NameNode所在机器通常会配置有大量内存（RAM）。
```

#### 2.DataNode

DataNode 负责将实际数据存储在HDFS中。

DataNode 也称为Slave（跟班小弟）。

```doc
NameNode 和 DataNode 会保持不断通信 。
DataNode 启动时，它将自己发布到NameNode 并汇报自己负责持有的块列表。
当某个DataNode 关闭时，它不会影响数据或集群的可用性。NameNode将安排由其他DataNode管理的块进行副本复制。
DataNode 所在机器通常配置有大量的硬盘空间。因为实际的数据存储在DataNode中。
DataNode 会定期（dfs.heartbeat.interval配置项配置，默认是3秒）向NameNode发送心跳，如果NameNode长时间没有接受到DataNode发送的心跳，NameNode就会认为该DataNode失效。
block 汇报时间间隔取参数 dfs.blockreport.intervalMsec，参数未配置的话默认为6小时。
```

#### 3.HDFS的工作机制

NameNode负责管理整个文件系统元数据

DataNode负责管理具体文件数据块存储

Secondary  NameNode 协助NameNode 进行元数据的备份。

HDFS的内部工作机制对客户端保持透明，客户端请求访问HDFS都 是通过向NameNode申请来进行。





#### 4.HDFS写数据流程

场景

NameNode 职责：

响应所有客户端的请求，维护着文件系统目录树（元数据），管理所有的DataNode。

————————————————————

HDFS shell 客户端：

hadoop fs -put a.txt  / 

通过命令上传  a.txt 到hdfs 根目录。

————————————————————

DataNode 职责：

存储数据文件，数据集。

```doc
step1:client 发起文件上传请求，通过RPC与NameNode建立通讯，
NameNode检查目标文件是否已存在，父目录是否存在，返回是否可以上传。
step2:client 请求第一个block该传输到哪些DataNode服务器上。
step3:NameNode根据配置文件中指定的备份数据及机架拓扑原理进行文件分配，返回可用的DataNode的地址如A,B,C。（Hadoop在设计时考虑到数据的安全与高效，数据文件默认在HDFS上存放三份，存储策略为本地一份，同机架内其它某一节点上一份，不同机架的某一节点一份。）
step4:client 请求3台DataNode 中的一台A上传数据（本质上是一个RPC调用，建立匹配pipeline）,A收到请求会继续调用B,然后B调用C,将三个pipeline 建立完成，后逐级返回client。
step5:开始往上传第一个block，以packet为单位（默认64k），A收到一个packet就会传给 B,B传给C,A每传一个packet会放入一个应答队列等待应答。
step6:数据被分割成一个个packet数据包在pipeline(通道)上依次传输，在pipeline反方向上，逐个发送ack(命令正确应答)，最终由pipeline中第一个DataNode节点A将pipeline  ack 发送给client。
step7:当一个block 传完成 之后，client 再次请求NameNode上传第二个block到服务器。

```



#### 5.HDFS读数据流程

HDFS shell 客户端

hadoop fs -get   /a.txt     /root/file/

```doc
step1:Client向NameNode发起RPC请求，来确定请求文件block所在的位置。
step2:NameNode会视情况返回文件的部分或者全部block列表，对于每个block，NameNode都会返回含有该block副本的DataNode地址。
step3:这些返回的DataNode地址，会按照集群拓扑结构得出DataNode与客户端的距离，然后进行排序，排序两个规则：网络拓扑结构中距离Client近的排靠前，心跳机制中超时汇报的DataNode状态 为STALE,这样的排靠后。
step4:Client 选择排序靠前的DataNode来读取block，如果客户端本身就是DataNode,那么将从本地直接获取数据。
step5:底层上本质是建立Socket Strem 重复的调用父类DataInputStream的read方法，直到这个块上的数据读取完毕。
step6:当读完列表的block后，若文件读取还没有结束，客户端会继续向NameNode获取下一批的block列表。
step7:读取完一个block都会进行checksum验证，如果读取DataNode时出现错误，客户端会通知NameNode，然后再从下一个拥有譔block副本的DataNode进行读取。
step8:read方法是并行的读取block 信息，不是一块一块的读取，NameNode只是返回Client请求包含块的DataNode地址，并不是返回请求块的数据。
step9:最终读取来所有的block会合并成一个完整的最终文件。

```



## 三、HDFS的应用开发

#### 1.HDFS的JAVA API操作

HDFS的生产应用中主要是客户端的开发，其核心步骤是从HDFS提供的api中构造一个HDFS的访问客户端对象，然后通过该客户端对象操作（CRUD）HDFS上的文件。

##### 1.1 搭建开发环境

创建Maven工程，引入 pom依赖

```xml
<dependencies>
    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>3.8.1</version>
        <scope>test</scope>
  </dependency>

  <dependency>
      <groupId>org.apache.hadoop</groupId>
      <artifactId>hadoop-hdfs</artifactId>
      <version>2.7.7</version>
  </dependency>

  <dependency>  
      <groupId>org.apache.hadoop</groupId>  
      <artifactId>hadoop-client</artifactId>  
      <version>2.7.7</version>  
  </dependency> 

  <dependency>
      <groupId>org.apache.hadoop</groupId>
      <artifactId>hadoop-common</artifactId>
      <version>2.7.7</version>
  </dependency>

</dependencies>



```



##### 1.2构造客户端对象

在java中操作HDFS

Configuration 该类的对象封装了客户端或者服务器的配置。

FileSystem 譔类的对象是一个文件系统对象，可以用该对象的一些方法对文件进行操作，通过FileSystem的静态方法get获得对象。

FileSystem fs = FileSystem.get(conf);

get方法从conf中的一个参数fs.defaultFS的配置值判断具体是什么类型的文件系统。

如果我们在代码中没有指定fs.defalutFS,并且工程classpath下也没有给定相应的配置，conf中的默认值就来自于hadoop的jar包中的core-default.xml 默认值为  file:///  ，则获取的将不是一个DistributedFileSystem的实例，而是一个本地文件系统的客户端对象。

##### 1.3HDFS 俺封装的 API

````java
package com.wenjin.zhu.hdfs;

import java.io.IOException;
import java.io.InputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

/**
 * 
 * 
 * @Title: HDFSManager.java
 * @Package com.wenjin.zhu.hdfs
 * @Description: java操作HDFS API
 * @author: wenjin.zhu
 * @date: 2018年12月3日 下午3:59:01
 * @version V1.0
 */
public class HDFSManager {
	/**
	 * 9000端口只允许本机访问，我远程连接当然会提示拒绝访问了。 可能是Hadoop为了保证集群的安全性，默认的是本集群之间的访问，
	 * 只允许配置的某一个IP访问吧。于是我想能不能把这个端口放开，允许所以IP访问。 于是我把hosts中的文件做了下面的修改。 0.0.0.0
	 * hadoop-master
	 * 
	 */
	private static String HDFSUri = "hdfs://192.168.13.136:9000";

	public static void main(String[] args) {

		Configuration conf = new Configuration();
		// 这里指定使用的是hdfs文件系统
		conf.set("fs.defaultFS", HDFSUri);
		// 通过 如下方式进行客户端身份的设置
		System.setProperty("HADOOP_USER_NAME", "root");
		// 通过FileSystem 的静态方法获取文件系统客户端对象
		try {
			FileSystem fs = FileSystem.get(conf);
			// 也可以通过如下 方式 指定文件系统的类型，并且 同时设置用户身份。
			// FileSystem fs2 = FileSystem.get(new URI(HDFSUri), conf,"root");

			// 列出hdfs上/根目录下的所有文件和目录
			FileStatus[] statuses = fs.listStatus(new Path("/"));
			for (FileStatus status : statuses) {
				System.out.println(status);
			}

			// 1.创建文件并输入一些文字。
			// createFile(fs,"/hello/temp1.log");
			// createFile(fs,"/hello/createDir1/temp1.txt");

			// 2.创建一个目录
			// createDir(fs,"/hello/createDir1");

			// 3.删除一个目录
			// deleteDir(fs,"/hello/createDir1",false);
			// deleteDir(fs,"/hello/createDir1",true);

			// 4.查看是否有该目录或 文件
			// System.err.println("--->"+isExists(fs,"/hello/temp1.log"));

			// 5.读取文件内容
			// readFile(fs,"/hello/temp1.log");

			// 6.上传文件
			// uploadFile(fs,"D://1mm//ygh.xls","/hello");

			// 7.下载文件
			// downloadFile(fs, "/hello/zwj.doc", "D://1mm//");

			// 关闭对象
			fs.close();
			// fs2.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.err.println("end");

	}

	/**
	 * 创建一个目录
	 */
	public static void createDir(FileSystem fs, String path) throws IllegalArgumentException, IOException {
		fs.mkdirs(new Path(path));
	}

	/**
	 * 删除一个目录或文件
	 */

	public static void deleteDir(FileSystem fs, String path, boolean temp)
			throws IllegalArgumentException, IOException {
		// fs.delete(new Path("/hello/temp1")); 可以删除一切目录，此方法过时了。
		fs.delete(new Path(path), temp); // 当一个目录下有其他文件或目录时，当这里是false时，我们就不可以删除该目录，改成true就可以忽略这个条件
	}

	/**
	 * 在目录下创建一个log文件，并写入一行文本
	 */
	public static void createFile(FileSystem fs, String path) throws IllegalArgumentException, IOException {
		// Create 在hdfs的/hello/目录下创建一个log文件，并写入一行文本
		FSDataOutputStream os = fs.create(new Path(path));
		os.write(("Hello   " + path).getBytes());
		os.flush();
		os.close();
	}

	/**
	 * 读取文件内容
	 */
	public static void readFile(FileSystem fs, String filePath) throws IllegalArgumentException, IOException {

		InputStream in = fs.open(new Path(filePath));
		IOUtils.copyBytes(in, System.out, 4096, false);
		IOUtils.closeStream(in);

	}

	/**
	 * 检查文件或目录是否存在
	 */
	public static boolean isExists(FileSystem fs, String filePath) throws IllegalArgumentException, IOException {

		return fs.exists(new Path(filePath));

	}

	/**
	 * 上传文件
	 */
	public static void uploadFile(FileSystem fs, String hdfsPath, String filePath)
			throws IllegalArgumentException, IOException {
		// put 上传文件 前者本地文件 ，后者上传的目标位置
		fs.copyFromLocalFile(new Path(hdfsPath), new Path(filePath));

	}

	/**
	 * 下载文件
	 * 
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public static void downloadFile(FileSystem fs, String hdfsPath, String filePath)
			throws IllegalArgumentException, IOException {
		// Download下载 文件 前者路径是 hdfs 后者路径是本地file文件系统目录
		fs.copyToLocalFile(false, new Path(hdfsPath), new Path(filePath), true);
	}

}

````





##### error

###### 9000端口无法访问问题

netstat -tpnl 查看端口占用情况，是否私有，只允许本地机器访问。

192.168.13.136:9000  这样子的  说明就是可以被访问的

如果是127.0.0.1:9000就不可以被外部机器访问，仅允许本地访问。

###### (null) entry in command string: null chmod 0644

 fs.copyToLocalFile(hdfs_path, local_path);

更换成下面这种写法就不报错了

 fs.copyToLocalFile(false,hdfs_path, local_path,true);

fs.copyToLocalFile(false,new Path("/hello/zwj.doc"), new Path("D://1mm//"),true);

第一个false参数表示不删除源文件，第4个true参数表示使用本地原文件系统，因为这个程序是在Windows系统下运行的。

###### executable null\bin\winutils.exe

本地windows 安装并解压win版hadoop环境，并配置Home和path 即可。



## 四、MapReduce

### 1.MapReduce  Thinkings

MapReduce 思想核心是 “分而治之”

适用于大量复杂的任务处理场景。

Map负责“分”   复杂的分若干个简单的,而且互相之间没有依赖进行处理（分解成若干个并行操作）。

Reduce负责“合”   对map阶段的结果进行全局汇总。

————————————————————————

MapReduce是一个分布式运算程序的编程框架。

核心功能是 将用户编写的业务逻辑代码和自带默认组件整合成一个完整的分布式运算程序，并发运行在Hadoop集群上。

MapReduce框架结构

一个完整的mapreduce程序在分布式运行时有三类实例进程：

1、MRppMaster ：负责整个程序的过程调度及状态协调。

2、MapTask：负责map阶段的整个数据处理流程。

3、ReduceTask：负责reduce阶段的整个数据处理流程。



### 2.MapReduce 编程规范

step1.用户编写的程序分为三个部分，Mapper,Reducer,Driver(提交运行mr程序的客户端)

step2.Mapper的输入数据是KV键值对的形式（KV类型可自定义）

step3.Mapper的输出数据是KV键值对的形式（KV类型可自定义）

step4.Mapper中的业务逻辑写在map()方法中。

step5.map()方法 对每一个<K,V>调用一次。

step6.Reducer的输入数据类型对应着Mapper的输出数据类型。也是KV。

step7.Reducer的业务逻辑写在reduce()方法中。

step8.Reducetask进程对每一组相同的k的<k,v>组调用 一次reduce()方法。

step9.用户自定义的Mapper和Reducer都要继承各自的父类。

step10.整个程序需要一个Driver来进行提交，提交的是一个描述了各种必要信息的job对象。



### 3.编写第一个MapReduce小程序

WordCount

需求：在一堆给定的文本文件中统计输出每一个单词出现 的总次数。

















## 五、HBase

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







## FTP服务器搭建

1、安装vsftpd

```
1 yum -y install vsftpd
```

2、设置开机启动

```
1 systemctl enable vsftpd
```

3、启动ftp服务

```
1 systemctl start vsftpd.service
```

4、打开防火墙，开放21端口

```
1 firewall-cmd --zone=public --add-port=21/tcp --permanent
2 firewall-cmd --permanent --zone=public --add-service=ftp
3 firewall-cmd --reload
```


### Linux 离线安装 FTP

首先检查是否安装过： rpm -qa | grep vsftpd

如果出现  vsftpd-xxx，那就说明安装了。

没有即进行以下安装。



下载 FTP：http://rpmfind.net/linux/rpm2html/search.php?query=vsftpd(x86-64)



```
查看系统版本
cat /etc/*release*
```

将下载好的包，上传到服务器。

 查看vsftpd版本：vsftpd –version

1、 安装vsftpd

　　rpm -ivh vsftpd-3.0.2-21.el7.x86_64.rpm

2、 测试是否安装成功

　　[root@localhost ~]# service vsftpd start
　　为 vsftpd 启动 vsftpd：[确定]

3、 配置

      [root@localhost ~]# whereis vsftpd

　　vsftpd:  /usr/sbin/vsftpd    /etc/vsftpd    /usr/share/man/man8/vsftpd.8.gz

　　yum安装的主要目录为上述的3个目录，其中配置文件vsftpd.conf在/etc/vsftpd中，下面看下怎么配置vsftpd.conf







默认配置文件: /etc/vsftpd/vsftpd.conf

```xml
# Example config file /etc/vsftpd/vsftpd.conf

anonymous_enable=NO #不允许匿名访问
local_enable=YES #接受本地用户
write_enable=YES#可以上传(全局控制)
local_umask=022#本地用户上传文件的umask
allow_ftpd_full_access
#anon_upload_enable=NO #匿名用户可以上传
#anon_mkdir_write_enable=NO #匿名用户可以建目录
#anon_other_write_enable=NO #匿名用户修改删除
dirmessage_enable=YES
xferlog_enable=YES
connect_from_port_20=YES
#chown_uploads=YES
#chown_username=whoever #匿名上传文件所属用户名
#xferlog_file=/var/log/xferlog
xferlog_std_format=YES
#idle_session_timeout=600
#data_connection_timeout=120
#nopriv_user=ftpsecure
#async_abor_enable=YES
#ascii_upload_enable=YES
#ascii_download_enable=YES
#deny_email_enable=YES
#banned_email_file=/etc/vsftpd/banned_emails
chroot_local_user=YES #禁止切换根目录
#chroot_list_enable=YES
chroot_list_file=/etc/vsftpd/chroot_list
#
#ls_recurse_enable=YES
listen=NO
listen_ipv6=YES

pam_service_name=vsftpd
userlist_enable=NO
tcp_wrappers=YES

userlist_file=/etc/vsftpd/user_list
allow_writeable_chroot=YES


```

```doc

error
1.ftp 连接 提示 500 OOPS: vsftpd: refusing to run with writable root inside chroot()
ftp根目录（/var/ftp）的权限不对,不允许完全没有限制777。
从2.3.5之后，vsftpd增强了安全检查，如果用户被限定在了其主目录下，则该用户的主目录不能再具有写权限了！如果检查发现还有写权限，就会报该错误。

 要修复这个错误，可以用命令chmod a-w /home/user去除用户主目录的写权限，注意把目录替换成你自己的。或者你可以在vsftpd的配置文件中增加下列两项中的一项：
allow_writeable_chroot=YES

2.500 OOPS: cannot locate user entry:nobody

原因为系统的nobody账号被删除


编辑/etc/vsftpd/vsftpd.conf
将nopriv_user=ftpuser用户，修改为一个系统存在的账号即可。
```



如果启用了selinux，则需要禁用，禁用selinux,否则在文件上传的时候会提示vsftp 553 Could not create file。



```
vi /etc/selinux/config 


SELINUX=disabled    # 这儿


getsebool -a | grep ftp

setsebool allow_ftpd_full_access on
getsebool -a | grep ftp

```





```doc

vsftpd 的配置目录为 /etc/vsftpd，包含下列的配置文件：

vsftpd.conf 为主要配置文件
ftpusers 配置禁止访问 FTP 服务器的用户列表
user_list 配置用户访问控制
```



```doc

添加用户

2.添加ftp用户

下面是添加ftpuser用户，设置根目录为/home/wwwroot/ftpuser,禁止此用户登录SSH的权限，并限制其访问其它目录。
#chroot_list_enable=YES
# (default follows)
#chroot_list_file=/etc/vsftpd.chroot_list

改为
chroot_list_enable=YES
# (default follows)
chroot_list_file=/etc/vsftpd/chroot_list

3.增加用户ftpuser，指向目录/home/wwwroot/ftpuser,禁止登录SSH权限。
useradd -d /home/ftpuser -g ftp -s /sbin/nologin ftpuser

4.设置用户口令
passwd ftpuser

5、编辑文件chroot_list:
vi /etc/vsftpd/chroot_list

内容为ftp用户名,每个用户占一行,如：

ftpuser
john

6、重新启动vsftpd
service vsftpd restart
```











useradd ftpuser

passwd ftpuser



useradd -d /home/ftpuser ftpuser//增加用户ftpuser，并制定ftpuser用户的主目录为/home/ftpuser

passwd ftpuser

```doc
# usermod -s /sbin/nologin test //限定用户test不能telnet，只能ftp
# usermod -s /sbin/bash test //用户test恢复正常
# usermod -d /test test //更改用户test的主目录为/test


2.4 限制该用户仅能通过 FTP 访问
限制用户 ftpuser只能通过 FTP 访问服务器，而不能直接登录服务器：

usermod -s /sbin/nologin ftpuser
```

默认文件在  /var/ftp/pub







service vsftpd stop
service vsftpd start

  一、.对于centos7自带的防火墙的相关指令

 systemctl stop firewalld.service #停止

firewall systemctl disable firewalld.service #禁止firewall开机启动

 systemctl status firewalld.service #查看firewall的状态



```doc
CentOS7 查看硬盘情况
lsblk 　　　　                                 查看分区和磁盘

df -h 　　                                        查看空间使用情况

fdisk -l 　　                                    分区工具查看分区信息

cfdisk /dev/sda  　　                      查看分区

blkid 　                                       　查看硬盘label（别名）
du -sh ./* 　　                                统计当前目录各文件夹大小
free -h 　                                    　查看内存大小
cat /proc/cpuinfo| grep "cpu cores"| uniq  　　查看cpu核心数


```





