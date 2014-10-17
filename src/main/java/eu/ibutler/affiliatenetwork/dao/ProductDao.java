package eu.ibutler.affiliatenetwork.dao;

import java.util.List;

import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.NoSuchEntityException;
import eu.ibutler.affiliatenetwork.entity.FileTemplate;
import eu.ibutler.affiliatenetwork.entity.Product;

public interface ProductDao {

	public Product selectById(long dbId) throws DbAccessException, NoSuchEntityException;
	
	public long insertOne(Product product) throws DbAccessException;
	
	public void insertAll(List<Product> products) throws DbAccessException;
	
	public List<Product> selectByFileId(int fileId) throws DbAccessException, NoSuchEntityException;
}
