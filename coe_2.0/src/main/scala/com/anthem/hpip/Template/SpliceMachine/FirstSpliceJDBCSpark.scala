package com.anthem.hpip.Template.SpliceMachine

import java.sql.DriverManager
import java.sql.Connection
import com.splicemachine.db.jdbc.ClientDriver
import org.joda.time.DateTime
import org.joda.time.Minutes
import org.joda.time.Seconds
import grizzled.slf4j.Logging

object FirstSpliceJDBCSpark extends Logging {
  
  def main(args : Array[String]) {
    
     val startTime = DateTime.now
    
    val driver = "com.splicemachine.db.jdbc.ClientDriver"
	
    		val dbUrl = "jdbc:splice://dwbdtest1r5w1.wellpoint.com:1527/splicedb;user=srcpdpspcpbthdv;password="
    		
    		val newdbURL = dbUrl.concat(getCredentialSecret("jceks://hdfs//dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/bin//dev849.jceks","dev849pass"))
 
    		
		var connection:Connection = null
    
		try {
    
			// make the connection
			Class.forName(driver)
			connection = DriverManager.getConnection(newdbURL)
    
			var ps = connection.prepareStatement("SET ROLE DV_SMARTPCP_DVLPR");
			

			
			// create the statement
			var statement = connection.createStatement()
    
		//	val resultSet = statement.executeQuery("select MBR_KEY, SRC_GRP_NBR from CDL_EDWD_ALLPHI_XM. MBR where HC_ID='414A76152'and SSN='203297731'and BRTH_DT='1978-01-01'and RCRD_STTS_CD='ACT'and VRSN_CLOS_DT='8888-12-31'")
			
				//val resultSet = statement.executeQuery("SELECT * FROM DV_PDPSPCP_XM.MBR_DEV where HC_ID='470A76556'")
							val resultSet = statement.executeQuery("select * from sys.sysviews")	
			
			var counter =0
    
			while ( resultSet.next() ) {
				counter += 1
				val val_a = resultSet.getString(1)
				val val_b = resultSet.getString(2)
				println("record=[" + counter + "] a=[" + val_a + "] b=[" +val_b + "]")
			}
			resultSet.close()
			statement.close()
		} catch {
			case ex : java.sql.SQLException => println("SQLException: "+ex)
		} finally {
		  println("connection closed")
			connection.close()
			
			
		}
	
		val endTime = DateTime.now
		 info(" [COE 2.0 Splice JDBC] Start Time: "+ startTime);
		 info(" [COE 2.0 Splice JDBC] End Time: " + endTime)
     println(" [COE 2.0 Splice JDBC] Minutes diff: " + Minutes.minutesBetween(startTime, DateTime.now()).getMinutes + " mins.")
     info (" [COE 2.0 Splice JDBC] Seconds Diff: " + Seconds.secondsBetween(startTime, endTime).getSeconds + " seconds. ")
  	
     println(" [COE 2.0 Splice JDBC] Start Time: "+ startTime);
		 println(" [COE 2.0 Splice JDBC] End Time: " + endTime)   
     println (" [COE 2.0 Splice JDBC] Seconds Diff: " + Seconds.secondsBetween(startTime, endTime).getSeconds + " seconds. ")
    
    
  }
  
      def getCredentialSecret (aCredentialStore: String, aCredentialAlias: String): String ={
      val config = new org.apache.hadoop.conf.Configuration()
      config.set(org.apache.hadoop.security.alias.CredentialProviderFactory.CREDENTIAL_PROVIDER_PATH, aCredentialStore)
      String.valueOf(config.getPassword(aCredentialAlias))

    }
  
}