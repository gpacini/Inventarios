package com.serinse.pers.dao.inventory;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import com.serinse.pers.dao.DAOJPABase;
import com.serinse.pers.entity.inventory.Lot;

@Stateless
@LocalBean
public class DAOJPALot extends DAOJPABase<Lot, Long> {

	@EJB
	DAOJPALot daojpaLot;

	public DAOJPALot() {
		super(Lot.class);
	}

	public List<Lot> findByStorehouseAndProductId(String storehouse, Long id) {
		String sql = "Select t1 from Lot t1 where t1.product.product.id = :id AND t1.product.storehouse.name = :storehouse";
		TypedQuery<Lot> query = this.em.createQuery(sql, Lot.class);
		query.setParameter("id", id);
		query.setParameter("storehouse", storehouse);
		return query.getResultList();
	}
	
	public List<Lot> findAllActiveByExpirationDate(Date date){
		String sql = "Select t1 from Lot t1 where t1.expirationDate < :date";
		TypedQuery<Lot> query = this.em.createQuery(sql, Lot.class);
		query.setParameter("date", date);
		//query.setParameter("mailSent", false);
		return query.getResultList();
	}
}