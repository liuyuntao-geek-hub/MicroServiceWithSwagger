/**
 * 
 */
package connecthive;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.hadoop.security.UserGroupInformation;

/**
 * @author AF53723
 *
 */
public class TestKerberosHive {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		Connection con  = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		
		
		org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
		conf.set("hadoop.security.authentication", "Kerberos");
		UserGroupInformation.setConfiguration(conf); 
		System.out.println("UserGroupInformation"+UserGroupInformation.getCurrentUser());
		UserGroupInformation.loginUserFromKeytab("af53723@DEVAD.WELLPOINT.COM", "/etc/krb5.keytab"); 
		System.out.println("Line 30");

		Class.forName("");
		

	}
	
//	static void getConnection( Subject signedOnUserSubject ) throws Exception{
//	       Connection conn = (Connection) Subject.doAs(signedOnUserSubject, new PrivilegedExceptionAction<Object>()
//	           {
//	               public Object run()
//	               {
//	                       Connection con = null;
//	                       String JDBC_DB_URL = "jdbc:hive2://dwbdtest1r1m.wellpoint.com:1000/dv_pdppadpph_nogbd_r000_in;" +
//	                                              "principal=hive/_HOST@DEVAD.WELLPOINT.COM;" +
//	                                              "auth=kerberos;kerberosAuthType=fromSubject";
//	                       try {
//	                               Class.forName("org.apache.hive.jdbc.HiveDrive");
//	                               con =  DriverManager.getConnection(JDBC_DB_URL);
//	                       } catch (SQLException e) {
//	                               e.printStackTrace();
//	                       } catch (ClassNotFoundException e) {
//	                               e.printStackTrace();
//	                       }
//	                       return con;
//	               }
//	           });
//	       
//	}

}
