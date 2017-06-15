package com.serinse.web.app;

import java.util.List;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;

import com.serinse.ejb.impl.inventory.ProductBean;
import com.serinse.ejb.impl.inventory.StorehouseBean;
import com.serinse.pers.entity.inventory.Product;
import com.serinse.pers.entity.inventory.Storehouse;

@Singleton
public class ProductsTimer {

	@Inject ProductBean productBean;
	@Inject StorehouseBean storehouseBean;
	
	@Schedule(hour="*/12")
	public void timer(){
		List<Product> products = productBean.findAllById();
		List<Storehouse> storehouses = storehouseBean.findAllById();
		productsFor: for( Product p : products ){
			for( Storehouse sh : storehouses ){
				if( p.getQuantities().get(sh).getQuantity() > 0 ){
					p.setActive(true);
					continue productsFor;
				}
			}
			p.setActive( false );
		}
	}
	
}
