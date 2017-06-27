package com.serinse.web.inventory;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.serinse.ejb.impl.inventory.DeliveryBean;
import com.serinse.ejb.impl.inventory.ProductBean;
import com.serinse.ejb.impl.inventory.UpdateLogBean;
import com.serinse.pers.entity.inventory.Delivery;
import com.serinse.pers.entity.inventory.Product;
import com.serinse.pers.entity.inventory.UpdateLog;
import com.serinse.web.inventory.helpers.UpdateLogHelper;

@Named
@ViewScoped
public class UpdateLogService implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject UpdateLogBean updateLogBean;
	@Inject ProductBean productBean;
	@Inject DeliveryBean deliveryBean;
	
	private LazyDataModel<UpdateLogHelper> updateLogs;
	
	@PostConstruct
	public void init(){
		updateLogs = new LazyDataModel<UpdateLogHelper>(){
			private static final long serialVersionUID = 18234923842189342L;
			
			@Override
			public List<UpdateLogHelper> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				updateLogs.setRowCount(updateLogBean.count(filters));
				List<UpdateLog> items = updateLogBean.getResultList(first, pageSize, sortField, sortOrder, filters);
				
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
				
				return itemList;
			}
		};
		updateLogs.setRowCount(updateLogBean.count(new HashMap<String, Object>()));
	}

	public LazyDataModel<UpdateLogHelper> getUpdateLogs() {
		return updateLogs;
	}

	public void setUpdateLogs(LazyDataModel<UpdateLogHelper> updateLogs) {
		this.updateLogs = updateLogs;
	}
	
}
