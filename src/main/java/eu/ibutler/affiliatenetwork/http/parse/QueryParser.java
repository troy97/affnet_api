package eu.ibutler.affiliatenetwork.http.parse;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.ibutler.affiliatenetwork.http.ParsingException;
import eu.ibutler.affiliatenetwork.utils.LinkUtils;

/**
 * This class has utility methods to parse 
 * http parameters
 * @author troy
 *
 */
public class QueryParser {
	private static Logger log = Logger.getLogger(QueryParser.class.getName());
	
	/**
	 * Parse query string to Map of "name-value" pairs
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
}
