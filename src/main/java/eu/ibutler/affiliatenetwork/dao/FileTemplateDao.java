package eu.ibutler.affiliatenetwork.dao;

import java.util.List;

import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.NoSuchEntityException;
import eu.ibutler.affiliatenetwork.dao.exceptions.UniqueConstraintViolationException;
import eu.ibutler.affiliatenetwork.entity.FileTemplate;

public interface FileTemplateDao {

	public FileTemplate selectById(int id) throws DbAccessException, NoSuchEntityException;
	
	public FileTemplate selectByShopId(int shopId) throws DbAccessException, NoSuchEntityException;
	
	public List<FileTemplate> getAllFiles() throws DbAccessException;
	
	public List<FileTemplate> getAllActive() throws DbAccessException;
	
	public int insertOne(FileTemplate file) throws DbAccessException, UniqueConstraintViolationException;
	
}
