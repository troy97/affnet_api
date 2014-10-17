package eu.ibutler.affiliatenetwork.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import eu.ibutler.affiliatenetwork.dao.impl.UserDaoImpl;

/**
 * Every DAO implementation should extends this class and 
 * override extractOne(rs), which is template method.
 * This makes extractAll(rs) method available.
 * @author Anton Lukashchuk
 *
 * @param <T>
 */
public abstract class Extractor<T> {
	
	protected Logger logger = Logger.getLogger(this.getClass().getName());
	
	/**
	 * Template method to extract one entity of type T from 
	 * given result set
	 * @param rs
	 * @return entity of type T
	 */
	protected abstract T extractOne(ResultSet rs) throws SQLException;
	
	/**
	 * Extracts all entities of type T
	 * from given result set
	 * @param rs
	 * @return List<T> entities
	 * @throws SQLException
	 */
	protected List<T> extractAll(ResultSet rs) throws SQLException {
		List<T> result = new ArrayList<>();
		while(rs.next()) {
			result.add(extractOne(rs));
		}
		return result;
	}

}
