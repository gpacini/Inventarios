package com.serinse.ejb.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException (rollback=true)
public class BoardRollBackException extends Exception {

	private static final long serialVersionUID = 1L;
	private String message;
	
	public BoardRollBackException(String m){
		super(m);
		message = m;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
	
}
