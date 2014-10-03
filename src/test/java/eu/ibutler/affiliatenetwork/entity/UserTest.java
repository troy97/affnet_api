package eu.ibutler.affiliatenetwork.entity;

import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class UserTest {

	@Test
	public void testUser() {
		User user = new User();
		assertThat(user.getName(), is("default"));
		assertThat(user.getEncryptedPassword(), is(Encrypter.encrypt("default")));
		assertThat(user.getEmail(), is("default@default.net"));
		assertThat(user.getDbId(), is(0));
	}
	
}
