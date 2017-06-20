package com.serinse.web.controllers.inventory;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.SortOrder;

import com.serinse.ejb.impl.inventory.DeliveryBean;
import com.serinse.ejb.impl.inventory.InventoryBean;
import com.serinse.ejb.impl.inventory.ProductBean;
import com.serinse.pers.entity.adm.Role;
import com.serinse.pers.entity.inventory.Delivery;
import com.serinse.pers.entity.inventory.DeliveryType;
import com.serinse.pers.entity.inventory.Inventory;
import com.serinse.pers.entity.inventory.Product;
import com.serinse.web.inventory.helpers.DeliveriesDataModel;
import com.serinse.web.session.UserSessionBean;

@Named
@ViewScoped
public class DeliveriesController implements Serializable{

	private static final long serialVersionUID = 127546234572L;

	@Inject DeliveryBean deliveryBean;
	@Inject UserSessionBean userSessionBean;
	@Inject InventoryBean inventoryBean;
	@Inject ProductBean productBean;
	
	private DeliveriesDataModel outDeliveries;
	private DeliveriesDataModel inDeliveries;
	
	private Delivery deliveryToEdit;
	private Inventory inventory;
	
	private Date outDateFilter;
	private Date inDateFilter;
	
	@PostConstruct
	public void init(){
		Long inventoryId = -1L;
		Long productId = -1L;
		try {
			String sInventoryId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
					.get("invId");
			if (sInventoryId == null) {
				System.out.println("No hay inventario seleccionado");
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "No hay ningun inventario seleccionado", ""));
				return;
			}
			inventoryId = Long.parseLong(sInventoryId);
			inventory = inventoryBean.findById(inventoryId);
			if (inventory == null) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "No existe el inventario seleccionado", ""));
				return;
			}
			if (!userSessionBean.userHasClientAccess(inventory.getClient())) {
				FacesContext.getCurrentInstance().getExternalContext().redirect("/serinse/error/permissionDenied.jsf");
				return;
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "El id seleccionado es incorrecto", ""));
			return;
		}
		try{
			String sProductId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
					.get("prodId");
			if( sProductId != null ){
				productId = Long.parseLong(sProductId);
			}
		} catch(Exception e){
			//Do nothing
		}

		HashMap<String, String> newMap = new HashMap<>();
		if( !productId.equals(-1L) ){
			Product product = productBean.findById(productId);
			if( product != null ){
				newMap.put("product", product.getId()+"");
			}
		}
		newMap.put("inventory", inventoryId+"");
		newMap.put("deliveryType", DeliveryType.DESPACHO.getName());
		
		outDeliveries = new DeliveriesDataModel(this, newMap);
		outDeliveries.setRowCount(deliveryBean.count(newMap));
		
		@SuppressWarnings("unchecked")
		HashMap<String, String> newMap2 = (HashMap<String, String>) newMap.clone();
		newMap2.put("deliveryType", DeliveryType.ENTREGA.getName());
		inDeliveries = new DeliveriesDataModel(this, newMap2);
		inDeliveries.setRowCount(deliveryBean.count(newMap2));
	}
	
	public boolean hasEditPermission(){
		Set<String> roles = new HashSet<>();
		roles.add(Role.ADMIN_ROLE);
		roles.add(Role.CHIEF_ROLE);
		roles.add(Role.PROGRAMMING_ROLE);
		return userSessionBean.hasAnyRole(roles);
	}
	
	public boolean hasAdminPermission(){
		Set<String> roles = new HashSet<>();
		roles.add(Role.ADMIN_ROLE);
		roles.add(Role.PROGRAMMING_ROLE);
		return userSessionBean.hasAnyRole(roles);
	}
	
	public int getDaysInStorehouse(Delivery delivery){
		Long time = delivery.getAskDate().getTime() - (new Date()).getTime();
		time *= time < 0 ? -1 : 1;
		Long days = TimeUnit.DAYS.convert(time, TimeUnit.MILLISECONDS);
		return days.intValue();
	}
	
	public void getInventoryLink() {
		try {
			FacesContext.getCurrentInstance().getExternalContext()
					.redirect("products.jsf?id=" + inventory.getId());
		} catch (IOException e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "No se pudo abrir el inventario", ""));
		}
	}
	
	public DeliveriesDataModel getOutDeliveries() {
		return outDeliveries;
	}
	public void setOutDeliveries(DeliveriesDataModel outDeliveries) {
		this.outDeliveries = outDeliveries;
	}
	public DeliveriesDataModel getInDeliveries() {
		return inDeliveries;
	}
	public void setInDeliveries(DeliveriesDataModel inDeliveries) {
		this.inDeliveries = inDeliveries;
	}

	public Delivery getDeliveryToEdit() {
		return deliveryToEdit;
	}

	public void setDeliveryToEdit(Delivery deliveryToEdit) {
		this.deliveryToEdit = deliveryToEdit;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	public Date getOutDateFilter() {
		return outDateFilter;
	}

	public void setOutDateFilter(Date outDateFilter) {
		this.outDateFilter = outDateFilter;
	}

	public Date getInDateFilter() {
		return inDateFilter;
	}

	public void setInDateFilter(Date inDateFilter) {
		this.inDateFilter = inDateFilter;
	}

	public int count(Map<String, String> newMap) {
		return deliveryBean.count(newMap);
	}

	public List<Delivery> getResultList(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, String> newMap) {
		return deliveryBean.getResultList(first, pageSize, sortField, sortOrder, newMap);
	}
	
}
