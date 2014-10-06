package eu.ibutler.affiliatenetwork.dao;

import java.util.List;

import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.NoSuchEntityException;
import eu.ibutler.affiliatenetwork.dao.exceptions.UniqueConstraintViolationException;
import eu.ibutler.affiliatenetwork.entity.Admin;

public interface AdminDao {
	
	/**
	 * Check if there's such admin in the DB
	 * @param email
	 * @param password
	 * @return Entity object
	 * @throws NoSuchEntityException if there's no matching entity
	 * @throws DbAccessException
	 */
	public Admin selectAdmin(String email, String password) throws DbAccessException, NoSuchEntityException;

	/**
	 * Get list of all entities
	 * @return List<Admin>
	 * @throws DbAccessException
	 */
	public List<Admin> selectAllAdmins() throws DbAccessException;

	/**
	 * Add new admin to DB
	 * @param admin
	 * @return index of new entry assigned by DBMS
	 * @throws DbAccessException
	 * @throws UniqueConstraintViolationException if there's such entry in the DB already
	 */
	public int insertAdmin(Admin admin) throws DbAccessException, UniqueConstraintViolationException;
}