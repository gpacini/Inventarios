package com.serinse.web.controllers.inventory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.serinse.common.ProjectParameterEnum;
import com.serinse.ejb.impl.inventory.DeliveryBean;
import com.serinse.ejb.impl.inventory.InventoryBean;
import com.serinse.ejb.impl.inventory.ProductBean;
import com.serinse.ejb.impl.inventory.ProductByStorehouseBean;
import com.serinse.ejb.impl.inventory.RequisitionBean;
import com.serinse.ejb.impl.inventory.StorehouseBean;
import com.serinse.ejb.impl.projectParameter.ProjectParameterBean;
import com.serinse.pers.entity.inventory.Delivery;
import com.serinse.pers.entity.inventory.DeliveryType;
import com.serinse.pers.entity.inventory.Inventory;
import com.serinse.pers.entity.inventory.Product;
import com.serinse.pers.entity.inventory.ProductByStorehouse;
import com.serinse.pers.entity.inventory.Requisition;
import com.serinse.pers.entity.inventory.Storehouse;
import com.serinse.pers.entity.projectParameter.ProjectParameter;
import com.serinse.web.session.UserSessionBean;

@Named("uploadRequisitionController")
@ViewScoped
public class UploadRequisitionController implements Serializable{

	private static final long serialVersionUID = 1710441087030165760L;
	
	@Inject
	InventoryBean inventoryBean;
	
	@Inject
	StorehouseBean storehouseBean;
	
	@Inject
	ProductByStorehouseBean productByStorehouseBean;
	
	@Inject
	DeliveryBean deliveryBean;
	
	@Inject
	ProjectParameterBean projectParameterBean;
	
	@Inject 
	RequisitionBean requisitionBean;
	
	@Inject
	ProductBean productBean;
	
	@Inject
	UserSessionBean userSessionBean;
	
	private DeliveryType selectedType;
	private Storehouse storehouse;
	private Inventory inventory;
	
	private Long storehouseId;
	
	private String whoAsked;
	private String whoReceived;
	private Date askDate;
	private String leadTime;
	private Date deliveryDate;
	private String deliveryPoint;
	private String cities;
	private String status;
	private String physicalRequisition;
	
	private Requisition requisition;
	
	private Boolean renderForm;
	private Boolean storehouseSelected;
	
	private List<ProductByStorehouse> productByStorehouseToSave;
	private List<Delivery> deliveriesToSave;
	
	private List<ProductWrapper> selectedProducts;
	private LazyDataModel<Product> products;
	
	
	public Boolean getRenderForm() {
		return renderForm;
	}

	public void setRenderForm(Boolean renderForm) {
		this.renderForm = renderForm;
	}

	@PostConstruct
	public void init( ){
		Map<String, String> parameterMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		Long invId = Long.parseLong(parameterMap.get("invId"));
		inventory = inventoryBean.findById(invId);
		renderForm = false;
		storehouseSelected = false;
		selectedProducts = new ArrayList<>();
		if( inventory != null )
			products = new LazyDataModel<Product> () {

			private static final long serialVersionUID = 82131782367821L;

			@Override
            public List<Product> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
				Map<String,String> newMap = new HashMap<String,String>();
				for (Map.Entry<String, Object> entry : filters.entrySet()) {
				       if(entry.getValue() instanceof String){
				            newMap.put(entry.getKey(), (String) entry.getValue());
				          }
				 }
            	products.setRowCount(productBean.count(newMap, inventory));
                return productBean.getResultList(first, pageSize, sortField, sortOrder, newMap, inventory);
            }
        };
        products.setRowCount(productBean.count(new HashMap<String, String> (), inventory));
	}
	
	public void save(){
		
		if( selectedProducts.size() == 0 ){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe seleccionar al menos 1 producto", ""));
		}
		
		productByStorehouseToSave = new ArrayList<>();
		deliveriesToSave = new ArrayList<>();
		for( ProductWrapper product : selectedProducts ){
			if( !addProductToSave(product) ) return;
			addDeliveryToSave(product);
		}
		
		requisition = new Requisition();
		ProjectParameter consecutive = null;
		int currentConsecutive;
		String sConsecutive = "";
		if( selectedType.equals(DeliveryType.DESPACHO)){
			consecutive = projectParameterBean.findProjectParameterByParemeterName(ProjectParameterEnum.REQUISITION_EGRESS_CONSECUTIVE);
			sConsecutive += "D";
		} else {
			consecutive = projectParameterBean.findProjectParameterByParemeterName(ProjectParameterEnum.REQUISITION_INGRESS_CONSECUTIVE);
			sConsecutive += "R";
		}
		
		currentConsecutive = Integer.parseInt(consecutive.getValue()) + 1;
		sConsecutive += String.format("%06d", currentConsecutive);
		
		requisition.setUser(userSessionBean.getUsername());
		requisition.setConsecutive(sConsecutive);
		requisition.setPhysicalRequisition(physicalRequisition);
		requisitionBean.save(requisition);
		
		for( Delivery delivery : deliveriesToSave ){
			delivery.setRequisition(requisition);
			delivery.setCreationDate(new Date());
			deliveryBean.save(delivery);
		}
		for( ProductByStorehouse pbs : productByStorehouseToSave ){
			if( pbs.getIsNew() ){
				productByStorehouseBean.save(pbs);
			} else {
				productByStorehouseBean.update(pbs);
			}
			pbs.getProduct().setActive(true);
			productBean.update(pbs.getProduct());
		}
		
		consecutive.setValue(currentConsecutive+"");
		projectParameterBean.updateProjectParameter(consecutive);
		
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Se creo la requisicion con codigo: " + sConsecutive, ""));
		selectedType = null;
		storehouse = null;
		renderForm = false;
		storehouseSelected = false;
		selectedProducts = new ArrayList<>();
		storehouseId = null;
		whoAsked = "";
		whoReceived = "";
		askDate = null;
		leadTime = "";
		deliveryDate = null;
		deliveryPoint = "";
		cities = "";
		status = "";
		physicalRequisition = null;
	}
	
	public String getPhysicalRequisition() {
		return physicalRequisition;
	}

	public void setPhysicalRequisition(String physicalRequisition) {
		this.physicalRequisition = physicalRequisition;
	}

	public void addSelectedProduct(Long id){
		Product product = productBean.findById(id);
		if( product == null ) return;
		selectedProducts.add(new 
				ProductWrapper(product, storehouse));
	}
	
	private void addDeliveryToSave(ProductWrapper product){
		Delivery delivery = new Delivery();
		delivery.setAskDate(askDate);
		delivery.setCities(cities == null ? "" : cities);
		delivery.setDeliveryDate(deliveryDate == null ? new Date() : deliveryDate);
		delivery.setDeliveryPoint(deliveryPoint == null ? "" : deliveryPoint);
		delivery.setDeliveryType(selectedType);
		delivery.setLeadTime(leadTime == null ? "" : leadTime);
		delivery.setProduct(product.product);
		delivery.setStatus(status == null ? "" : status);
		delivery.setQuantity(new Double(product.quantity));
		delivery.setStorehouse(storehouse);
		delivery.setWhoAsked(whoAsked);
		delivery.setWhoReceived(whoReceived);
		deliveriesToSave.add(delivery);
	}
	
	private boolean addProductToSave(ProductWrapper product){
		if( product.getQuantity() == null ){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, 
					"Tiene que ingresar la cantidad para todos los productos.", ""));
			return false;
		}
		ProductByStorehouse psh = productByStorehouseBean.findByProductAndStorehouse(product.id, storehouse.getName());
		double deliveryQuantity = product.getQuantity();
		if( selectedType.equals(DeliveryType.DESPACHO) ){
			deliveryQuantity = -1 * deliveryQuantity;
		}
		double newQuantity = 0;
		if (psh != null) {
			newQuantity += psh.getQuantity() + deliveryQuantity;
			if (newQuantity < 0) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, 
						"El producto: " + product.name +" no tiene suficiente cantidad en bodega.", ""));
				return false;
			}
			psh.setQuantity(newQuantity);
		} else {
			newQuantity += deliveryQuantity;
			if (newQuantity < 0) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, 
						"El producto: " + product.name +" no tiene suficiente cantidad en bodega.", ""));
				return false;
			}
			psh = new ProductByStorehouse();
			psh.setProduct(product.product);
			psh.setStorehouse(storehouse);
			psh.setQuantity(newQuantity);
			psh.setIsNew(true);
		}
		productByStorehouseToSave.add(psh);
		return true;
	}
	
	public List<ProductWrapper> getSelectedProducts() {
		return selectedProducts;
	}

	public void setSelectedProducts(List<ProductWrapper> selectedProducts) {
		this.selectedProducts = selectedProducts;
	}

	public void removeSelectedProduct(Long id){
		ProductWrapper productToRemove = null;
		for( ProductWrapper product : selectedProducts ){
			if( product.id.equals(id) ){
				productToRemove = product;
			}
		}
		if( productToRemove != null ){
			selectedProducts.remove(productToRemove);
		}
	}
	
	public Boolean productIsSelected(Long id){
		for( ProductWrapper product : selectedProducts ){
			if( product.id.equals(id) ){
				return true;
			}
		}
		return false;
	}
	
	private void recalculateProductQuantities(){
		if( selectedProducts.size() > 0){
			for( ProductWrapper product : selectedProducts ){
				ProductByStorehouse pbs = productByStorehouseBean.findByProductAndStorehouse(product.getId(), storehouse.getName());
				if( pbs == null ){
					product.setCurrentQuantity(0);
				} else {
					product.setCurrentQuantity(pbs.getQuantity().intValue());
				}
			}
		}
	}

	public void setStorehouseId(Long storehouseId) {
		this.storehouseId = storehouseId;
		storehouse = storehouseBean.findById(storehouseId);
		storehouseSelected = true;
		recalculateProductQuantities();
	}
	
	public SelectItem[] getStorehouseSelectItems(){
		List<Storehouse> storehouses = storehouseBean.findAllById();
		SelectItem[] items = new SelectItem[storehouses.size()];
		for( int i = 0; i < storehouses.size(); i++ ){
			Storehouse s = storehouses.get(i);
			SelectItem item = new SelectItem(s.getId()+"", s.getName());
			items[i] = item;
		}
		return items;
	}

	public SelectItem[] getDeliverySelectItems(){
		SelectItem[] items = new SelectItem[2];
		SelectItem item1 = new SelectItem(DeliveryType.DESPACHO, "Salida");
		SelectItem item2 = new SelectItem(DeliveryType.ENTREGA, "Ingreso");
		items[0] = item1;
		items[1] = item2;
		return items;
	}
	
	public String getLabelFor(String fieldName){	
		if( !renderForm && !storehouseSelected ) return "";
		switch(fieldName){
		case "whoAsked":
			if( selectedType.equals(DeliveryType.DESPACHO) ){
				return "Quien Solicito?";
			} else {
				return "Quien Envio?";
			}
		case "whoReceived":
			if( selectedType.equals(DeliveryType.DESPACHO) ){
				return "Despachador";
			} else {
				return "Quien Recibio?";
			}
		case "askDate":
			if( selectedType.equals(DeliveryType.DESPACHO) ){
				return "Fecha de Solicitud";
			} else {
				return "Fecha de Ingreso";
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
	
	public boolean getRenderFor(String fieldName){
		if( !renderForm && !storehouseSelected ) return false;
		switch(fieldName){
		case "whoAsked":
			return true;
		case "whoReceived":
			return true;
		case "askDate":
			return true;
		case "leadTime":
			return selectedType.equals(DeliveryType.DESPACHO);
		case "deliveryDate":
			return selectedType.equals(DeliveryType.DESPACHO);
		case "deliveryPoint":
			return selectedType.equals(DeliveryType.DESPACHO);
		case "cities":
			return selectedType.equals(DeliveryType.DESPACHO);
		case "status":
			return selectedType.equals(DeliveryType.DESPACHO);
		default:
			break;
		}
		
		return false;
	}
	
	public Boolean getStorehouseSelected() {
		return storehouseSelected;
	}

	public void setStorehouseSelected(Boolean storehouseSelected) {
		this.storehouseSelected = storehouseSelected;
	}

	public Storehouse getStorehouse() {
		return storehouse;
	}

	public void setStorehouse(Storehouse storehouse) {
		this.storehouse = storehouse;
	}

	public Long getStorehouseId() {
		return storehouseId;
	}
	
	public DeliveryType getSelectedType() {
		return selectedType;
	}

	public void setSelectedType(DeliveryType selectedType) {
		this.selectedType = selectedType;
		renderForm = true;
	}
	
	public Inventory getInventory() {
		return inventory;
	}
	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}
	
	public LazyDataModel<Product> getProducts() {
		return products;
	}

	public void setProducts(LazyDataModel<Product> products) {
		this.products = products;
	}

	public String getWhoAsked() {
		return whoAsked;
	}
	public void setWhoAsked(String whoAsked) {
		this.whoAsked = whoAsked;
	}
	public String getWhoReceived() {
		return whoReceived;
	}
	public void setWhoReceived(String whoReceived) {
		this.whoReceived = whoReceived;
	}
	public Date getAskDate() {
		return askDate;
	}
	public void setAskDate(Date askDate) {
		this.askDate = askDate;
	}
	public String getLeadTime() {
		return leadTime;
	}
	public void setLeadTime(String leadTime) {
		this.leadTime = leadTime;
	}
	public Date getDeliveryDate() {
		return deliveryDate;
	}
	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
	public String getDeliveryPoint() {
		return deliveryPoint;
	}
	public void setDeliveryPoint(String deliveryPoint) {
		this.deliveryPoint = deliveryPoint;
	}
	public String getCities() {
		return cities;
	}
	public void setCities(String cities) {
		this.cities = cities;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public class ProductWrapper {
		private Long id;
		private String name;
		private String code;
		private Integer currentQuantity;
		private Integer quantity;
		private Product product;
		
		public ProductWrapper(Product product, Storehouse selectedStorehouse ){
			this.id = product.getId();
			this.name = product.getMaterial();
			this.code = product.getCode();
			this.currentQuantity = product.getQuantities().get(selectedStorehouse).getQuantity().intValue();
			this.product = product;
		}
		
		public void changeStorehouse(Storehouse sh){
			currentQuantity = product.getQuantities().get(sh).getQuantity().intValue();
		}
		
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public Integer getCurrentQuantity() {
			return currentQuantity;
		}
		public void setCurrentQuantity(Integer currentQuantity) {
			this.currentQuantity = currentQuantity;
		}
		public Integer getQuantity() {
			return quantity;
		}
		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}
	}

}
