package com.serinse.pers.dao.inventory;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.serinse.pers.dao.DAOJPABase;
import com.serinse.pers.entity.inventory.Inventory;

@Stateless
@LocalBean
public class DAOJPAInventory extends DAOJPABase<Inventory, Long> {

	public DAOJPAInventory(){
		super(Inventory.class);
	}
	
	public Inventory findByClientId(Long id){
		Query query = this.em.createQuery("SELECT t1 FROM Inventory t1 where t1.client.id = :id" );
		query.setParameter("id", id);
		
		try {
			return (Inventory) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Inventory> findAllByName(){
		Query query = this.em.createQuery("SELECT t1 FROM Inventory t1 where 1=1 Order By t1.client.name asc");
		return query.getResultList();
	}
	
}
