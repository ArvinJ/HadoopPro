#!/bin/sh
#!/bin/bash
wififilenum=`ls -l /home/ftpuser/wifi |wc -l`
echo "wififilenum is $wififilenum" >> /var/log/flume/monit-files-count.log
if [ "$wififilenum" > 5000 ]
	then
	echo 'filenum in wifi is too much' >> /var/log/flume/monit-files-count.log
	service vsftpd stop && \
	find /home/ftpuser/wifi/ -name "*.bcp" |xargs -i mv -f {} /home/ftpuser/wifi.bak/
	service vsftpd start
fi
