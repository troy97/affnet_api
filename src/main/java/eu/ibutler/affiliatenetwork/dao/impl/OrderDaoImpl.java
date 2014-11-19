package eu.ibutler.affiliatenetwork.dao.impl;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;

import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.utils.DbConnectionPool;
import eu.ibutler.affiliatenetwork.dao.utils.JdbcUtils;

public class OrderDaoImpl {
	private static Logger log = Logger.getLogger(ProductDaoImpl.class.getName());
	private DbConnectionPool connectionPool = null;
	private static final String INSERT_SQL = "INSERT INTO tbl_orders (  product_id,"
																		+ " distributor_id,"
																		+ " sub_id,"
																		+ " click_id,"
			
																		+ " status,"
																		+ " price_original,"
																		+ " currency_original_id,"
																		+ " price_common,"
																		+ " title,"
																		
																		+ " created_at,"
																		+ " updated_at"
																		+ ") ";
	
	
	
	public OrderDaoImpl() {
		this.connectionPool = DbConnectionPool.getInstance();
	}

	
	public long insertOne(long product_id,
							int distributor_id,
							int sub_id,
							long click_id,
							String status,
							double price_original,
							int currency_original_id,
							double price_common,
							String title,
							long created_at,
							long updated_at
			
			) throws DbAccessException {
		PreparedStatement pstm = null;
		ResultSet rs = null;
		Connection conn = null;
		try{
			conn = connectionPool.getConnection();
			String sql = INSERT_SQL;
			sql+="VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			pstm = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			pstm.setLong(1, product_id);
			pstm.setInt(2, distributor_id);
			pstm.setInt(3, sub_id);
			pstm.setLong(4, click_id);
			
			pstm.setString(5, status);
			pstm.setDouble(6, price_original);
			pstm.setInt(7, currency_original_id);
			pstm.setDouble(8, price_common);
			pstm.setString(9, title);
			
			pstm.setLong(10, created_at);
			pstm.setLong(11, updated_at);
		
			pstm.executeUpdate();
			rs=pstm.getGeneratedKeys();
			rs.next();	
			int idColumnNumber = 1;
			return rs.getLong(idColumnNumber);
		}
		catch(SQLException e){
			log.debug("SQL exception in insertOne()");
			throw new DbAccessException("Error accessing DB", e);
		}
		finally{
			JdbcUtils.close(rs);
			JdbcUtils.close(pstm);
			JdbcUtils.close(conn);
		}
	}	


}

