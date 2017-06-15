package com.serinse.ejb.impl.administration;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.primefaces.model.SortOrder;

import com.serinse.ejb.AbstractBean;
import com.serinse.pers.dao.adm.DAOJPARole;
import com.serinse.pers.dao.adm.DAOJPAUser;
import com.serinse.pers.entity.adm.User;
import com.serinse.pers.entity.client.Client;

@Stateless
@LocalBean
public class UserBean extends AbstractBean<User> {

	@EJB
	private DAOJPAUser daojpaUser;
	
	@EJB
	private DAOJPARole daojpaRole;
	
	@PostConstruct
	public void init( ){
		super.init(daojpaUser);
	}

	public User findUserByUsername(String userName) {
		return daojpaUser.findUserByUsername(userName);
	}

	public boolean isUserNameUnique(User user) {
		User userFromDatabase = findUserByUsername(user.getUsername());
		if (userFromDatabase != null
				&& !userFromDatabase.getId().equals(user.getId())) {
			return false;
		}
		return true;
	}

	public User findUserById(Long id) {
		return daojpaUser.findById(id);
	}
	
	public User findByEmail(String email){
		return daojpaUser.findUserByEmail(email);
	}
	
	public List<User> findAllByRole(String rolename){
		return daojpaUser.findAllByRole(rolename);
	}
	
	public int count(Map<String, String> filters){
		return daojpaUser.count(filters);
	}
	
	public List<User> getResultList(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
    	List<User> users = daojpaUser.getResultList(first, pageSize, sortField, sortOrder, filters);

    	return users;
    }
	
	public List<User> getUsersByClient(String name){
		return daojpaUser.getUsersByClient(name);
	}
	
	public void deleteClientsUsers(String clientName){
		daojpaUser.deleteClientsUsers(clientName);
	}

	public void saveClientByUser(Client client, User user){
		daojpaUser.saveClientByUser(client, user);
	}

}
