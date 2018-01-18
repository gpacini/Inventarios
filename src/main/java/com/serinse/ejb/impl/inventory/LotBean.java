package com.serinse.ejb.impl.inventory;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.serinse.ejb.AbstractBean;
import com.serinse.pers.dao.inventory.DAOJPALot;
import com.serinse.pers.entity.inventory.Lot;

@Stateless
@LocalBean
public class LotBean extends AbstractBean<Lot> {

	@EJB
	private DAOJPALot daojpaLot;
	
	@PostConstruct
	public void init(){
		super.init(daojpaLot);
	}
	
	public List<Lot> findByStorehouseAndProductId(String storehouse, Long id){
		return daojpaLot.findByStorehouseAndProductId(storehouse, id);
	}
}