#!/bin/sh
#!/bin/bash

/bin/find /home/ftpuser/fj -name "*.COMPLETED" |xargs rm -rf
/bin/find /home/ftpuser/wifi -name "*.COMPLETED" |xargs rm -rf
/bin/find /home/ftpuser/wifi.03 -name "*.COMPLETED" |xargs rm -rf
/bin/find /home/ftpuser/wifi.02 -name "*.COMPLETED" |xargs rm -rf
/bin/find /home/ftpuser/wifi.01 -name "*.COMPLETED" |xargs rm -rf
/bin/find /home/ftpuser/wb  -name "*.COMPLETED" |xargs rm -rf
/bin/find /home/ftpuser/wb.03  -name "*.COMPLETED" |xargs rm -rf
/bin/find /home/ftpuser/wb.02  -name "*.COMPLETED" |xargs rm -rf
/bin/find /home/ftpuser/wb.01  -name "*.COMPLETED" |xargs rm -rf


