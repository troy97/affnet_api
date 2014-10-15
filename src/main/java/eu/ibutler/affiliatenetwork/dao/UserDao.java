package eu.ibutler.affiliatenetwork.dao;

import java.sql.Connection;
import java.util.List;

import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.NoSuchEntityException;
import eu.ibutler.affiliatenetwork.dao.exceptions.UniqueConstraintViolationException;
import eu.ibutler.affiliatenetwork.entity.User;

public interface UserDao {
	
	/**
	 * Check if there's such user in the DB
	 * @param email
	 * @param password
	 * @return Entity object
	 * @throws NoSuchEntityException if there's no matching entity
	 * @throws DbAccessException
	 */
	public User selectOne(String email, String password) throws DbAccessException, NoSuchEntityException;

	/**
	 * Get list of all entities
	 * @return List<User>
	 * @throws DbAccessException
	 */
	public List<User> selectAllUsers() throws DbAccessException;

	/**
	 * Add new user to DB
	 * @param user
	 * @return index of new entry assigned by DBMS
	 * @throws DbAccessException
	 * @throws UniqueConstraintViolationException if there's such entry in the DB already
	 */
	public int insertOne(User user) throws DbAccessException, UniqueConstraintViolationException;
	
	/**
	 * Ban or remove ban from User
	 * @param isActive
	 * @throws DbAccessException
	 */
	public void setActive(String email, boolean isActive) throws DbAccessException;
	
	/**
	 * Updates existing user in the DB, all fields except "id" can be changed
	 * @param user
	 * @throws DbAccessException
	 * @throws UniqueConstraintViolationException if updated information violates UNIQUE constraint on some column
	 */
	public void updateUser(User updatedUser)  throws DbAccessException, UniqueConstraintViolationException;

	/**
	 * Quick-fix method, will be removed after implementing normal transaction manager
	 * @param user
	 * @param conn
	 * @return
	 * @throws DbAccessException
	 * @throws UniqueConstraintViolationException
	 */
	public int insertOne(User user, Connection conn) throws DbAccessException, UniqueConstraintViolationException;

	public void updateUser(User newUser, Connection conn) throws DbAccessException, UniqueConstraintViolationException;
}
