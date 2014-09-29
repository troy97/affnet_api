package eu.ibutler.affiliatenetwork.jdbc;

import java.sql.*;

public interface ConnectionFactory {
	
	public Connection getConnection() throws SQLException;
	
	public void close();
}
