package com.serinse.web.controllers.inventory;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.serinse.common.Constants;
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
import com.serinse.pers.entity.inventory.DeliveryType;
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
	private List<Storehouse> storehouses;

	private static final int ADD = 1;
	private static final int UPDATE = 2;
	private Product productToEdit;
	private Map<Storehouse, Double> currentQuantities;
	private int action;
	private String changeReason;
	private List<String> currentProductPositions;
	
	private List<String> availableCodes;
	private String selectedCode;
	
	private Boolean showProductDetail;
	private Boolean renderProductPositions;
	private Boolean showEditProductDialog;

	@PostConstruct
	public void init() {
		Long inventoryId = -1L;
		try {
			String sInventoryId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
					.get("id");
			if (sInventoryId == null) {
				System.out.println("No hay inventario seleccionado");
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "No hay ningun inventario seleccionado", ""));
				FacesContext.getCurrentInstance().getExternalContext().redirect(userSessionBean.getHomeLink());
				return;
			}
			inventoryId = Long.parseLong(sInventoryId);
			inventory = inventoryBean.findById(inventoryId);
			if (inventory == null) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "No existe el inventario seleccionado", ""));
				FacesContext.getCurrentInstance().getExternalContext().redirect(userSessionBean.getHomeLink());
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
		storehouses = storehouseBean.findAllById();
		products = new LazyDataModel<Product>() {

			private static final long serialVersionUID = 175674564345L;

			@Override
			public List<Product> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				Map<String, String> newMap = new HashMap<String, String>();
				for (Map.Entry<String, Object> entry : filters.entrySet()) {
					if (entry.getValue() instanceof String) {
						newMap.put(entry.getKey(), (String) entry.getValue());
					}
				}
				newMap.put("not_available", "true");
				if( userSessionBean.getUserRole().getRoleName().equals(Role.CLIENT_ROLE) ){
					newMap.put("active_item", "true");
				}
				products.setRowCount(productBean.count(newMap, inventory));
				return productBean.getResultList(first, pageSize, sortField, sortOrder, newMap, inventory);
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
		products.setRowCount(productBean.count(new HashMap<String, String>(), inventory));

		productToEdit = new Product();
		action = ADD;
		renderProductPositions = false;
		showProductDetail = false;
	}

	public boolean isPhotoSet(Product p) {
		return p.getPhoto() != null;
	}

	public LazyDataModel<Product> getProducts() {
		return products;
	}
	
	public void productSelected(){
		showProductDetail = true;
		productToEdit = productBean.findByCode(productToEdit.getCode());
		RequestContext.getCurrentInstance().execute("PF('productDetailsDialog').show();");
	}
	
	public void productUnselected(){
		showProductDetail = false;
	}
	
	public void selectAvailableCode(String code){
		
		action = UPDATE;
		String brand = productToEdit.getBrand();
		String material = productToEdit.getMaterial();
		productToEdit = productBean.findByCode(code);
		productToEdit.setBrand(brand);
		productToEdit.setMaterial(material);
		for( Storehouse sh : storehouses ){
			productToEdit.getQuantities().get(sh).setQuantity(0.0);
		}
		changeReason = "Nuevo Producto";
		selectedCode = code;
		
		RequestContext.getCurrentInstance().update("productsForm:updateProductDialog");
		RequestContext.getCurrentInstance().execute("PF('updateProductDialog').show()");
	}

	public void getUploadRequisitionLink() {
		try {
			FacesContext.getCurrentInstance().getExternalContext()
					.redirect("uploadRequisition.jsf?invId=" + inventory.getId());
		} catch (IOException e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "No se pudo abrir los movimientos", ""));
		}
	}

	public void getDeliveriesLink() {
		try {
			FacesContext.getCurrentInstance().getExternalContext()
					.redirect("deliveries.jsf?invId=" + inventory.getId());
		} catch (IOException e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "No se pudo abrir los movimientos", ""));
		}
	}

	public void getProductDeliveriesLink(Product p) {
		try {
			FacesContext.getCurrentInstance().getExternalContext()
					.redirect("deliveries.jsf?prodId=" + p.getId() + "&invId=" + inventory.getId());
		} catch (IOException e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "No se pudo abrir los movimientos", ""));
		}
	}

	public boolean getRenderActions() {
		HashSet<String> roles = new HashSet<>();
		roles.add(Role.ADMIN_ROLE);
		roles.add(Role.CHIEF_ROLE);
		roles.add(Role.DIGITATOR_ROLE);
		roles.add(Role.PROGRAMMING_ROLE);
		return userSessionBean.hasAnyRole(roles);
	}

	public int getQuantityFor(Storehouse sh, Product p) {
		ProductByStorehouse pbs = p.getQuantities().get(sh);
		if (pbs == null)
			return 0;
		Double quantity = pbs.getQuantity();
		return quantity.intValue();
	}

	public String getDaysInStorehouseFor(Storehouse sh, Product p) {
		ProductByStorehouse pbs = p.getQuantities().get(sh);
		if (pbs == null)
			return "N/A";
		if( !(pbs.getQuantity() > 0))
			return "N/A";
		long currentDiff = -1L;
		List<Delivery> deliveries = deliveryBean.findLastByProductAndTypeAndStorehouse(p.getId(), DeliveryType.ENTREGA, sh.getId());
		for (Delivery delivery : deliveries) {
			long thisDiff = (new Date()).getTime() - delivery.getDeliveryDate().getTime();
			if (thisDiff > currentDiff)
				currentDiff = thisDiff;
		}
		if( currentDiff == -1L ){
			SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd");
			try{
				Date febDate = format.parse("2017-02-01");
				currentDiff = (new Date()).getTime() - febDate.getTime();
			} catch(Exception e){
				return "150";
			}
		}
		return "" + TimeUnit.DAYS.convert(currentDiff, TimeUnit.MILLISECONDS);
	}

	public boolean getRenderAdminActions() {
		HashSet<String> roles = new HashSet<>();
		roles.add(Role.ADMIN_ROLE);
		roles.add(Role.CHIEF_ROLE);
		roles.add(Role.PROGRAMMING_ROLE);
		return userSessionBean.hasAnyRole(roles);
	}

	public Inventory getInventory() {
		return inventory;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	public List<Storehouse> getStorehouses() {
		return storehouses;
	}

	public void setStorehouses(List<Storehouse> storehouses) {
		this.storehouses = storehouses;
	}

	public void saveProduct() {
		if (productToEdit == null)
			return;
		if (action == ADD) {
			productToEdit.setCode(productToEdit.getCode().trim());
			productToEdit.setBrand(productToEdit.getBrand().trim());
			productToEdit.setMaterial(productToEdit.getMaterial().trim());
			productToEdit.setInventory(inventory);
			if (productToEdit.getCode().trim().equals("")) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "El codigo no puede estar en blanco", ""));
				return;
			}
			if (productToEdit.getMaterial().trim().equals("")) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "El material no puede estar en blanco", ""));
				return;
			}
			if (productToEdit.getBrand().trim().equals("")) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "La marca no puede estar en blanco", ""));
				return;
			}
			Product p = productBean.findByCode(productToEdit.getCode());
			if (p != null) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ya existe un producto con ese nombre", ""));
				return;
			}
			productBean.save(productToEdit);
			showEditProductDialog = false;
			RequestContext.getCurrentInstance().update("productsForm");
		} else if (action == UPDATE) {
			if( selectedCode != null ){
				if( !selectedCode.equals(productToEdit.getCode())){
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, "El codigo no es el que fue seleccionado", ""));
					return;
				}
			}
			productToEdit.setCode(productToEdit.getCode().trim());
			productToEdit.setBrand(productToEdit.getBrand().trim());
			productToEdit.setMaterial(productToEdit.getMaterial().trim());
			changeReason = changeReason.trim();
			if (productToEdit.getCode().trim().equals("")) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "El codigo no puede estar en blanco", ""));
				return;
			}
			if (productToEdit.getMaterial().trim().equals("")) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "El material no puede estar en blanco", ""));
				return;
			}
			if (productToEdit.getBrand().trim().equals("")) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "La marca no puede estar en blanco", ""));
				return;
			}
			if (changeReason.trim().equals("")) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "La razon no puede estar en blanco", ""));
				return;
			}
			productBean.update(productToEdit);
			for (ProductByStorehouse pbs : productToEdit.getQuantities().values()) {
				productByStorehouseBean.update(pbs);
				UpdateLog updateLog = new UpdateLog(UpdateLog.UPDATE, userSessionBean.getUsername(), UpdateLog.PRODUCT);
				updateLog.setReason(changeReason);
				updateLog.setCode(productToEdit.getCode());
				updateLog.setCurrentValue(pbs.getQuantity());
				updateLog.setPreviousValue(currentQuantities.get(pbs.getStorehouse()));
				if (!updateLog.getCurrentValue().equals(updateLog.getPreviousValue())) {
					updateLogBean.save(updateLog);
				}
			}
			selectedCode = null;
			showEditProductDialog = false;
			RequestContext.getCurrentInstance().update("productsForm");
		}
	}
	
	public List<String> getAvailablesCodes(){
		Map<String, String> newMap = new HashMap<>();
		newMap.put("not_available", "false");
		List<Product> nProducts = productBean.getResultList(0, 1000, null, null, newMap, inventory);
		availableCodes = new ArrayList<>();
		for( Product product : nProducts ){
			availableCodes.add(product.getCode());
		}
		return availableCodes;
	}

	public String getUpdateDialogCommand() {
		if (action == ADD)
			return "Agregar";
		if (action == UPDATE)
			return "Actualizar";
		return "No Action";
	}

	public boolean isEditAction() {
		return action == UPDATE;
	}

	public void showAddProduct() {
		action = ADD;
		productToEdit = new Product();
		selectedCode = null;
		RequestContext.getCurrentInstance().update("productsForm");
		RequestContext.getCurrentInstance().execute("PF('updateProductDialog').show();");
	}

	public void showUpdateProduct(Product p) {
		action = UPDATE;
		productToEdit = productBean.findByCode(p.getCode());

		currentQuantities = new HashMap<>();
		for (ProductByStorehouse pbs : p.getQuantities().values()) {
			currentQuantities.put(pbs.getStorehouse(), pbs.getQuantity());
		}
		
		showEditProductDialog = true;

		RequestContext.getCurrentInstance().update("productsForm");
		RequestContext.getCurrentInstance().execute("PF('updateProductDialog').show();");
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

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}
	
	public String getPositionsOnRackString(){
		String finalString = "";
		for( String position : productToEdit.getPositions() ){
			finalString += " - " + position;
		}
		finalString = finalString.replaceFirst(" - ", "");
		return finalString;
	}
	
	public String getPositionsOnRackString(Product p){
		String finalString = "";
		for( String position : p.getPositions() ){
			finalString += " - " + position;
		}
		finalString = finalString.replaceFirst(" - ", "");
		return finalString;
	}
	
	public void calculatePositionsForProduct(){
		currentProductPositions = new ArrayList<String>(productToEdit.getPositions());
		for( int i = productToEdit.getPositions().size() ; i < 15 ; i++ ){
			currentProductPositions.add("");
		}
		renderProductPositions = true;
		
		RequestContext.getCurrentInstance().update("productsForm");
		RequestContext.getCurrentInstance().execute("PF('positionsDialog').show();");
	}
	
	public void savePositions(){
		Set<String> positions = new HashSet<>();
		for( String position : currentProductPositions ){
			if( position.equals("") || position.equals(" ") ){
				continue;
			}
			positions.add(position);
		}
		productToEdit.setPositions(positions);
		productBean.update(productToEdit);
		
		RequestContext.getCurrentInstance().update("productsForm:productsDataTable");
	}
	
	public void selectProductForPositions(Product product){
		productToEdit = productBean.findByCode(product.getCode());
		calculatePositionsForProduct();
	}

	public void showImage(Product p) {
		productToEdit = p;
		RequestContext.getCurrentInstance().update("productsForm:photoDialog");
		RequestContext.getCurrentInstance().execute("PF('photoDialog').show();");
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
		for( Storehouse sh : storehouses ){
			ProductByStorehouse pbs = p.getQuantities().get(sh);
			pbs.setQuantity(0.0);
			productByStorehouseBean.update(pbs);
			p.getQuantities().put(sh, pbs);
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
	
	public Double getTotalCost(Product p){
		if( p.getUnitCost() == null || p.getUnitCost() == 0.0 ) return 0.0;
		Double tCost = 0.0;
		for( Storehouse sh : storehouses ){
			tCost += p.getQuantities().get(sh).getQuantity() * p.getUnitCost();
		}
		return tCost;
	}

	public Boolean getShowProductDetail() {
		return showProductDetail;
	}

	public void setShowProductDetail(Boolean showProductDetail) {
		this.showProductDetail = showProductDetail;
	}

	public List<String> getCurrentProductPositions() {
		return currentProductPositions;
	}

	public void setCurrentProductPositions(List<String> currentProductPositions) {
		this.currentProductPositions = currentProductPositions;
	}

	public Boolean getRenderProductPositions() {
		return renderProductPositions;
	}

	public void setRenderProductPositions(Boolean renderProductPositions) {
		this.renderProductPositions = renderProductPositions;
	}

	public Boolean getShowEditProductDialog() {
		return showEditProductDialog;
	}

	public void setShowEditProductDialog(Boolean showEditProductDialog) {
		this.showEditProductDialog = showEditProductDialog;
	}
}
