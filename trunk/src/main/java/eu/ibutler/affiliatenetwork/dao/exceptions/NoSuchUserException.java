package eu.ibutler.affiliatenetwork.dao.exceptions;

@SuppressWarnings("serial")
public class NoSuchUserException extends DaoException {

	public NoSuchUserException() {
		super();
	}

	public NoSuchUserException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoSuchUserException(String message) {
		super(message);
	}

	public NoSuchUserException(Throwable cause) {
		super(cause);
	}
	
}
