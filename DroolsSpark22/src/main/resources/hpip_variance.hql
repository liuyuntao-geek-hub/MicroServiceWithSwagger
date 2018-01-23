CREATE  TABLE dv_pdphpipph_nogbd_r000_wh.hpip_variance(
tableName string , 
partitionColumn string,
partitionValue string , 
columnName string,
operationDescription string,
operation string , 
operationValue double , 
percentageVariance double ,
threshold double,
isThresholdCrossed string,
last_updt_dtm timestamp )
STORED AS parquet;