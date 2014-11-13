package eu.ibutler.affiliatenetwork.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.base.Throwables;

import eu.ibutler.affiliatenetwork.dao.Extractor;
import eu.ibutler.affiliatenetwork.dao.FileDao;
import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.UniqueConstraintViolationException;
import eu.ibutler.affiliatenetwork.dao.utils.DbConnectionPool;
import eu.ibutler.affiliatenetwork.dao.utils.JdbcUtils;
import eu.ibutler.affiliatenetwork.entity.UploadedFile;

public class FileDaoImpl extends Extractor<UploadedFile> implements FileDao{

	private static Logger log = Logger.getLogger(FileDaoImpl.class.getName());
	
	private DbConnectionPool connectionPool = null;
	
	/**
	 * Public constructor 
	 */
	public FileDaoImpl() {
		this.connectionPool = DbConnectionPool.getInstance();
	}
	
	/**
	 * @return List of objects obtained from db
	 * @throws DbAccessException
	 */
	@Override
	public List<UploadedFile> getAllFiles() throws DbAccessException {
		Connection conn = null;
		Statement stm=null;
		ResultSet rs = null;	
		try{
			conn=connectionPool.getConnection();
			stm = conn.createStatement();
			String sql = "SELECT * FROM tbl_files";
			rs = stm.executeQuery(sql);
			return extractAll(rs);
		}
		catch(SQLException e){
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
	public List<UploadedFile> getLastNfiles(int n, int shopId) throws DbAccessException {
		Connection conn = null;
		Statement stm=null;
		ResultSet rs = null;	
		try{
			conn=connectionPool.getConnection();
			stm = conn.createStatement();
			String sql = "SELECT * FROM tbl_files WHERE shop_id=" + shopId + " ORDER BY upload_time DESC LIMIT " + n + ";";
			rs = stm.executeQuery(sql);
			return extractAll(rs);
		}
		catch(SQLException e){
			log.debug("SQL exception");
			throw new DbAccessException("Error accessing DB", e);	
		}
		finally{
			JdbcUtils.close(rs);
			JdbcUtils.close(stm);
			JdbcUtils.close(conn);
		}
	}
	

	/**
	 * Inserts uploaded file into DB
	 * If given file is active (file.isActive == true) then
	 * currently active file in DB is deactivated before INSERT operation
	 * @return auto-generated index assigned to this entry by DBMS
	 * @throws UniqueConstraintViolationException if insert statement violates unique constraint
	 * @throws DbAccessException if other error occurred during attempt to write to DB 
	 */
	@Override
	public int insertOne(UploadedFile file) throws DbAccessException, UniqueConstraintViolationException {
		Connection conn = null;
		Statement stm = null;
		ResultSet rs = null;
		try{
			conn = connectionPool.getConnection();
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			conn.setAutoCommit(false);
			stm = conn.createStatement();
			//deactivate active file for current shop in DB if given file is active 
			if(file.isActive()) {
				String sql = "UPDATE tbl_files SET is_active = false WHERE is_active = true AND shop_id = \'" + file.getShopId() + "\';";
				stm.executeUpdate(sql);
			}
			String sql = "INSERT INTO tbl_files (name, fs_path, upload_time, shop_id,"
					+ " file_size, is_active, is_valid, products_count, validation_message, is_processed) ";
			sql+="VALUES (";
			sql+="\'"+ file.getName() +"\', ";
			sql+="\'"+ file.getFsPath() +"\', ";
			sql+="\'"+ file.getUploadTime() +"\', ";
			sql+="\'"+ file.getShopId() +"\', ";
			sql+="\'"+ file.getSize() +"\', ";
			sql+="\'"+ file.isActive() +"\', ";
			sql+="\'"+ file.isValid() +"\', ";
			sql+="\'"+ file.getProductsCount() +"\', ";
			sql+="\'"+ file.getValidationMessage() +"\', ";
			sql+="\'"+ file.isProcessed() +"\' ";
			sql+=");";
			stm.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			rs=stm.getGeneratedKeys();
			rs.next();	
			int idColumnNumber = 1;
			int generatedIndex = rs.getInt(idColumnNumber);
			JdbcUtils.commit(conn);
			return generatedIndex;
		}
		catch(SQLException e){
			JdbcUtils.rollback(conn);
			if(e.getMessage().contains("ERROR: duplicate key value violates unique constraint")) {
				log.debug("Duplicate unique constraint");
				throw new UniqueConstraintViolationException();
			} else {
				log.debug("DB access error: " + Throwables.getStackTraceAsString(e));
				throw new DbAccessException("Error accessing DB", e);
			}
		}
		finally{
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				log.debug("Exception: unable to resume AutoCommit");
			}
			JdbcUtils.close(rs);
			JdbcUtils.close(stm);
			JdbcUtils.close(conn);
		}
	}	
	
	/**
	 * @param uploadedFile
	 * @throws DbAccessException
	 */
	@Override
	public void update(UploadedFile file) throws DbAccessException {
		synchronized (FileDaoImpl.class) {
			Connection conn = null;
			Statement stm = null;
			ResultSet rs = null;
			try{
				conn = connectionPool.getConnection();
				conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
				conn.setAutoCommit(false);
				stm = conn.createStatement();
				//deactivate active file for current shop in DB if given file is active 
				if(file.isActive()) {
					String sql = "UPDATE tbl_files SET is_active = false WHERE is_active = true AND shop_id = \'" + file.getShopId() + "\';";
					stm.executeUpdate(sql);
				}
				String sql = "UPDATE tbl_files SET "
						+ "is_active="+ file.isActive() +", "
						+ "is_valid="+ file.isValid() +", "
						+ "is_processed="+ file.isProcessed() +", "
						+ "products_count="+ file.getProductsCount() +", "
						+ "validation_message=\'"+ file.getValidationMessage() +"\' "
						+ "WHERE id=" + file.getId() + ";";
				stm.executeUpdate(sql);
				JdbcUtils.commit(conn);
			}
			catch(SQLException e){
				JdbcUtils.rollback(conn);
				log.error("Error updating file entry: " + e);
				throw new DbAccessException(e.getMessage());
			}
			finally{
				try {
					conn.setAutoCommit(true);
				} catch (SQLException e) {
					log.debug("Exception: unable to resume AutoCommit");
				}
				JdbcUtils.close(rs);
				JdbcUtils.close(stm);
				JdbcUtils.close(conn);
			}
		}//synchronized
	}
	
	public void update(UploadedFile file, Connection conn) throws DbAccessException {
			Statement stm = null;
			ResultSet rs = null;
			try{
				stm = conn.createStatement();
				//deactivate active file for current shop in DB if given file is active 
				if(file.isActive()) {
					String sql = "UPDATE tbl_files SET is_active = false WHERE is_active = true AND shop_id = \'" + file.getShopId() + "\';";
					stm.executeUpdate(sql);
				}
				String sql = "UPDATE tbl_files SET "
						+ "is_active="+ file.isActive() +", "
						+ "is_valid="+ file.isValid() +", "
						+ "is_processed="+ file.isProcessed() +", "
						+ "products_count="+ file.getProductsCount() +", "
						+ "validation_message=\'"+ file.getValidationMessage() +"\' "
						+ "WHERE id=" + file.getId() + ";";
				stm.executeUpdate(sql);
			}
			catch(SQLException e){
				log.error("Error updating file entry: " + e);
				throw new DbAccessException(e.getMessage());
			}
			finally{
				JdbcUtils.close(rs);
				JdbcUtils.close(stm);
			}
	}

	@Override
	protected UploadedFile extractOne(ResultSet rs) throws SQLException {
		return new UploadedFile(rs.getInt("id"), rs.getString("name"), rs.getString("fs_path"),
				rs.getLong("upload_time"), rs.getInt("shop_id"), rs.getBoolean("is_active"),
				rs.getBoolean("is_valid"), rs.getInt("products_count"), rs.getLong("file_size"),
				rs.getString("validation_message"), rs.getBoolean("is_processed"));
	}

	@Override
	public List<UploadedFile> getAllActive() throws DbAccessException {
		Connection conn = null;
		Statement stm=null;
		ResultSet rs = null;	
		try{
			conn=connectionPool.getConnection();
			stm = conn.createStatement();
			String sql = "SELECT * FROM tbl_files WHERE is_active = true ORDER BY upload_time DESC;";
			rs = stm.executeQuery(sql);
			return extractAll(rs);
		}
		catch(SQLException e){
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
	public List<UploadedFile> selectActiveOlderThan(long age) throws DbAccessException {
		Connection conn = null;
		Statement stm=null;
		ResultSet rs = null;	
		try{
			conn=connectionPool.getConnection();
			stm = conn.createStatement();
			long currentTime = System.currentTimeMillis();
			String sql = "SELECT * FROM tbl_files WHERE is_active = true AND upload_time < " + (currentTime - age) + " ;";
			rs = stm.executeQuery(sql);
			return extractAll(rs);
		}
		catch(SQLException e){
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
	public List<UploadedFile> selectUnprocessed() throws DbAccessException {
		Connection conn = null;
		Statement stm=null;
		ResultSet rs = null;	
		try{
			conn=connectionPool.getConnection();
			stm = conn.createStatement();
			String sql = "SELECT * FROM tbl_files WHERE is_processed = false ORDER BY upload_time ASC ;";
			rs = stm.executeQuery(sql);
			return extractAll(rs);
		}
		catch(SQLException e){
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
