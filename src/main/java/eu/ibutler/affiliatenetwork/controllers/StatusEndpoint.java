package eu.ibutler.affiliatenetwork.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import eu.ibutler.affiliatenetwork.config.AppConfig;
import eu.ibutler.affiliatenetwork.filters.RequestCountingFilter;

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
 * All time variables are stored in milliseconds since EPOCH.
 * @author Anton Lukashchuk
 *
 */
public class StatusEndpoint {
	
	private static AppConfig cfg = AppConfig.getInstance();
	private static Logger logger = Logger.getLogger(StatusEndpoint.class.getName());
	
	private static int errors = 0;
	private static int warnings = 0;
	
	public static long startTime = 0L;
	public static String startTimeString = null;
	private static long upTime = 0L;
	
	public static void init(long serviceStartTime) {
		startTime = serviceStartTime;
		startTimeString = new SimpleDateFormat("yyyy MMM dd  HH:mm:ss").format(new Date(serviceStartTime)).toString();
	}
	
	public static String updateAndGet() {
		String responseOrderly = null;
		
		upTime = System.currentTimeMillis() - startTime;
    	long days =	TimeUnit.MILLISECONDS.toDays(upTime);
    	long hours = TimeUnit.MILLISECONDS.toHours(upTime) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(upTime));
        long minutes = TimeUnit.MILLISECONDS.toMinutes(upTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(upTime));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(upTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(upTime));
		int errorCount = errors;
		int warningCount = warnings;
		long requestCount = RequestCountingFilter.getRequestCounter();
		boolean dbStatus = testDb();
		boolean fsStatus = testFs();
		
		responseOrderly = "object{\n"
						+ "\"started_at\": \"" + startTimeString + "\",\n"
						+ "\"running_for\": \"" + days + "d " + hours + "h " + minutes + "m " + seconds + "s\",\n"
						+ "\"db_health\": " + dbStatus + ",\n"
						+ "\"fs_health\": " + fsStatus + ",\n"
						+ "\"errors\": " + errorCount + ",\n"
						+ "\"warnings\": " + warningCount + ",\n"
						+ "\"requests\": " + requestCount + "\n"
						+ "}*\n";
		return responseOrderly;
	}
	

	/**
	 * Increment error counter
	 * @return
	 */
	public static void incrementErrors() {
		errors++;
	}

	/**
	 * Increment warning counter
	 * @return
	 */
	public static void incrementWarnings() {
		warnings++;
	}
	
	/**
	 * Try to create and then delete table in the DB
	 * @return true in case of success, false otherwise
	 */
	private static boolean testDb() {
		boolean result = false;
		String db_url = cfg.getWithEnv("dbURL");
		String user = cfg.getWithEnv("dbUser");
		String password = cfg.getWithEnv("dbPassword");
		//open connection and test DB
		try(Connection conn = DriverManager.getConnection(db_url, user, password);
			Statement stmt = conn.createStatement()) {
			//execute a query
			stmt.executeUpdate("CREATE TABLE dbtest_table(id INT, PRIMARY KEY (id));");
			stmt.executeUpdate("DROP TABLE dbtest_table;");
			//db OK
			result = true;
		}catch(SQLException e){
			incrementErrors();
			logger.error("DB test exception");
		}
		return result;
	}
	
	/**
	 * Try to write to file and then read from it
	 * @return true in case of success, false otherwise
	 */
	private static boolean testFs() {
		boolean result = false;
		File fsTestFile = new File(cfg.getWithEnv("uploadPath") + "/fsTestFile.txt");
		try (FileOutputStream out = new FileOutputStream(fsTestFile);
				FileInputStream in = new FileInputStream(fsTestFile)) {
			String testStr = "Just a string";
			out.write(testStr.getBytes("UTF-8"));
			out.flush();
			byte[] fromFile = new byte[testStr.length()];
			in.read(fromFile);
			if( (new String(fromFile, "UTF-8")).equals(testStr) ) {
				result = true;
			}
			fsTestFile.delete();
		} catch (Exception e) {
			incrementErrors();
			logger.error("Exception while checking file system: " + e.getClass().getName());
			result = false;
		}
		return result;
	}

}
