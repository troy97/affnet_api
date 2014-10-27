package eu.ibutler.affiliatenetwork.utils.validation;

import org.apache.commons.fileupload.MultipartStream;

public interface FileValidator {

	public boolean validateDownload(MultipartStream stream) throws ValidationFailException;
	
	public boolean validateFile(String filePath) throws ValidationFailException;
	
}
