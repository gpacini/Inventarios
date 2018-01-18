package com.serinse.pers.dao.inventory;

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
		String sql = "Select t1 form Lot t1 where t1.product.product.id = :id AND t1.product.storehouse.name = :storehouse";
		TypedQuery<Lot> query = this.em.createQuery(sql, Lot.class);
		query.setParameter("id", id);
		query.setParameter("storehouse", storehouse);
		return query.getResultList();
	}
}