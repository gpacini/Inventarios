package com.serinse.web.controllers.inventory;

import java.io.Serializable;
import java.util.ArrayList;
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

import com.serinse.ejb.impl.inventory.DeliveryBean;
import com.serinse.ejb.impl.inventory.InventoryBean;
import com.serinse.ejb.impl.inventory.RequisitionBean;
import com.serinse.ejb.impl.inventory.StorehouseBean;
import com.serinse.pers.entity.inventory.Delivery;
import com.serinse.pers.entity.inventory.DeliveryType;
import com.serinse.pers.entity.inventory.Inventory;
import com.serinse.pers.entity.inventory.Requisition;
import com.serinse.pers.entity.inventory.Storehouse;
import com.serinse.web.session.UserSessionBean;

@Named("searchRequisitionController")
@ViewScoped
public class SearchRequisitionController implements Serializable {

	private static final long serialVersionUID = 7901538820102450792L;

	@Inject
	RequisitionBean requisitionBean;

	@Inject
	DeliveryBean deliveryBean;
	@Inject
	InventoryBean inventoryBean;
	@Inject
	StorehouseBean storehouseBean;

	@Inject
	UserSessionBean userSessionBean;

	private List<Delivery> deliveries;
	private Requisition requisition;
	private Delivery firstDelivery;

	private String requisitionCode;

	private Boolean requisitionFound;

	private List<Inventory> inventories;
	private List<Storehouse> storehouses;
	private List<DeliveryType> deliveryTypes;

	private Long selectedInventory;
	private String selectedStorehouse;
	private String selectedDeliveryType;

	private LazyDataModel<Delivery> requisitionsFound;
	private Boolean renderRequisitionList;

	@PostConstruct
	public void init() {
		reset();
		List<Inventory> tempInventories = inventoryBean.findAllById();
		inventories = new ArrayList<>();
		for (Inventory inventory : tempInventories) {
			if (userSessionBean.userHasClientAccess(inventory.getClient())) {
				inventories.add(inventory);
			}
		}

		storehouses = storehouseBean.findAllById();
		deliveryTypes = new ArrayList<>();
		deliveryTypes.add(DeliveryType.DESPACHO);
		deliveryTypes.add(DeliveryType.ENTREGA);
	}
	
	public void reset(){
		deliveries = new ArrayList<>();
		requisition = null;
		firstDelivery = null;
		requisitionCode = null;
		selectedInventory = null;
		selectedStorehouse = null;
		selectedDeliveryType = null;
		requisitionsFound = null;
		requisitionFound = false;
		renderRequisitionList = false;
	}
	
	public void deliverySelected(){
		requisitionFound = true;
		requisition = firstDelivery.getRequisition();
		deliveries = deliveryBean.findByRequisitionId(requisition.getId());
		requisitionCode = requisition.getConsecutive();
		
		RequestContext.getCurrentInstance().update("requisitionForm:requisitionDialog");
		RequestContext.getCurrentInstance().execute("PF('requisitionDialog').show();");
	}

	public void searchRequisitionByData() {

		renderRequisitionList = false;
		requisitionFound = false;
		
		if (selectedInventory == null) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe seleccionar un inventario", ""));
			return;
		}

		Map<String, String> filters = new HashMap<String, String>();
		filters.put("inventory", selectedInventory + "");
		if (selectedStorehouse != null && !selectedStorehouse.equals("")) {
			filters.put("storehouse", selectedStorehouse);
		}
		if (selectedDeliveryType != null &&  !selectedDeliveryType.equals("")) {
			filters.put("deliveryType", selectedDeliveryType);
		}
		requisitionsFound = new LazyDataModel<Delivery>() {
			
			private static final long serialVersionUID = 1826371263L;
			Map<String, String> nFilters = filters;

			@Override
			public List<Delivery> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				requisitionsFound.setRowCount(requisitionBean.count(nFilters));
				return requisitionBean.getResultList(first, pageSize, sortField, sortOrder, nFilters);
			}
			
			@Override
			public Delivery getRowData(String rowKey){
				@SuppressWarnings("unchecked")
				List<Delivery> deliveries = (List<Delivery>) getWrappedData();
			    Long value = Long.valueOf(rowKey);

			    for (Delivery delivery : deliveries) {
			        if (delivery.getId().equals(value)) {
			            return delivery;
			        }
			    }

			    return null;
			}
			
			@Override
			public Object getRowKey(Delivery delivery) {
			    return delivery != null ? delivery.getId() : null;
			}
		};
		requisitionsFound.setRowCount(requisitionBean.count(filters));
		
		renderRequisitionList = true;
		
		System.out.println("Numero de requisiciones encontradas: " + requisitionsFound.getRowCount());
		
		RequestContext.getCurrentInstance().update("requisitionForm:requisitionDataTable");
	}

	public void searchRequisition() {
		if (requisitionCode == null || requisitionCode.trim().equals("")) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe Ingresar un numero de requisicion", ""));
			return;
		}
		requisition = requisitionBean.findByCode(requisitionCode);
		if (requisition == null)
			requisition = requisitionBean.findByPhysicalRequisition(requisitionCode);
		if (requisition == null) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "No existe la requisicion buscada", ""));
			return;
		}
		deliveries = deliveryBean.findByRequisitionId(requisition.getId());
		if (deliveries.size() == 0) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "La requisicion esta vacia", ""));
			return;
		}
		firstDelivery = deliveries.get(0);

		if (userSessionBean.userHasClientAccess(firstDelivery.getProduct().getInventory().getClient())) {
			requisitionFound = true;

			RequestContext.getCurrentInstance().update("requisitionForm:requisitionDialog");
			RequestContext.getCurrentInstance().execute("PF('requisitionDialog').show();");
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"No tiene permiso para acceder a esta requisicion", ""));
		}
	}

	public String getLabelFor(String fieldName) {
		switch (fieldName) {
		case "whoAsked":
			if (firstDelivery.getDeliveryType().equals(DeliveryType.DESPACHO)) {
				return "Quien Solicito?";
			} else {
				return "Quien Envio?";
			}
		case "whoReceived":
			if (firstDelivery.getDeliveryType().equals(DeliveryType.DESPACHO)) {
				return "Despachador";
			} else {
				return "Quien Recibio?";
			}
		case "askDate":
			if (firstDelivery.getDeliveryType().equals(DeliveryType.DESPACHO)) {
				return "Fecha de Solicitud";
			} else {
				return "Fecha de Recibo";
			}
		case "leadTime":
			return "Lead Time";
		case "deliveryDate":
			return "Fecha de Envio";
		case "deliveryPoint":
			return "Punto de Entrega";
		case "cities":
			return "Ciudad de Entrega";
		case "status":
			return "Estado";
		default:
			break;
		}

		return "";
	}

	public boolean getRenderFor(String fieldName) {
		switch (fieldName) {
		case "whoAsked":
			return true;
		case "whoReceived":
			return true;
		case "askDate":
			return true;
		case "leadTime":
			return firstDelivery.getDeliveryType().equals(DeliveryType.DESPACHO);
		case "deliveryDate":
			return firstDelivery.getDeliveryType().equals(DeliveryType.DESPACHO);
		case "deliveryPoint":
			return firstDelivery.getDeliveryType().equals(DeliveryType.DESPACHO);
		case "cities":
			return firstDelivery.getDeliveryType().equals(DeliveryType.DESPACHO);
		case "status":
			return firstDelivery.getDeliveryType().equals(DeliveryType.DESPACHO);
		default:
			break;
		}

		return false;
	}

	public Delivery getFirstDelivery() {
		return firstDelivery;
	}

	public void setFirstDelivery(Delivery firstDelivery) {
		this.firstDelivery = firstDelivery;
	}

	public Boolean getRequisitionFound() {
		return requisitionFound;
	}

	public void setRequisitionFound(Boolean requisitionFound) {
		this.requisitionFound = requisitionFound;
	}

	public List<Delivery> getDeliveries() {
		return deliveries;
	}

	public void setDeliveries(List<Delivery> deliveries) {
		this.deliveries = deliveries;
	}

	public Requisition getRequisition() {
		return requisition;
	}

	public void setRequisition(Requisition requisition) {
		this.requisition = requisition;
	}

	public String getRequisitionCode() {
		return requisitionCode;
	}

	public void setRequisitionCode(String requisitionCode) {
		this.requisitionCode = requisitionCode;
	}

	public List<Inventory> getInventories() {
		return inventories;
	}

	public void setInventories(List<Inventory> inventories) {
		this.inventories = inventories;
	}

	public List<Storehouse> getStorehouses() {
		return storehouses;
	}

	public void setStorehouses(List<Storehouse> storehouses) {
		this.storehouses = storehouses;
	}

	public List<DeliveryType> getDeliveryTypes() {
		return deliveryTypes;
	}

	public void setDeliveryTypes(List<DeliveryType> deliveryTypes) {
		this.deliveryTypes = deliveryTypes;
	}

	public Long getSelectedInventory() {
		return selectedInventory;
	}

	public void setSelectedInventory(Long selectedInventory) {
		this.selectedInventory = selectedInventory;
	}

	public String getSelectedStorehouse() {
		return selectedStorehouse;
	}

	public void setSelectedStorehouse(String selectedStorehouse) {
		this.selectedStorehouse = selectedStorehouse;
	}

	public String getSelectedDeliveryType() {
		return selectedDeliveryType;
	}

	public void setSelectedDeliveryType(String selectedDeliveryType) {
		this.selectedDeliveryType = selectedDeliveryType;
	}

	public LazyDataModel<Delivery> getRequisitionsFound() {
		return requisitionsFound;
	}

	public void setRequisitionsFound(LazyDataModel<Delivery> requisitionsFound) {
		this.requisitionsFound = requisitionsFound;
	}

	public Boolean getRenderRequisitionList() {
		return renderRequisitionList;
	}

	public void setRenderRequisitionList(Boolean renderRequisitionList) {
		this.renderRequisitionList = renderRequisitionList;
	}

}
