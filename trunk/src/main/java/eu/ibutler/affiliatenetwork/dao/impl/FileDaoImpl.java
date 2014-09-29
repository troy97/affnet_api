package eu.ibutler.affiliatenetwork.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import eu.ibutler.affiliatenetwork.dao.FileDao;
import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.NoSuchEntityException;
import eu.ibutler.affiliatenetwork.dao.exceptions.UniqueConstraintViolationException;
import eu.ibutler.affiliatenetwork.entity.UploadedFile;
import eu.ibutler.affiliatenetwork.jdbc.DbConnectionPool;
import eu.ibutler.affiliatenetwork.jdbc.JdbcUtils;

public class FileDaoImpl implements FileDao{

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
			return createFilesFromRs(rs);
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
	
	/**
	 *
	 * @param rs
	 * @return List<> of objects obtained from db
	 * @throws SQLException if some DB error occurred
	 */
	private List<UploadedFile> createFilesFromRs(ResultSet rs) throws SQLException {
		List<UploadedFile> toReturn=new ArrayList<UploadedFile>();
		UploadedFile fresh=null;
		while(true){
			try {
				fresh=createOneFileFromRs(rs);
				toReturn.add(fresh);
			}
			catch (NoSuchEntityException e) {
				//reached the end of rs
				break;
			}
		};
		return toReturn;
	}	
	
	/**
	 *
	 * @param rs
	 * @return User object
	 * @throws NoSuchEntityException if failed to create new instance
	 * @throws SQLException if some DB error occurred
	 */
	private UploadedFile createOneFileFromRs(ResultSet rs) throws SQLException, NoSuchEntityException {
		UploadedFile toReturn = null;
		if(rs.next()){
			toReturn = new UploadedFile(rs.getInt("id"), rs.getString("name"), rs.getString("fs_path"),
					rs.getLong("upload_time"), rs.getInt("webshop_id"));
		} else {
			throw new NoSuchEntityException(); //throw exception if given rs is empty
		}
		return toReturn;
	}

	/**
	 * Inserts uploaded file into DB
	 * @return auto-generated index assigned to this entry by DBMS
	 * @throws UniqueConstraintViolationException if insert statement violates unique constraint
	 * @throws DbAccessException if other error occurred during attempt to write to DB 
	 */
	@Override
	public int insertFile(UploadedFile file) throws DbAccessException, UniqueConstraintViolationException {
		Connection conn = null;
		Statement stm = null;
		ResultSet rs = null;
		try{
			conn = connectionPool.getConnection();
			stm = conn.createStatement();
			String sql = "INSERT INTO tbl_files (name, fs_path, upload_time, webshop_id) ";
			sql+="VALUES (";
			sql+="\'"+ file.getName() +"\', ";
			sql+="\'"+ file.getFsPath() +"\', ";
			sql+="\'"+ file.getUploadTime() +"\', ";
			sql+="\'"+ file.getWebshopId() +"\'";
			sql+=");";
			stm.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			rs=stm.getGeneratedKeys();
			rs.next();	
			int idColumnNumber = 1;
			return rs.getInt(idColumnNumber);
		}
		catch(SQLException e){
			if(e.getMessage().contains("ERROR: duplicate key value violates unique constraint \"tbl_files_fs_path_key\"")) {
				log.debug("Duplicate unique constraint");
				throw new UniqueConstraintViolationException();
			} else {
				log.debug("DB access error");
				throw new DbAccessException("Error accessing DB", e);
			}
		}
		finally{
			JdbcUtils.close(rs);
			JdbcUtils.close(stm);
			JdbcUtils.close(conn);
		}
	}	
	
	@Override
	public void updateUploadTime(UploadedFile file) {
		Connection conn = null;
		Statement stm = null;
		ResultSet rs = null;
		try{
			conn = connectionPool.getConnection();
			stm = conn.createStatement();
			String sql = "UPDATE tbl_files SET upload_time=" + file.getUploadTime();
			sql+="WHERE fs_path=\'" + file.getFsPath() + "\'";
			sql+=";";
			stm.executeUpdate(sql);
		}
		catch(SQLException e){
			log.error("Error updating upload time");
		}
		finally{
			JdbcUtils.close(rs);
			JdbcUtils.close(stm);
			JdbcUtils.close(conn);
		}
	}

}