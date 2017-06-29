package com.serinse.web.app;

import java.util.List;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;

import com.serinse.ejb.impl.inventory.ProductBean;
import com.serinse.ejb.impl.inventory.StorehouseBean;
import com.serinse.pers.entity.inventory.Product;
import com.serinse.pers.entity.inventory.ProductByStorehouse;

@Singleton
public class ProductsTimer {

	@Inject ProductBean productBean;
	@Inject StorehouseBean storehouseBean;
	
	@Schedule(hour="*/1")
	public void timer(){
		List<Product> products = productBean.findAllById();
		productsFor: for( Product p : products ){
			for( ProductByStorehouse pbs : p.getQuantities() ){
				if( pbs.getQuantity() > 0 ){
					p.setActive(true);
					productBean.update(p);
					continue productsFor;
				}
			}
			p.setActive( false );
			productBean.update(p);
		}
	}
	
}
