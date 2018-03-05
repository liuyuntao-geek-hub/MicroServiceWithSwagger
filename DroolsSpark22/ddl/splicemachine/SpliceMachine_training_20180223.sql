explain select count (*) from customers c , t_header t, t_detail d --splice-properties joinStrategy=broadcast  
where c.customer_master_id = t.customer_master_id and t.transaction_header_key = d.transaction_header_key; 

show tables;

explain select count (*) from  --splice-properties joinOrder=FIXED  
customers c , t_header t, t_detail d where c.customer_master_id = t.customer_master_id and t.transaction_header_key = d.transaction_header_key; 

explain select count (*) from customers c , t_header t, t_detail d  --splice-properties useSpark=false
where c.customer_master_id = t.customer_master_id and t.transaction_header_key = d.transaction_header_key; 


select * from sys.sysviews;


CREATE TABLE SYSSCHEMAS (SCHEMAID CHAR(36) NOT NULL, SCHEMANAME VARCHAR(128) NOT NULL, AUTHORIZATIONID VARCHAR(128) NOT NULL);
CREATE TABLE SYSSCHEMAS (SCHEMAID CHAR(36) NOT NULL, SCHEMANAME VARCHAR(128) NOT NULL, AUTHORIZATIONID VARCHAR(128) NOT NULL);


run '/home/splice/splicemachine/scripts/test.sql';
 
explain select * from sys.SYSCOLUMNS ;
 
 
 -- example to create table insert and add value
 create table z (i int, j int, k int);
 insert into z values (	1	,	1	,	1	),
(	2	,	2	,	2	),
(	3	,	3	,	3	),
(	4	,	4	,	4	),
(	5	,	5	,	5	),
(	6	,	6	,	6	),
(	7	,	7	,	7	),
(	8	,	8	,	8	),
(	9	,	9	,	9	),
(	10	,	10	,	10	),
(	11	,	11	,	11	),
(	12	,	12	,	12	),
(	13	,	13	,	13	);

 insert into z select * from z;
  insert into z select * from z;
   insert into z select * from z;
    insert into z select * from z;
     insert into z select * from z;
      insert into z select * from z;
       insert into z select * from z;
        insert into z select * from z;
 
 create index ij on z (j);
 
  create index ij2 on z (j,i,k);
 
 explain select * from z --splice-properties index=ij
 ;
 
  explain select * from z --splice-properties index=ij2
  where j=1
 ;
 
 analyze schema splice;
 
 values current_timestamp;
 
 values (current schema);
 

 create view splice.systablestatistics as select s.schemaname,t.tablename,c.conglomeratename,sum(ts.rowCount) as TOTAL_ROW_COUNT,sum(ts.rowCount)/sum(ts.numPartitions) as AVG_ROW_COUNT,sum(ts.partition_size) as TOTAL_SIZE,sum(ts.numPartitions) as NUM_PARTITIONS,sum(ts.partition_size)/sum(ts.numPartitions) as AVG_PARTITION_SIZE,max(ts.meanrowWidth) as ROW_WIDTH,ts.statsType as STATS_TYPE,min(ts.sampleFraction) as SAMPLE_FRACTION from sys.systables t,sys.sysschemas s,sys.sysconglomerates c,sys.systablestats ts where t.tableid = c.tableid and c.conglomeratenumber = ts.conglomerateid and t.schemaid = s.schemaid  group by s.schemaname,t.tablename,c.conglomeratename,ts.statsType;
 
 select * from splice.systablestatistics ;
 
 -- check what is running on HBase
 call syscs_util.syscs_get_running_operations();
 
 -- force to purge tables deleted
 call syscs_util.vacuum();
 
 -- call compaction
 call SYSCS_UTIL.SYSCS_PERFORM_MAJOR_COMPACTION_ON_SCHEMA(splice);
 
 select count(*) from z;
 
 
 -- external table operation
 -- Step 1 - create external table pointing the file 
 create external table mytable (
 id INT, name VARCHAR(24)
 )
                    PARTITIONED BY (name)
                    ROW FORMAT DELIMITED FIELDS
                    TERMINATED BY ','
                    ESCAPED BY '\\' 
                    LINES TERMINATED BY '\\n'
                    STORED AS TEXTFILE
                    LOCATION '/home/splice/splicemachine/scripts/text';
                    
 select * from mytable;
 
 -- Step 2 - insert into the table
 insert into mytable select * from mytable;
 -- This step actually did not touch the original text.txt file in the ../text/ folder
 -- It add a new csv file
 
 
 -- Step 3 - Drop the table
 select * from mytable;
 
 drop table mytable;
 
 -- File not touch = test.txt has 3 rows + another csv file still there
 
 -- Step 5 - recreate the external table: all the 6 rows in place
 
 
 