package eu.ibutler.affiliatenetwork.validation;

import java.util.List;

import org.apache.commons.fileupload.MultipartStream;

import eu.ibutler.affiliatenetwork.entity.Product;
import eu.ibutler.affiliatenetwork.entity.UploadedFile;
import eu.ibutler.affiliatenetwork.utils.csv.exceptions.InconsistentRecordException;
import eu.ibutler.affiliatenetwork.utils.csv.exceptions.ParsingException;

@FileExtension(".zip")
public class ZipValidator extends FileValidator {

	@Override
	public byte[] validateDownload(MultipartStream stream)
			throws ValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<Product> getProducts(UploadedFile uploadedFile)
			throws ParsingException, InconsistentRecordException {
		// TODO Auto-generated method stub
		return null;
	}


}
