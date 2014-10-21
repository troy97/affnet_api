package eu.ibutler.affiliatenetwork.dao;

import java.util.Collection;

import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.NoSuchEntityException;
import eu.ibutler.affiliatenetwork.dao.exceptions.UniqueConstraintViolationException;

/**
 * DAO interface, that declares common DAO methods for
 * entities of type <T>
 * @author Anton Lukashchuk
 *
 * @param <T>
 */
public interface DAO<T> {
	
	//C
	public long insertOne(T entity) throws DbAccessException, UniqueConstraintViolationException;
	public void insertAll(Collection<T> entities) throws DbAccessException, UniqueConstraintViolationException;

	//R
	public T selectById(long id) throws DbAccessException, NoSuchEntityException;
	public Collection<T> selectAll(int limit) throws DbAccessException;

	//U	
	public void update(T entity) throws DbAccessException, NoSuchEntityException;

	//D
	public void deleteById(long id) throws DbAccessException, NoSuchEntityException;
	
	
}
