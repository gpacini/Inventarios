package com.serinse.pers.exception;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class UnsavedEntityException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UnsavedEntityException() {
		super();
	}

	public UnsavedEntityException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsavedEntityException(String message) {
		super(message);
	}

	public UnsavedEntityException(Throwable cause) {
		super(cause);
	}

}
