#!/bin/bash
#!/bin/sh

errorpath=`tac /var/log/flume-ng/flume-cmf-flume-AGENT-node-7.log |grep 'Exception opening file: /home/ftpuser' |tail -n 20 |awk -F " " '{print $NF}' |awk -F "/" '{print ("/"$2"/"$3"/"$4"/")}'| sort -u`

for path in $errorpath
	do
	echo $path"flumespool/.flumespool-main.meta"
	rm -f $path".flumespool/.flumespool-main.meta"
done

metapath=`tac /var/log/flume-ng/flume-cmf-flume-AGENT-node-7.log |grep 'Permission denied' |tail -n 20 |awk -F " " '{print $2}'| sort -u`
for meta in $metapath
	do
	echo $meta
	chmod 777 $meta
done

mv -f /var/log/flume-ng/flume-cmf-flume-AGENT-node-7.log /var/log/flume-ng/flume-cmf-flume-AGENT-node-7.log.bak


/bin/curl -i -u admin:admin  "http://node-1:7180/api/v19/clusters/Cluster%201/services/flume/commands/restart" -X POST -H "Content-Type:application/json" -d '{"serviceName":"Flume","clusterName":"Cluster%201"}'

