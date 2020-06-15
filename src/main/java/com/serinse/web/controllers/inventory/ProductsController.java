package com.serinse.web.controllers.inventory;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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

import com.serinse.common.Constants;
import com.serinse.common.FileHelpers;
import com.serinse.ejb.impl.general.PhotoBean;
import com.serinse.ejb.impl.inventory.DeliveryBean;
import com.serinse.ejb.impl.inventory.InventoryBean;
import com.serinse.ejb.impl.inventory.ProductBean;
import com.serinse.ejb.impl.inventory.ProductByStorehouseBean;
import com.serinse.ejb.impl.inventory.StorehouseBean;
import com.serinse.ejb.impl.inventory.UpdateLogBean;
import com.serinse.pers.entity.adm.Role;
import com.serinse.pers.entity.general.Photo;
import com.serinse.pers.entity.inventory.Delivery;
import com.serinse.pers.entity.inventory.Inventory;
import com.serinse.pers.entity.inventory.Product;
import com.serinse.pers.entity.inventory.ProductByStorehouse;
import com.serinse.pers.entity.inventory.Storehouse;
import com.serinse.pers.entity.inventory.UpdateLog;
import com.serinse.web.session.UserSessionBean;

@Named("productsController")
@ViewScoped
public class ProductsController implements Serializable {

	private static final long serialVersionUID = -4414186728286515484L;

	@Inject
	InventoryBean inventoryBean;

	@Inject
	ProductByStorehouseBean productByStorehouseBean;

	@Inject
	StorehouseBean storehouseBean;

	@Inject
	UserSessionBean userSessionBean;

	@Inject
	PhotoBean photoBean;

	@Inject
	DeliveryBean deliveryBean;

	@Inject
	ProductBean productBean;

	@Inject
	UpdateLogBean updateLogBean;

	private Inventory inventory;
	private LazyDataModel<Product> products;
	private List<Product> filteredProducts;
	
	private List<Storehouse> storehouses;
	
	private List<String> brands;
	private List<String> categories;

	private Product productToEdit;
	private String changeReason;
	
	private Boolean showProductDetail;
	private Boolean newProduct;

	@PostConstruct
	public void init() {
		Long inventoryId = -1L;
		try {
			String sInventoryId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
					.get("id");
			if (sInventoryId == null) {
				FacesContext.getCurrentInstance().getExternalContext().redirect(userSessionBean.getHomeLink());
				return;
			}
			inventoryId = Long.parseLong(sInventoryId);
			inventory = inventoryBean.findById(inventoryId);
			if (inventory == null) {
				FacesContext.getCurrentInstance().getExternalContext().redirect(userSessionBean.getHomeLink());
				return;
			}
			if (!userSessionBean.userHasClientAccess(inventory.getClient())) {
				FacesContext.getCurrentInstance().getExternalContext().redirect("/serinse/error/permissionDenied.jsf");
				return;
			}
		} catch (Exception e) {
			showErrorMessage("El id seleccionado es incorrecto");
			return;
		}
		products = new LazyDataModel<Product>() {

			private static final long serialVersionUID = 175674564345L;

			@Override
			public List<Product> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				filters.put("not_available", "true");
				if( userSessionBean.getUserRole().getRoleName().equals(Role.CLIENT_ROLE) ){
					filters.put("active_item", "true");
				}
				products.setRowCount(productBean.count(filters, inventory));
				return productBean.getResultList(first, pageSize, sortField, sortOrder, filters, inventory);
			}
			
			@Override
			public Product getRowData(String rowKey){
				@SuppressWarnings("unchecked")
				List<Product> products = (List<Product>) getWrappedData();
			    Long value = Long.valueOf(rowKey);

			    for (Product product : products) {
			        if (product.getId().equals(value)) {
			            return product;
			        }
			    }

			    return null;
			}
			
			@Override
			public Object getRowKey(Product product) {
			    return product != null ? product.getId() : null;
			}
		};
		products.setRowCount(productBean.count(new HashMap<String, Object>(), inventory));
		storehouses = storehouseBean.findAllById();
		showProductDetail = false;
		brands = productBean.getBrandsListByInventory(inventory);
		categories = productBean.getCategoriesListByInventory(inventory);
	}
	
	public void save(){
		productToEdit.setBrand(productToEdit.getBrand().trim());
		productToEdit.setCode(productToEdit.getCode().trim());
		productToEdit.setMaterial(productToEdit.getMaterial().trim());
		if( newProduct ){
			Product old = productBean.findByCode(productToEdit.getCode());
			if( old != null ){
				showErrorMessage("Ya existe un producto con ese codigo.");
				return;
			}
		} else {
			Product old = productBean.findByCode(productToEdit.getCode());
			if( old != null && !old.getId().equals(productToEdit.getId()) ){
				showErrorMessage("Ya existe un producto con ese codigo.");
				return;
			}
		}
		if( FileHelpers.isStringEmptyOrNull(productToEdit.getCode()) 
				|| FileHelpers.isStringEmptyOrNull(productToEdit.getBrand()) 
				|| FileHelpers.isStringEmptyOrNull(productToEdit.getMaterial()) ){
			showErrorMessage("El codigo, marca o material no pueden estar en blanco.");
			return;
		}
		if( !newProduct && FileHelpers.isStringEmptyOrNull(changeReason) ){
			showErrorMessage("La razon de cambio no puede estar en blanco");
			return;
		}
		
		if( newProduct ){
			productToEdit.setInventory(inventory);
			productToEdit.setActive(false);
			if( productToEdit.getId() == null ){
				productBean.save(productToEdit);
			}
			else{
				productBean.update(productToEdit);
			}
		} else {
			Product old = productBean.findById(productToEdit.getId());
			for( ProductByStorehouse pbs : productToEdit.getQuantities() ){

				if( pbs.getQuantity() == null ){
					pbs.setQuantity(0.0);
				}

				UpdateLog log = new UpdateLog();
				log.setAction("Actualizacion");
				log.setCode(productToEdit.getCode());
				log.setDate(new Date());
				log.setReason(changeReason);
				log.setUser(userSessionBean.getUsername());
				log.setType("Producto");
				log.setPreviousValue(new Double(old.getStorehouseQuantity(pbs.getStorehouse().getName())));
				log.setCurrentValue(pbs.getQuantity());
				updateLogBean.save(log);
			}
			productBean.update(productToEdit);
		}
		
		if( productToEdit.getPhoto() != null ){
			Photo photo = productToEdit.getPhoto();
			photo.setDescription(productToEdit.getMaterial());
			photo.setTitle(productToEdit.getCode());
			photo.setFkId(productToEdit.getId());
			if( photo.getId() != null ){
				photoBean.update(photo);
			} else {
				photoBean.save(photo);
			}
		}

		brands = productBean.getBrandsListByInventory(inventory);
		categories = productBean.getCategoriesListByInventory(inventory);
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Producto guardado exitosamente", ""));
		
	}
	
	public List<String> getCategories(){
		return categories;
	}
	
	public List<String> getBrands(){
		return brands;
	}
	
	public void addProduct(){
		productToEdit = new Product();
		for(Storehouse sh : storehouses ){
			ProductByStorehouse pbs = new ProductByStorehouse();
			pbs.setQuantity(0.0);
			pbs.setStorehouse(sh);
			pbs.setProduct(productToEdit);
			SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
			try {
				pbs.setLastDate(sdf.parse("2017-01-01"));
			} catch (ParseException e) {
				pbs.setLastDate(new Date());
			}
			productToEdit.getQuantities().add(pbs);
		}
		newProduct = true;
		showProductDetail = true;
	}

	public boolean isPhotoSet(Product p) {
		return p.getPhoto() != null;
	}
	
	public void productSelected(){
		showProductDetail = true;
		changeReason = "";
		newProduct = false;
		productToEdit = productBean.findByCode(productToEdit.getCode());
	}
	
	public void reset(){
		productToEdit = null;
		showProductDetail = false;
		changeReason = null;
		newProduct = false;
	}

	public boolean getRenderActions() {
		HashSet<String> roles = new HashSet<>();
		roles.add(Role.ADMIN_ROLE);
		roles.add(Role.CHIEF_ROLE);
		roles.add(Role.DIGITATOR_ROLE);
		roles.add(Role.PROGRAMMING_ROLE);
		return userSessionBean.hasAnyRole(roles);
	}

	public boolean getRenderAdminActions() {
		HashSet<String> roles = new HashSet<>();
		roles.add(Role.ADMIN_ROLE);
		roles.add(Role.CHIEF_ROLE);
		roles.add(Role.PROGRAMMING_ROLE);
		return userSessionBean.hasAnyRole(roles);
	}

	public void getUploadRequisitionLink() {
		try {
			FacesContext.getCurrentInstance().getExternalContext()
					.redirect("uploadRequisition.jsf?invId=" + inventory.getId());
		} catch (IOException e) {
			showErrorMessage("No se pudo abrir la pantalla de requisiciones");
		}
	}

	public void getDeliveriesLink() {
		try {
			FacesContext.getCurrentInstance().getExternalContext()
					.redirect("deliveries.jsf?invId=" + inventory.getId());
		} catch (IOException e) {
			showErrorMessage("No se pudieron abrir los movimientos");
		}
	}

	public void getProductDeliveriesLink(Product p) {
		try {
			FacesContext.getCurrentInstance().getExternalContext()
					.redirect("deliveries.jsf?prodId=" + p.getId() + "&invId=" + inventory.getId());
		} catch (IOException e) {
			showErrorMessage("No se pudieron abrir los movimientos");
		}
	}
	
	public void deleteProduct(Product p){
		List<Delivery> deliveries = deliveryBean.findByProductId(p.getId());
		for( Delivery delivery : deliveries ){
			if( delivery.getRequisition().getId() != 1 ){
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "No se puede eliminar porque pertenece a una requisicion", ""));
				return;
			}
		}
		for( Delivery delivery : deliveries ){
			try{
				deliveryBean.delete(delivery);
			} catch(Exception e){
				e.printStackTrace();
			}
		}
		p.setBrand("N/A");
		p.setMaterial("N/A");
		for( ProductByStorehouse pbs : p.getQuantities() ){
			pbs.setQuantity(0.0);
			productByStorehouseBean.update(pbs);
		}
		productBean.update(p);
		
		UpdateLog updateLog = new UpdateLog(UpdateLog.UPDATE, userSessionBean.getUsername(), UpdateLog.PRODUCT);
		updateLog.setReason("Codigo Eliminado");
		updateLog.setCode(p.getCode());
		updateLog.setCurrentValue(0.0);
		updateLog.setPreviousValue(0.0);
		
		Photo photo = photoBean.findByTableAndId(Constants.products_photos_table, p.getId());
		if( photo != null ){
			photoBean.delete(photo);
		}
		
		RequestContext.getCurrentInstance().update("productsForm:productsDataTable");
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Producto eliminado correctamente", ""));
	}
	
	private static void showErrorMessage(String message){
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, ""));
	}

	public Boolean getShowProductDetail() {
		return showProductDetail;
	}

	public void setShowProductDetail(Boolean showProductDetail) {
		this.showProductDetail = showProductDetail;
	}

	public List<Storehouse> getStorehouses() {
		return storehouses;
	}

	public void setStorehouses(List<Storehouse> storehouses) {
		this.storehouses = storehouses;
	}

	public LazyDataModel<Product> getProducts() {
		return products;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	public Product getProductToEdit() {
		return productToEdit;
	}

	public void setProductToEdit(Product productToEdit) {
		this.productToEdit = productToEdit;
	}

	public String getChangeReason() {
		return changeReason;
	}

	public void setChangeReason(String changeReason) {
		this.changeReason = changeReason;
	}

	public Boolean getNewProduct() {
		return newProduct;
	}

	public void setNewProduct(Boolean newProduct) {
		this.newProduct = newProduct;
	}

	public List<Product> getFilteredProducts() {
		return filteredProducts;
	}

	public void setFilteredProducts(List<Product> filteredProducts) {
		this.filteredProducts = filteredProducts;
	}
	
}
