package com.serinse.pers.exception;

import javax.ejb.ApplicationException;


@ApplicationException(rollback = false)
public class FailProcessWithoutRollbackException extends Exception {

	private static final long serialVersionUID = 1L;

	public FailProcessWithoutRollbackException() {
		super();
	}

	public FailProcessWithoutRollbackException(String message, Throwable cause) {
		super(message, cause);

	}

	public FailProcessWithoutRollbackException(String message) {
		super(message);
	}

	public FailProcessWithoutRollbackException(Throwable cause) {
		super(cause);
	}

}