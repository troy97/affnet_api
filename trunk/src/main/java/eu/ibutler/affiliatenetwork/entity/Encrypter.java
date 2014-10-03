package eu.ibutler.affiliatenetwork.entity;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * Class allows encryption and decryption of a given String
 * @author anton
 *
 */
public class Encrypter {
	
	private static final String salt = AppProperties.getInstance().getProperty("salt");
	
	/**
	 * SHA256HEX-hash the string with salt.
	 * @param string to encrypt
	 */
	public static String encrypt(String string)
	{
	    return DigestUtils.sha256Hex(string + salt);
	}
	
	/**
	 * Check if a given string matches given encrypted string.
	 * @param givenString
	 * @return True if match, else false.
	 */
	public static boolean check(String plainString, String encryptedString)
	{
	    return (encryptedString.equals(DigestUtils.sha256Hex(plainString + salt)));
	}
	

}
