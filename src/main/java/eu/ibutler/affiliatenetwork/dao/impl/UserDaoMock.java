package eu.ibutler.affiliatenetwork.dao.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.ibutler.affiliatenetwork.dao.UserDao;
import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.NoSuchUserException;
import eu.ibutler.affiliatenetwork.entity.Encrypter;
import eu.ibutler.affiliatenetwork.entity.User;

public class UserDaoMock implements UserDao {
	
	private Set<User> users = new HashSet<User>();

	public UserDaoMock() {
		users.add(new User("ebay", "ebay@gmail.com", "ebay", Encrypter.encrypt("1111"), 1));
		users.add(new User("amazon", "amazon@gmail.com", "amazon", Encrypter.encrypt("1111"), 2));
	}

	@Override
	public User login(String login, String password) throws DbAccessException, NoSuchUserException {
		for(User u : this.users) {
			if(u.getLogin().equals(login) && u.getEncryptedPassword().equals(password)){
				return u;
			}
		}
		throw new NoSuchUserException();
	}

	@Override
	public List<User> getAllUsers() throws DbAccessException {
		return null;
	}

	@Override
	public int addUser(User user) throws DbAccessException {
		users.add(user);
		return 0;
	}

}
