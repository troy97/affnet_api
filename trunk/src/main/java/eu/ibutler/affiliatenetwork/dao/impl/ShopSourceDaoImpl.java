package eu.ibutler.affiliatenetwork.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.base.Throwables;

import eu.ibutler.affiliatenetwork.dao.Extractor;
import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.UniqueConstraintViolationException;
import eu.ibutler.affiliatenetwork.dao.utils.DbConnectionPool;
import eu.ibutler.affiliatenetwork.dao.utils.JdbcUtils;
import eu.ibutler.affiliatenetwork.entity.Shop;
import eu.ibutler.affiliatenetwork.entity.ShopSource;

public class ShopSourceDaoImpl extends Extractor<ShopSource> {
	
	private static final String INSERT_SQL = "INSERT INTO tbl_shop_sources (shop_id, file_format, download_url,"
			+ " basic_http_auth_required, basic_http_auth_username, basic_http_auth_password) ";
	private static final String SELECT_SQL = "SELECT * FROM tbl_shop_sources ";
	private static final String UPDATE_SQL = "UPDATE tbl_shop_sources SET ";
	
	private static Logger logger = Logger.getLogger(ShopSourceDaoImpl.class.getName());
	private DbConnectionPool connectionPool = null;
	
	public ShopSourceDaoImpl() {
		this.connectionPool = DbConnectionPool.getInstance();
	}



	
	public List<ShopSource> selectNew() throws DbAccessException {
		Connection conn = null;
		Statement stm=null;
		ResultSet rs = null;	
		try{
			conn=connectionPool.getConnection();
			stm = conn.createStatement();
			String sql = SELECT_SQL + "WHERE last_queried_at=0 AND is_active=true;";
			rs = stm.executeQuery(sql);
			return extractAll(rs);
		}
		catch(SQLException e){
			logger.debug("SQL exception");
			throw new DbAccessException("Error accessing DB", e);	
		}
		finally{
			JdbcUtils.close(rs);
			JdbcUtils.close(stm);
			JdbcUtils.close(conn);
		}
	}
	
	public List<ShopSource> selectOlderThan(long age) throws DbAccessException {
		Connection conn = null;
		Statement stm=null;
		ResultSet rs = null;	
		try{
			conn=connectionPool.getConnection();
			stm = conn.createStatement();
			long currentTime = System.currentTimeMillis();
			String sql = "SELECT * FROM tbl_shop_sources WHERE id IN"
					+ " (SELECT shop_source_id FROM"
					+ " (SELECT DISTINCT ON (shop_source_id) * FROM tbl_synch_attempts"
					+ " WHERE is_successful=TRUE ORDER BY shop_source_id, time_start DESC) AS foo"
					+ " WHERE " +currentTime+ "-time_start > " +age+ ")";
			rs = stm.executeQuery(sql);
			return extractAll(rs);
		}
		catch(SQLException e){
			logger.debug("SQL exception");
			throw new DbAccessException("Error accessing DB", e);	
		}
		finally{
			JdbcUtils.close(rs);
			JdbcUtils.close(stm);
			JdbcUtils.close(conn);
		}
	}
	
	public int insertSynch(boolean is_successful, String msg, long timeStart,
			long  timeStop, int sourceId) throws DbAccessException {
		Statement stm = null;
		ResultSet rs = null;
		Connection conn = null;
		try{
			conn = connectionPool.getConnection();
			stm = conn.createStatement();
			String sql = "INSERT INTO tbl_synch_attempts (is_successful, error_message,"
					+ " time_start, time_stop, shop_source_id) ";
			sql+="VALUES (";
			sql+="\'"+ is_successful +"\', ";
			sql+="\'"+ msg +"\', ";
			sql+="\'"+ timeStart +"\', ";
			sql+="\'"+ timeStop +"\', ";
			sql+="\'"+ sourceId +"\' ";
			sql+=");";
			stm.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			rs=stm.getGeneratedKeys();
			rs.next();	
			int idColumnNumber = 1;
			return rs.getInt(idColumnNumber);
		}
		catch(SQLException e){
			throw new DbAccessException("Error accessing DB", e);
		}
		finally{
			JdbcUtils.close(rs);
			JdbcUtils.close(stm);
			JdbcUtils.close(conn);
		}
	}	
	

	@Override
	protected ShopSource extractOne(ResultSet rs) throws SQLException {
		return new ShopSource(rs.getInt("id"),
				rs.getInt("shop_id"),
				rs.getString("file_format"),
				rs.getString("download_url"),
				rs.getBoolean("basic_http_auth_required"),
				rs.getString("basic_http_auth_username"),
				rs.getString("basic_http_auth_password"));
	}



	public void updateSourceTime(long currentTimeMillis, int id) throws DbAccessException {
		Connection conn = null;
		Statement stm = null;
		try{
			conn = connectionPool.getConnection();
			stm = conn.createStatement();
			String sql = "UPDATE tbl_shop_sources SET "
					+ "last_queried_at="+ currentTimeMillis +" "
					+ "WHERE id=" + id + ";";
			stm.executeUpdate(sql);
		}
		catch(SQLException e){
			logger.error("Error updating entry: " + e);
			throw new DbAccessException(e.getMessage());
		}
		finally{
			JdbcUtils.close(stm);
			JdbcUtils.close(conn);
		}
	}
	
}
