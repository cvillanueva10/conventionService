package org.akpsi.conventionapp.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionFactory {

//	@Value("${jdbc.url}")
	private static String jdbcUrl = "jdbc:mysql://adrianacala.com:3305/akpsi";
	
//	@Value("${jdbc.username}")
	private static String username = "akpsi";
	
//	@Value("${jdbc.password}")
	private static String password = "zetaetatheta";
	
	// Private constructor so you create a new instance of this class.
	private ConnectionFactory(){
	}
	
	public static Connection getConnection(){
		Connection conn;
		try{
			Class.forName("com.mysql.jdbc.Driver"); 
			conn = DriverManager.getConnection(jdbcUrl, username, password);
		}catch(Exception e){
			return null;
		}
		return conn;
	}
	
}
