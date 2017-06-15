package com.serinse.web.controllers;

import java.io.Serializable;
import java.util.Set;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.serinse.web.session.UserSessionBean;

@Named("controllerUserSession")
@SessionScoped
public class ControllerUserSession implements Serializable{
	
	@Inject UserSessionBean userSessionBean;

	private static final long serialVersionUID = -6305201319681890277L;
	
	public boolean isLoggedIn(){
		return userSessionBean.isUserInSession();
	}
	
	public boolean belongsTo(String role){
		return userSessionBean.hasRole(role);
	}
	
	public boolean hasAnyRole(Set<String> roles){
		return userSessionBean.hasAnyRole(roles);
	}
	
	public String getHomeLink(){
		if( !userSessionBean.isUserInSession() ) return "";
		return userSessionBean.getHomeLink();
	}
	
}
