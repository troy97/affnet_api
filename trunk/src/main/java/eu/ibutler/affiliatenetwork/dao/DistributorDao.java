package eu.ibutler.affiliatenetwork.dao;

import java.util.List;

import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.NoSuchEntityException;
import eu.ibutler.affiliatenetwork.dao.exceptions.UniqueConstraintViolationException;
import eu.ibutler.affiliatenetwork.entity.Distributor;

public interface DistributorDao {
	
	/**
	 * Check if there's such user in the DB
	 * @param email
	 * @param password
	 * @return Entity object
	 * @throws NoSuchEntityException if there's no matching entity
	 * @throws DbAccessException
	 */
	public Distributor selectById(int id) throws DbAccessException, NoSuchEntityException;


	/**
	 * Add new user to DB
	 * @param user
	 * @return index of new entry assigned by DBMS
	 * @throws DbAccessException
	 * @throws UniqueConstraintViolationException if there's such entry in the DB already
	 */
	public int insertOne(Distributor distributor) throws DbAccessException, UniqueConstraintViolationException;

}
