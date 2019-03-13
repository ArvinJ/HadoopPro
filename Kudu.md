# 安装

yum -y install ntp && /etc/init.d/ntpd restart

安装好ntp 同步时间





**配置yum：**



vim /etc/yum.repos.d/CentOS-Base.repo 

添加

```xml
[cloudera-kudu]
# Packages for Cloudera's Distribution for kudu, Version 5, on RedHator CentOS 7 x86_64
name=Cloudera's Distribution for kudu, Version 5
baseurl=http://archive.cloudera.com/kudu/redhat/7/x86_64/kudu/5/
gpgkey = http://archive.cloudera.com/kudu/redhat/7/x86_64/kudu/RPM-GPG-KEY-cloudera
gpgcheck = 1
```





**安装kudu-master：**

**安装**

yum install kudu kudu-master kudu-client0 kudu-client-devel -y

 vi /etc/default/kudu-master







安装 kudu-tserver

yum install kudu kudu-tserver kudu-client0 kudu-client-devel -y



/etc/init.d/kudu-tserver restart

service kudu-master start|stop 
service kudu-tserver start|stop 











# Error



运行github上kduu的java example，报错如下
`NoLeaderFoundException: Master config (hadoop-master:7051) has no leader.`
网上百度后感觉都不是我想要的答案，上CDH查看kudu-ui的log，信息如下
`NoLeaderFoundException: Master config (hadoop-master:7051) has no leader.`
再百度，修改重启即可：

```
# 集群-->kudu -->配置 --> Master -->高级 -->gflagfile 的 Master 高级配置代码段>
-rpc_encryption=disabled
-rpc_authentication=disabled
-trusted_subnets=0.0.0.0/0
# 右上角 ‘保存更改’ --> 重启集群 --> OK
```

