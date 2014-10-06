package eu.ibutler.affiliatenetwork.entity;

import org.junit.Test;

import eu.ibutler.affiliatenetwork.utils.Encrypter;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class UserTest {

	@Test
	public void testUser() {
		Admin user = new Admin();
		assertThat(user.getName(), is("default"));
		assertThat(user.getEncryptedPassword(), is(Encrypter.encrypt("default")));
		assertThat(user.getEmail(), is("default@default.net"));
		assertThat(user.getDbId(), is(0));
	}
	
}
