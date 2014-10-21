package eu.ibutler.affiliatenetwork.entity;

import eu.ibutler.affiliatenetwork.utils.Encrypter;

public class Distributor {
	
	private int id = 0;
	
	private String email = null;
	private String encryptedPassword = null;
	private String firstName = null;
	private String lastName = null;
	
	/**
	 * DAO constructor
	 * @param id
	 * @param email
	 * @param firstName
	 * @param lastName
	 */
	public Distributor(int id, String email, String encryptedPassword, String firstName, String lastName) {
		this.id = id;
		this.email = email;
		this.encryptedPassword = encryptedPassword;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	
	/**
	 * Public constructor
	 * @param email
	 * @param firstName
	 * @param lastName
	 */
	public Distributor(String email, String plainPassword, String firstName, String lastName) {
		this.email = email;
		this.encryptedPassword = Encrypter.encrypt(plainPassword);
		this.firstName = firstName;
		this.lastName = lastName;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
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


	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}


	public String getFirstName() {
		return firstName;
	}


	public String getLastName() {
		return lastName;
	}



	

}
