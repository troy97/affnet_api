package eu.ibutler.affiliatenetwork.file;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Throwables;

import au.com.bytecode.opencsv.CSVReader;
import eu.ibutler.affiliatenetwork.ProcessingException;
import eu.ibutler.affiliatenetwork.ValidationException;
import eu.ibutler.affiliatenetwork.config.Config;
import eu.ibutler.affiliatenetwork.dao.impl.FileDaoImpl;
import eu.ibutler.affiliatenetwork.dao.impl.ProductDaoImpl;
import eu.ibutler.affiliatenetwork.dao.utils.JdbcUtils;
import eu.ibutler.affiliatenetwork.entity.Product;
import eu.ibutler.affiliatenetwork.entity.UploadedFile;

@FileExtension(".csv")
public class CSVProcessor extends AbstractFileProcessor {
	
	private static final char[] POSSIBLE_SEPARATORS = {'\t', ',', ';'};
	private static final byte NEW_LINE = (byte) '\n';
	
	private List<String> mYaColumns  = null;
	private List<String> optYaColumns  = null;

	@Override
	public byte[] validateDownload(InputStream stream) throws ValidationException {
		byte[] result;
		try {
			byte[] firstLine = getFirstLine(stream, NEW_LINE);
			validateHeader( new String(firstLine, Config.ENCODING));
			result = firstLine;
		} catch (Exception e) {
			throw new ValidationException("Invalid header " + e);
		}
		return result;
	}
	
	/**
	 * Get byte array from InputStream until first occurrence of lineEnd byte
	 * @return byte[] ending with lineEnd
	 * @throws IOException
	 */
	private byte[] getFirstLine(InputStream in, byte lineEnd) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte b = 0;
		while( b != lineEnd ) {
			b=(byte)in.read();
			out.write(b);
		}
		return out.toByteArray();
	}
	
	/**
	 * Check first line of file for presence of correct CSV header.
	 * Tries do validate header using all separators from POSSIBLE_SEPARATORS array
	 * @param header as string
	 * @return value separator character
	 * @throws FileValidationException
	 */
	private char validateHeader(String headerAsStr) throws ValidationException {
		boolean valid = false;
		char result = ',';
		for(char separator : POSSIBLE_SEPARATORS) {
			try {
				CSVReader reader = new CSVReader( new StringReader(headerAsStr), separator );
				List<String> csvHeader = new ArrayList<String>( Arrays.asList(reader.readNext()) );
				reader.close();
				this.validateHeader0(csvHeader);
				valid = true;
				result = separator;
				break;
			} catch (ValidationException | IOException ignor) {}
		}
		if(!valid) {
			throw new ValidationException("Invalid header.");
		}
		return result;
	}
	
	
	
	/**
	 * Checks if given header contains all of mandatory columns,
	 * each of them only ones,
	 * no empty "" header names
	 * @param someHeader
	 * @param mandatoryColumns
	 * @throws ValidationException
	 */
	private void validateHeader0(List<String> header) throws ValidationException {
		List<String> mandatoryColumns;
		List<String> optionalColumns;
		if(header.contains(FileFormat.VENDORMODEL_TYPE_M)) { //that is not exactly correct, I must check not only presence of this column, but its value too
			mandatoryColumns = FileFormat.getMandatoryColumnNames(FileFormat.Type.VENDORMODEL);
			optionalColumns = FileFormat.getOptionalColumnNames(FileFormat.Type.VENDORMODEL);
		} else {
			mandatoryColumns = FileFormat.getMandatoryColumnNames(FileFormat.Type.SIMPLIFIED);
			optionalColumns = FileFormat.getOptionalColumnNames(FileFormat.Type.SIMPLIFIED);
		}
		this.mYaColumns = mandatoryColumns;
		this.optYaColumns = optionalColumns;
		//check for empty "" header names
		if( header.contains("") ) {
			String msg = "Header contains empty \"\" column name, reject file...";
			logger.debug(msg);
			throw new ValidationException(msg);
		}		
		//check for presence of all mandatory columns
		if( !header.containsAll(mandatoryColumns) ) {
			String msg = "File is missing some mandatory data."
					+ " Header line is: " + header + "; Mandatory columns are: " + mandatoryColumns +
					". Reject file, throw exception...";
			//logger.debug(msg);
			throw new ValidationException(msg);
		}
		//check for duplicate column names
		Set<String> s = new HashSet<String>(header);
		if(s.size() != header.size()) {
			String msg = "File has duplicate column name in header.";
			logger.debug(msg);
			throw new ValidationException(msg);
		}
	}//validateHeader

	
	@Override
	public UploadedFile validateFile(UploadedFile file) {
		String validationMsg = "Validation OK";
		boolean valid = true;
		
		int lineNumber = 0;
		try {
			char separator = getSeparator(file); //I got separator, means header is valid too.
			CSVReader reader = new CSVReader( new FileReader(file.getFsPath()), separator );
/*			List<String> header = new ArrayList<String>( Arrays.asList(reader.readNext()) );
			
			String[] line;
			while( (line = reader.readNext())!=null ) {
				lineNumber++;
				List<String> data = new ArrayList<String>( Arrays.asList(line) );
				this.validateData0(header, data);
			}*/

			reader.close();
		} catch (Exception e) {
			validationMsg = "Validation error on line: " + lineNumber + ". ";
			valid = false;
			logger.debug(validationMsg + Throwables.getStackTraceAsString(e));
		}
		
		file.setValidationMessage(validationMsg);
		file.setValid(valid);
		return file;
	}
	
	private Product extractProduct0(int fileId, int shopId, List<String> header, List<String> data) {
		//mandatory
		String realUrl = data.get(header.indexOf(FileFormat.COLUMN_URL_PATH_M));
		String name;
		if(header.contains(FileFormat.SIMPLIFIED_NAME_M)) {
			name = data.get(header.indexOf(FileFormat.SIMPLIFIED_NAME_M));
		} else {
			name = data.get(header.indexOf(FileFormat.VENDORMODEL_VENDOR_M)) + " " + data.get(header.indexOf(FileFormat.VENDORMODEL_MODEL_M));
		}
		double price = Double.valueOf( data.get(header.indexOf(FileFormat.COLUMN_PRICE_M)) );
		String currencyCode = data.get(header.indexOf(FileFormat.COLUMN_PRICE_CURRENCY_M));
		String category = data.get(header.indexOf(FileFormat.COLUMN_CATEGORY_M));
		
		//optional
		String imageUrl = null;
		try {
			imageUrl = data.get(header.indexOf(FileFormat.COLUMN_PICTURE_O));
		} catch (IndexOutOfBoundsException ignore) {}
		
		String description = null;
		try {
			description = data.get(header.indexOf(FileFormat.COLUMN_DESCRIPTION_O));
		} catch (IndexOutOfBoundsException ignore) {}
		
		String descriptionShort = null; // yandex does not support short desc
		
		String ean = null;
		try {
			ean = data.get(header.indexOf(FileFormat.COLUMN_BARCODE_O));
		} catch (IndexOutOfBoundsException ignore) {}
		
		double localDeliveryCost = -1.0;
		try {
			localDeliveryCost = Double.valueOf( data.get(header.indexOf(FileFormat.COLUMN_LOCAL_DELIVERY_COST_O)) );
		} catch (NumberFormatException | IndexOutOfBoundsException ignore) {}
		
		return new Product(fileId, shopId, realUrl, name, price, currencyCode, category,
				imageUrl, description, descriptionShort, ean, localDeliveryCost);
	}
	
	/**
	 * Validate data line from csv file
	 * @param header
	 * @param data
	 * @throws ValidationException if data is invalid
	 */
	private void validateData0(List<String> header, List<String> data) throws ValidationException {
		//check record valididy
		if(header.size() != data.size()) {
			String msg = "Header length not equal to data line length.";
			logger.debug(msg);
			throw new ValidationException(msg);
		}
		
		//check mandatory data validity
		List<String> mColumns = mYaColumns;
			//check for empty cells
		for(String c : mColumns) {
			int index = header.indexOf(c);
			if(data.get(index).isEmpty()) {
				String msg = "Empty mandatory column: " + c;
				logger.debug(msg);
				throw new ValidationException(msg);
			}
		}
			//TODO: check many other conditions here, like length, data type and other...
		
		
		//check optional data validity
		List<String> optColumns = optYaColumns;
			//TODO: if cell is not empty, check many other conditions here, like length, data type and other...
	}

	
	private char getSeparator(UploadedFile file) throws ValidationException {
		char separator;
		try {
			InputStream in = new FileInputStream(file.getFsPath());
			byte[] firstLine = getFirstLine(in, NEW_LINE);
			separator = validateHeader( new String(firstLine, Config.ENCODING) );
		} catch (Exception e) {
			String msg = "Can't get header line. Reason: " + e;
			logger.debug(msg);
			throw new ValidationException(msg);
		}
		return separator;
	}
	
	@Override
	public void processFile(UploadedFile file) throws ProcessingException {
		CSVReader reader = null;
		ProductDaoImpl productDao = new ProductDaoImpl();
		int lineCounter = 0;
		try {
			productDao.setProcessing(file.getShopId(), true);
			char separator = getSeparator(file);
			reader = new CSVReader( new FileReader(file.getFsPath()), separator );
			List<String> header = new ArrayList<String>( Arrays.asList(reader.readNext()) );
			
			String[] line;
			while( (line = reader.readNext())!=null ) {
				lineCounter++;
				Connection conn = JdbcUtils.getConnection();
				try {
					List<String> data = new ArrayList<String>( Arrays.asList(line) );
					Product product = this.extractProduct0(file.getId(), file.getShopId(), header, data);
					super.validateProduct(product);

					Product existing = productDao.selectByUrl(product.getRealUrl(), product.getShopId());
					if(existing == null) {
						long id =new ProductDaoImpl().insertOne(product, conn);
						product.setId(id);
					} else {
						product = existing;
					}
					product.setActive(true);
					product.setProcessing(false);
					productDao.update(product, conn);
					JdbcUtils.commitAndClose(conn);
				} catch (Exception e) {
					logger.debug("Invalid product, continue to next. Line: " + lineCounter + " Exception: " + Throwables.getStackTraceAsString(e));
					JdbcUtils.rollbackAndClose(conn);
				}
			}
			
			productDao.deactivateOld(file.getShopId());
			
			file.setProcessed(true);
			file.setActive(true);
			file.setProductsCount(lineCounter);
			new FileDaoImpl().update(file);
		} catch (Exception e) {
			String msg = "Fatal processing error, line number: " + lineCounter + ". " + Throwables.getStackTraceAsString(e);
			logger.debug(msg);
		} finally {
			if(reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					logger.debug("Failed to close csv reader: " + Throwables.getStackTraceAsString(e));
				}
			}
		}
		
	}



}
