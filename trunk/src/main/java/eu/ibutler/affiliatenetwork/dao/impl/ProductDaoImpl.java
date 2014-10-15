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
			String sql = "INSERT INTO tbl_products (url_path, name, description, file_id, webshop_id) ";
			sql+="VALUES (";
			sql+="\'"+ product.getUrlPath()+"\', ";
			sql+="\'"+ product.getName()+"\', ";
			sql+="\'"+ product.getDescription()+"\',";
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
			String sql="INSERT INTO tbl_products (url_path, name, description, file_id, webshop_id) ";
			sql+="VALUES (?, ?, ?, ?, ?)";
			pstm=conn.prepareStatement(sql);
			for(Product product : products){
				pstm.setString(1, product.getUrlPath());
				pstm.setString(2, product.getName());
				pstm.setString(3, product.getDescription());
				pstm.setInt(   4, product.getFileDbId());
				pstm.setInt(   5, product.getWebshopDbId());
				pstm.addBatch();
			}
			try{
				pstm.executeBatch();
			}
			catch(BatchUpdateException e){
				conn.rollback();
				throw new BatchUpdateException();
			}
			conn.commit();
		}
		catch(SQLException e){
			throw new DbAccessException("Error accessing DB", e);
		}
		finally{
			JdbcUtils.close(pstm);
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				System.out.println("Exception: unable to resume AutoCommit");
			}
		}
		
	}

	@Override
	protected Product extractOne(ResultSet rs) throws SQLException {
		return new Product(rs.getInt("id"), rs.getString("url_path"), rs.getString("name"),
				rs.getString("description"), rs.getInt("file_id"), rs.getInt("webshop_id"));
	}

}
