package com.serinse.pers.dao.inventory;

import java.util.List;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.primefaces.model.SortOrder;

import com.serinse.pers.dao.DAOJPABase;
import com.serinse.pers.entity.inventory.Delivery;
import com.serinse.pers.entity.inventory.DeliveryType;
import com.serinse.pers.entity.inventory.Requisition;

@Stateless
@LocalBean
public class DAOJPARequisition extends DAOJPABase<Requisition, Long> {

	public DAOJPARequisition(){
		super(Requisition.class);
	}
	
	public Requisition findByConsecutive(String code){
		return findSingleByParameter("consecutive", code);
	}
	
	public Requisition findByPhysicalRequisition(String code){
		return findSingleByParameter("physicalRequisition", code);
	}
	


	private String getFilterCondition(Map<String, String> filters, String sortField, SortOrder sortOrder) {
		String sql = "SELECT t1 FROM Delivery t1";
		if (filters.size() > 0) {
			sql += " WHERE t1.requisition.consecutive != 'null_requisition'";
			for (Map.Entry<String, String> entry : filters.entrySet()) {
				if (entry.getKey().equals("inventory")) {
					sql += " AND t1.product.inventory.id = :inventory";
				} else if(entry.getKey().equals("deliveryType")){
					sql += " AND t1.deliveryType = :deliveryType";
				} else if( entry.getKey().equals("storehouse") ){
					sql += " AND t1.storehouse.name = :storehouse";
				}
			}
		}
		if (sortField != null) {
			sql += " order by t1." + sortField;
			if (sortOrder == SortOrder.ASCENDING) {
				sql += " ASC";
			} else {
				sql += " DESC";
			}
		}
		sql += " GROUP BY t1.requisition.id";
		return sql;
	}

	public int count(Map<String, String> filters) {
		Query query = this.em.createQuery(getFilterCondition(filters, null, null));
		for (Map.Entry<String, String> entry : filters.entrySet()) {
			if (entry.getKey().equals("inventory")) {
				query.setParameter("inventory", Long.parseLong(entry.getValue()));
			} else if(entry.getKey().equals("deliveryType")){
				try {
					query.setParameter("deliveryType", DeliveryType.getByName(entry.getValue()));
				} catch (Exception e) {
				}
			} else if( entry.getKey().equals("storehouse") ){
				query.setParameter("storehouse", entry.getValue());
			}
		}
		return query.getResultList().size();
	}

	public List<Delivery> getResultList(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, String> filters) {
		TypedQuery<Delivery> query = this.em.createQuery(getFilterCondition(filters, sortField, sortOrder), Delivery.class);
		for (Map.Entry<String, String> entry : filters.entrySet()) {
			if (entry.getKey().equals("inventory")) {
				query.setParameter("inventory", Long.parseLong(entry.getValue()));
			} else if(entry.getKey().equals("deliveryType")){
				try {
					query.setParameter("deliveryType", DeliveryType.getByName(entry.getValue()));
				} catch (Exception e) {
				}
			} else if( entry.getKey().equals("storehouse") ){
				query.setParameter("storehouse", entry.getValue());
			}
		}
		query.setFirstResult(first);
		query.setMaxResults(pageSize);
		return query.getResultList();
	}
	
}
