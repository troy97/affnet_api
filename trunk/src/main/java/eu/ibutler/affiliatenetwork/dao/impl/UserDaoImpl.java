package eu.ibutler.affiliatenetwork.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.log4j.Logger;

import eu.ibutler.affiliatenetwork.dao.UserDao;
import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.NoSuchEntityException;
import eu.ibutler.affiliatenetwork.dao.exceptions.UniqueConstraintViolationException;
import eu.ibutler.affiliatenetwork.entity.User;
import eu.ibutler.affiliatenetwork.jdbc.DbConnectionPool;
import eu.ibutler.affiliatenetwork.jdbc.JdbcUtils;

/**
 * Provides DB access methods for entities of
 * class User
 * @author Anton Lukashchuk
 *
 */
public class UserDaoImpl implements UserDao{

	private static Logger log = Logger.getLogger(UserDaoImpl.class.getName());
	private DbConnectionPool connectionPool = null;
	
	/**
	 *Public constructor
	 */
	public UserDaoImpl() {
		this.connectionPool = DbConnectionPool.getInstance();
	}
	
	/**
	 * Search user in DB by login and password
	 * @return User object if found matching login and password
	 * @throws NoSuchEntityException if user wasn't found
	 * @throws DbAccessException 
	 */
	@Override
	public User login(String userLogin, String encryptedUserPassword) throws DbAccessException, NoSuchEntityException{
		User result = null;
		Statement stm=null;
		ResultSet rs=null;
		Connection conn=null;
		try{
			conn = connectionPool.getConnection();
			stm = conn.createStatement();
			rs = stm.executeQuery("SELECT * "
					+ "FROM tbl_admins "
					+ "WHERE login = \'" + userLogin + "\' AND password = \'" + encryptedUserPassword + "\'");
			result = createOneUserFromRs(rs);
		} catch(SQLException e){
			log.debug("login() SQLException");
			throw new DbAccessException("Error accessing DB", e);
		}
		finally{
			JdbcUtils.close(rs);
			JdbcUtils.close(stm);
			JdbcUtils.close(conn);
		}
		return result;
	}
	
	/**
	 * Get list of all Users in DB
	 */
	@Override
	public List<User> getAllUsers() throws DbAccessException {
		return null;
	}
	
	/**
	 * Inserts new user into DB
	 * @return id of the user assigned by database
	 */
	@Override
	public int addUser(User user) throws DbAccessException, UniqueConstraintViolationException {
		Statement stm = null;
		ResultSet rs = null;
		Connection conn = null;
		try{
			conn = connectionPool.getConnection();
			stm = conn.createStatement();
			String sql = "INSERT INTO tbl_admins (login, password, email, name) ";
			sql+="VALUES (";
			sql+="\'"+user.getLogin()+"\', ";
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
	
/*	
	@Override
	public void insertUsers(List<User> usersToAdd) throws DBAccessException{
		Connection conn=null;
		PreparedStatement pstm = null;
		try {
			conn=getConnection();
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			conn.setAutoCommit(false);
			String sql="INSERT INTO users (login, password, email, name, lastName) ";
			sql+="VALUES (?, ?, ?, ?, ?)";
			pstm=conn.prepareStatement(sql);
			for(User user : usersToAdd){
				pstm.setString(1, user.getLogin());
				pstm.setString(2, user.getPassword());
				pstm.setString(3, user.getEmail());
				pstm.setString(4, user.getName());
				pstm.setString(5, user.getLastName());
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
			throw new DBAccessException("Error accessing DB", e);
		}
		finally{
			JdbcUtils.close(pstm);
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				System.out.println("Exception: unable to resume AutoCommit in UserDaoClass.insertUsers()");
			}
		}
	}
	@Override
	public List<User> getAllUsers() throws DBAccessException {
		Statement stm=null;
		ResultSet rs = null;	
		try{
			Connection conn=getConnection();
			stm = conn.createStatement();
			String sql = "SELECT id, login, password, name, lastName, email FROM users;";
			rs = stm.executeQuery(sql);
			return createUsersFromRs(rs);
		}
		catch(SQLException e){
			e.printStackTrace();
			throw new DBAccessException("Error accessing DB", e);	
		}
		finally{
			JdbcUtils.close(rs);
			JdbcUtils.close(stm);
		}
	}*/
	
	/**
	 *
	 * @param rs
	 * @return User object
	 * @throws NoSuchEntityException if failed to create User
	 */
	private User createOneUserFromRs(ResultSet rs) throws SQLException, NoSuchEntityException {
		if(rs.next()){
			return new User(rs.getString("email"), rs.getString("name"), rs.getString("login"), rs.getString("password"), rs.getInt("id"));
		} else {		
			throw new NoSuchEntityException();
		}
	}	
	
/*	*//**
	 *
	 * @param rs
	 * @return List<User> which is not null and size>0 or throws exception
	 * @throws SQLException
	 *//*
	private List<User> createUsersFromRs(ResultSet rs) throws SQLException {
		List<User> toReturn=new ArrayList<User>();
		User freshUser=null;
		while(true){
			try {
				freshUser=createOneUserFromRs(rs);
			}
			catch (SQLException e) {
				break;
			}
			toReturn.add(freshUser);
		};
		if(toReturn.size()==0) throw new SQLException();
		return toReturn;
	}	*/


}
