package com.serinse.ejb.impl.administration;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import com.serinse.ejb.impl.inventory.LotBean;
import com.serinse.pers.entity.inventory.Lot;

@Singleton
@Startup
@LocalBean
public class ExpirationDateTimer {

	@Inject LotBean lotBean;
	
	private Date currentDate;
	
	@PostConstruct
	public void init() {
		currentDate = new Date();
		System.out.println("Starting timer");
	}
	
	@Schedule(second="*/30")
	public void checkExpirationDates() {
		currentDate = new Date();
		System.out.println("Checking dates");
		List<Lot> expiredLots = lotBean.findAllActiveByExpirationDate(currentDate);
		for( Lot lot : expiredLots ) {
			System.out.println("El lote " + lot.getLotNumber() +" del articulo " + lot.getProduct().getProduct().getMaterial() + " de la bodega " + lot.getProduct().getStorehouse().getName() + " se vencio el " + lot.getExpirationDate());
		}
	}
}
