package com.serinse.ejb.impl.mail;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.commons.mail.Email;

import com.serinse.common.ProjectParameterEnum;
import com.serinse.ejb.impl.projectParameter.ProjectParameterBean;
import com.serinse.pers.entity.projectParameter.ProjectParameter;

@Stateless
@LocalBean
public class MailSender {

	@EJB
	ProjectParameterBean projectParameterBean;
	
	private ProjectParameter user;
	private ProjectParameter server;
	private ProjectParameter port;
	private ProjectParameter ssl;
	private ProjectParameter active;
	private ProjectParameter password;
	
	@PostConstruct
	public void init(){
		user = projectParameterBean.getOrInitialize(ProjectParameterEnum.EMAIL_USER);
		password = projectParameterBean.getOrInitialize(ProjectParameterEnum.EMAIL_PASS);
		server = projectParameterBean.getOrInitialize(ProjectParameterEnum.EMAIL_SERVER);
		port = projectParameterBean.getOrInitialize(ProjectParameterEnum.EMAIL_PORT);
		ssl = projectParameterBean.getOrInitialize(ProjectParameterEnum.EMAIL_SSL);
		active = projectParameterBean.getOrInitialize(ProjectParameterEnum.EMAIL_ACTIVE);
	}
	
	public void sendMail(MailData mail) throws MailSenderException{
		if( active.getIsNew() || active.getValueAsInt().intValue() == 0) {
			throw new MailSenderException("Mail is not active");
		}
		Email email = new SimpleEmail();
		email.setHostName(server.getValue());
		email.setSmtpPort(new Integer(port.getValue()));
		email.setAuthenticator(new DefaultAuthenticator(user.getValue(), password.getValue()));
		email.setSSL(ssl.getValueAsInt().intValue() != 0);
		try{
			email.setFrom(mail.getFrom(), mail.getFromName() != null ? mail.getFromName() : "Inventarios Serinse");
			email.setSubject(mail.getSubject());
			email.setMsg(mail.getMessage());
			email.addTo(mail.getTo());
			if( mail.getCc() != null ){
				email.addCc(mail.getCc());
			}
			email.send();
		} catch( EmailException e){
			throw new MailSenderException("Couldn't send email", e.getMessage());
		}
	}
	
	
}