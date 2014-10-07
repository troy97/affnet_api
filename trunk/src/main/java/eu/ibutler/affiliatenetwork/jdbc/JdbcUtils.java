package eu.ibutler.affiliatenetwork.jdbc;

import java.sql.*;

import org.apache.log4j.Logger;

import eu.ibutler.affiliatenetwork.dao.impl.UserDaoImpl;

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
}
