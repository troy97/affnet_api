package eu.ibutler.affiliatenetwork.entity;

import org.apache.commons.codec.digest.DigestUtils;

public class PasswordEncrypter {
	
	private static final String salt = "shf8w7yr4ryuGHh876tr4ty347tytr8yfgi";
	
	/**
	 * SHA256-hash the password with the salt.
	 * @param password
	 */
	public static String encrypt(String password)
	{
	    return password = DigestUtils.sha256Hex(password + salt);
	}

	/**
	 * Check if a given password matches given encrypted string.
	 * @param givenPassword
	 * @return True is correct, else false.
	 */
	public static boolean checkPassword(String plainPassword, String encryptedPassword)
	{
	    return (encryptedPassword.equals(DigestUtils.sha256Hex(plainPassword + salt)));
	}

}
