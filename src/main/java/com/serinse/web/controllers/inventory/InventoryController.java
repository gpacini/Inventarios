package com.serinse.web.controllers.inventory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.serinse.ejb.impl.inventory.InventoryBean;
import com.serinse.pers.entity.inventory.Inventory;
import com.serinse.web.session.UserSessionBean;

@Named
@ViewScoped
public class InventoryController implements Serializable{

	private static final long serialVersionUID = 1192388921346L;

	@Inject InventoryBean inventoryBean;
	@Inject UserSessionBean userSessionBean;
	
	private List<Inventory> inventories;
	
	@PostConstruct
	public void init(){
		List<Inventory> tempList = inventoryBean.findAllByName();
		inventories = new ArrayList<>();
		for(Inventory inv : tempList ){
			if( userSessionBean.userHasClientAccess(inv.getClient())){
				inventories.add(inv);
			}
		}
	}
	
	public String getInventoryLink(Inventory inv){
		return "/serinse/content/products.jsf?id=" + inv.getId();
	}

	public List<Inventory> getInventories() {
		return inventories;
	}

	public void setInventories(List<Inventory> inventories) {
		this.inventories = inventories;
	}
}
