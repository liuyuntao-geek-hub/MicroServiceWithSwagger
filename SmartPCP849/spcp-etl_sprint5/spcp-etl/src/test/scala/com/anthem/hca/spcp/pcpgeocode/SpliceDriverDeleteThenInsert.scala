package com.anthem.hca.spcp.pcpgeocode

import com.splicemachine.derby.impl.SpliceSpark
import com.splicemachine.spark.splicemachine.SplicemachineContext
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import com.anthem.hca.spcp.config.SparkConfig

object SpliceDriverDeleteThenInsert {

  def main(args: Array[String]) {

    val spark = SparkConfig.spark
    SpliceSpark.setContext(spark.sparkContext)

    val dbUrlWithOutPwd = "jdbc:splice://dwbdtest1r5w1.wellpoint.com:1527/splicedb;user=srcpdpspcpbthdv;password="
    val dbUrl = dbUrlWithOutPwd.concat(getCredentialSecret("jceks://hdfs//dv/hdfsapp/ve2/pdp/spcp/phi/no_gbd/r000/bin//dev849.jceks", "dev849pass"))

    val splicemachineContext = new SplicemachineContext(dbUrl)
    println(dbUrl)
    val df1 = splicemachineContext.df("SELECT PROV_ORG_TAX_ID, IP_NPI, PCP_ID FROM DV_PDPSPCP_XM.TINNPI_TO_PCP_ID")

    df1.printSchema()
    df1.show()

    spark.close()
  }

  def getCredentialSecret(aCredentialStore: String, aCredentialAlias: String): String = {
    val config = new org.apache.hadoop.conf.Configuration()
    config.set(org.apache.hadoop.security.alias.CredentialProviderFactory.CREDENTIAL_PROVIDER_PATH, aCredentialStore)
    String.valueOf(config.getPassword(aCredentialAlias))
  }
}