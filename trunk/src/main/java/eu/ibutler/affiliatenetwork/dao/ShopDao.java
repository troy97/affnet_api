package eu.ibutler.affiliatenetwork.dao;

import java.util.List;

import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.NoSuchEntityException;
import eu.ibutler.affiliatenetwork.entity.Shop;

public interface ShopDao {

	public List<Shop> getAllShops() throws DbAccessException;
	
	public Shop selectById(int dbId) throws DbAccessException, NoSuchEntityException;
	
}
