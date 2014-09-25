package eu.ibutler.affiliatenetwork.entity;

/**
 * This class only holds login and password pair
 * only two getter methods, nothing else
 * @author anton
 *
 */
public class LoginAndPassword {
	
	private String login = "";
	private String password = "";
	
	public LoginAndPassword(String login, String password) {
		this.login = login;
		this.password = password;
	}

	public String getLogin() {
		return login;
	}

	public String getPassword() {
		return password;
	}
}
