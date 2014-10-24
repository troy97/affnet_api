package eu.ibutler.affiliatenetwork.dao;

import java.util.List;

import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.UniqueConstraintViolationException;
import eu.ibutler.affiliatenetwork.entity.FileTemplate;

public interface FileTemplateDao {

	public List<FileTemplate> getAllFiles() throws DbAccessException;
	
	public List<FileTemplate> getAllActive() throws DbAccessException;
	
	public int insertOne(FileTemplate file) throws DbAccessException, UniqueConstraintViolationException;
	
}
