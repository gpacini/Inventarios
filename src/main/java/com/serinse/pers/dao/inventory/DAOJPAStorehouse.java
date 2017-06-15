package com.serinse.pers.dao.inventory;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.serinse.pers.dao.DAOJPABase;
import com.serinse.pers.entity.inventory.Storehouse;

@Stateless
@LocalBean
public class DAOJPAStorehouse extends DAOJPABase<Storehouse, Long> {

	public DAOJPAStorehouse(){
		super(Storehouse.class);
	}
	
}
