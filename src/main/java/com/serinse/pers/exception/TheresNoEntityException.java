package com.serinse.pers.exception;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = false)
public class TheresNoEntityException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TheresNoEntityException() {
		super();
	}

	public TheresNoEntityException(String message, Throwable cause) {
		super(message, cause);
	}

	public TheresNoEntityException(String message) {
		super(message);
	}

	public TheresNoEntityException(Throwable cause) {
		super(cause);
	}

}
