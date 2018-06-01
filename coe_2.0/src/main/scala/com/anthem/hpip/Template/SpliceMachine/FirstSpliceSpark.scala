package com.anthem.hpip.Template.SpliceMachine


import com.splicemachine.spark.splicemachine.SplicemachineContext
import com.splicemachine.derby.impl.SpliceSpark
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.joda.time.DateTime
import org.joda.time.Minutes
import org.joda.time.Seconds
import grizzled.slf4j.Logging

object FirstSpliceSpark extends Logging{
  // Need this class to specify the Schema of the DF that is loaded into the target table
  case class Claim(ID: String, AMOUNT: String)
  
  def main(args: Array[String]) {
   
    val startTime = DateTime.now
    
    val conf = new SparkConf()
   // conf.set("spark.serializer", "com.splicemachine.serializer.SpliceKryoSerializer")
   // conf.set("spark.kryo.registrator", "com.splicemachine.derby.impl.SpliceSparkKryoRegistrator")
    
  // val sparkSession = SparkSession.builder().appName("Reader").config(conf).getOrCreate()

  // sparkSession.sparkContext.setLogLevel("off")
   
    println("******* Created Spark Session *******")
    val sparkSession = SparkSession.builder().appName("Reader").master("local[*]").config(conf).getOrCreate()
    //SpliceSpark.setContext(sparkSession.sparkContext)
    
    import sparkSession.implicits._
    
    val dbUrl = "jdbc:splice://dwbdtest1r5w1.wellpoint.com:1527/splicedb;user=srcpdpspcpbthdv;password="
    val splicemachineContext = new SplicemachineContext(dbUrl.concat(getCredentialSecret("jceks://hdfs//dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/bin//dev849.jceks","dev849pass")))
   
    
    println(dbUrl.concat(getCredentialSecret("jceks://hdfs//dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/bin//dev849.jceks","dev849pass")))
    println("******* Created Splicemachine Context *******")
    // READ DATA FROM A TABLE
  
    //splicemachineContext.df("Set role DV_SMARTPCP_DVLPR;select MBR_KEY, SRC_GRP_NBR from CDL_EDWD_ALLPHI_XM. MBR where HC_ID='414A76152'and SSN='203297731'and BRTH_DT='1978-01-01'and RCRD_STTS_CD='ACT'and VRSN_CLOS_DT='8888-12-31'").show()
    val testDF = splicemachineContext.df("select * from sys.sysviews")
    testDF.show()
    
    
/*    
    // UPDATE EXISTING ROWs IN A TABLE
    val existingClaims = Seq(Claim(13,133.67), Claim(14,222.85),Claim(2,666.44),Claim(4,111.33))
    val claimDF = sparkSession.sparkContext.parallelize(existingClaims).toDF()
    splicemachineContext.update(claimDF,"pingcsbd.claims")
    
    // INSERT NEW ROWS INTO A TABLE
    val newclaims = Seq(Claim(24,244.67), Claim(25,889.32))
    val newclaimDF = sparkSession.sparkContext.parallelize(newclaims).toDF()
    //splicemachineContext.insert(newclaimDF,"pingcsbd.claims")
        */

    val endTime = DateTime.now
		 info(" [COE 2.0 Splice Spark Adapter Encrypt] Start Time: "+ startTime);
		 info(" [COE 2.0 Splice Spark Adapter Encrypt] End Time: " + endTime)
     println(" [COE 2.0 Splice Spark Adapter Encrypt] Minutes diff: " + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")
     info (" [COE 2.0 Splice Spark Adapter Encrypt] Seconds Diff: " + Seconds.secondsBetween(startTime, endTime).getSeconds + " seconds. ")
  	
     println(" [COE 2.0 Splice Spark Adapter Encrypt] Start Time: "+ startTime);
		 println(" [COE 2.0 Splice Spark Adapter Encrypt] End Time: " + endTime)   
     println (" [COE 2.0 Splice Spark Adapter Encrypt] Seconds Diff: " + Seconds.secondsBetween(startTime, endTime).getSeconds + " seconds. ")
    
} 
    def getCredentialSecret (aCredentialStore: String, aCredentialAlias: String): String ={
      val config = new org.apache.hadoop.conf.Configuration()
      config.set(org.apache.hadoop.security.alias.CredentialProviderFactory.CREDENTIAL_PROVIDER_PATH, aCredentialStore)
      String.valueOf(config.getPassword(aCredentialAlias))

    }
    


  
}