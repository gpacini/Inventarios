package com.serinse.pers.dao.inventory;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import com.serinse.pers.dao.DAOJPABase;
import com.serinse.pers.entity.inventory.ProductByStorehouse;

@Stateless
@LocalBean
public class DAOJPAProductByStorehouse extends DAOJPABase<ProductByStorehouse, Long> {

	public DAOJPAProductByStorehouse(){
		super(ProductByStorehouse.class);
	}

	@SuppressWarnings("unchecked")
	public List<ProductByStorehouse> findByProductId(Long id) {
		Query query = this.em.createQuery("SELECT t1 FROM ProductByStorehouse t1 WHERE t1.product.id = :id");
		query.setParameter("id", id);
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<ProductByStorehouse> findActiveByProductId(Long id) {
		Query query = this.em.createQuery("SELECT t1 FROM ProductByStorehouse t1 WHERE t1.product.id = :id AND t1.quantity >= 0");
		query.setParameter("id", id);
		return query.getResultList();
	}

	public ProductByStorehouse findByProductAndStorehouse(Long productId, String storehouseName) {
		Query query = this.em.createQuery("SELECT t1 FROM ProductByStorehouse t1 WHERE t1.product.id = :id AND t1.storehouse.name =:name");
		query.setParameter("id", productId);
		query.setParameter("name", storehouseName);
		try{
			return (ProductByStorehouse) query.getSingleResult();
		} catch( Exception e ){
			return null;
		}
	}

	public List<ProductByStorehouse> findByProductAndStorehouseWhereQuantityNone() {
		Query query = this.em.createQuery("SELECT t1 FROM ProductByStorehouse t1 WHERE t1.quantity = :quantity AND t1.product.active = :active");
		query.setParameter("quantity", 0.0);
		query.setParameter("active", true);
		return query.getResultList();
	}
	
}
