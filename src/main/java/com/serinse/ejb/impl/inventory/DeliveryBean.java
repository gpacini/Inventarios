package com.serinse.ejb.impl.inventory;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.primefaces.model.SortOrder;

import com.serinse.ejb.AbstractBean;
import com.serinse.pers.dao.inventory.DAOJPADelivery;
import com.serinse.pers.entity.inventory.Delivery;
import com.serinse.pers.entity.inventory.DeliveryType;
import com.serinse.pers.exception.NoDeletedEntityException;

@Stateless
@LocalBean
public class DeliveryBean extends AbstractBean<Delivery> {

	@EJB
	private DAOJPADelivery daojpaDelivery;
	
	@PostConstruct
	public void init(){
		super.init(daojpaDelivery);
	}
	
	public List<Delivery> findByInventoryIdAndDeliveryType(Long invId, DeliveryType dt, int from, int to){
		return daojpaDelivery.findByInventoryIdAndDeliveryType(invId, dt, from, to);
	}
	
	public List<Delivery> findByInventoryIdAndProduct(Long id, String product){
		return daojpaDelivery.findByInventoryIdAndProduct(id, product);
	}

	public List<Delivery> findByInventoryIdAndDate(Long id, Date date){
		return daojpaDelivery.findByInventoryIdAndDate(id, date);
	}

	public List<Delivery> findByProductIdAndDate(Long id, Date date){
		return daojpaDelivery.findByProductIdAndDate(id, date);
	}
	
	public List<Delivery> findByInventoryId(Long id){
		return daojpaDelivery.findByInventoryId(id);
	}
	
	public List<Delivery> findByProductId(Long id){
		return daojpaDelivery.findByProductId(id);
	}

	public List<Delivery> findByProductCode(String code){
		return daojpaDelivery.findByProductCode(code);
	}
	
	public void delete(Delivery d) throws NoDeletedEntityException{
		daojpaDelivery.delete(d);
	}

	public List<Delivery> findLastByProductAndTypeAndStorehouse(Long id, DeliveryType entrega, Long storehouseId) {
		return daojpaDelivery.findLastByProductAndTypeAndStorehouse(id, entrega, storehouseId);
	}
	
	public List<Delivery> findByInventoryIdLimit(Long invId, int limit){
		return daojpaDelivery.findByInventoryIdLimit(invId, limit);
	}
	
	public List<Delivery> findByRequisitionId(Long id){
		return daojpaDelivery.findByRequisitionId(id);
	}

	public int count(Map<String, String> filters) {
        return daojpaDelivery.count(filters);
    }

    public List<Delivery> getResultList(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
    	return daojpaDelivery.getResultList(first, pageSize, sortField, sortOrder, filters);
    }
	
}
