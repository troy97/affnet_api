package eu.ibutler.affiliatenetwork.dao.impl;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.base.Throwables;

import eu.ibutler.affiliatenetwork.dao.Extractor;
import eu.ibutler.affiliatenetwork.dao.ProductDao;
import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.NoSuchEntityException;
import eu.ibutler.affiliatenetwork.dao.utils.DbConnectionPool;
import eu.ibutler.affiliatenetwork.dao.utils.JdbcUtils;
import eu.ibutler.affiliatenetwork.entity.Product;

public class ProductDaoImpl extends Extractor<Product> implements ProductDao {
	private static Logger log = Logger.getLogger(ProductDaoImpl.class.getName());
	private DbConnectionPool connectionPool = null;
	private static final String INSERT_SQL = "INSERT INTO tbl_products (  file_id,"
																		+ " shop_id,"
																		+ " is_active,"
																		+ " is_processing,"
			
																		+ " real_url,"
																		+ " name,"
																		+ " price,"
																		+ " currency_code,"
																		+ " category,"
																		
																		+ " image_url,"
																		+ " description,"
																		+ " description_short,"
																		+ " ean, "
																		+ " shipping_price) ";
	
	private static final String UPDATE_SQL = "UPDATE tbl_products SET file_id = ?,"
																		+ " shop_id = ?,"
																		+ " is_active = ?,"
																		+ " is_processing = ?,"
															
																		+ " real_url = ?,"
																		+ " name = ?,"
																		+ " price = ?,"
																		+ " currency_code = ?,"
																		+ " category = ?,"
																		
																		+ " image_url = ?,"
																		+ " description = ?,"
																		+ " description_short = ?,"
																		+ " ean = ?, "
																		+ " shipping_price = ? "
																		+ " WHERE id = ? ;";
	
	private static final String SELECT_ALL_SQL = "SELECT * FROM tbl_products ";
	
	public ProductDaoImpl() {
		this.connectionPool = DbConnectionPool.getInstance();
	}

	@Override
	public Product selectById(long dbId) throws DbAccessException, NoSuchEntityException {
		Connection conn=null;
		Statement stm=null;
		ResultSet rs=null;
		try{
			conn = connectionPool.getConnection();
			stm = conn.createStatement();
			rs = stm.executeQuery(SELECT_ALL_SQL + "WHERE id = \'" + dbId + "\'");
			if(rs.next()) {
				return extractOne(rs);
			} else {
				throw new NoSuchEntityException();
			}
		} catch(SQLException e){
			log.debug("SQL exception");
			throw new DbAccessException("Error accessing DB", e);
		}
		finally{
			JdbcUtils.close(rs);
			JdbcUtils.close(stm);
			JdbcUtils.close(conn);
		}
	}
	
	public Product selectByUrl(String url, int shopId) throws DbAccessException {
		Connection conn=null;
		PreparedStatement pstm=null;
		ResultSet rs=null;
		try{
			conn = connectionPool.getConnection();
			String sql = SELECT_ALL_SQL + "WHERE real_url = ? AND shop_id = ? ;";
			pstm = conn.prepareStatement(sql);
			
			pstm.setString(1, url);
			pstm.setInt(2, shopId);
			
			rs = pstm.executeQuery();
			if(rs.next()) {
				return extractOne(rs);
			} else {
				return null;
			}
		} catch(SQLException e){
			log.debug("SQL exception");
			throw new DbAccessException("Error accessing DB", e);
		}
		finally{
			JdbcUtils.close(rs);
			JdbcUtils.close(pstm);
			JdbcUtils.close(conn);
		}
	}
	
	/**
	 * Selects all active products for a concrete shop, return ResultSet object,
	 * Caller of this method is responsible for closing this ResultSet, his Statement and Connection
	 * @param shopId
	 * @return
	 * @throws DbAccessException
	 * @throws NoSuchEntityException
	 */
	public ResultSet selectAllByShop(int shopId) throws DbAccessException {
		Connection conn=null;
		PreparedStatement pstm=null;
		ResultSet rs=null;
		try{
			conn = connectionPool.getConnection();
			String sql = SELECT_ALL_SQL + "WHERE shop_id = ? AND is_active = true ;";
			pstm = conn.prepareStatement(sql);
			pstm.setInt(1, shopId);
			
			return pstm.executeQuery();
			
		} catch(SQLException e){
			log.debug("SQL exception");
			JdbcUtils.close(rs);
			JdbcUtils.close(pstm);
			JdbcUtils.close(conn);
			throw new DbAccessException("Error accessing DB", e);
		}
	}
	
	@Override
	public long insertOne(Product product) throws DbAccessException {
		PreparedStatement pstm = null;
		ResultSet rs = null;
		Connection conn = null;
		try{
			conn = connectionPool.getConnection();
			String sql = INSERT_SQL;
			sql+="VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			pstm = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			pstm.setInt(1, product.getFileId());
			pstm.setInt(2, product.getShopId());
			pstm.setBoolean(3, product.isActive());
			pstm.setBoolean(4, product.isProcessing());
			
			pstm.setString(5, product.getRealUrl());
			pstm.setString(6, product.getName());
			pstm.setDouble(7, product.getPrice());
			pstm.setString(8, product.getCurrencyCode());
			pstm.setString(9, product.getCategory());
			
			pstm.setString(10, product.getImageUrl());
			pstm.setString(11, product.getDescription());
			pstm.setString(12, product.getDescriptionShort());
			pstm.setString(13, product.getEan());
			pstm.setDouble(14, product.getShippingPrice());
		
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
	
	public long insertOne(Product product, Connection conn) throws DbAccessException {
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try{
			String sql = INSERT_SQL;
			sql+="VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			pstm = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			pstm.setInt(1, product.getFileId());
			pstm.setInt(2, product.getShopId());
			pstm.setBoolean(3, product.isActive());
			pstm.setBoolean(4, product.isProcessing());
			
			pstm.setString(5, product.getRealUrl());
			pstm.setString(6, product.getName());
			pstm.setDouble(7, product.getPrice());
			pstm.setString(8, product.getCurrencyCode());
			pstm.setString(9, product.getCategory());
			
			pstm.setString(10, product.getImageUrl());
			pstm.setString(11, product.getDescription());
			pstm.setString(12, product.getDescriptionShort());
			pstm.setString(13, product.getEan());
			pstm.setDouble(14, product.getShippingPrice());
		
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
		}
	}

	@Override
	public void insertAll(List<Product> products) throws DbAccessException {
		Connection conn=null;
		PreparedStatement pstm = null;
		try {
			conn = connectionPool.getConnection();
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			conn.setAutoCommit(false);
			String sql=INSERT_SQL;
			sql+="VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			pstm=conn.prepareStatement(sql);
			for(Product product : products){
				pstm.setInt(1, product.getFileId());
				pstm.setInt(2, product.getShopId());
				pstm.setBoolean(3, product.isActive());
				pstm.setBoolean(4, product.isProcessing());
				
				pstm.setString(5, product.getRealUrl());
				pstm.setString(6, product.getName());
				pstm.setDouble(7, product.getPrice());
				pstm.setString(8, product.getCurrencyCode());
				pstm.setString(9, product.getCategory());
				
				pstm.setString(10, product.getImageUrl());
				pstm.setString(11, product.getDescription());
				pstm.setString(12, product.getDescriptionShort());
				pstm.setString(13, product.getEan());
				pstm.setDouble(14, product.getShippingPrice());
				pstm.addBatch();
			}
			try{
				pstm.executeBatch();
			}
			catch(BatchUpdateException e){
				conn.rollback();
				System.err.println(Throwables.getStackTraceAsString(e));
				throw e;
			}
			conn.commit();
		}
		catch(SQLException e){
			throw new DbAccessException("Error accessing DB", e);
		}
		finally{
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				log.debug("Exception: unable to resume AutoCommit");
			}
			JdbcUtils.close(pstm);
			JdbcUtils.close(conn);
		}
		
	}
	
	/**
	 * @param product
	 * @throws DbAccessException
	 */
	public void update(Product product) throws DbAccessException {
			Connection conn = null;
			PreparedStatement pstm = null;
			ResultSet rs = null;
			try{
				conn = connectionPool.getConnection();
				conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
				conn.setAutoCommit(false);
				String sql = UPDATE_SQL;
				pstm = conn.prepareStatement(sql);
				
				pstm.setInt(1, product.getFileId());
				pstm.setInt(2, product.getShopId());
				pstm.setBoolean(3, product.isActive());
				pstm.setBoolean(4, product.isProcessing());
				
				pstm.setString(5, product.getRealUrl());
				pstm.setString(6, product.getName());
				pstm.setDouble(7, product.getPrice());
				pstm.setString(8, product.getCurrencyCode());
				pstm.setString(9, product.getCategory());
				
				pstm.setString(10, product.getImageUrl());
				pstm.setString(11, product.getDescription());
				pstm.setString(12, product.getDescriptionShort());
				pstm.setString(13, product.getEan());
				pstm.setDouble(14, product.getShippingPrice());
				
				pstm.setDouble(15, product.getId());
				
				pstm.executeUpdate(sql);
				JdbcUtils.commit(conn);
			}
			catch(SQLException e){
				JdbcUtils.rollback(conn);
				log.error("Error updating entry: " + e);
				throw new DbAccessException(e.getMessage());
			}
			finally{
				try {
					conn.setAutoCommit(true);
				} catch (SQLException e) {
					log.debug("Exception: unable to resume AutoCommit");
				}
				JdbcUtils.close(rs);
				JdbcUtils.close(pstm);
				JdbcUtils.close(conn);
			}
	}
	
	public void update(Product product, Connection conn) throws DbAccessException {
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try{
			String sql = UPDATE_SQL;
			pstm = conn.prepareStatement(sql);
			
			pstm.setInt(1, product.getFileId());
			pstm.setInt(2, product.getShopId());
			pstm.setBoolean(3, product.isActive());
			pstm.setBoolean(4, product.isProcessing());
			
			pstm.setString(5, product.getRealUrl());
			pstm.setString(6, product.getName());
			pstm.setDouble(7, product.getPrice());
			pstm.setString(8, product.getCurrencyCode());
			pstm.setString(9, product.getCategory());
			
			pstm.setString(10, product.getImageUrl());
			pstm.setString(11, product.getDescription());
			pstm.setString(12, product.getDescriptionShort());
			pstm.setString(13, product.getEan());
			pstm.setDouble(14, product.getShippingPrice());
			
			pstm.setDouble(15, product.getId());
			
			pstm.executeUpdate();
		}
		catch(SQLException e){
			log.error("Error updating entry: " + e);
			throw new DbAccessException(e.getMessage());
		}
		finally{
			JdbcUtils.close(rs);
			JdbcUtils.close(pstm);
		}
}
	
	/**
	 * Set "is_processing" status for those products that belong
	 * to given shop and are active at the moment
	 * @param shopId
	 * @throws DbAccessException
	 */
	public void setProcessing(int shopId, boolean isProcessing) throws DbAccessException {
			Connection conn = null;
			PreparedStatement pstm = null;
			ResultSet rs = null;
			try{
				conn = connectionPool.getConnection();
				String sql = "UPDATE tbl_products SET is_processing = ? WHERE shop_id = ? AND is_active = true ;";
				pstm = conn.prepareStatement(sql);

				pstm.setBoolean(1, isProcessing);
				pstm.setInt(2, shopId);

				pstm.executeUpdate();
			}
			catch(SQLException e){
				log.error("Error updating entry: " + e);
				throw new DbAccessException(e.getMessage());
			}
			finally{
				JdbcUtils.close(rs);
				JdbcUtils.close(pstm);
				JdbcUtils.close(conn);
			}
	}
	
	/**
	 * Set is_active=false for those products, that are not under processing
	 * @param shopId
	 * @throws DbAccessException
	 */
	public void deactivateOld(int shopId) throws DbAccessException {
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try{
			conn = connectionPool.getConnection();
			String sql = "UPDATE tbl_products SET is_active = false, is_processing = false WHERE shop_id = ? AND is_processing = true ;";
			pstm = conn.prepareStatement(sql);
			
			pstm.setInt(1, shopId);
			
			pstm.executeUpdate();
		}
		catch(SQLException e){
			log.error("Error updating entry: " + Throwables.getStackTraceAsString(e));
			throw new DbAccessException(e.getMessage());
		}
		finally{
			JdbcUtils.close(rs);
			JdbcUtils.close(pstm);
			JdbcUtils.close(conn);
		}
	}

	@Override
	public Product extractOne(ResultSet rs) throws SQLException {
		return new Product( rs.getLong("id"),
							rs.getInt("file_id"),
							rs.getInt("shop_id"),
							rs.getBoolean("is_active"),
							rs.getBoolean("is_processing"),
							
							rs.getString("real_url"),
							rs.getString("name"),
							rs.getDouble("price"),
							rs.getString("currency_code"),
							rs.getString("category"),
							
							rs.getString("image_url"),
							rs.getString("description"),
							rs.getString("description_short"),
							rs.getString("ean"),
							rs.getDouble("shipping_price"));
	}

	@Override
	public List<Product> selectByFileId(int fileId) throws DbAccessException {
		Connection conn=null;
		Statement stm=null;
		ResultSet rs=null;
		try{
			conn = connectionPool.getConnection();
			stm = conn.createStatement();
			rs = stm.executeQuery(SELECT_ALL_SQL + "WHERE file_id = \'" + fileId + "\'");
			return extractAll(rs);
		} catch(SQLException e){
			log.debug("SQL exception");
			throw new DbAccessException("Error accessing DB", e);
		}
		finally{
			JdbcUtils.close(rs);
			JdbcUtils.close(stm);
			JdbcUtils.close(conn);
		}
	}

}
