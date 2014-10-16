package eu.ibutler.affiliatenetwork.dao.impl;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.log4j.Logger;

import eu.ibutler.affiliatenetwork.dao.Extractor;
import eu.ibutler.affiliatenetwork.dao.ProductDao;
import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.NoSuchEntityException;
import eu.ibutler.affiliatenetwork.dao.exceptions.UniqueConstraintViolationException;
import eu.ibutler.affiliatenetwork.entity.Product;
import eu.ibutler.affiliatenetwork.jdbc.DbConnectionPool;
import eu.ibutler.affiliatenetwork.jdbc.JdbcUtils;

public class ProductDaoImpl extends Extractor<Product> implements ProductDao {
	private static Logger log = Logger.getLogger(ProductDaoImpl.class.getName());
	private DbConnectionPool connectionPool = null;
	private static final String INSERT_SQL = "INSERT INTO tbl_products (  url_path,"
																		+ " name,"
																		+ " description,"
																		+ " short_description,"
																		+ " image_url,"
																		+ " price,"
																		+ " price_currency,"
																		+ " weight,"
																		+ " shipping_price,"
																		+ " category,"
																		+ " ean,"
																		+ " file_id,"
																		+ " webshop_id) ";
	
	public ProductDaoImpl() {
		this.connectionPool = DbConnectionPool.getInstance();
	}

	@Override
	public Product selectById(int dbId) throws DbAccessException, NoSuchEntityException {
		Connection conn=null;
		Statement stm=null;
		ResultSet rs=null;
		try{
			conn = connectionPool.getConnection();
			stm = conn.createStatement();
			rs = stm.executeQuery("SELECT * "
					+ "FROM tbl_products "
					+ "WHERE id = \'" + dbId + "\'");
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

	@Override
	public long insertOne(Product product) throws DbAccessException {
		Statement stm = null;
		ResultSet rs = null;
		Connection conn = null;
		try{
			conn = connectionPool.getConnection();
			stm = conn.createStatement();
			String sql = INSERT_SQL;
			sql+="VALUES (";
			sql+="\'"+ product.getUrlPath()+"\', ";
			sql+="\'"+ product.getName()+"\', ";
			sql+="\'"+ product.getDescription()+"\',";
			sql+="\'"+ product.getShortDescription()+"\',";
			sql+="\'"+ product.getImageUrl()+"\',";
			sql+="\'"+ product.getPrice()+"\',";
			sql+="\'"+ product.getPriceCurrency()+"\',";
			sql+="\'"+ product.getWeight()+"\',";
			sql+="\'"+ product.getShippingPrice()+"\',";
			sql+="\'"+ product.getCategory()+"\',";
			sql+="\'"+ product.getEan()+"\',";
			sql+="\'"+ product.getFileDbId()+"\',";
			sql+="\'"+ product.getWebshopDbId()+"\'";
			sql+=");";
			stm.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			rs=stm.getGeneratedKeys();
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
			JdbcUtils.close(stm);
			JdbcUtils.close(conn);
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
			sql+="VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			pstm=conn.prepareStatement(sql);
			for(Product product : products){
				pstm.setString( 1, product.getUrlPath());
				pstm.setString( 2, product.getName());
				pstm.setString( 3, product.getDescription());
				pstm.setString( 4, product.getShortDescription());
				pstm.setString( 5, product.getImageUrl());
				pstm.setDouble( 6, product.getPrice());
				pstm.setString( 7, product.getPriceCurrency());
				pstm.setInt(    8, product.getWeight());
				pstm.setDouble( 9, product.getShippingPrice());
				pstm.setString(10, product.getCategory());
				pstm.setString(11, product.getEan());
				pstm.setInt(   12, product.getFileDbId());
				pstm.setInt(   13, product.getWebshopDbId());
				pstm.addBatch();
			}
			try{
				pstm.executeBatch();
			}
			catch(BatchUpdateException e){
				conn.rollback();
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

	@Override
	protected Product extractOne(ResultSet rs) throws SQLException {
		return new Product( rs.getLong("id"),
							rs.getString("url_path"),
							rs.getString("name"),
							rs.getString("description"),
							rs.getString("short_description"),
							rs.getString("image_url"),
							rs.getDouble("price"),
							rs.getString("price_currency"),
							rs.getInt("weight"),
							rs.getDouble("shipping_price"),
							rs.getString("category"),
							rs.getString("ean"),
							rs.getInt("file_id"),
							rs.getInt("webshop_id"));
	}

	@Override
	public List<Product> selectByFileId(int fileId) throws DbAccessException {
		Connection conn=null;
		Statement stm=null;
		ResultSet rs=null;
		try{
			conn = connectionPool.getConnection();
			stm = conn.createStatement();
			rs = stm.executeQuery("SELECT * "
					+ "FROM tbl_products "
					+ "WHERE file_id = \'" + fileId + "\'");
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
