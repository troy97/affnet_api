package eu.ibutler.affiliatenetwork.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.log4j.Logger;

import eu.ibutler.affiliatenetwork.dao.Extractor;
import eu.ibutler.affiliatenetwork.dao.UserDao;
import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.NoSuchEntityException;
import eu.ibutler.affiliatenetwork.dao.exceptions.UniqueConstraintViolationException;
import eu.ibutler.affiliatenetwork.dao.utils.DbConnectionPool;
import eu.ibutler.affiliatenetwork.dao.utils.JdbcUtils;
import eu.ibutler.affiliatenetwork.entity.User;

public class UserDaoImpl extends Extractor<User> implements UserDao {
	private static Logger log = Logger.getLogger(UserDaoImpl.class.getName());
	private DbConnectionPool connectionPool = null;
	
	/**
	 *Public constructor
	 */
	public UserDaoImpl() {
		this.connectionPool = DbConnectionPool.getInstance();
	}
	
	/**
	 * Search user in DB by email and password
	 * @return User object if found matching login and password
	 * @throws NoSuchEntityException if user wasn't found
	 * @throws DbAccessException 
	 */
	@Override
	public User selectOne(String email, String encryptedUserPassword) throws DbAccessException, NoSuchEntityException{
		Connection conn=null;
		Statement stm=null;
		ResultSet rs=null;
		try{
			conn = connectionPool.getConnection();
			stm = conn.createStatement();
			rs = stm.executeQuery("SELECT * "
					+ "FROM tbl_shop_users "
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
	public List<User> selectAllUsers() throws DbAccessException {
		return null;
	}
	
	/**
	 * Inserts new user into DB
	 * @return id of the user assigned by database
	 */
	@Override
	public int insertOne(User user) throws DbAccessException, UniqueConstraintViolationException {
		Statement stm = null;
		ResultSet rs = null;
		Connection conn = null;
		try{
			conn = connectionPool.getConnection();
			stm = conn.createStatement();
			String sql = "INSERT INTO tbl_shop_users (email, password_ssha256_hex, created_at, name_first, name_last, is_active, shop_id) ";
			sql+="VALUES (";
			sql+="\'"+user.getEmail()+"\', ";
			sql+="\'"+user.getEncryptedPassword()+"\', ";
			sql+="NOW(), ";
			sql+="\'"+user.getFirstName()+"\',";
			sql+="\'"+user.getLastName()+"\',";
			sql+="\'"+user.isActive()+"\',";
			sql+="\'"+user.getShopId()+"\'";
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
	
	/**
	 * Inserts new user into DB
	 * @return id of the user assigned by database
	 */
	@Override
	public int insertOne(User user, Connection conn) throws DbAccessException, UniqueConstraintViolationException {
		Statement stm = null;
		ResultSet rs = null;
		try{
			stm = conn.createStatement();
			String sql = "INSERT INTO tbl_shop_users (email, password_ssha256_hex, created_at, name_first, name_last, is_active, shop_id) ";
			sql+="VALUES (";
			sql+="\'"+user.getEmail()+"\', ";
			sql+="\'"+user.getEncryptedPassword()+"\', ";
			sql+="NOW(), ";
			sql+="\'"+user.getFirstName()+"\',";
			sql+="\'"+user.getLastName()+"\',";
			sql+="\'"+user.isActive()+"\',";
			sql+="\'"+user.getShopId()+"\'";
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
		}
	}
	

	@Override
	public void setActive(String email, boolean isActive)
			throws DbAccessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateUser(User user) throws DbAccessException, UniqueConstraintViolationException {
		Statement stm = null;
		Connection conn = null;
		try{
			conn = connectionPool.getConnection();
			stm = conn.createStatement();
			String sql = "UPDATE tbl_shop_users SET ";
			sql+="email=\'"+user.getEmail()+"\', ";
			sql+="password_ssha256_hex=\'"+user.getEncryptedPassword()+"\', ";
			sql+="name_first=\'"+user.getFirstName()+"\', ";
			sql+="name_last=\'"+user.getLastName()+"\' ";
			sql+="WHERE id=" + user.getDbId() + ";";
			stm.executeUpdate(sql);
		}
		catch(SQLException e){
			if(e.getMessage().contains("ERROR: duplicate key")) {
				throw new UniqueConstraintViolationException();
			} else {
				throw new DbAccessException("Error accessing DB", e);
			}
		}
		finally{
			JdbcUtils.close(stm);
			JdbcUtils.close(conn);
		}
	}	
	
	@Override
	public void updateUser(User user, Connection conn) throws DbAccessException, UniqueConstraintViolationException {
		Statement stm = null;
		try{
			stm = conn.createStatement();
			String sql = "UPDATE tbl_shop_users SET ";
			sql+="email=\'"+user.getEmail()+"\', ";
			sql+="password_ssha256_hex=\'"+user.getEncryptedPassword()+"\', ";
			sql+="name_first=\'"+user.getFirstName()+"\', ";
			sql+="name_last=\'"+user.getLastName()+"\' ";
			sql+="WHERE id=" + user.getDbId() + ";";
			stm.executeUpdate(sql);
		}
		catch(SQLException e){
			if(e.getMessage().contains("ERROR: duplicate key")) {
				throw new UniqueConstraintViolationException();
			} else {
				throw new DbAccessException("Error accessing DB", e);
			}
		}
		finally{
			JdbcUtils.close(stm);
		}
	}

	@Override
	protected User extractOne(ResultSet rs) throws SQLException {
		return new User(rs.getInt("id"), rs.getString("email"), rs.getString("password_ssha256_hex"),
				rs.getString("created_at"), rs.getString("name_first"), rs.getString("name_last"),
				rs.getBoolean("is_active"), rs.getInt("shop_id"));
	}	

}
