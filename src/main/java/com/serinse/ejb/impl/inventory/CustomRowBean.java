package com.serinse.ejb.impl.inventory;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.serinse.ejb.AbstractBean;
import com.serinse.pers.dao.inventory.DAOJPACustomRowByClient;
import com.serinse.pers.entity.client.Client;
import com.serinse.pers.entity.inventory.customRow.CustomRowByClient;

@Stateless
@LocalBean
public class CustomRowBean extends AbstractBean<CustomRowByClient>{
	
	
	@EJB
	DAOJPACustomRowByClient daojpaCustomRowByClient;
	
	public CustomRowBean(){
		super.init(daojpaCustomRowByClient);
	}
	
	public boolean exists(Client client, int rowId){
		return daojpaCustomRowByClient.getCustomRowByRowIdAndClient(rowId, client) != null;
	}
	
	public List<CustomRowByClient> findByClient(Client client){
		return daojpaCustomRowByClient.findByClient(client);
	}
}
