package eu.ibutler.affiliatenetwork.entity;

import org.apache.log4j.Logger;

/**
 * Class represents a web-shop user who may upload 
 * *.csv files to our server
 * Password is stored in encrypted form.
 * @author Anton Lukashchuk
 *
 */
public class User {
	
	private static Logger log = Logger.getLogger(User.class.getName());
	
	private String name;
	private String email;
	private String login;
	private String encryptedPassword;
	private int dbId = 0; //id obtained from DB when adding new user, 0 is default
	
	/**
	 * Constructs user with all String fields set to "default",
	 * dbId field set to 0
	 */
	public User() {
		this.name = "default";
		this.email = "default";
		this.login = "default";
		this.encryptedPassword = Encrypter.encrypt("default");
		log.debug("New user: \"" + this.login + "\" created.");
	}

	/**
	 * Constructs new User with given parameters. 
	 * @param name
	 * @param email
	 * @param login
	 * @param encryptedPassword
	 */
	public User(String name, String email, String login, String plainPassword) {
		this.name = name;
		this.email = email;
		this.login = login;
		this.encryptedPassword = Encrypter.encrypt(plainPassword);
		this.dbId = 0;
		log.debug("New user: \"" + this.login + "\" created.");
	}
	
	/**
	 * This constructor is only used when DAO creates user, dbId and encrypted password
	 * must be provided.
	 * If this constructor is called not from DAO, and thus dbId is unknown yet,
	 * set it to 0 and update later with setDbId() method.
	 * @param name
	 * @param email
	 * @param login
	 * @param encryptedPassword
	 * @param dbId user Id from database.
	 */
	public User(String name, String email, String login, String encryptedPassword, int dbId) {
		this.name = name;
		this.email = email;
		this.login = login;
		this.encryptedPassword = encryptedPassword;
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

	public String getEncryptedPassword() {
		return encryptedPassword;
	}

	public void setPassword(String plainPassword) {
		this.encryptedPassword = Encrypter.encrypt(plainPassword);
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
