package com.serinse.pers.exception;

public class NoDeletedEntityException extends Exception {

	private static final long serialVersionUID = 1L;

	public NoDeletedEntityException() {
		super();
	}

	public NoDeletedEntityException(String message, Throwable cause) {
		super(message, cause);

	}

	public NoDeletedEntityException(String message) {
		super(message);
	}

	public NoDeletedEntityException(Throwable cause) {
		super(cause);
	}

}
