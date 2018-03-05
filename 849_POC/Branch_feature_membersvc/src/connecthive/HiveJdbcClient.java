package connecthive;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.security.auth.login.LoginContext;

import org.apache.hadoop.security.UserGroupInformation;

public class HiveJdbcClient {

	private static String driverName = "org.apache.hive.jdbc.HiveDriver";

	public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException {

		org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
		conf.set("hadoop.security.authentication", "Kerberos");
		UserGroupInformation.setConfiguration(conf); 
		System.out.println("UserGroupInformation"+UserGroupInformation.getCurrentUser());
//		LoginContext lc =kinit();
//		UserGroupInformation.loginUserFromSubject(lc.getSubject());
		UserGroupInformation.loginUserFromKeytab("krbtgt/DEVAD.WELLPOINT.COM@DEVAD.WELLPOINT.COM", "/tmp/krb5cc_cdc1122636986_yLPzrO.keytab"); 
		System.out.println("Line 30");

		Class.forName(driverName);


		Connection con = DriverManager.getConnection(
				"jdbc:hive2://dwbdtest1r2m3.wellpoint.com:2181,dwbdtest1r1m.wellpoint.com:2181,dwbdtest1r2m.wellpoint.com:2181/;serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hiveserver2");
		Statement stmt = con.createStatement();

		String tableName = "test";
		ResultSet res  = null;

		// show tables
		String sql = "show tables '" + tableName + "'";
		System.out.println("Running: " + sql);
		res = stmt.executeQuery(sql);
		if (res.next()) {
			System.out.println(res.getString(1));
		}

		// describe table
		sql = "describe " + tableName;
		System.out.println("Running: " + sql);
		res = stmt.executeQuery(sql);
		while (res.next()) {
			System.out.println(res.getString(1) + "\t" + res.getString(2) + "\t" + res.getString(2));
		}

		res.close();
		stmt.close();
		con.close();
	}


}
