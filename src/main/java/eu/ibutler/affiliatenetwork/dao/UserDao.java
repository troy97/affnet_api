package eu.ibutler.affiliatenetwork.dao;

import java.util.List;

import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.NoSuchEntityException;
import eu.ibutler.affiliatenetwork.dao.exceptions.UniqueConstraintViolationException;
import eu.ibutler.affiliatenetwork.entity.User;

public interface UserDao {
	
	/**
	 * check if there's such user in the DB
	 * @param login
	 * @param password
	 * @return User object or "null" if there's no such user
	 */
	public User login(String login, String password) throws DbAccessException, NoSuchEntityException;

	/**
	 * Adds new user to DB
	 * @param user
	 * @return id of new user in the DB
	 */
	public List<User> getAllUsers() throws DbAccessException;

	/**
	 * Add new user to DB
	 * @param user
	 * @return index of new entry assigned by DBMS
	 * @throws DbAccessException
	 * @throws UniqueConstraintViolationException if there's such entry in the DB already
	 */
	int addUser(User user) throws DbAccessException, UniqueConstraintViolationException;


}