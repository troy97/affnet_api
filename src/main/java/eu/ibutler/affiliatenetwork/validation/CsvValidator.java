package eu.ibutler.affiliatenetwork.validation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.fileupload.MultipartStream;

import eu.ibutler.affiliatenetwork.entity.Product;
import eu.ibutler.affiliatenetwork.entity.UploadedFile;
import eu.ibutler.affiliatenetwork.http.parse.exceptions.FileValidationException;
import eu.ibutler.affiliatenetwork.utils.csv.CSVParser;
import eu.ibutler.affiliatenetwork.utils.csv.CSVRecord;
import eu.ibutler.affiliatenetwork.utils.csv.CSVUtils;
import eu.ibutler.affiliatenetwork.utils.csv.exceptions.InconsistentRecordException;
import eu.ibutler.affiliatenetwork.utils.csv.exceptions.ParsingException;

/**
 * This class allows validation and processing of CSV files that are uploaded to AffNet service.
 * 
 * @author Anton Lukashchuk
 *
 */
@FileExtension(".csv")
public class CsvValidator extends FileValidator{

	/**
	 * Implementation for CSV files
	 * @see eu.ibutler.affiliatenetwork.validation.FileValidator#validateDownload(org.apache.commons.fileupload.MultipartStream)
	 */
	@Override
	public byte[] validateDownload(MultipartStream stream) throws ValidationException {
		byte[] result;
		try {
			byte[] header = getFirstLine(stream, MultipartStream.LF);
			validateHeader(header);
			result = header;
		} catch (Exception e) {
			throw new ValidationException("Invalid csv header");
		}
		return result;
	}
	
	/**
	 * Get byte array from MultipartStream until first occurrence of lineEnd byte
	 * @return byte[] ending with lineEnd
	 * @throws IOException if there's no more data in given multipart straem
	 */
	private byte[] getFirstLine(MultipartStream mps, byte lineEnd) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte b = 0;
		while( b != lineEnd ) {
			b=mps.readByte();
			out.write(b);
		}
		return out.toByteArray();
	}

	/**
	 * Check first line of file for presence of correct CSV header
	 * @param firstLine
	 * @throws IOException
	 * @throws FileValidationException
	 */
	private void validateHeader(byte[] firstLine) throws IOException, ValidationException {
		List<String> csvHeaders = CSVUtils.parseLine(firstLine, "UTF-8");
		if( !csvHeaders.containsAll(CSVUtils.getMandatoryColumnNames()) ) {
			logger.debug("File is missing some mandatory data."
					+ " Header line is: " + csvHeaders + "; Mandatory columns are: " + CSVUtils.getMandatoryColumnNames() +
					". Reject file, throw exception...");
			throw new ValidationException();
		}
	}

	/**
	 * @see FileValidator#getProducts(UploadedFile)
	 */
	@Override
	protected List<Product> getProducts(UploadedFile uploadedFile) throws ParsingException, InconsistentRecordException {
		List<Product> products = null;
		try{	
			products = new ArrayList<Product>();
			CSVParser csvParser = new CSVParser(uploadedFile.getFsPath());
			int count = 0;
			for(CSVRecord record : csvParser.parse()) {
				if(record.isConsistent()) {
					products.add( new Product(record, uploadedFile.getDbId(), uploadedFile.getShopId()) );
				} else {
					logger.debug("Incosistent CSV record found, line number: " + count + " throw exception.");
					throw new InconsistentRecordException("Incosistent CSV record found, line number: " + count);
				}
				count++;
			}
		} catch (ParsingException e) {
			logger.debug("Unable to extract all products from uploaded csv file: " + e.getClass().getName());
			throw e;
		}
		return products;
	}

}
