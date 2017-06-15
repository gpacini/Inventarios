package com.serinse.web.session;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

import com.serinse.ejb.impl.administration.PermissionBean;
import com.serinse.ejb.impl.administration.UserBean;
import com.serinse.ejb.impl.client.ClientBean;
import com.serinse.ejb.impl.inventory.InventoryBean;
import com.serinse.pers.entity.adm.Permission;
import com.serinse.pers.entity.adm.Role;
import com.serinse.pers.entity.adm.User;
import com.serinse.pers.entity.client.Client;
import com.serinse.pers.entity.inventory.Inventory;
import com.serinse.web.app.SessionManager;

@SessionScoped
public class UserSessionBean implements Serializable {

	private static final long serialVersionUID = 5012543931596313562L;

	private String sessionKey;
	private String username;
	private User user;
	
	private List<Client> allowedClients;
	
	private String redirectLink;
	private boolean redirect = false;
	
	@Inject UserBean userbean;
	@Inject InventoryBean inventoryBean;
	@Inject SessionManager sessionManager;
	@Inject PermissionBean permissionBean;
	@Inject ClientBean clientBean;
	
	@PostConstruct
	public void init( ){
		username = null;
		sessionKey = null;
	}
	
	public void logout(){
		sessionManager.removeSession(username);
		user = null;
		username = null;
		sessionKey = null;
		removeRedirectLink();
	}
	
	public boolean isUserInSession( ){
		return username != null;
	}
	
	public boolean doesUserExists(String username){
		User user = userbean.findUserByUsername(username);
		return user != null;
	}
	
	public boolean logInUser(String username, String password){
		User user = userbean.findUserByUsername(username);
		if( user.getPassword().equals(User.passwordToHash(password) )){
			this.username = user.getUsername();
			this.user = User.getDummyUser(user);
			this.sessionKey = user.getSessionKey();
			sessionManager.addSession(username, this);
			allowedClients = clientBean.getClientsByUsername(this.username);
			return true;
		}
		return false;
	}
	
	public boolean isFirstLogin(){
		return sessionKey == null;
	}
	
	public boolean createSessionKey(){
		sessionKey = User.passwordToHash((new Date()).getTime() + username);
		User u = userbean.findById(user.getId());
		u.setSessionKey(sessionKey);
		userbean.save(u);
		return true;
	}
	
	public Role getUserRole( ){
		return user.getRole();
	}
	
	public boolean hasRole(String role){
		return getUserRole().getRoleName().equals(role);
	}
	
	public boolean hasAnyRole(Set<String> roles){
		for( String r : roles ){
			if( hasRole(r) ){
				return true;
			}
		}
		return false;
	}
	
	public String getSessionKey( ){
		return sessionKey;
	}
	
	public void redirectLink(String url){
		redirectLink = url;
		redirect = true;
	}
	
	public boolean redirect(){
		return redirect;
	}
	
	public String getRedirectLink(){
		return redirectLink;
	}
	
	public String removeRedirectLink(){
		redirect = false;
		return redirectLink;
	}
	
	public String getUsername(){
		return username;
	}
	
	public boolean hasPermission(String url){
		List<Permission> permissions = permissionBean.getPermissionsByRole(getUserRole().getRoleName());
		for(Permission permission : permissions){
			if( permission.getPath().equalsIgnoreCase(url)){
				return true;
			}
		}
		return false;
	}
	
	public String getHomeLink(){
		if( hasRole(Role.ADMIN_ROLE) || hasRole(Role.PROGRAMMING_ROLE) ){
			return "/serinse/admin/users.jsf";
		} else if( hasRole(Role.DIGITATOR_ROLE) || hasRole(Role.CHIEF_ROLE) || hasRole(Role.CLIENT_ROLE) ){
			return "/serinse/content/inventory.jsf";
		}
		return "/serinse/error/permissionDenied.jsf";
	}
	
	public boolean userHasClientAccess(Client client){
		if( !hasRole(Role.CLIENT_ROLE) ) return true;
		for( Client c : allowedClients ){
			if( c.getId().equals(client.getId()) ){
				return true;
			}
		}
		return false;
	}
}
