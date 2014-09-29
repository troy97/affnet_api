package eu.ibutler.affiliatenetwork.jdbc;

import java.sql.*;

public class JdbcUtils {
	
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
}
