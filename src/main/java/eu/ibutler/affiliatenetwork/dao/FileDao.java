package eu.ibutler.affiliatenetwork.dao;

import java.util.List;

import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.UniqueConstraintViolationException;
import eu.ibutler.affiliatenetwork.entity.UploadedFile;

public interface FileDao {
	
	public List<UploadedFile> getAllFiles() throws DbAccessException;
	
	public List<UploadedFile> getLastNfiles(int n, int shopId) throws DbAccessException;
	
	public int insertFile(UploadedFile file) throws DbAccessException, UniqueConstraintViolationException;
	
	public void updateUploadTime(UploadedFile file)  throws DbAccessException;


}
