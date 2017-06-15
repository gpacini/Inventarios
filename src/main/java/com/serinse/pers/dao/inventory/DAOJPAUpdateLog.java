package com.serinse.pers.dao.inventory;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.serinse.pers.dao.DAOJPABase;
import com.serinse.pers.entity.inventory.UpdateLog;

@Stateless
@LocalBean
public class DAOJPAUpdateLog extends DAOJPABase<UpdateLog, Long> {

	public DAOJPAUpdateLog(){
		super(UpdateLog.class);
	}
	
}
