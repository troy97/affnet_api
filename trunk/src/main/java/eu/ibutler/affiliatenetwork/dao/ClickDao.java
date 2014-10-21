package eu.ibutler.affiliatenetwork.dao;

import java.util.List;

import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.NoSuchEntityException;
import eu.ibutler.affiliatenetwork.entity.Click;

public interface ClickDao extends DAO<Click> {

	public List<Click> selectByShopId(int shopId) throws DbAccessException;
	
	public List<Click> selectByDistributorId(int distributorId)  throws DbAccessException;
	
	public List<Click> selectByProductId(int productId) throws DbAccessException;
	
}
