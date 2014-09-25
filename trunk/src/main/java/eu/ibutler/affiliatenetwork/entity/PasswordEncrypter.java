package eu.ibutler.affiliatenetwork.entity;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

public class PasswordEncrypter {
	
	private static final String salt = RandomStringUtils.random(32);

}
