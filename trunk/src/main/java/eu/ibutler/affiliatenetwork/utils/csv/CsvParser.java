package eu.ibutler.affiliatenetwork.utils.csv;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;
import eu.ibutler.affiliatenetwork.entity.Product;
import eu.ibutler.affiliatenetwork.entity.UploadedFile;
import eu.ibutler.affiliatenetwork.http.ParsingException;

public class CsvParser {
	
	private static Logger logger = Logger.getLogger(CsvParser.class.getName());
	
	private String filePath = null;
	

	public CsvParser(String filePath) {
		if(filePath == null) {
			throw new NullPointerException();
		} else {
			this.filePath = filePath;
		}
	}

	/**
	 * CSV file considered valid if it contains all mandatory columns
	 * @param file
	 * @return -1 if file is invalid or 0 or more number that shows total entry number
	 */
	public int isValid() {
		int result = -1;
		List<String> mandatoryColumns = new ArrayList<String>( Arrays.asList(CSVutils.COLUMN_URL_PATH,
																			 CSVutils.COLUMN_NAME,
																			 CSVutils.COLUMN_DESCRIPTION) );
		try ( CSVReader reader = new CSVReader(new FileReader(this.filePath)) ) {
			//Headers must be the first line
			List<String> headers = new ArrayList<String>( Arrays.asList(reader.readNext()) );
			if(headers.containsAll(mandatoryColumns)) {
				result = 0;
				while(reader.readNext() != null) {
					result++;
				}
			}
		} catch (Exception e) {
			logger.debug("Exception while validating file: " + e.getClass().getName());
		}
		return result;
	}
	
	public List<String> getHeaders() {
		List<String> result = new ArrayList<>();
		try ( CSVReader reader = new CSVReader(new FileReader(this.filePath)) ) {
			//Headers must be the first line
			result = new ArrayList<String>(Arrays.asList(reader.readNext()));
		} catch (Exception e) {
			logger.debug("Exception while validating file: " + e.getClass().getName());
		}
		return result;
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public List<CSVRecord> parse() throws ParsingException {
		List<CSVRecord> result = new ArrayList<CSVRecord>();
		try ( CSVReader reader = new CSVReader(new FileReader(this.filePath)) ) {
			String[] headers = reader.readNext();
			String[] line = null;
			while( (line = reader.readNext())!=null ) {
				result.add( new CSVRecord(headers, line) );
			}
		} catch (Exception e) {
			logger.debug("Exception while parsing file: " + e.getClass().getName());
			throw new ParsingException();
		}
		
		return result;
	}
	
	
}
