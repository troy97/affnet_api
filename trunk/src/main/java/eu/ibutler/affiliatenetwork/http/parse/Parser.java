package eu.ibutler.affiliatenetwork.http.parse;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.ibutler.affiliatenetwork.http.parse.exceptions.ParsingException;

/**
 * This class has utility methods to parse 
 * http parameters
 * @author troy
 *
 */
public class Parser {
	private static Logger log = Logger.getLogger(Parser.class.getName());
	
	/**
	 * Parse query string to Map of "name-value" pairs
	 * Resulting map consists of name keys and values,
	 * NULL or empty-string values are not supported
	 * @param query
	 * @return Map<name, value>
	 * @throws ParsingException
	 */
	public static Map<String, String> parseQuery(String query) throws ParsingException {
		try {
			Map<String, String> result = new HashMap<>();
			query = URLDecoder.decode(query, "UTF-8");
			String[] nameValuePairs = query.split("&");
			for(String pair : nameValuePairs) {
				//if pair ends with "=" then value is empty (NULL), don't add it to result
				if(!pair.endsWith("=")) {
					String[] parsed = pair.split("=");
					result.put(parsed[0], parsed[1]);
				}
			}
			return result;
		} catch (Exception e) {
			log.debug("Parsing error, exception: " + e.getClass().getCanonicalName());
			throw new ParsingException();
		}
	}
	
	/**
	 * Get parameter with desired name from given query
	 * @param query
	 * @param paramName
	 * @return
	 */
	public static String getParam(String query, String paramName) {
		String result = null;
		try {
			result = Parser.parseQuery(query).get(paramName);
		} catch (Exception e) {
			//NOP
		}
		return result;
	}
}
