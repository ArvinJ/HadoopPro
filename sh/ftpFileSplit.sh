#!/bin/bash
echo "fileSplit.sh start ..."
count=0
source_dir='/home/ftpuser/wifi/'
target_dir1='/home/ftpuser/wifi.01/'
target_dir2='/home/ftpuser/wifi.02/'
target_dir3='/home/ftpuser/wifi.03/'

echo "`date` ftpflieSplit_wifi.sh is starting." 
   for file in $(ls $source_dir |grep ^.*bcp\\b$)
   	do
     	if expr $count % 3 == 0
       	   then
           	mv -v $source_dir$file ${target_dir1}
    	   elif expr $count % 3 == 1
       	   then
          	mv -v $source_dir$file ${target_dir2}
     	   else
           	mv -v $source_dir$file ${target_dir3}
        fi
    	((count++))
	done
echo "`date` ftpfileSplit_wifi.sh end."

