#!/bin/sh
#!/bin/bash

countwb=`ps -fe|grep ftpFileSplit_wb.sh |grep -v grep|wc -l`
countwifi=`ps -fe|grep ftpFileSplit_wifi.sh |grep -v grep|wc -l`
echo $countwb
if [ $countwb -eq 0 ]
   then
   /bin/sh /usr/local/jobs/ftpFileSplit_wb.sh 
   else
   echo "ftpfileSplit_wb.sh is running"
fi

echo $countwifi
if [ $countwifi -eq 0 ]
   then
   /bin/sh /usr/local/jobs/ftpFileSplit_wifi.sh
   else
   echo "ftpfileSplit_wifi.sh is running"
fi

