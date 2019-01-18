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

```
  [root@localhost ~]# whereis vsftpd
```

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





