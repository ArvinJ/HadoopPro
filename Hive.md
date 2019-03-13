Hive是数据仓库，主要来做历史数据分析，

Hive的产生：非java编程者对hdfs的数据做mapreduce操作

Hive 底层基于HDFS进行存储，

元数据是存在关系型数据库里，

Hive的所有操作都 是hdfs 和MR操作。 Hive提供客户端访问，以及将sql转换成对应的MapReduce(select count(*))操作和HDFS（select * ...）操作。

Hive 的思路 是将 sql  转换成mapreduce 执行

### Hive搭建模式

1、local模式。此模式连接到一个In-memory 的内存数据库Derby，一般用于Unit Test，基本不用。

2、单用户模式。将mysql	与hive 分割在不同的服务器上，通过远程进行访问。

3、远程服务器模式/多用户模式。使用三台服务器，一台搭建hive ,一台搭建mysql，一台搭建metastore server



yum -y install wget

#### 安装mysql

yum install -y mysql-server

```doc
一：去官网查看最新安装包

https://dev.mysql.com/downloads/repo/yum/

二：下载MySQL源安装包

wget http://dev.mysql.com/get/mysql57-community-release-el7-11.noarch.rpm
安装MySql源
yum -y install mysql57-community-release-el7-11.noarch.rpm
查看一下安装效果
yum repolist enabled | grep mysql.*

三：安装MySQL服务器

yum install mysql-community-server

四：启动MySQL服务
service mysqld start
systemctl start  mysqld.service

运行一下命令查看一下运行状态 

systemctl status mysqld.service


五：初始化数据库密码

查看一下初始密码

grep "password" /var/log/mysqld.log

登录

mysql -uroot -p

修改密码

ALTER USER 'root'@'localhost' IDENTIFIED BY '****************';

mysql默认安装了密码安全检查插件（validate_password），默认密码检查策略要求密码必须包含：大小写字母、数字和特殊符号，并且长度不能少于8位。否则会提示ERROR 1819 (HY000)


六：数据库授权

数据库没有授权，只支持localhost本地访问

mysql>GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY '123456' WITH GRANT OPTION;
//远程连接数据库的时候需要输入用户名和密码
用户名：root
密码:123456
指点ip:%代表所有Ip,此处也可以输入Ip来指定Ip
输入后使修改生效还需要下面的语句
mysql>FLUSH PRIVILEGES;

也可以通过修改表来实现远程：

    mysql -u root -p

七：设置自动启动

systemctl enable mysqld

systemctl daemon-reload


设置开机启动
chkconfig mysqld on
```



```xml

删除conf文件配置
:.,$-1d
回车删除

```



#### hive 搭建

主要是mysql 服务 的配置信息和hdfs存储数据的路径。

vi hive-site.xml

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<configuration>
  <property>  
  <name>hive.metastore.warehouse.dir</name>  
  <value>hdfs://wenjin/hive</value>  
</property>  
   
   
<property>  
  <name>javax.jdo.option.ConnectionURL</name>  
  <value>jdbc:mysql://192.168.13.201/hivetop?createDatabaseIfNotExist=true</value>  
</property>  
   
<property>  
  <name>javax.jdo.option.ConnectionDriverName</name>  
  <value>com.mysql.jdbc.Driver</value>  
</property>  
   
<property>  
  <name>javax.jdo.option.ConnectionUserName</name>  
  <value>root</value>  
</property>  
   
<property>  
  <name>javax.jdo.option.ConnectionPassword</name>  
  <value>RIyygyfj1221.</value>  
</property>  
</configuration>
```



### DML DDL DCL

crud       定义       控制

#### hive使用

先启动  hadoop，mysql

在使用hive

#### Hive SQL

###### hive创建数据库

create database if not exists foo;

describe database hivetop;

show databases;

use heihei;

show tables;

```doc
注意：
默认情况下是不允许直接删除一个有表的数据库的：
删除一个有表的数据库有两种办法：
1. 先把表删干净，再删库。
2. 删库时在后面加上cascade，表示级联删除此数据库下的所有表：
drop database if exists foo cascade;
```

##### 删除数据库

drop database if exists hivetop;





##### hive建立数据表

https://cwiki.apache.org/confluence/display/Hive/LanguageManual+DDL#LanguageManualDDL-CreateTableCreate/Drop/TruncateTable

```doc
CREATE  TABLE IF NOT EXISTS heihei.exzwj
(
id int,
name string,
likes ARRAY<string>,
addr MAP<string,string>
)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY  ','
COLLECTION ITEMS TERMINATED BY '-'
MAP KEYS TERMINATED BY ':' 
LINES TERMINATED BY '\n';


```

show tables;

desc exzwj;

删除表

drop table exzwj;

查询详细的表结构和一些规则数据。

desc formatted exzwj;

#### MANAGED_TABLE  为内部表

准备数据   

```doc
1,Tom,book-music-plays,cn:sh-haha:pd
2,Frank,swimming-art-lol,cn:bj-haha:cq
3,Amy,sleep-book-music,cn:ah-hf:ss
4,Tom2,book-music-plays,cn:sh-haha:pd
5,Frank3,swimming-art-lol,cn:bj-haha:cq
6,Amy4,sleep-book-music,cn:ah-hf:ss
```

保存在/root/data/ex1

scp ex1 root@node-4:/root/

##### hive 加载上传插入数据

首次

```
LOAD DATA LOCAL INPATH '/root/data/ex1' INTO TABLE exzwj 
```

后续  加入  OVERWRITE  更新覆盖

LOAD DATA LOCAL INPATH '/root/ex1' OVERWRITE  INTO TABLE exzwj;



#### EXTERNAL 外部表

```doc

CREATE  EXTERNAL TABLE IF NOT EXISTS heihei.exzwj2
(
id int,
name string,
likes ARRAY<string>,
addr MAP<string,string>
)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY  ','
COLLECTION ITEMS TERMINATED BY '-'
MAP KEYS TERMINATED BY ':' 
LINES TERMINATED BY '\n'
LOCATION   '/zdcj' ;

```

LOAD DATA LOCAL INPATH '/root/data/ex1' INTO TABLE exzwj2 

同样的方式插入数据

hdfs 会自动创建文件夹，存入表数据。



#### hive内、外部表的区别

外部表需要指定hdfs目录。

外部表  在删除后，表结构会被 删除，而 具体存在hdfs中的表数据不会被删除。内部表 删除后，所有的数据皆删除。



#### hive 分区

分区就是为了加目录，按天按月按一定规则去拆分多个目录，提高查询效率。

#### 静态分区

分区列表中的字符不能出现在表的列中。

```doc
CREATE  TABLE IF NOT EXISTS heihei.ex1
(
id int,
name string,
likes ARRAY<string>,
addr MAP<string,string>
)
PARTITIONED BY (age int)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY  ','
COLLECTION ITEMS TERMINATED BY '-'
MAP KEYS TERMINATED BY ':' 
LINES TERMINATED BY '\n' ;

```

导入数据

LOAD DATA LOCAL INPATH '/root/ex1' INTO TABLE ex1 partition(age=10);



#### 添加 分区

在添加分区时，添加分区的字段在表定义的时候要定义好。

添加分区的时候，必须在现有分区的基础之上

删除分区的时候，会将所有存在的分区都删除

```doc
CREATE  TABLE IF NOT EXISTS heihei.ex2
(
id int,
name string,
likes ARRAY<string>,
addr MAP<string,string>
)
PARTITIONED BY (age int,sex string)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY  ','
COLLECTION ITEMS TERMINATED BY '-'
MAP KEYS TERMINATED BY ':' 
LINES TERMINATED BY '\n' ;


load data local inpath '/root/ex1' into table ex2 partition(age=10,sex='boy');

load data local inpath '/root/ex1' into table ex2 partition(age=20,sex='boy');

alter table ex2 add partition(age=10,sex='man');

alter table ex2 drop partition(sex='man');
alter table ex2 drop partition(age=10,sex='girl');


```

**删除分区的时候，内部表 表数据会被删除。外部表分区表数据不会被删除**



````doc
CREATE  EXTERNAL TABLE IF NOT EXISTS heihei.ex3
(
id int,
name string,
likes ARRAY<string>,
addr MAP<string,string>
)
PARTITIONED BY (age int,sex string)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY  ','
COLLECTION ITEMS TERMINATED BY '-'
MAP KEYS TERMINATED BY ':' 
LINES TERMINATED BY '\n' 
LOCATION   '/zdcj' ;


load data local inpath '/root/ex1' into table ex3 partition(age=10,sex='aa');
load data local inpath '/root/ex1' into table ex3 partition(age=10,sex='ab');
load data local inpath '/root/ex1' into table ex3 partition(age=20,sex='ab');
load data local inpath '/root/ex1' into table ex3 partition(age=20,sex='ab');
alter table ex3 drop partition(sex='ab');
alter table ex3 drop partition(age=10,sex='aa');


也可以直接  向  分区目录中  hdfs dfs -put  /root/ex2   /zdcj/age=10/sex=aa/    
select * from ex3;  会增加数据的。其实 load data local  这个操作就是向hdfs里面上传文件。
````



插入表数据三种

load data   (hdfs dfs -put)快

insert into (mapredus 操作 )特别慢

FROM table1  insert overwrite table  table2  select id,name,likes;

第三种  作用：1 复制表;2可以做为中间表;





#### Hive SerDe  正则匹配    

序列化和反序列化。定义数据 读取的格式规范（正则表达式）。

```doc
CREATE TABLE logtbl (
    host STRING,
    identity STRING,
    t_user STRING,
    time STRING,
    request STRING,
    referer STRING,
    agent STRING)
  ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.RegexSerDe'
  WITH SERDEPROPERTIES (
    "input.regex" = "([^ ]*) ([^ ]*) ([^ ]*) \\[(.*)\\] \"(.*)\" (-|[0-9]*) (-|[0-9]*)"
  )
  STORED AS TEXTFILE;

```



 **读时检查**



当插入的数据后，执行select 操作时，如果之前插入的数据不符合我们建表时的规则，那么就会显示为空。SerDe时只要有一个字符匹配不上一条记录全为NULL  ，DELIMITED时匹配不上的字段为NULL.

mysql  关系型数据库都 是 写时检查。有问题就会报错。



#### hive  beeline（另一种访问方式）

提供另一种访问hive的客户端方式跟HiveServer2配合使用

```doc
Beeline
使用Beeline所有的操作命令都 要加！
Beeline 要与HiveServer2配合使用
服务端启动hiveserver2
客户的通过beeline两种方式连接到hive
1、beeline -u jdbc:hive2://node-4:10000/heihei -n root
!quit  退出
2、beeline
beeline> !connect jdbc:hive2://node-4:10000/heihei
默认 用户名、密码不验证

```



#### Hive JDBC

必须访问hiveserver2  10000

```doc
Hive JDBC运行方式
服务端启动hiveserver2后，在java代码中通过调用hive的jdbc访问默认端口10000进行连接、访问

```





#### Hive 函数

udf   一进一出

udaf  多进一出（聚合函数）

udtf 一进多出（explode）

##### 自定义函数

利用java 代码编写实现继承UDF

实现 evaluate方法

```java
public class TuoMin extends UDF {

	public Text evaluate(final Text s) {
		if (s == null) {
			return null;
		}
		String str = s.toString().substring(0, 3) + "***";
		return new Text(str);
	}

}
```

将程序打包成jar   copy 上传到hive的那台机器上 ，

进入hive 客户端 添加  jar文件 add  jar '/root/tuomin.jar';

create TEMPORARY function tm as  'com.bjsxt.hive.TuoMin';

select  tm(name) from ex2;



动态分区