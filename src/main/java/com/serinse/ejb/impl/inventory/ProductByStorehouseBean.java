package com.serinse.ejb.impl.inventory;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.serinse.ejb.AbstractBean;
import com.serinse.pers.dao.inventory.DAOJPAProductByStorehouse;
import com.serinse.pers.entity.inventory.ProductByStorehouse;

@Stateless
@LocalBean
public class ProductByStorehouseBean extends AbstractBean<ProductByStorehouse> {

	@EJB
	private DAOJPAProductByStorehouse daojpaProductByStorehouse;
	
	@PostConstruct
	public void init(){
		super.init(daojpaProductByStorehouse);
	}
	
	public List<ProductByStorehouse> findByProductId(Long id){
		return daojpaProductByStorehouse.findByProductId(id);
	}
	
	public ProductByStorehouse findByProductAndStorehouse(Long productId, String storehouseName){
		return daojpaProductByStorehouse.findByProductAndStorehouse(productId, storehouseName);
	}

	public List<ProductByStorehouse> findByProductAndStorehouseWhereQuantityNone() {
		return daojpaProductByStorehouse.findByProductAndStorehouseWhereQuantityNone();
	}
	
}
