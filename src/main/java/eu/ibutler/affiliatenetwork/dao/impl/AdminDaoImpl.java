package eu.ibutler.affiliatenetwork.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.log4j.Logger;

import eu.ibutler.affiliatenetwork.dao.AdminDao;
import eu.ibutler.affiliatenetwork.dao.Extractor;
import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.NoSuchEntityException;
import eu.ibutler.affiliatenetwork.dao.exceptions.UniqueConstraintViolationException;
import eu.ibutler.affiliatenetwork.entity.Admin;
import eu.ibutler.affiliatenetwork.jdbc.DbConnectionPool;
import eu.ibutler.affiliatenetwork.jdbc.JdbcUtils;

/**
 * Provides DB access methods for entities of
 * class User
 * @author Anton Lukashchuk
 *
 */
public class AdminDaoImpl extends Extractor<Admin> implements AdminDao{

	private static Logger log = Logger.getLogger(AdminDaoImpl.class.getName());
	private DbConnectionPool connectionPool = null;
	
	/**
	 *Public constructor
	 */
	public AdminDaoImpl() {
		this.connectionPool = DbConnectionPool.getInstance();
	}
	
	/**
	 * Search user in DB by email and password
	 * @return User object if found matching login and password
	 * @throws NoSuchEntityException if user wasn't found
	 * @throws DbAccessException 
	 */
	@Override
	public Admin selectAdmin(String email, String encryptedUserPassword) throws DbAccessException, NoSuchEntityException{
		Statement stm=null;
		ResultSet rs=null;
		Connection conn=null;
		try{
			conn = connectionPool.getConnection();
			stm = conn.createStatement();
			rs = stm.executeQuery("SELECT * "
					+ "FROM tbl_admins "
					+ "WHERE email = \'" + email + "\' AND password_ssha256_hex = \'" + encryptedUserPassword + "\'");
			if(rs.next()) {
				return extractOne(rs);
			} else {
				throw new NoSuchEntityException();
			}
		} catch(SQLException e){
			log.debug("Signin SQL error");
			throw new DbAccessException("Error accessing DB", e);
		}
		finally{
			JdbcUtils.close(rs);
			JdbcUtils.close(stm);
			JdbcUtils.close(conn);
		}
	}
	
	/**
	 * Get list of all Users in DB
	 */
	@Override
	public List<Admin> selectAllAdmins() throws DbAccessException {
		return null;
	}
	
	/**
	 * Inserts new user into DB
	 * @return id of the user assigned by database
	 */
	@Override
	public int insertAdmin(Admin user) throws DbAccessException, UniqueConstraintViolationException {
		Statement stm = null;
		ResultSet rs = null;
		Connection conn = null;
		try{
			conn = connectionPool.getConnection();
			stm = conn.createStatement();
			String sql = "INSERT INTO tbl_admins (password_ssha256_hex, email, name) ";
			sql+="VALUES (";
			sql+="\'"+user.getEncryptedPassword()+"\', ";
			sql+="\'"+user.getEmail()+"\', ";
			sql+="\'"+user.getName()+"\'";
			sql+=");";
			stm.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			rs=stm.getGeneratedKeys();
			rs.next();	
			int idColumnNumber = 1;
			return rs.getInt(idColumnNumber);
		}
		catch(SQLException e){
			if(e.getMessage().contains("ERROR: duplicate key")) {
				throw new UniqueConstraintViolationException();
			} else {
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
	protected Admin extractOne(ResultSet rs) throws SQLException {
		return new Admin(rs.getString("name"), rs.getString("email"), rs.getString("password_ssha256_hex"), rs.getInt("id"));
	}	
	
}
