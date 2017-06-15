package com.serinse.pers.exception;

import javax.ejb.ApplicationException;


@ApplicationException(rollback = true)
public class FailProcessException extends Exception {

	private static final long serialVersionUID = 1L;

	public FailProcessException() {
		super();
	}

	public FailProcessException(String message, Throwable cause) {
		super(message, cause);

	}

	public FailProcessException(String message) {
		super(message);
	}

	public FailProcessException(Throwable cause) {
		super(cause);
	}

}