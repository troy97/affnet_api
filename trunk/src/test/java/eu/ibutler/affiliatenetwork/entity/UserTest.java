package eu.ibutler.affiliatenetwork.entity;

import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class UserTest {

	@Test
	public void testUser() {
		User user = new User();
		assertThat(user.getName(), is("default"));
		assertThat(user.getLogin(), is("default"));
		assertThat(user.getPassword(), is("default"));
		assertThat(user.getEmail(), is("default"));
		assertThat(user.getDbId(), is(0));
	}
	
}
