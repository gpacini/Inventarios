package com.serinse.ejb.impl.inventory;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.serinse.ejb.AbstractBean;
import com.serinse.pers.dao.inventory.DAOJPAUpdateLog;
import com.serinse.pers.entity.inventory.UpdateLog;

@Stateless
@LocalBean
public class UpdateLogBean extends AbstractBean<UpdateLog> {

	@EJB
	private DAOJPAUpdateLog daojpaUpdateLog;
	
	@PostConstruct
	public void init(){
		super.init(daojpaUpdateLog);
	}
	
}
