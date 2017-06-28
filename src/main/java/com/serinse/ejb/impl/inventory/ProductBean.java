package com.serinse.ejb.impl.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.primefaces.model.SortOrder;

import com.serinse.common.Constants;
import com.serinse.ejb.AbstractBean;
import com.serinse.ejb.impl.general.PhotoBean;
import com.serinse.pers.dao.inventory.DAOJPAProduct;
import com.serinse.pers.dao.inventory.DAOJPAProductByStorehouse;
import com.serinse.pers.dao.inventory.DAOJPAStorehouse;
import com.serinse.pers.entity.general.Photo;
import com.serinse.pers.entity.inventory.Inventory;
import com.serinse.pers.entity.inventory.Product;
import com.serinse.pers.entity.inventory.ProductByStorehouse;
import com.serinse.pers.entity.inventory.Storehouse;

@Stateless
@LocalBean
public class ProductBean extends AbstractBean<Product>{
	
	@EJB
	private DAOJPAProduct daojpaProduct;
	
	@EJB
	private DAOJPAProductByStorehouse daojpaProductByStorehouse;
	
	@EJB DAOJPAStorehouse daojpaStorehouse;
	
	@EJB PhotoBean photoBean;

	@PostConstruct
	public void init(){
		super.init(daojpaProduct);
	}
	
	public List<Product> findAllById(){
		List<Product> all = daojpaProduct.findAllById();
		for(Product p : all ){
			calculateProductQuantities(p);
		}
		return all;
	}
	
	public void save(Product product){
		super.save(product);
		List<Storehouse> storehouses = daojpaStorehouse.findAll();
		for( Storehouse storehouse : storehouses){
			ProductByStorehouse pbs = new ProductByStorehouse();
			pbs.setQuantity(0.0);
			pbs.setProduct(product);
			pbs.setStorehouse(storehouse);
			pbs.setIsNew(true);
			daojpaProductByStorehouse.save(pbs);
		}
	}
	
	public Product findById(Long id){
		Product product = daojpaProduct.findById(id);
		if( product == null ) return null;
		calculateProductQuantities(product);
		return product;
	}
	
	public Product findByCode(String code){
		Product product = daojpaProduct.findByCode(code);
		if( product == null ) return null;
		calculateProductQuantities(product);
		Photo photo = photoBean.findByTableAndId(Constants.products_photos_table, product.getId());
		if (photo != null) {
			product.setPhoto(photo);
		}
		return product;
	}
	
	public List<Product> findByInventoryId(Long invId){
		List<Product> products = daojpaProduct.findByInventoryId(invId);
		for( Product product : products ){
			calculateProductQuantities(product);
		}
		return products;
	}
	
	public List<Product> findByInventoryIdAndProduct(Long invId, String productName){
		List<Product> products = daojpaProduct.findByInventoryIdAndProduct(invId, productName);
		for( Product product : products ){
			calculateProductQuantities(product);
		}
		return products;
	}
	
	private void calculateProductQuantities(Product product){
		product.setProductByStorehouses(daojpaProductByStorehouse.findActiveByProductId(product.getId()));
		Map<Storehouse, ProductByStorehouse> map = new HashMap<>();
		for( ProductByStorehouse pbs : product.getProductByStorehouses() ){
			map.put(pbs.getStorehouse(), pbs);
//			Integer currentQuantity = product.getQuantities().get(pbs.getStorehouse());
//			if( currentQuantity == null ){
//				product.getQuantities().put(pbs.getStorehouse(), pbs.getQuantity().intValue());
//			} else {
//				currentQuantity += pbs.getQuantity().intValue();
//				product.getQuantities().put(pbs.getStorehouse(), currentQuantity);
//			}
		}
		product.setQuantities(map);
	}
	
	public int count(Map<String, String> filters, Inventory inventory) {
        return daojpaProduct.count(filters, inventory);
    }

    public List<Product> getResultList(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters, Inventory inventory) {
    	List<Product> products = daojpaProduct.getResultList(first, pageSize, sortField, sortOrder, filters, inventory);
    	for (Product p : products) {
			Photo photo = photoBean.findByTableAndId(Constants.products_photos_table, p.getId());
			if (photo != null) {
				p.setPhoto(photo);
			}
			calculateProductQuantities(p);
		}
    	return products;
    }

}
