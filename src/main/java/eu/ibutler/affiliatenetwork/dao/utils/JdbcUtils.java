package eu.ibutler.affiliatenetwork.dao.utils;

import java.sql.*;

import org.apache.log4j.Logger;

public class JdbcUtils {
	
	private static Logger log = Logger.getLogger(JdbcUtils.class.getName());
	
	public static void close(ResultSet rs){
		if(rs!=null){
			try{
				rs.close();
			}
			catch(SQLException e){
				//NOP
			}
		}
	}
	
	public static void close(Statement stm){
		if(stm!=null){
			try{
				stm.close();
			}
			catch(SQLException e){
				//NOP
			}
		}
	}
	
	public static void close(PreparedStatement pstm){
		if(pstm!=null){
			try{
				pstm.close();
			}
			catch(SQLException e){
				//NOP
			}
		}
	}
	
	public static void close(Connection conn){
		if(conn!=null){
			try{
				conn.close();
			}
			catch(SQLException e){
				//NOP
			}
		}
	}
	
	public static void commit(Connection conn) {
		try {
			conn.commit();
		} catch (SQLException e) {
			log.debug("Error commiting transaction");
			e.printStackTrace();
		}
	}
	
	public static void rollback(Connection conn) {
		try {
			conn.rollback();
		} catch (SQLException e) {
			log.debug("Error rolling back transaction");
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Part of transaction manager implementation
	 * @return connection with started transaction
	 */
	public static Connection getConnection() {
		Connection result = null;
		try {
			result = DbConnectionPool.getInstance().getConnection();
			result.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
			result.setAutoCommit(false);
		} catch (SQLException e) {
			log.debug("Unable create connection");
		}
		return result;
	}
	
	/**
	 * Part of transaction manager implementation
	 * Commits transaction and closes connection 
	 */
	public static void commitAndClose(Connection conn) {
		JdbcUtils.commit(conn);
		try {
			conn.setAutoCommit(true);
		} catch (SQLException ignore) {
			ignore.printStackTrace();
		}
		JdbcUtils.close(conn);
	}
	
	/**
	 * Part of transaction manager implementation
	 * Rolls back transaction and closes connection 
	 */
	public static void rollbackAndClose(Connection conn) {
		JdbcUtils.rollback(conn);
		try {
			conn.setAutoCommit(true);
		} catch (SQLException ignore) {
			ignore.printStackTrace();
		}
		JdbcUtils.close(conn);
	}
}
