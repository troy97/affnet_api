package eu.ibutler.affiliatenetwork.entity;

import eu.ibutler.affiliatenetwork.utils.Encrypter;

public class User {

	private int dbId = 0;
	private String email = null;
	private String encryptedPassword = null;
	private String createdAt = null;
	private String firstName = null;
	private String lastName = null;
	private boolean isActive = false;
	private int shopId = 0;
	
	/**
	 * Public constructor for new User registration purpose
	 * @param email
	 * @param plainPassword
	 * @param firstName
	 * @param lastName
	 * @param shopId
	 */
	public User(String email, String plainPassword, String firstName, String lastName, int shopId) {
		this.email = email;
		this.encryptedPassword = Encrypter.encrypt(plainPassword);
		this.firstName = firstName;
		this.lastName = lastName;
		this.shopId = shopId;
	}

	/**
	 * For use by DAO only
	 * @param dbId
	 * @param email
	 * @param encryptedPassword
	 * @param createdAt
	 * @param firstName
	 * @param lastName
	 * @param isActive
	 * @param shopId
	 */
	public User(int dbId, String email, String encryptedPassword,
			String createdAt, String firstName, String lastName,
			boolean isActive, int shopId) {
		this.dbId = dbId;
		this.email = email;
		this.encryptedPassword = encryptedPassword;
		this.createdAt = createdAt;
		this.firstName = firstName;
		this.lastName = lastName;
		this.isActive = isActive;
		this.shopId = shopId;
	}
	
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		return result;
	}

	
	/**
	 * Two users are only equal if they have the same email
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
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		return true;
	}

	public int getDbId() {
		return dbId;
	}

	public void setDbId(int dbId) {
		this.dbId = dbId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		if(email != null) {
			this.email = email;
		}
	}

	public String getEncryptedPassword() {
		return encryptedPassword;
	}

	public void setEncryptedPassword(String encryptedPassword) {
		if(encryptedPassword != null) {
			this.encryptedPassword = encryptedPassword;
		}
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		if(createdAt != null) {
			this.createdAt = createdAt;
		}
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		if(firstName != null) {
			this.firstName = firstName;
		}
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		if(lastName != null) {
			this.lastName = lastName;
		}
	}

	public int getShopId() {
		return shopId;
	}

	public void setShopId(int shopId) {
		this.shopId = shopId;
	}

	public boolean isActive() {
		return isActive;
	}
	
	@Override
	public User clone() {
		return new User(dbId, email, encryptedPassword, createdAt, firstName, lastName, isActive, shopId);
	}
	
	
	
}
