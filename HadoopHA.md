### Hadoop HA(Hig*h A*vailability)



#### linux 设置

ssh免密登录，

ssh-keygen -t rsa

ssh-copy-id node-4

ssh-copy-id node-5

ssh-copy-id node-6

ssh-copy-id node-7



关闭防火墙，

systemctl stop firewalld.service #停止firewall 
systemctl disable firewalld.service #禁止firewall开机启动 
systemctl status firewalld.service #查看firewall的状态

service iptables stop

同步节点时间

yum install ntpdate

网络同步时间

ntpdate cn.pool.ntp.org



vi /etc/sysconfig/network

NETWORKING=yes

HOSTNAME=node-1



vi /etc/hosts

添加hosts文件配置项

192.168.13.201 node-4
192.168.13.202 node-5
192.168.13.203 node-6
192.168.13.204 node-7







#### JDK环境安装1.8

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



#### zookeeper 集群搭建

```doc
安装配置Zookeeper

1、下载及安装

下载地址：http://mirrors.hust.edu.cn/apache/zookeeper/
在这里下载 zookeeper-3.4.8/ 
    tar -zxvf zookeeper-3.4.11.tar.gz 
2、拷贝 zoo_sample.cfg
进入zookeeper的conf目录，拷贝zoo_sample.cfg并重命名为zoo.cfg ：
    cd zookeeper-3.4.11/conf/
    cp zoo_sample.cfg zoo.cfg

3、修改 zoo.cfg
    vi zoo.cfg

ip映射主机名

// vi /etc/hosts

192.168.13.140 node-2
192.168.13.136 node-1
192.168.13.137 node-3

修改如下，若原文件没有dataDir则直接添加：

    dataDir=/tmp/zookeeper/data
    dataLogDir=/tmp/zookeeper/logs
    
    //在最后添加，指定zookeeper集群主机及端口，机器数必须为奇数
    server.1=node-1:2888:3888
    server.2=node-2:2888:3888
    server.3=node-3:2888:3888
    
    或
    server.1= 192.168.13.136:2888:3888
    server.2= 192.168.13.140:2888:3888
    server.3= 192.168.13.137:2888:3888

4、创建并编辑myid

创建ServerID标识

    //在zookeeper根目录下创建zoo.cfg中配置的目录
    mkdir /tmp/zookeeper/data
    
    //创建并编辑文件
    vi myid
    
    //输入1，即表示当前机器为在zoo.cfg中指定的server.1
    1
    
    //保存退出
    :wq

5、拷贝zookeeper到其他机器
上述操作是在node-1机器上进行的，要将zookeeper拷贝到其他zookeeper集群机器上：
    cd /root
    scp -r zookeeper-3.4.8/ hadoop@node-2:/root
    scp -r zookeeper-3.4.8/ hadoop@node-3:/root
集群中各组件的安装目录最好保持一致。

6、修改其他机器的myid文件

myid文件是作为当前机器在zookeeper集群的标识，这些标识在zoo.cfg文件中已经配置好了，但是之前在master188这台机器上配置的myid为1，所以还需要修改其他机器的myid文件：

    //在node-2机器上
    echo 2 > /tmp/zookeeper/data/myid
    //在node-3机器上
    echo 3 > /tmp/zookeeper/data/myid

7、启动zookeeper集群
    cd zookeeper-3.4.8/bin/
    //分别在node-1、node-2、node-3上启动
    ./zkServer.sh start
   
    //查看状态
    ./zkServer.sh status

三台机器的zookeeper状态必须只有一个leader，其他都是follower。

    //查看进程，若有QuorumpeerMain，则启动成功
    jps
```



#### hadoop 安装

```doc
进入http://hadoop.apache.org/  官网
下载hadoop
tar zxvf hadoop-2.7.7.tar.gz
解压
hadoop 配置 文件修改
一般在主节点进行修改，完毕后scp下发给其他各个从节点机器。
```

 进入配置文件目录下 /root/hadoop/hadoop-2.7.7/etc/hadoop

1.hadoop-env.sh

```xml
export JAVA_HOME=/root/jdk1.8/jdk1.8.0_162
```

2.core-site.xml

```xml
<configuration>

<property>
 <!-- 指定hdfs的nameservice为wenjin --> 
  <name>fs.defaultFS</name>
  <value>hdfs://wenjin</value>
  </property>
<property>
	 <!-- 指定hadoop临时目录,datatmp这个目录需要提前建立好 -->
  <name>hadoop.tmp.dir</name>
<value>/root/hadoop/hadoop-2.7.7/datatmp</value>
  </property>

<!-- 指定zookeeper地址 -->
  <property>
    <name>ha.zookeeper.quorum</name>
    <value>node-5:2181,node-6:2181,node-7:2181</value>
  </property>

</configuration>

```

3.hdfs-site.xml

```xml
<configuration>

 <!-- 指定HDFS副本的数量，默认备份为3，这里我们指定为2 -->
<property>
  <name>dfs.replication</name>
  <value>2</value>
</property>

<!--指定hdfs的nameservice为wenjin，需要和core-site.xml中的保持一致 -->
  <property>
    <name>dfs.nameservices</name>
    <value>wenjin</value>
  </property>
  <!-- wenjin下面有两个NameNode，分别是nn1，nn2 -->
  <property>
    <name>dfs.ha.namenodes.wenjin</name>
    <value>nn1,nn2</value>
  </property>
  <!-- nn1的RPC通信地址 -->
  <property>
    <name>dfs.namenode.rpc-address.wenjin.nn1</name>
    <value>node-4:9000</value>
  </property>
  <!-- nn1的http通信地址 -->
  <property>
    <name>dfs.namenode.http-address.wenjin.nn1</name>
    <value>node-4:50070</value>
  </property>
  <!-- nn2的RPC通信地址 -->
  <property>
    <name>dfs.namenode.rpc-address.wenjin.nn2</name>
    <value>node-5:9000</value>
  </property>
  <!-- nn2的http通信地址 -->
  <property>
    <name>dfs.namenode.http-address.wenjin.nn2</name>
    <value>node-5:50070</value>
  </property>
  <!-- 指定NameNode的元数据在JournalNode上的存放位置 -->
  <property>
    <name>dfs.namenode.shared.edits.dir</name>
    <value>qjournal://node-4:8485;node-5:8485;node-6:8485/wenjin</value>
  </property>
  <!-- 指定JournalNode在本地磁盘存放数据的位置 -->
  <property>
    <name>dfs.journalnode.edits.dir</name>
    <value>/home/hello/app/hadoop-2.5.1/journaldata</value>
  </property>
  <!-- 开启NameNode失败自动切换 -->
  <property>
    <name>dfs.ha.automatic-failover.enabled</name>
    <value>true</value>
  </property>
  <!-- 配置失败自动切换实现方式 -->
  <property>
    <name>dfs.client.failover.proxy.provider.wenjin</name>
    <value>org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider</value>
  </property>
  <!-- 配置隔离机制方法，多个机制用换行分割，即每个机制暂用一行-->
  <property>
    <name>dfs.ha.fencing.methods</name>
    <value>
      sshfence
      shell(/bin/true)
    </value>
  </property>
  <!-- 使用sshfence隔离机制时需要ssh免登陆 -->
  <property>
    <name>dfs.ha.fencing.ssh.private-key-files</name>
    <value>/home/hello/.ssh/id_rsa</value>
  </property>
  <!-- 配置sshfence隔离机制超时时间 -->
  <property>
    <name>dfs.ha.fencing.ssh.connect-timeout</name>
    <value>30000</value>
  </property>


</configuration>
```

4.mapred-site.xml

```xml
<configuration>

<!-- 指定mr运行时框架，这里指定在yarn上，默认是local -->

<property>
  <name>mapreduce.framework.name </name>
  <value>yarn</value>
</property>

</configuration>
```

5.yarn-site.xml

```xml
<configuration>

<!-- 开启RM高可用 -->
  <property>
    <name>yarn.resourcemanager.ha.enabled</name>
    <value>true</value>
  </property>
  <!-- 指定RM的cluster id -->
  <property>
    <name>yarn.resourcemanager.cluster-id</name>
    <value>yrc</value>
  </property>
  <!-- 指定RM的名字 -->
  <property>
    <name>yarn.resourcemanager.ha.rm-ids</name>
    <value>rm1,rm2</value>
  </property>
  <!-- 分别指定RM的地址 -->
  <property>
    <name>yarn.resourcemanager.hostname.rm1</name>
    <value>node-4</value>
  </property>
  <property>
    <name>yarn.resourcemanager.hostname.rm2</name>
    <value>node-5</value>
  </property>
  <!-- 指定zk集群地址 -->
  <property>
    <name>yarn.resourcemanager.zk-address</name>
    <value>node-5:2181,node-6:2181,node-7:2181</value>
  </property>
  <property>
    <name>yarn.nodemanager.aux-services</name>
    <value>mapreduce_shuffle</value>
  </property>


</configuration>
```

6.slaves或者workers

```xml
node-6
node-7
```

7.yarn-env.sh

```xml
export JAVA_HOME=/root/jdk1.8/jdk1.8.0_162
```



将配置好的hadoop拷贝到其他节点

        scp -r  /root/hadoop/hadoop-2.7.7 root@node5:/root/hadoop/hadoop-2.7.7
        scp -r  /root/hadoop/hadoop-2.7.7 root@node6:/root/hadoop/hadoop-2.7.7
        scp -r  /root/hadoop/hadoop-2.7.7 root@node7:/root/hadoop/hadoop-2.7.7
#### 启动

step1 依次 启动zookeeper

..../bin/zkServer.sh start

step2 依次 启动 journalnode    

.../sbin/hadoop-daemon.sh start journalnode

step3 node-4  格式化hdfs

 .../bin/ hdfs namenode -format

step3.2  同步node-5 

```doc
格式化后会在根据core-site.xml中的hadoop.tmp.dir配置生成个文件，
这里我配置的是/root/hadoop/hadoop-2.7.7/datatmp，
然后将/root/hadoop/hadoop-2.7.7/datatmp拷贝到node-5的/root/hadoop/hadoop-2.7.7/datatmp下。
scp -r /root/hadoop/hadoop-2.7.7/datatmp hello@node2:/root/hadoop/hadoop-2.7.7/datatmp
##也可以这样，建议hdfs namenode -bootstrapStandby
```



step4   格式化ZKFC

.../bin/hdfs zkfc -formatZK

启动HDFS(在node-4上执行)
        ..../sbin/start-dfs.sh

启动YARN(分别在node-4,node-5上执行)
        ./sbin/start-yarn.sh

分别查看状态hdfs

http://192.168.13.201:50070/dfshealth.html#tab-overview

http://192.168.13.202:50070/dfshealth.html#tab-overview

yarn 

http://192.168.13.202:8088/cluster



测试  在 hadoop /sbin/ 下面执行  自动 切换

namenode进程关掉：hadoop-daemon.sh stop namenode

active  与 standby  进行切换。

或者  poweroff





### EORROR

# Cannot create directory /tmp. Name node is in safe mode

```doc
问题原因：
hdfs在启动开始时会进入安全模式，这时文件系统中的内容不允许修改也不允许删除，直到安全模式结束。安全模式主要是为了系统启动的时候检查各个DataNode上数据块的有效性，同时根据策略必要的复制或者删除部分数据块。运行期通过命令也可以进入安全模式。在实践过程中，系统启动的时候去修改和删除文件也会有安全模式不允许修改的出错提示，只需要等待一会儿即可。
问题解决：

可以等待其自动退出安全模式，也可以使用手动命令来离开安全模式：

hadoop dfsadmin -safemode leave

```

