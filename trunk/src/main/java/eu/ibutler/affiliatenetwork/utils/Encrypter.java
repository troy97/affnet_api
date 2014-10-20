package eu.ibutler.affiliatenetwork.utils;

import org.apache.commons.codec.digest.DigestUtils;

import eu.ibutler.affiliatenetwork.config.AppConfig;

/**
 * Class allows encryption and decryption of a given String
 * @author anton
 *
 */
public class Encrypter {
	
	private static final String salt = AppConfig.getInstance().get("salt");
	
	/**
	 * SHA256HEX-hash the string with salt.
	 * @param string to encrypt
	 */
	public static String encrypt(String string)
	{
		if(string == null) {
			return null;
		} else {
			return DigestUtils.sha256Hex(string + salt);
		}
	}
	
	/**
	 * Check if a given string matches given encrypted string.
	 * @param givenString
	 * @return True if match, else false.
	 */
	public static boolean check(String plainString, String encryptedString)
	{
		if(plainString == null || encryptedString == null) {
			return false;
		} else {
			return (encryptedString.equals(DigestUtils.sha256Hex(plainString + salt)));
		}
	}
	

}
