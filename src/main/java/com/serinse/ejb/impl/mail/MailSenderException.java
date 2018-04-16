package com.serinse.ejb.impl.mail;

public class MailSenderException extends Exception{

	private static final long serialVersionUID = 1797820127675174987L;
	private String reason;
	
	public MailSenderException(String reason, String message){
		super(reason+": "+message);
		this.reason = reason;
	}
	
	public MailSenderException(String reason){
		super(reason);
		this.reason = reason;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
}