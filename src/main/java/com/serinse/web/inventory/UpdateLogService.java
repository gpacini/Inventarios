package com.serinse.web.inventory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.serinse.ejb.impl.inventory.DeliveryBean;
import com.serinse.ejb.impl.inventory.ProductBean;
import com.serinse.ejb.impl.inventory.UpdateLogBean;
import com.serinse.pers.entity.inventory.Delivery;
import com.serinse.pers.entity.inventory.Product;
import com.serinse.pers.entity.inventory.UpdateLog;
import com.serinse.web.common.response.JSONResponse;
import com.serinse.web.inventory.helpers.UpdateLogHelper;
import com.serinse.web.users.helpers.ListResponse;

@Path("updatelog")
public class UpdateLogService {

	@Inject UpdateLogBean updateLogBean;
	@Inject ProductBean productBean;
	@Inject DeliveryBean deliveryBean;
	
	@Path("index")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONResponse index(){
		List<UpdateLog> items = updateLogBean.findAllById();
		
		ArrayList<UpdateLogHelper> itemList = new ArrayList<UpdateLogHelper>();
		for( UpdateLog item : items ){
			Product p;
			Delivery d;
			if( item.getType().equals(UpdateLog.PRODUCT)){
				p = productBean.findByCode(item.getCode());
			} else {
				d = deliveryBean.findById(Long.parseLong(item.getCode()));
				p = d.getProduct();
			}
			
			UpdateLogHelper itemH = new UpdateLogHelper();
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			itemH.action = item.getAction();
			itemH.client = p.getInventory().getClient().getName();
			itemH.code = item.getCode();
			itemH.date = dateFormat.format(item.getDate());
			itemH.finalValue = item.getCurrentValue().intValue()+"";
			itemH.initialValue = item.getPreviousValue().intValue()+"";
			itemH.name = p.getMaterial();
			itemH.reason = item.getReason();
			itemH.table = item.getType();
			itemH.user = item.getUser();
			itemList.add(itemH);
		}
		
		return new ListResponse<UpdateLogHelper>(itemList, "Lista de bodegas");
	}
	
}
