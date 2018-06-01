#!/bin/bash
beeline -u "jdbc:hive2://dwbdtest1r2m3.wellpoint.com:2181,dwbdtest1r1m.wellpoint.com:2181,dwbdtest1r2m.wellpoint.com:2181/;serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hiveserver2"  -e "select * from ts_pdphpipph_nogbd_r000_wh.hpip_variance limit 10" >> variance.txt

#RECIPENT_LIST="af50694@anthem.com af30986@anthem.com"
#RECIPENT_LIST=$0
mail -s "Notification: Variance Descrepency" $1 < variance.txt
