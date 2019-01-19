### Zookeeper 集群搭建



```doc
ZooKeeper是一个开放源码的分布式应用程序协调服务.

1、在zookeeper的集群中，各个节点共有下面3种角色和4种状态：

角色：leader,follower,observer

状态：leading,following,observing,looking

Zookeeper的核心是原子广播，这个机制保证了各个Server之间的同步。

实现这个机制的协议叫做Zab协议（ZooKeeper Atomic Broadcast protocol）。Zab协议有两种模式，它们分别是恢复模式（Recovery选主）和广播模式（Broadcast同步）。当服务启动或者在领导者崩溃后，Zab就进入了恢复模式，当领导者被选举出来，且大多数Server完成了和leader的状态同步以后，恢复模式就结束了。状态同步保证了leader和Server具有相同的系统状态。
LOOKING：当前Server不知道leader是谁，正在搜寻。

LEADING：当前Server即为选举出来的leader。

FOLLOWING：leader已经选举出来，当前Server与之同步。

OBSERVING：observer的行为在大多数情况下与follower完全一致，但是他们不参加选举和投票，而仅仅接受(observing)选举和投票的结果。

Zookeeper节点部署越多，服务的可靠性越高，建议部署奇数个节点，因为zookeeper集群是以宕机个数过半才会让整个集群宕机的。
```





| 主机名    | IP地址           |
| ------ | -------------- |
| node-1 | 192.168.13.136 |
| node-2 | 192.168.13.140 |
| node-3 | 192.168.13.137 |
|        |                |

### 三、安装配置Zookeeper

#### 1、下载及安装

下载地址：http://mirrors.hust.edu.cn/apache/zookeeper/

在这里下载 zookeeper-3.4.8/ 

在node-1机器上，下载后解压到/root目录下：

```
tar -zxvf zookeeper-3.4.11.tar.gz 
```

#### 2、拷贝 `zoo_sample.cfg`

进入zookeeper的conf目录，拷贝`zoo_sample.cfg`并重命名为`zoo.cfg` ：

```
cd zookeeper-3.4.11/conf/

cp zoo_sample.cfg zoo.cfg

```

#### 3、修改 `zoo.cfg`

```
vi zoo.cfg
```



ip映射主机名

// vi /etc/hosts

192.168.13.140 node-2
192.168.13.136 node-1
192.168.13.137 node-3

修改如下，若原文件没有dataDir则直接添加：

```
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
```

#### 4、创建并编辑`myid` 

**创建ServerID标识**

```
//在zookeeper根目录下创建zoo.cfg中配置的目录
mkdir /tmp/zookeeper/data

//创建并编辑文件
vi myid

//输入1，即表示当前机器为在zoo.cfg中指定的server.1
1

//保存退出
:wq
```

#### 5、拷贝zookeeper到其他机器

上述操作是在node-1机器上进行的，要将zookeeper拷贝到其他zookeeper集群机器上：

```
cd /root

scp -r zookeeper-3.4.8/ hadoop@node-2:/root

scp -r zookeeper-3.4.8/ hadoop@node-3:/root
```

集群中各组件的安装目录最好保持一致。



#### 6、修改其他机器的`myid`文件

`myid`文件是作为当前机器在zookeeper集群的标识，这些标识在`zoo.cfg`文件中已经配置好了，但是之前在master188这台机器上配置的`myid`为1，所以还需要修改其他机器的`myid`文件：

```
//在node-2机器上
echo 2 > /tmp/zookeeper/data/myid
//在node-3机器上
echo 3 > /tmp/zookeeper/data/myid
```

#### 7、启动zookeeper集群

```
cd zookeeper-3.4.8/bin/
//分别在node-1、node-2、node-3上启动
./zkServer.sh start

//查看状态
./zkServer.sh status
```

三台机器的zookeeper状态必须只有一个`leader`，其他都是`follower`。

```
//查看进程，若有QuorumpeerMain，则启动成功
jps
```

#### 8、zookeeper error
安装zookeeper时候，可以查看进程启动，
但是状态显示报错：Error contacting service. It is probably not running
没有建立主机和ip之间的映射关系。
建立主机和ip之间映射关系的命令为
vim /etc/hosts 
在文件的末端加入各个主机和ip地址之间的映射关系就可以了。

zoo.cfg
myid  1,2,3
tmp/zookeeper/dataDir/


