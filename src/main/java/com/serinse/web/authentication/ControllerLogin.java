package com.serinse.web.authentication;

import java.io.IOException;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import com.serinse.web.session.UserSessionBean;

@Named
@RequestScoped
public class ControllerLogin implements Serializable{

	private static final long serialVersionUID = 81263176325236L;
	
	@Inject UserSessionBean userSessionBean;
	
	private String username;
	private String password;
	
	@PostConstruct
	public void init(){
		
		try{
			if( userSessionBean.isUserInSession() ){
				FacesContext.getCurrentInstance().getExternalContext().redirect(userSessionBean.getHomeLink());
				return;
			}
		} catch( Exception e ){
			//DO Nothing
		}
		
		username = "";
		password = "";
	}
	
	public void logIn(){
		if(username.trim().equalsIgnoreCase("") || password.trim().equalsIgnoreCase("") ){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Los campos no pueden estar vacios", ""));
			return;
		}
		
		if( !userSessionBean.doesUserExists(username) ){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "El usuario no existe", ""));
			return;
		}
		if( !userSessionBean.logInUser(username, password)){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contrasena Incorrecta", ""));
			return;
		}
		//TODO comprobar que no sea el primer login o sino redirigirlo a cambiar la contrasena
		try{
			if( userSessionBean.redirect() ){
				FacesContext.getCurrentInstance().getExternalContext().redirect(userSessionBean.removeRedirectLink());
				return;
			} else {
				FacesContext.getCurrentInstance().getExternalContext().redirect(userSessionBean.getHomeLink());
				return;
			}
		} catch( Exception e ){
			//DO Nothing
		}
	}
	
	public void logout(){
		userSessionBean.logout();
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("/login.jsf");
		} catch (IOException e) {
			//Do Nothing
		}
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
