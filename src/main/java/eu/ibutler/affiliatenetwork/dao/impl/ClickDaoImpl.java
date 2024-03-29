package eu.ibutler.affiliatenetwork.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Throwables;

import eu.ibutler.affiliatenetwork.dao.ClickDao;
import eu.ibutler.affiliatenetwork.dao.Extractor;
import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.NoSuchEntityException;
import eu.ibutler.affiliatenetwork.dao.exceptions.UniqueConstraintViolationException;
import eu.ibutler.affiliatenetwork.dao.utils.DbConnectionPool;
import eu.ibutler.affiliatenetwork.dao.utils.JdbcUtils;
import eu.ibutler.affiliatenetwork.entity.Click;

public class ClickDaoImpl extends Extractor<Click> implements ClickDao {
	
	private static final String INSERT_SQL = "INSERT INTO tbl_clicks (shop_id, product_id, distributor_id, sub_id, product_name, product_price, shipping_price, click_time) "
			+ "VALUES(?, ?, ?, ?, ?, ?, ?, ?) ;";
	private static final String SELECT_SQL = "SELECT * FROM tbl_clicks ";
	

	private DbConnectionPool connectionPool = null;
	
	/**
	 * Public constructor
	 */
	public ClickDaoImpl() {
		this.connectionPool = DbConnectionPool.getInstance();
	}

	@Override
	public long insertOne(Click entity) throws DbAccessException {
		PreparedStatement pstm = null;
		ResultSet rs = null;
		Connection conn = null;
		try{
			conn = connectionPool.getConnection();
			String sql = INSERT_SQL;
			pstm = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			
			pstm.setInt(1, entity.getShopId());
			pstm.setLong(2, entity.getProductId());
			pstm.setInt(3, entity.getDistributorId());
			if(entity.getSubId()<0) {
				pstm.setNull(4, Types.NULL);
			} else {
				pstm.setInt(4, entity.getSubId());
			}
			pstm.setString(5, entity.getProductName());
			pstm.setDouble(6, entity.getProductPrice());
			pstm.setDouble(7, entity.getShippingPrice());
			pstm.setLong(8, entity.getClickTime());
			
			pstm.executeUpdate();
			rs=pstm.getGeneratedKeys();
			rs.next();	
			int idColumnNumber = 1;
			return rs.getLong(idColumnNumber);
		}
		catch(SQLException e){
			logger.debug("Error inserting entity: " + Throwables.getStackTraceAsString(e));
			throw new DbAccessException("Error accessing DB", e);
		}
		finally{
			JdbcUtils.close(rs);
			JdbcUtils.close(pstm);
			JdbcUtils.close(conn);
		}
	}

	@Override
	public void insertAll(Collection<Click> entities) throws DbAccessException,
			UniqueConstraintViolationException {
		
		throw new UnsupportedOperationException();
		
	}

	@Override
	public Click selectById(long id) throws DbAccessException,
			NoSuchEntityException {
		
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<Click> selectAll(int limit) throws DbAccessException {
		Connection conn = null;
		Statement stm=null;
		ResultSet rs = null;	
		try{
			conn=connectionPool.getConnection();
			stm = conn.createStatement();
			String sql = SELECT_SQL;
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

	@Override
	public void update(Click entity) throws DbAccessException,
			NoSuchEntityException {
		
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void deleteById(long id) throws DbAccessException,
			NoSuchEntityException {
		
		throw new UnsupportedOperationException();
		
	}

	@Override
	public List<Click> selectByShopId(int id) throws DbAccessException {
		Connection conn = null;
		Statement stm=null;
		ResultSet rs = null;	
		try{
			conn=connectionPool.getConnection();
			stm = conn.createStatement();
			String sql = SELECT_SQL + "WHERE webshop_id=" + id + ";";
			rs = stm.executeQuery(sql);
			return extractAll(rs);
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
	public List<Click> selectByDistributorId(int id) throws DbAccessException {
		Connection conn = null;
		Statement stm=null;
		ResultSet rs = null;	
		try{
			conn=connectionPool.getConnection();
			stm = conn.createStatement();
			String sql = SELECT_SQL + "WHERE distributor_id=" + id + ";";
			rs = stm.executeQuery(sql);
			return extractAll(rs);
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
	public List<Click> selectByProductId(int id) throws DbAccessException {
		Connection conn = null;
		Statement stm=null;
		ResultSet rs = null;	
		try{
			conn=connectionPool.getConnection();
			stm = conn.createStatement();
			String sql = SELECT_SQL + "WHERE product_id=" + id + ";";
			rs = stm.executeQuery(sql);
			return extractAll(rs);
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
	protected Click extractOne(ResultSet rs) throws SQLException {
		return new Click(rs.getInt("id"),
				rs.getInt("product_id"),
				rs.getInt("shop_id"),
				rs.getInt("distributor_id"),
				rs.getInt("sub_id"),
				rs.getString("product_name"),
				rs.getDouble("product_price"),
				rs.getDouble("shipping_price")
				
				);
	}



}
