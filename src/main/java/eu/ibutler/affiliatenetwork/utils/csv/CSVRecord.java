package eu.ibutler.affiliatenetwork.utils.csv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CSVRecord {
	
	private List<String> header = null;
	private List<String> data = null;
	
	/**
	 * 
	 * @param header array of header names
	 * @param data array of values
	 */
	public CSVRecord(String[] header, String[] data) {
		this.header = new ArrayList<String>( Arrays.asList(header) );
		this.data = new ArrayList<String>( Arrays.asList(data) );
	}
	
	/**
	 * 
	 * @param header string, comma separated names
	 * @param data string, comma separated values
	 */
	public CSVRecord(String header, String data) {
		this.header = new ArrayList<String>( Arrays.asList(header.split(",")) );
		this.data = new ArrayList<String>( Arrays.asList(data.split(",")) );
	}
	
	/**
	 * Only checks if header and data array have the same length  
	 * @return
	 */
	public boolean isConsistent() {
		return header.size() == data.size();
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public String get(String name) {
		int index = header.indexOf(name);
		if(index == -1) {
			return null;
		} else {
			return data.get(index);
		}
	}
	
	/**
	 * 
	 * @param index
	 * @return
	 */
	public String get(int index) {
		try {
			return data.get(index);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	/**
	 * @return "value1,value2, .. valueN"
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		for(String value : this.data) {
			result.append(value + ",");
		}
		//delete last ","
		result.deleteCharAt(result.length()-1);
		return result.toString();
	}
	
	

}
