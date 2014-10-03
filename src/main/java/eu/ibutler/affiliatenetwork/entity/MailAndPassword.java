package eu.ibutler.affiliatenetwork.entity;

/**
 * This class only holds login and password pair
 * only two getter methods, nothing else
 * @author anton
 *
 */
public class MailAndPassword {
	
	private String email = "";
	private String password = "";
	
	public MailAndPassword(String login, String password) {
		this.email = login;
		this.password = password;
	}

	public String getMail() {
		return this.email;
	}

	public String getPassword() {
		return this.password;
	}
}
