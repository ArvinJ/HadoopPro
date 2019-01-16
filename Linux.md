linux 

###### 修改hostname

````doc
hostnamectl set-hostname node-1             # 使用这个命令会立即生效且重启也生效

vi /etc/hosts

编辑下hosts文件， 给127.0.0.1添加hostname

cat /etc/hosts                                           # 检查

127.0.0.1   localhost localhost.localdomain localhost4 localhost4.localdomain4 node-1

::1         localhost localhost.localdomain localhost6 localhost6.localdomain6

````



#### 查看JAVA_HOME

echo $JAVA_HOME




##### 查看ip地址

ip addr 

ifconfig

yum install net-tools

```doc
vi /etc/sysconfig/network-scripts/ifcfg-eth0
删除UUID和MAC地址
ONBOOT=yes
BOOTPROTO=static
IPADDR=192.168.1.101
NETMASK=255.255.255.0
GATEWAY=192.168.1.2
DNS1=1921.68.1.2

```



##### 关闭防火墙和 selinux

vi /etc/selinux/config

SELINUX=disabled



###### 同步时间

网络环境要调通,确保各节点时间 同步

手动同步集群各机器时间

date -s "2018-11-27 11:10:20"

yum install ntpdate

网络同步时间









