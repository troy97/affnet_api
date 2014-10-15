package eu.ibutler.affiliatenetwork.controllers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;

import eu.ibutler.affiliatenetwork.MainClass;
import eu.ibutler.affiliatenetwork.filters.RequestCountingFilter;
import eu.ibutler.affiliatenetwork.utils.AppConfig;

/**
 * This class represents Status Endpoint for AffiliateNetwork service
 * and provides such information in orderly format:
 * - service start time
 * - service up duration
 * - file system health
 * - database health
 * - error count
 * - warning count
 * - number of requests to server since start
 * @author Anton Lukashchuk
 *
 */
@SuppressWarnings("restriction")
@WebController("/status")
public class StatusPageController extends AbstractHttpHandler implements RestrictedAccess {
	
	private static AppConfig properties = AppConfig.getInstance();
	private static Logger log = Logger.getLogger(StatusPageController.class.getName());
	private final long serviceStartTime;

	/**
	 * 
	 * @param Time in milliseconds when this http server started
	 */
	public StatusPageController() {
		this.serviceStartTime = MainClass.getStartTime();
	}

	/**
	 * @see com.sun.net.httpserver.HttpHandler
	 */
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		//get request
		String requestMethod = exchange.getRequestMethod();
		if(!requestMethod.equals("GET")) {
			sendRedirect(exchange, Links.ERROR_PAGE_CONTROLLER_FULL_URL);
			return;
		}
		
		try(InputStream in = exchange.getRequestBody()) {}
		
		//get service status
		Date startDate = new Date(this.serviceStartTime);
		String startTime = new SimpleDateFormat("yyyy MMM dd  HH:mm:ss").format(startDate);
		long upTimeMillis = System.currentTimeMillis() - this.serviceStartTime;
	   // String upTime = String.format("%02d:%02d:%02d:%02d", 
    	long days =	TimeUnit.MILLISECONDS.toDays(upTimeMillis);
    	long hours = TimeUnit.MILLISECONDS.toHours(upTimeMillis) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(upTimeMillis));
        long minutes = TimeUnit.MILLISECONDS.toMinutes(upTimeMillis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(upTimeMillis));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(upTimeMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(upTimeMillis));
		int errorCount = getErrorCount();
		int warningCount = getWarningCount();
		long requestCount = getRequestCount();
		boolean dbStatus = testDb();
		boolean fsStatus = testFs();
		
		
/*		object{

			   string "started_at" /^[0-9]{4} [A-Z][a-z]{2} [0-9]{1,2} [0-9]{2}:[0-9]{2}:[0-9]{2}$/; // Server time when service was started
			   numeric "running_for" /^[0-9]+d [0-9]{1,2}h [0-9]{1,2}s$/; // Service running duration in friendly format
			   boolean "db_health"; // True if DB write and read succeed in one transaction
			   boolean "fs_health"; // True if file write and read operation succeed
			   numeric "errors"; // Number of errors occurred / logged since service started
			   numeric "warnings"; // Number of warnings occurred / logged since service started
			   numeric "requests"; // Number of requests made to server since service started

		}**/		
		String responseOrderly = "object{\n"
				+ "\"started_at\": \"" + startTime + "\",\n"
				+ "\"running_for\": \"" + days + "d " + hours + "h " + seconds + "s\",\n"
				+ "\"db_health\": " + dbStatus + ",\n"
				+ "\"fs_health\": " + fsStatus + ",\n"
				+ "\"errors\": " + errorCount + ",\n"
				+ "\"warnings\": " + warningCount + ",\n"
				+ "\"requests\": " + requestCount + "\n"
				+ "}*\n";
				
		//generate response html
		String responseHtml = responseOrderly;
		
		//send response
		exchange.sendResponseHeaders(200, responseHtml.getBytes("UTF-8").length);
		try(BufferedOutputStream out = new BufferedOutputStream(exchange.getResponseBody())){
			out.write(responseHtml.getBytes());
			out.flush();
		}
	}//handler

	/**
	 * Atomic request counter from filter 
	 * @return
	 */
	private long getRequestCount() {
		return RequestCountingFilter.getRequestCounter();
	}

	/**
	 * Number of lines in ERR.log
	 * @return
	 */
	private int getErrorCount() {
		int result = 0;
		try {
			result = countLinesInFile(properties.get("logErrorFilePath"));
		} catch (IOException ignore) {/*no errors, leave result =0*/}
		return result;
	}

	/**
	 * Number of lines in WARN.log
	 * @return
	 */
	private int getWarningCount() {
		int result = 0;
		try {
			result = countLinesInFile(properties.get("logWarningFilePath"));
		} catch (IOException ignore) {/*no warnings, leave result =0*/}
		return result;
	}
	
	private int countLinesInFile(String filename) throws IOException {
	    InputStream is = new BufferedInputStream(new FileInputStream(filename));
	    try {
	        byte[] c = new byte[1024];
	        int count = 0;
	        int readChars = 0;
	        boolean empty = true;
	        while ((readChars = is.read(c)) != -1) {
	            empty = false;
	            for (int i = 0; i < readChars; ++i) {
	                if (c[i] == '\n') {
	                    ++count;
	                }
	            }
	        }
	        return (count == 0 && !empty) ? 1 : count;
	    } finally {
	        is.close();
	    }
	}

	/**
	 * Try to create and then delete table in the DB
	 * @return true in case of success, false otherwise
	 */
	private boolean testDb() {

		boolean result = false;
		
		// Database URL
		String DB_URL = properties.get("dbURL");
		
		// Database credentials
		String USER = properties.get("dbUser");
		String PASS = properties.get("dbPassword");
		
		//open connection and test DB
		try(Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
				Statement stmt = conn.createStatement()) {
			//form statements
			String createTableSql = "CREATE TABLE test(id INT, PRIMARY KEY (id));";
			String dropTableSql = "DROP TABLE test;";
			//execute a query
			stmt.executeUpdate(createTableSql);
			stmt.executeUpdate(dropTableSql);
			//db ok
			result = true;
		}catch(SQLException e){
			log.error("DB test exception");
		}

		return result;
	}
	
	/**
	 * Try to write file and then read from it
	 * @return true in case of success, false otherwise
	 */
	private boolean testFs() {
		//test result: false = fs problem;
		//			   true = fs OK
		boolean result = false;
		
		String FS_TEST_PATH = properties.get("fsTestPath");
		
		File fsTestFile = new File(FS_TEST_PATH);
		try (FileWriter writer = new FileWriter(fsTestFile);
				FileReader reader = new FileReader(fsTestFile)) {
			String testString = "File system test";
			writer.write(testString);
			writer.flush();
			char[] fileData = new char[testString.length()];
			reader.read(fileData);
			if(!(new String(fileData)).equals(testString)) {
				throw new FileNotFoundException();
			}
			result = true;
			//if exception will be thrown when calling
			//writer or reader .close() it's still fs error.
		} catch (FileNotFoundException e) {
			log.error("File system read problem");
			result = false;
		} catch (IOException e) {
			log.error("File system write problem");
			result = false;
		}
		
		return result;
	}

}
