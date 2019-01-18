package com.anthem.spliceJDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.*;

public class javaSimpleConnect {
	public static void main(String[] arg) {
	    //JDBC Connection String - sample connects to local database
	    String dbUrl = "jdbc:splice://dwbdtest1r1w5.wellpoint.com:1527/splicedb;user=srcpdpspcpbthts;password=******";
	    

	    try{
	        //For the JDBC Driver - Use the Apache Derby Client Driver
	        Class.forName("com.splicemachine.db.jdbc.ClientDriver");
	    }catch(ClassNotFoundException cne){
	        cne.printStackTrace();
	        return; //exit early if we can't find the driver
	    }

	    try(Connection conn = DriverManager.getConnection(dbUrl)){
	        //Create a statement
	        try(Statement statement = conn.createStatement()){

/*	            //Create a table
	            statement.execute("CREATE TABLE MYTESTTABLE(a int, b varchar(30))");

	            //Insert data into the table
	            statement.execute("insert into MYTESTTABLE values (1,'a')");
	            statement.execute("insert into MYTESTTABLE values (2,'b')");
	            statement.execute("insert into MYTESTTABLE values (3,'c')");
	            statement.execute("insert into MYTESTTABLE values (4,'c')");
	            statement.execute("insert into MYTESTTABLE values (5,'c')");
*/
	            int counter=0;
	            //Execute a Query
	            try(ResultSet rs=statement.executeQuery("select * from DV_PDPSPCP_XM.PROVIDER_INFO {limit 10}")){
	                while(rs.next()){
	                    counter++;
	                    String val_a=rs.getObject(1).toString();
	                    String val_b=rs.getObject(2).toString();
	                    System.out.println("record=["+counter+"] a=["+val_a+"] b=["+val_b+"]");
	                }
	            }
	        }

	    }catch (SQLException se) {
	        se.printStackTrace();
	    }
	}


}
