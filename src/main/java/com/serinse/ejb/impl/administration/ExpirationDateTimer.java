package com.serinse.ejb.impl.administration;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import com.serinse.ejb.impl.inventory.LotBean;
import com.serinse.pers.entity.inventory.Lot;

@Singleton
@Startup
public class ExpirationDateTimer {

	@Inject LotBean lotBean;
	
	private Date currentDate;
	
	@PostConstruct
	public void init() {
		currentDate = new Date();
		System.out.println("Starting timer");
	}
	
	@Schedule(second="0", minute="0", hour="5", dayOfWeek="*", persistent=false)
	public void checkExpirationDates() {
		currentDate = new Date();
		System.out.println("Checking dates");
		List<Lot> expiredLots = lotBean.findAllActiveByExpirationDate(currentDate);
		for( Lot lot : expiredLots ) {
			
		}
	}
}
