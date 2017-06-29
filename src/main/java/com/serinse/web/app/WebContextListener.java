package com.serinse.web.app;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.serinse.common.ProjectParameterEnum;
import com.serinse.ejb.impl.administration.PermissionBean;
import com.serinse.ejb.impl.administration.RoleBean;
import com.serinse.ejb.impl.administration.UserBean;
import com.serinse.ejb.impl.client.ClientBean;
import com.serinse.ejb.impl.inventory.DeliveryBean;
import com.serinse.ejb.impl.inventory.InventoryBean;
import com.serinse.ejb.impl.inventory.ProductBean;
import com.serinse.ejb.impl.inventory.ProductByStorehouseBean;
import com.serinse.ejb.impl.inventory.RequisitionBean;
import com.serinse.ejb.impl.inventory.StorehouseBean;
import com.serinse.ejb.impl.projectParameter.ProjectParameterBean;
import com.serinse.pers.entity.adm.Permission;
import com.serinse.pers.entity.adm.PermissionCreator;
import com.serinse.pers.entity.adm.Role;
import com.serinse.pers.entity.adm.User;
import com.serinse.pers.entity.inventory.Storehouse;
import com.serinse.pers.entity.projectParameter.ProjectParameter;

@WebListener
public class WebContextListener implements ServletContextListener {

	@Inject UserBean userBean;
	@Inject RoleBean roleBean;
	@Inject ClientBean clientBean;
	@Inject StorehouseBean storehouseBean;
	@Inject ProductBean productBean;
	@Inject ProductByStorehouseBean productByStorehouseBean;
	@Inject DeliveryBean deliveryBean;
	@Inject ProjectParameterBean projectParameterBean;
	@Inject RequisitionBean requisitionBean;
	@Inject PermissionBean permissionBean;
	@Inject InventoryBean inventoryBean;
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		
		User user = userBean.findUserByUsername("gpacini");
		if( user != null ){
			user.setRole(roleBean.findByName(Role.PROGRAMMING_ROLE));
			userBean.update(user);
		}
		User user2 = userBean.findUserByUsername("admin");
		if( user2 != null ){
			user2.setRole(roleBean.findByName(Role.PROGRAMMING_ROLE));
			userBean.update(user2);
		}
		
//		List<Product> products = productBean.findAllById();
//		productsFor: for( Product p : products ){
//			for( ProductByStorehouse pbs : p.getQuantities() ){
//				if( pbs.getQuantity() > 0 ){
//					p.setActive(true);
//					productBean.update(p);
//					continue productsFor;
//				}
//			}
//			p.setActive( false );
//			productBean.update(p);
//		}
//		
//		for( Product p : products ){
//			for( ProductByStorehouse pbs : p.getQuantities() ){
//				List<Delivery> deliveries = deliveryBean.findLastByProductAndTypeAndStorehouse(p.getId(), DeliveryType.ENTREGA, pbs.getStorehouse().getId());
//				Date newestDate = null;
//				for( Delivery delivery : deliveries ){
//					if( newestDate == null ){
//						newestDate = delivery.getAskDate();
//					} else {
//						if( delivery.getAskDate().compareTo(newestDate) > 0 ){
//							newestDate = delivery.getAskDate();
//						}
//					}
//				}
//				if( newestDate == null ){
//					SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
//					try {
//						pbs.setLastDate(sdf.parse("2017-01-15"));
//					} catch (ParseException e) {
//						pbs.setLastDate(new Date());
//					}
//				} else {
//					pbs.setLastDate(newestDate);
//				}
//				productByStorehouseBean.update(pbs);
//			}
//		}
//		
//		List<Client> clients = clientBean.findAllById();
//		for( Client client : clients ){
//			Inventory inv = inventoryBean.findByClientId(client.getId());
//			if( inv == null ){
//				inv = new Inventory();
//				inv.setClient(client);
//				inventoryBean.save(inv);
//			}
//		}
	}
	
	private void initializeConsecutive(ProjectParameterEnum value){
		ProjectParameter parameter = projectParameterBean.findProjectParameterByParemeterName(value);
		if( parameter == null ){
			parameter = new ProjectParameter();
			parameter.setName(value.getName());
			parameter.setValue("0");
			projectParameterBean.saveProjectParameter(parameter);
		}
	}
	
	private void createRole(String name, String description, int importance){
		Role temp = roleBean.findByName(name);
		if( temp == null ){
			Role r = new Role();
			r.setRoleName(name);
			r.setDescription(description);
			r.setImportance(importance);
			roleBean.save(r);
		}
	}
	
	private void createStorehouse(String name, String location){
		Storehouse temp = storehouseBean.findByName(name);
		if( temp == null ){
			Storehouse sh = new Storehouse();
			sh.setName(name);
			sh.setLocation(location);
			storehouseBean.save(sh);
		}
	}
	
	private void createDefaultPermissions(){
		Role chief = roleBean.findByName(Role.CHIEF_ROLE);
		createPermission(PermissionCreator.UPLOAD_REQUISITION_PATH(), chief);
		createPermission(PermissionCreator.SEARCH_REQUISITION_PATH(), chief);
		createPermission(PermissionCreator.PRODUCTS_CONTENT_PATH(), chief);
		createPermission(PermissionCreator.MULTIPLE_INVENTORY_PATH(), chief);
		createPermission(PermissionCreator.LOG_PATH(), chief);
		createPermission(PermissionCreator.INVENTORY_CONTENT_PATH(), chief);
		createPermission(PermissionCreator.DELIVERIES_CONTENT_PATH(), chief);

		Role client = roleBean.findByName(Role.CLIENT_ROLE);
		createPermission(PermissionCreator.PRODUCTS_CONTENT_PATH(), client);
		createPermission(PermissionCreator.DELIVERIES_CONTENT_PATH(), client);
		createPermission(PermissionCreator.INVENTORY_CONTENT_PATH(), client);

		Role digitator = roleBean.findByName(Role.DIGITATOR_ROLE);
		createPermission(PermissionCreator.UPLOAD_REQUISITION_PATH(), digitator);
		createPermission(PermissionCreator.SEARCH_REQUISITION_PATH(), digitator);
		createPermission(PermissionCreator.PRODUCTS_CONTENT_PATH(), digitator);
		createPermission(PermissionCreator.MULTIPLE_INVENTORY_PATH(), digitator);
		createPermission(PermissionCreator.INVENTORY_CONTENT_PATH(), digitator);
		createPermission(PermissionCreator.DELIVERIES_CONTENT_PATH(), digitator);

		Role programmer = roleBean.findByName(Role.PROGRAMMING_ROLE);
		createPermission(PermissionCreator.UPLOAD_REQUISITION_PATH(), programmer);
		createPermission(PermissionCreator.SEARCH_REQUISITION_PATH(), programmer);
		createPermission(PermissionCreator.PRODUCTS_CONTENT_PATH(), programmer);
		createPermission(PermissionCreator.MULTIPLE_INVENTORY_PATH(), programmer);
		createPermission(PermissionCreator.LOG_PATH(), programmer);
		createPermission(PermissionCreator.INVENTORY_CONTENT_PATH(), programmer);
		createPermission(PermissionCreator.DELIVERIES_CONTENT_PATH(), programmer);
		createPermission(PermissionCreator.CLIENTS_ADMIN_PATH(), programmer);
		createPermission(PermissionCreator.USERS_ADMIN_PATH(), programmer);

		Role admin = roleBean.findByName(Role.ADMIN_ROLE);
		createPermission(PermissionCreator.UPLOAD_REQUISITION_PATH(), admin);
		createPermission(PermissionCreator.SEARCH_REQUISITION_PATH(), admin);
		createPermission(PermissionCreator.PRODUCTS_CONTENT_PATH(), admin);
		createPermission(PermissionCreator.MULTIPLE_INVENTORY_PATH(), admin);
		createPermission(PermissionCreator.LOG_PATH(), admin);
		createPermission(PermissionCreator.INVENTORY_CONTENT_PATH(), admin);
		createPermission(PermissionCreator.DELIVERIES_CONTENT_PATH(), admin);
		createPermission(PermissionCreator.CLIENTS_ADMIN_PATH(), admin);
		createPermission(PermissionCreator.USERS_ADMIN_PATH(), admin);
	}
	
	private void createPermission(Permission permission, Role role){
		if( !permissionBean.permissionByRoleExists(permission, role) ){
			permission.setRole(role);
			permissionBean.save(permission);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

}
