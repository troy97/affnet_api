package eu.ibutler.affiliatenetwork.jdbc;

import java.sql.SQLException;

public class ConnectionFactoryFactory {
	
	private static FactoryTypes currentFactoryType=FactoryTypes.NATIVE_JDBC;
	
	public enum FactoryTypes{NATIVE_JDBC, C3P0, OTHER};
	
	/**
	 *
	 * @return concrete ConnectionFactory
	 */
	public static ConnectionFactory getFactory(){	
		switch(currentFactoryType){
		case NATIVE_JDBC:{
			try {
				return new JdbcNativeFactory();
			} catch (SQLException e) {
				System.out.println("Unable to create JdbcNativeFactory in ConnectionFactoryFactory.getFactory()");
			}
		}
		break;
		/* case C3P0: return new C3P0Factory();
		break;
		case OTHER: return new OtherFactory();
		break;*/
		default: return null;	
		}
		return null;	
	}
	
	/**
	 * Set type of factory to use
	 * @param type
	 */
	public static void setFactoryType(ConnectionFactoryFactory.FactoryTypes type){
		currentFactoryType = type;
	}
}
