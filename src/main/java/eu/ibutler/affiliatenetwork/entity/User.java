package eu.ibutler.affiliatenetwork.entity;

import org.apache.log4j.Logger;

/**
 * Class represents a web-shop user who may upload 
 * *.csv files to our server
 * @author Anton Lukashchuk
 *
 */
public class User {
	
	private static Logger log = Logger.getLogger(User.class.getName());
	
	private String name;
	private String email;
	private String login;
	private String password;
	private int dbId = 0; //id obtained from DB when adding new user, 0 is default
	
	/**
	 * Constructs user with all String fields set to "default",
	 * dbId field set to 0
	 */
	public User() {
		this.name = "default";
		this.email = "default";
		this.login = "default";
		this.password = "default";
		log.debug("New user: \"" + this.login + "\" created.");
	}

	/**
	 * Constructs new User with given parameters. 
	 * If this constructor is called not from DAO, and thus dbId is unknown yet,
	 * set it to 0 and update later with setDbId() method.
	 * @param name
	 * @param email
	 * @param login
	 * @param password
	 * @param dbId user Id from database, set 0 if unknown.
	 */
	public User(String name, String email, String login, String password, int dbId) {
		this.name = name;
		this.email = email;
		this.login = login;
		this.password = password;
		this.dbId = dbId;
		log.debug("New user: \"" + this.login + "\" created.");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getDbId() {
		return dbId;
	}

	public void setDbId(int dbId) {
		this.dbId = dbId;
	}

	public String getLogin() {
		return login;
	}

	/**
	 * Two users are only equal if they have the same login
	 * everything else doesn't matter
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (login == null) {
			if (other.login != null)
				return false;
		} else if (!login.equals(other.login))
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		return this.login.hashCode();
	}
	
	
	
	

}
