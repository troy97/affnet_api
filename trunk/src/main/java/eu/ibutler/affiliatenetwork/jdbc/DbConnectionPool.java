package eu.ibutler.affiliatenetwork.jdbc;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import eu.ibutler.affiliatenetwork.utils.AppConfig;

/**
 *c3p0 Connection pool
 */
public class DbConnectionPool {
	
	private static Logger log = Logger.getLogger(DbConnectionPool.class.getName());
	private static AppConfig properties = AppConfig.getInstance();
	
	private static DbConnectionPool singleton = null;
	private ComboPooledDataSource cpds = null;
	
	private DbConnectionPool() {
		this.cpds = setupDataSource();
	}

	public static DbConnectionPool getInstance() {
		if(singleton == null) {
			singleton = new DbConnectionPool();
		}
		return singleton;
	}

	/**
	 * Sets up the configuration for database connection
	 * @throws PropertyVetoException
	 */
	public synchronized ComboPooledDataSource setupDataSource() {
		cpds = new ComboPooledDataSource();
		try {
			cpds.setDriverClass(properties.get("jdbcDriver"));
		} catch (PropertyVetoException e) {
			log.fatal("Failed to setup db connection pool");
			System.exit(1);
		} 
		cpds.setJdbcUrl(properties.get("dbURL")); 
		cpds.setUser(properties.get("dbUser")); 
		cpds.setPassword(properties.get("dbPassword")); 
		//cpds.setMinPoolSize(Integer.parseInt(properties.getProperty("dbMinPoolSize"))); 
		//cpds.setInitialPoolSize(Integer.parseInt(properties.getProperty("dbInitialPoolSize")));
		//cpds.setMaxPoolSize(Integer.parseInt(properties.getProperty("dbMaxPoolSize")));
		return cpds;
	}

	/**
	 * Creates connection to your db, specified in config.properties
	 * @return connection
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException {
		return this.cpds.getConnection();
	}
}

































