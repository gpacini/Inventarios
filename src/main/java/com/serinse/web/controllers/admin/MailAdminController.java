package com.serinse.web.controllers.admin;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.serinse.common.FileHelpers;
import com.serinse.common.ProjectParameterEnum;
import com.serinse.ejb.impl.mail.MailData;
import com.serinse.ejb.impl.mail.MailSender;
import com.serinse.ejb.impl.mail.MailSenderException;
import com.serinse.ejb.impl.projectParameter.ProjectParameterBean;
import com.serinse.pers.entity.projectParameter.ProjectParameter;

@Named
@ViewScoped
public class MailAdminController implements Serializable  {

	private static final long serialVersionUID = 1L;
	
	private ProjectParameter user;
	private String password;
	private ProjectParameter server;
	private ProjectParameter port;
	private ProjectParameter ssl;
	private ProjectParameter active;
	
	@Inject
	private ProjectParameterBean projectParameterBean;
	
	@Inject
	private MailSender mailSender;

	@PostConstruct
	public void init() {
		user = projectParameterBean.getOrInitialize(ProjectParameterEnum.EMAIL_USER);
		password = "";
		server = projectParameterBean.getOrInitialize(ProjectParameterEnum.EMAIL_SERVER);
		port = projectParameterBean.getOrInitialize(ProjectParameterEnum.EMAIL_PORT);
		ssl = projectParameterBean.getOrInitialize(ProjectParameterEnum.EMAIL_SSL);
		active = projectParameterBean.getOrInitialize(ProjectParameterEnum.EMAIL_ACTIVE);
	}
	
	public void save() {
		try {
			projectParameterBean.saveProjectParameter(user);
			projectParameterBean.saveProjectParameter(server);
			projectParameterBean.saveProjectParameter(port);
			projectParameterBean.saveProjectParameter(ssl);
			projectParameterBean.saveProjectParameter(active);
			
			if( !FileHelpers.isStringEmptyOrNull(password) ){
				ProjectParameter pPassword = projectParameterBean.getOrInitialize(ProjectParameterEnum.EMAIL_PASS);
				if( pPassword == null ){
					pPassword = new ProjectParameter();
					pPassword.setName(ProjectParameterEnum.EMAIL_PASS.getName());
				}
				pPassword.setValue(password);
				projectParameterBean.saveProjectParameter(pPassword);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void sendTestMail() {
		MailData mailData = new MailData("gpacini@serinse.com", 
				"inventarios@serinse.com", 
				"Prueba", 
				"Este es un correo de prueba para el sistema de inventarios");
		try {
			mailSender.sendMail(mailData);
		} catch (MailSenderException e) {
			e.printStackTrace();
		}
	}
	
	public boolean getRender(){
		return active.getValue().equalsIgnoreCase("Yes");
	}

	public ProjectParameterBean getProjectParameterBean() {
		return projectParameterBean;
	}

	public void setProjectParameterBean(ProjectParameterBean projectParameterBean) {
		this.projectParameterBean = projectParameterBean;
	}

	public ProjectParameter getUser() {
		return user;
	}

	public void setUser(ProjectParameter user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public ProjectParameter getServer() {
		return server;
	}

	public void setServer(ProjectParameter server) {
		this.server = server;
	}

	public ProjectParameter getPort() {
		return port;
	}

	public void setPort(ProjectParameter port) {
		this.port = port;
	}

	public ProjectParameter getSsl() {
		return ssl;
	}

	public void setSsl(ProjectParameter ssl) {
		this.ssl = ssl;
	}

	public ProjectParameter getActive() {
		return active;
	}

	public void setActive(ProjectParameter active) {
		this.active = active;
	}
	
}