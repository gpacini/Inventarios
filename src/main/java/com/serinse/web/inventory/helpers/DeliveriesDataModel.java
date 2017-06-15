package com.serinse.web.inventory.helpers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.serinse.pers.entity.inventory.Delivery;
import com.serinse.web.controllers.inventory.DeliveriesController;

public class DeliveriesDataModel extends LazyDataModel<Delivery>{
	
	private static final long serialVersionUID = 239472893472L;
	
	HashMap<String, String> defaultFilters;
	
	DeliveriesController deliveriesController;
	
	public DeliveriesDataModel(DeliveriesController controller, HashMap<String, String> filters){
		defaultFilters = filters;
		this.deliveriesController = controller;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Delivery> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {
		Map<String, String> newMap = (Map<String, String>) defaultFilters.clone();
		for (Map.Entry<String, Object> entry : filters.entrySet()) {
			if (entry.getValue() instanceof String) {
				newMap.put(entry.getKey(), (String) entry.getValue());
			}
		}
		setRowCount(deliveriesController.count(newMap));
		return deliveriesController.getResultList(first, pageSize, sortField, sortOrder, newMap);
	}
	
}
