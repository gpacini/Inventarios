package com.serinse.ejb.impl.inventory;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.serinse.ejb.AbstractBean;
import com.serinse.pers.dao.inventory.DAOJPAInventory;
import com.serinse.pers.entity.inventory.Inventory;

@Stateless
@LocalBean
public class InventoryBean extends AbstractBean<Inventory> {

	@EJB
	private DAOJPAInventory daojpaInventory;
	
	@PostConstruct
	public void init(){
		super.init(daojpaInventory);
	}
	
	public Inventory findByClientId(Long id){
		return daojpaInventory.findByClientId(id);
	}
	
	public List<Inventory> findAllByName(){
		return daojpaInventory.findAllByName();
	}
	
}
