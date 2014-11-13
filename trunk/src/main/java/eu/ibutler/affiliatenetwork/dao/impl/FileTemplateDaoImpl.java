package eu.ibutler.affiliatenetwork.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.base.Throwables;

import eu.ibutler.affiliatenetwork.dao.Extractor;
import eu.ibutler.affiliatenetwork.dao.FileTemplateDao;
import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.NoSuchEntityException;
import eu.ibutler.affiliatenetwork.dao.exceptions.UniqueConstraintViolationException;
import eu.ibutler.affiliatenetwork.dao.utils.DbConnectionPool;
import eu.ibutler.affiliatenetwork.dao.utils.JdbcUtils;
import eu.ibutler.affiliatenetwork.entity.FileTemplate;

public class FileTemplateDaoImpl extends Extractor<FileTemplate> implements FileTemplateDao {

	private static Logger log = Logger.getLogger(FileTemplateDaoImpl.class.getName());
	
	private DbConnectionPool connectionPool = null;
	
	/**
	 * Public constructor 
	 */
	public FileTemplateDaoImpl() {
		this.connectionPool = DbConnectionPool.getInstance();
	}
	
	/**
	 * @return List of objects obtained from db
	 * @throws DbAccessException
	 */
	@Override
	public List<FileTemplate> getAllFiles() throws DbAccessException {
		Connection conn = null;
		Statement stm=null;
		ResultSet rs = null;	
		try{
			conn=connectionPool.getConnection();
			stm = conn.createStatement();
			String sql = "SELECT * FROM tbl_file_templates";
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
	public int insertOne(FileTemplate file) throws DbAccessException, UniqueConstraintViolationException {
		synchronized (FileTemplateDaoImpl.class) {
			Connection conn = null;
			Statement stm = null;
			ResultSet rs = null;
			try{
				conn = connectionPool.getConnection();
				conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
				conn.setAutoCommit(false);
				stm = conn.createStatement();
				//deactivate active file for current shop in DB if given file is active 
				if(file.isActive()) {
					String sql = "UPDATE tbl_file_templates SET is_active = false WHERE is_active = true AND shop_id = \'" + file.getShopId() + "\';";
					stm.executeUpdate(sql);
				}
				//insert new active file
				String sql = "INSERT INTO tbl_file_templates (name, fs_path, created_at, shop_id, file_size, compressed_file_size, is_active, products_count) ";
				sql+="VALUES (";
				sql+="\'"+ file.getName() +"\', ";
				sql+="\'"+ file.getFsPath() +"\', ";
				sql+="\'"+ file.getCreateTime() +"\', ";
				sql+="\'"+ file.getShopId() +"\', ";
				sql+="\'"+ file.getSize() +"\', ";
				sql+="\'"+ file.getCompressedSize() +"\', ";
				sql+="\'"+ file.isActive() +"\', ";
				sql+="\'"+ file.getProductsCount() +"\'";
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
		}//synchronized
	}	
	
	public int insertOne(FileTemplate file, Connection conn) throws DbAccessException, UniqueConstraintViolationException {
			Statement stm = null;
			ResultSet rs = null;
			try{
				stm = conn.createStatement();
				//deactivate active file for current shop in DB if given file is active 
				if(file.isActive()) {
					String sql = "UPDATE tbl_file_templates SET is_active = false WHERE is_active = true AND shop_id = \'" + file.getShopId() + "\';";
					stm.executeUpdate(sql);
				}
				//insert new active file
				String sql = "INSERT INTO tbl_file_templates (name, fs_path, created_at, shop_id, file_size, compressed_file_size, is_active, products_count) ";
				sql+="VALUES (";
				sql+="\'"+ file.getName() +"\', ";
				sql+="\'"+ file.getFsPath() +"\', ";
				sql+="\'"+ file.getCreateTime() +"\', ";
				sql+="\'"+ file.getShopId() +"\', ";
				sql+="\'"+ file.getSize() +"\', ";
				sql+="\'"+ file.getCompressedSize() +"\', ";
				sql+="\'"+ file.isActive() +"\', ";
				sql+="\'"+ file.getProductsCount() +"\'";
				sql+=");";
				stm.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
				rs=stm.getGeneratedKeys();
				rs.next();	
				int idColumnNumber = 1;
				int generatedIndex = rs.getInt(idColumnNumber);
				return generatedIndex;
			}
			catch(SQLException e){
				if(e.getMessage().contains("ERROR: duplicate key value violates unique constraint")) {
					log.debug("Duplicate unique constraint");
					throw new UniqueConstraintViolationException();
				} else {
					log.debug("DB access error: " + Throwables.getStackTraceAsString(e));
					throw new DbAccessException("Error accessing DB", e);
				}
			}
			finally{
				JdbcUtils.close(rs);
				JdbcUtils.close(stm);
			}
	}	

	@Override
	protected FileTemplate extractOne(ResultSet rs) throws SQLException {
		return new FileTemplate(rs.getInt("id"),
								rs.getString("name"),
								rs.getString("fs_path"),
								rs.getInt("products_count"),
								rs.getBoolean("is_active"),
								rs.getLong("file_size"),
								rs.getLong("compressed_file_size"),
								rs.getLong("created_at"),
								rs.getInt("shop_id")
								);
	}

	@Override
	public List<FileTemplate> getAllActive() throws DbAccessException {
		Connection conn = null;
		Statement stm=null;
		ResultSet rs = null;	
		try{
			conn=connectionPool.getConnection();
			stm = conn.createStatement();
			String sql = "SELECT * FROM tbl_file_templates WHERE is_active = true ORDER BY created_at DESC;";
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
	public FileTemplate selectByShopId(int shopId) throws DbAccessException, NoSuchEntityException {
		Connection conn = null;
		Statement stm=null;
		ResultSet rs = null;	
		try{
			conn=connectionPool.getConnection();
			stm = conn.createStatement();
			String sql = "SELECT * FROM tbl_file_templates WHERE shop_id=" + shopId + ";";
			rs = stm.executeQuery(sql);
			return extractOne(rs);
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
	public FileTemplate selectById(int id) throws DbAccessException, NoSuchEntityException {
		Connection conn = null;
		Statement stm=null;
		ResultSet rs = null;	
		try{
			conn=connectionPool.getConnection();
			stm = conn.createStatement();
			String sql = "SELECT * FROM tbl_file_templates WHERE id=" + id;
			rs = stm.executeQuery(sql);
			if(rs.next()) {
				return extractOne(rs);
			} else {
				throw new NoSuchEntityException();
			}
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
	
	

	

}
