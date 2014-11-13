package eu.ibutler.affiliatenetwork.dao.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.ibutler.affiliatenetwork.dao.AdminDao;
import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.NoSuchEntityException;
import eu.ibutler.affiliatenetwork.entity.Admin;
import eu.ibutler.affiliatenetwork.utils.Encrypter;

public class AdminDaoMock implements AdminDao {
	
	private Set<Admin> users = new HashSet<Admin>();

	public AdminDaoMock() {
		users.add(new Admin("ebay", "ebay@gmail.com", Encrypter.encrypt("1111"), 1));
		users.add(new Admin("amazon", "amazon@gmail.com", Encrypter.encrypt("1111"), 2));
	}

	@Override
	public Admin selectAdmin(String login, String password) throws DbAccessException, NoSuchEntityException {
		for(Admin u : this.users) {
			if(u.getEmail().equals(login) && u.getEncryptedPassword().equals(password)){
				return u;
			}
		}
		throw new NoSuchEntityException();
	}

	@Override
	public List<Admin> selectAllAdmins() throws DbAccessException {
		return null;
	}

	@Override
	public int insertAdmin(Admin user) throws DbAccessException {
		users.add(user);
		return 0;
	}

}
