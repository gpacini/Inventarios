package com.serinse.ejb.impl.client;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.primefaces.model.SortOrder;

import com.serinse.ejb.AbstractBean;
import com.serinse.pers.dao.client.DAOJPAClient;
import com.serinse.pers.entity.adm.User;
import com.serinse.pers.entity.client.Client;

@Stateless
@LocalBean
public class ClientBean extends AbstractBean<Client> {

	@EJB
	private DAOJPAClient daojpaClient;
	
	@PostConstruct
	public void init(){
		super.init(daojpaClient);
	}

	public int count(Map<String, String> filters){
		return daojpaClient.count(filters);
	}
	
	public List<Client> getResultList(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
    	return daojpaClient.getResultList(first, pageSize, sortField, sortOrder, filters);
    }
	
	public List<Client> getClientsByUsername(String username){
		return daojpaClient.getClientsByUsername(username);
	}
	
	public void saveClientByUser(Client client, User user){
		daojpaClient.saveClientByUser(client, user);
	}
	
}
