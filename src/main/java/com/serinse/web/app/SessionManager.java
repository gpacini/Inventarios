package com.serinse.web.app;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import com.serinse.web.session.UserSessionBean;

@ApplicationScoped
public class SessionManager {

	private Map<String, UserSessionBean> activeSessions;
	
	@PostConstruct
	public void init( ){
		activeSessions = new HashMap<>();
	}
	
	public void addSession(String user, UserSessionBean bean){
		activeSessions.put(user, bean);
	}
	
	public void removeSession(String user){
		activeSessions.remove(user);
	}
	
	public boolean isUserLoggedIn(String user){
		return activeSessions.containsKey(user);
	}
	
}
