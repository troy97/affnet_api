package eu.ibutler.affiliatenetwork.dao;

import java.sql.Connection;
import java.util.List;

import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.NoSuchEntityException;
import eu.ibutler.affiliatenetwork.dao.exceptions.UniqueConstraintViolationException;
import eu.ibutler.affiliatenetwork.entity.Shop;

public interface ShopDao {

	public List<Shop> getAllShops() throws DbAccessException;
	
	public Shop selectById(int dbId) throws DbAccessException, NoSuchEntityException;
	
	public int insertOne(Shop shop) throws DbAccessException, UniqueConstraintViolationException;
	
	/**
	 * Updates existing shop in the DB, all fields except "id" can be changed
	 * @param shop - updatedShop
	 * @throws DbAccessException
	 * @throws UniqueConstraintViolationException if updated information violates UNIQUE constraint on some column
	 */
	public void updateShop(Shop updatedShop)  throws DbAccessException, UniqueConstraintViolationException;

	/**
	 * Quick-fix method, will be removed after implementing transaction manager
	 * @param shop
	 * @param conn
	 * @return
	 * @throws DbAccessException
	 * @throws UniqueConstraintViolationException
	 */
	public int insertOne(Shop shop, Connection conn) throws DbAccessException,
			UniqueConstraintViolationException;

	public void updateShop(Shop newShop, Connection conn) throws DbAccessException, UniqueConstraintViolationException;
	
}
