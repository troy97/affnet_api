package eu.ibutler.affiliatenetwork.utils.csv;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;
import eu.ibutler.affiliatenetwork.http.parse.exceptions.ParsingException;

public class CSVParser {
	
	private static Logger logger = Logger.getLogger(CSVParser.class.getName());

	private final String filePath;
	
	/**
	 * Public constructor
	 * @param filePath
	 * @throws NullPointerException if given parameter is NULL 
	 */
	public CSVParser(String filePath) {
		if(filePath == null) {
			throw new NullPointerException();
		} else {
			this.filePath = filePath;
		}
	}
	
	/**
	 * Return List of header names found in file
	 * headers must be the first line in the file.
	 * @return List<String> first line of file.
	 */
	public List<String> getHeaders() {
		List<String> result = new ArrayList<>();
		try ( CSVReader reader = new CSVReader(new FileReader(filePath)) ) {
			//Headers must be the first line
			result = new ArrayList<String>(Arrays.asList(reader.readNext()));
		} catch (Exception e) {
			logger.debug("Exception while validating file: " + e.getClass().getName());
		}
		return result;
	}

	/**
	 * Gets all entries in csv file as List of CSVRecords
	 * @return
	 * @throws IOException
	 */
	public List<CSVRecord> parse() throws ParsingException {
		List<CSVRecord> result = new ArrayList<CSVRecord>();
		try ( CSVReader reader = new CSVReader(new FileReader(filePath)) ) {
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
