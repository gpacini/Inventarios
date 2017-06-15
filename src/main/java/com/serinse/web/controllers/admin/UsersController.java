package com.serinse.web.controllers.admin;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.serinse.ejb.impl.administration.RoleBean;
import com.serinse.ejb.impl.administration.UserBean;
import com.serinse.pers.entity.adm.Role;
import com.serinse.pers.entity.adm.User;

@Named
@ViewScoped
public class UsersController implements Serializable{

	private static final long serialVersionUID = 18273516723L;
	
	
	@Inject UserBean userBean;
	@Inject RoleBean roleBean;
	
	private LazyDataModel<User> users;
	private User userToEdit;
	private String password;
	
	private List<Role> roles;
	private String selectedRole;
	
	private int action;
	private static final int ADD_ACTION = 1;
	private static final int EDIT_ACTION = 2;

	@PostConstruct
	public void init(){
		userToEdit = new User();
		roles = roleBean.findAllById();
		selectedRole = "";
		action = ADD_ACTION;
		password = "";
		
		users = new LazyDataModel<User>(){
			private static final long serialVersionUID = 175674564345L;

			@Override
            public List<User> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
				Map<String,String> newMap = new HashMap<String,String>();
				for (Map.Entry<String, Object> entry : filters.entrySet()) {
				       if(entry.getValue() instanceof String){
				            newMap.put(entry.getKey(), (String) entry.getValue());
				          }
				 }
            	users.setRowCount(userBean.count(newMap));
                return userBean.getResultList(first, pageSize, sortField, sortOrder, newMap);
            }
		};
		
		users.setRowCount(userBean.count(new HashMap<String, String>()));
		
	}
	
	public void saveUser(){
		userToEdit.setUsername(userToEdit.getUsername().trim());
		if( userToEdit.getUsername().trim().equals("") ){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "El nombre de usuario no puede estar vacio", ""));
			return;
		}
		if( userToEdit.getEmail().trim().equals("") ){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "El correo electronico no puede estar vacio", ""));
			return;
		}
		switch(action){
		case ADD_ACTION:
			saveNewUser();
			break;
		case EDIT_ACTION:
			saveEditUser();
			break;
		default:
			break;
		}
		
		RequestContext.getCurrentInstance().update("usersForm:usersDataTable");
		RequestContext.getCurrentInstance().execute("PF('userEditionDialog').hide();");
	}
	
	public void saveNewUser(){
		if( password.trim().equals("") ){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "La contrasena no puede estar vacia", ""));
			return;
		}
		User lastUser = userBean.findUserByUsername(userToEdit.getUsername());
		if( lastUser != null ){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ya existe un usuario con ese nombre", ""));
			return;
		}
		lastUser = userBean.findByEmail(userToEdit.getEmail());
		if( lastUser != null ){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ya existe un usuario con ese correo", ""));
			return;
		}
		userToEdit.setPassword(password);
		for( Role role : roles ){
			if( role.getRoleName().equals(selectedRole) ){
				userToEdit.setRole(role);
				break;
			}
		}
		userBean.save(userToEdit);
	}
	
	public void saveEditUser(){
		User lastUser = userBean.findUserByUsername(userToEdit.getUsername());
		if( lastUser != null && !lastUser.getId().equals(userToEdit.getId()) ){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ya existe un usuario con ese nombre", ""));
			return;
		}
		lastUser = userBean.findByEmail(userToEdit.getEmail());
		if( lastUser != null && !lastUser.getId().equals(userToEdit.getId()) ){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ya existe un usuario con ese correo", ""));
			return;
		}
		if( !password.trim().equals("") ){
			userToEdit.setPassword(password);
		}
		userBean.update(userToEdit);
		for( Role role : roles ){
			if( role.getRoleName().equals(selectedRole) ){
				userToEdit.setRole(role);
				break;
			}
		}
		userBean.update(userToEdit);
	}
	
	public void addUser(){
		password = "";
		userToEdit = new User();
		selectedRole = "";
		action = ADD_ACTION;
		RequestContext.getCurrentInstance().update("usersForm:userEditionDialog");
		RequestContext.getCurrentInstance().execute("PF('userEditionDialog').show();");
	}
	
	public void editUser(User user){
		password = "";
		userToEdit = user;
		if( userToEdit.getRole() != null ){
			selectedRole = userToEdit.getRole().getRoleName();
		} else {
			selectedRole = "";
		}
		action = EDIT_ACTION;
		RequestContext.getCurrentInstance().update("usersForm:userEditionDialog");
		RequestContext.getCurrentInstance().execute("PF('userEditionDialog').show();");
	}
	
	public LazyDataModel<User> getUsers() {
		return users;
	}

	public void setUsers(LazyDataModel<User> users) {
		this.users = users;
	}

	public User getUserToEdit() {
		return userToEdit;
	}

	public void setUserToEdit(User userToEdit) {
		this.userToEdit = userToEdit;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public String getSelectedRole() {
		return selectedRole;
	}

	public void setSelectedRole(String role) {
		selectedRole = role;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
