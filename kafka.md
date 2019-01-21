### kafka集群的搭建

下载地址

http://kafka.apache.org/downloads



##### 一、解压

cd /root

tar -zxvf kafka_2.11-2.1.0.tgz

#### 二、配置

cd  /root/kafka_2.11-2.1.0/config

 vi server.properties 

listeners=PLAINTEXT://node-1:9092

log.dirs=/tmp/kafka-logs

zookeeper.connect=node-1:2181,node-2:2181,node-3:2181



mkdir -p /tmp/kafka-logs

node-1服务器kafka已搭建完成。

将node-1中的kafka copy 到 node-2,node-3

scp -r kafka_2.11-2.1.0 root@node-2 /root/

scp -r kafka_2.11-2.1.0 root@node-3 /root/

修改 node-2  的server.properties 

 

broker.id=1,要与node-1中broker.id区别

listeners=PLAINTEXT://node-1:9092,需要改成本机对应ip或主机名

vi server.properties 

broker.id=1

listeners=PLAINTEXT://node-2:9092

mkdir -p /tmp/kafka-logs

broker.id=2

listeners=PLAINTEXT://node-3:9092

mkdir -p /tmp/kafka-logs

#### 三、启动

step1:启动zookeeper集群

step2:启动kafka集群

三台服务器分别运行启动命令

bin/kafka-server-start.sh -daemon config/server.properties

kafka-server-start.sh -daemon /root/kafka_2.11-2.1.0/config/server.properties

后台起动就加上   -daemon

**关闭**

bin/kafka-server-stop.sh

#### 四、shell

1 、创建topic

bin/kafka-topics.sh --create --zookeeper node-1:2181 --replication-factor 1 --partitions 1 --topic title

```doc
Created topic "title".
```



2、查看topic 列表

bin/kafka-topics.sh --list --zookeeper node-3:2181

```doc
title
```



3、打开生产者客户端

bin/kafka-console-producer.sh --broker-list node-1:9092 --topic title

bin/kafka-console-producer.sh --broker-list 192.168.13.136:9092 --topic title

bin/kafka-console-producer.sh --broker-list node-1:9092 --topic title --producer.config config/producer.properties

4、在另一台机器上打开一个消费者客户端

bin/kafka-console-consumer.sh --bootstrap-server 192.168.13.140:9092 --topic title --from-beginning

bin/kafka-console-consumer.sh --bootstrap-server 192.168.13.140:9092,192.168.13.136:9092,192.168.13.137:9092 --topic title --from-beginning

#### 注意

用 ip  能解决一些问题   ，主机名  有时会出错。