package eu.ibutler.affiliatenetwork.jdbc;

import java.sql.*;

public class JdbcNativeFactory implements ConnectionFactory {
	
	private String dbUrl="jdbc:mysql://127.0.0.1:3306/mycatalog";	
	private String login="troy";
	private String password="deus97";
	private Connection conn=null;
	
	public JdbcNativeFactory() throws SQLException{
		this.conn=DriverManager.getConnection(this.dbUrl, this.login, this.password);
	}
	
	public Connection getConnection(){
		return this.conn;
	}
	
	public void close() {
		try {
			this.conn.close();
		} catch (SQLException e) {
			System.out.println("Failed to close connection in JdbcNativeFactory.close()");
		}	
	}
}