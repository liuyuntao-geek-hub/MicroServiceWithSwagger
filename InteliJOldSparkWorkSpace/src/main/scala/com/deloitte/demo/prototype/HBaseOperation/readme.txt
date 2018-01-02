

================================================================
Readme.txt
---------------------------------------------------------------
-------------------------------------------------
Cloudera Manager URL: http://10.118.36.60:7180/
HUE URL: http://10.118.36.60:8888/

Username: yuntliu
Password (Linux Login): Temp4Now => Linux OS password
Password: Password.1 => Hadoop related password

Server IP	Hostname
10.118.36.60	ussltcsnl2608.solutions.glbsnet.com
10.118.36.61	ussltcsnl2609.solutions.glbsnet.com
10.118.36.103	ussltcsnl2838.solutions.glbsnet.com
-------------------------------------------------

hadoop fs -mkdir /user/yuntliu/HbaseWorkShop
hadoop fs -mkdir /user/yuntliu/HbaseWorkShop/HFileHome/tmp
hadoop fs -ls /user/yuntliu/HbaseWorkShop/HFileHome/tmp

Copy OldSparkScala:
From: C:\Users\yuntliu\Documents\workstation\Study\IntelliJ\InteliJOldSparkWorkSpace\target
To: /home/yuntliu/workstation/HbasePlayGround


com.deloitte.demo.prototype.HBaseOperation.SparkRddToHFileInsert
cd /home/yuntliu/workstation/HbasePlayGround
spark-submit --name "FirstRun" --master yarn-client  --class com.deloitte.demo.prototype.HBaseOperation.SparkRddToHFileInsert OldSparkScala.jar
