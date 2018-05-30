package com.serinse.ejb.impl.mail;

import java.util.ArrayList;

public class MailData {

	private String to;
	
	private String from;
	
	private String subject;
	
	private String message;
	
	private ArrayList<String> cc;
	
	private String fromName;
	
	public String getFromName() {
		return fromName;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	public MailData(String to, String from, String subject, String message){
		this.to = to;
		this.from = from;
		this.subject = subject;
		this.message = message;
	}
	
	public ArrayList<String> getCC() {
		return cc;
	}

	public void addCC(String nCC) {
		if ( cc == null ) cc = new ArrayList<String>();
		cc.add(nCC);
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}