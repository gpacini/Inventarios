package com.serinse.ejb.impl.administration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;

import com.serinse.ejb.impl.inventory.LotBean;
import com.serinse.ejb.impl.mail.MailData;
import com.serinse.ejb.impl.mail.MailSender;
import com.serinse.pers.entity.inventory.Lot;

@Singleton
public class ExpirationDateTimer {

	@Inject LotBean lotBean;
	@Inject MailSender sender;
	
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
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			String message = "El producto " + lot.getProduct().getProduct().getMaterial() + " con código " + lot.getProduct().getProduct().getCode();
			message += ", expiró la fecha " + sdf.format(lot.getExpirationDate());
			message += "\nEl producto pertenece al lote: " + lot.getLotNumber() +" en la ciudad de " + lot.getProduct().getStorehouse().getName();
			message += "\nCantidad de items restantes en el lote: " + lot.getQuantity();
			message += "\n\nSaludos Cordiales,\nSerinse S.A";
			MailData data = new MailData("jrendon@serinse.com", "inventarios@serinse.com", "Producto Expirado", message);
			data.addCC("avargas@serinse.com");
			data.addCC("malbuja@serinse.com");
			data.addCC("dzapata@serinse.com");
			data.addCC("gpacini@serinse.com");
			
			try {
				sender.sendMail(data);
				lot.setMailSent(true);
				lotBean.update(lot);
			} catch( Exception e ) {
				e.printStackTrace();
			}
			//Obtener mail del cliente
		}
	}
}
