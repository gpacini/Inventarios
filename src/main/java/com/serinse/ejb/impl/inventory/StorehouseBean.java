package com.serinse.ejb.impl.inventory;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.serinse.ejb.AbstractBean;
import com.serinse.pers.dao.inventory.DAOJPAStorehouse;
import com.serinse.pers.entity.inventory.Storehouse;

@Stateless
@LocalBean
public class StorehouseBean extends AbstractBean<Storehouse> {

	@EJB
	private DAOJPAStorehouse daojpaStorehouse;
	
	@PostConstruct
	public void init(){
		super.init(daojpaStorehouse);
	}
	
}
