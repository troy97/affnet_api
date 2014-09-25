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
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import eu.ibutler.affiliatenetwork.entity.AppProperties;
import eu.ibutler.affiliatenetwork.filters.RequestCountingFilter;

/**
 * This class represents Status Endpoint for AffiliateNetwork service
 * and provides such information:
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
public class StatusPageController implements HttpHandler {
	
	private static AppProperties properties = AppProperties.getInstance();
	private static Logger log = Logger.getLogger(StatusPageController.class.getName());
	private final long serviceStartTime;

	/**
	 * 
	 * @param Time in milliseconds when this http server started
	 */
	public StatusPageController(long startTime) {
		this.serviceStartTime = startTime;
	}

	/**
	 * @see com.sun.net.httpserver.HttpHandler
	 */
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		//increment request Counter
		//super.incRequestCounter();

		//get request
		String requestMethod = exchange.getRequestMethod();
		if(!requestMethod.equals("GET")) {
			//render error page here
		}
		BufferedInputStream in = new BufferedInputStream(exchange.getRequestBody());
		in.close();
		
		//get service status
		Date startTime = new Date(this.serviceStartTime);
		long upTimeMillis = System.currentTimeMillis() - this.serviceStartTime;
	    String upTime = String.format("%02d:%02d:%02d:%02d", 
	    		TimeUnit.MILLISECONDS.toDays(upTimeMillis),
	    		TimeUnit.MILLISECONDS.toHours(upTimeMillis) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(upTimeMillis)),
	            TimeUnit.MILLISECONDS.toMinutes(upTimeMillis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(upTimeMillis)),
	            TimeUnit.MILLISECONDS.toSeconds(upTimeMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(upTimeMillis)));
		String fsStatus = "down";
		if(testFs()) {fsStatus = "OK";}
		String dbStatus = "down";
		if(testDb()) {dbStatus = "OK";}
		int errorCount = getErrorCount();
		int warningCount = getWarningCount();
		long requestCount = getRequestCount();
		
		//generate response html
		String responseHtml = "<html>"
				+ "<head>"
				+ "<h2>Affiliate Network service status</h2>"
				+ "</head>"
				+ "<body>"
				+ "Service was started at: " + startTime + ";</br>"
				+ "Service up-time is (dd:hh:mm:ss): " + upTime + ";</br>"
				+ "File system status is: " + fsStatus + ";</br>"
				+ "Database status is: " + dbStatus + ";</br>"
				+ "Entries in log-ERORR.log: " + errorCount + ";</br>"
				+ "Entries in log-WARN.log: " + warningCount + ";</br>"
				+ "Request count is: " + requestCount + ".</br>"
				+ "</body>"
				+ "</html>";
		
		//send response
		exchange.sendResponseHeaders(200, responseHtml.length());
		BufferedOutputStream out = new BufferedOutputStream(exchange.getResponseBody());
		out.write(responseHtml.getBytes());
		out.flush();
		out.close();
	}

	private long getRequestCount() {
		return RequestCountingFilter.getRequestCounter();
	}

	private int getErrorCount() {
		int result = 0;
		try {
			result = countLinesInFile(properties.getProperty("logErrorFilePath"));
		} catch (IOException ignore) {/*no errors, leave result =0*/}
		return result;
	}

	private int getWarningCount() {
		int result = 0;
		try {
			result = countLinesInFile(properties.getProperty("logWarningFilePath"));
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
		String DB_URL = properties.getProperty("dbURL");
		
		// Database credentials
		String USER = properties.getProperty("dbUser");
		String PASS = properties.getProperty("dbPassword");
		
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
		
		String FS_TEST_PATH = properties.getProperty("fsTestPath");
		
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
