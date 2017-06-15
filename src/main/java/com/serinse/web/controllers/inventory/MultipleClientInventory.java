package com.serinse.web.controllers.inventory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.serinse.ejb.impl.inventory.InventoryBean;
import com.serinse.ejb.impl.inventory.ProductBean;
import com.serinse.ejb.impl.inventory.ProductByStorehouseBean;
import com.serinse.ejb.impl.inventory.StorehouseBean;
import com.serinse.pers.entity.inventory.Inventory;
import com.serinse.pers.entity.inventory.Product;
import com.serinse.pers.entity.inventory.ProductByStorehouse;
import com.serinse.pers.entity.inventory.Storehouse;

@Named
@ViewScoped
public class MultipleClientInventory implements Serializable{

	private static final long serialVersionUID = 2529609615094982541L;
	
	public static final long ALL_STORHOUSES = -1L;
	
	@Inject InventoryBean inventoryBean;
	@Inject ProductBean productBean;
	@Inject StorehouseBean storehouseBean;
	@Inject ProductByStorehouseBean productByStorehouseBean;
	
	private List<String> selectedInventories;
	private List<Storehouse> selectedStorehouses;
	private List<SelectItem> inventories;
	private List<SelectItem> storehouses;
	private List<Product> products;
	private Long selectedStorehouse;
	private boolean displayDatatable;
	
	@PostConstruct
	public void init(){
		displayDatatable = false;
		inventories = new ArrayList<>();
		List<Inventory> items = inventoryBean.findAllById();
		for( Inventory inventory : items ){
			SelectItem item = new SelectItem(inventory.getId(), inventory.getClient().getName());
			inventories.add(item);
		}
		List<Storehouse> lStorehouses = storehouseBean.findAllById();
		storehouses = new ArrayList<>();
		storehouses.add(new SelectItem(ALL_STORHOUSES, "Todas"));
		for( Storehouse sh : lStorehouses ){
			SelectItem item = new SelectItem(sh.getId(), sh.getName());
			storehouses.add(item);
		}
	}
	
	public void search(){
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Por favor espere mientras se carga la tabla", ""));
		products = new ArrayList<>();
		selectedStorehouses = new ArrayList<>();
		for( String sInvId : selectedInventories ){
			Long invId = Long.parseLong(sInvId);
			products.addAll(productBean.findByInventoryId(invId));
		}
		Storehouse storehouse = null;
		if( selectedStorehouse != ALL_STORHOUSES ){
			storehouse = storehouseBean.findById(selectedStorehouse);
			selectedStorehouses.add(storehouse);
		} else {
			selectedStorehouses = storehouseBean.findAllById();
		}
		displayDatatable = true;
	}

	public int getQuantityFor(Storehouse sh, Product p) {
		ProductByStorehouse pbs = p.getQuantities().get(sh);
		return pbs.getQuantity().intValue();
	}
	
	public List<SelectItem> getStorehouses(){
		return storehouses;
	}
	
	public List<SelectItem> getInventories(){
		return inventories;
	}

	public List<String> getSelectedInventories() {
		return selectedInventories;
	}

	public void setSelectedInventories(List<String> selectedInventories) {
		this.selectedInventories = selectedInventories;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	public boolean isDisplayDatatable() {
		return displayDatatable;
	}

	public void setDisplayDatatable(boolean displayDatatable) {
		this.displayDatatable = displayDatatable;
	}

	public Long getSelectedStorehouse() {
		return selectedStorehouse;
	}

	public void setSelectedStorehouse(Long selectedStorehouse) {
		this.selectedStorehouse = selectedStorehouse;
	}

	public List<Storehouse> getSelectedStorehouses() {
		return selectedStorehouses;
	}

	public void setSelectedStorehouses(List<Storehouse> selectedStorehouses) {
		this.selectedStorehouses = selectedStorehouses;
	}

}
