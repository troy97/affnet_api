package eu.ibutler.affiliatenetwork.entity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import org.junit.Test;

public class PasswordEncrypterTest {
	
	@Test
	public void testEncryptionOK() {
		String password = "qwerty35";
		String encryptedPassword = Encrypter.encrypt(password);
		assertThat(password.equals(encryptedPassword), is(false));
	}
	
	@Test
	public void testDecryptionOK() {
		String password = "qweHL98_gf";
		String encryptedPassword = Encrypter.encrypt(password);
		assertThat(Encrypter.check(password, encryptedPassword), is(true));
	}
	
	@Test
	public void testIncorrectPassword() {
		String password = "strongNoH_757password";
		String encryptedPassword = Encrypter.encrypt(password);
		assertThat(Encrypter.check("wrong_password", encryptedPassword), not(true));
	}

}
