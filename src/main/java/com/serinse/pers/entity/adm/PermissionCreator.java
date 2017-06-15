package com.serinse.pers.entity.adm;

public class PermissionCreator {

	public static Permission USERS_ADMIN_PATH(){
		return getPermission(PermissionEnum.PAGE, "admin/users.jsf");
	}
	public static Permission CLIENTS_ADMIN_PATH(){
		return getPermission(PermissionEnum.PAGE, "admin/clients.jsf");
	}
	public static Permission DELIVERIES_CONTENT_PATH(){
		return getPermission(PermissionEnum.PAGE, "content/deliveries.jsf");
	}
	public static Permission INVENTORY_CONTENT_PATH(){
		return getPermission(PermissionEnum.PAGE, "content/inventory.jsf");
	}
	public static Permission MULTIPLE_INVENTORY_PATH(){
		return getPermission(PermissionEnum.PAGE, "content/multipleClientInventory.jsf");
	}
	public static Permission PRODUCTS_CONTENT_PATH(){
		return getPermission(PermissionEnum.PAGE, "content/products.jsf");
	}
	public static Permission SEARCH_REQUISITION_PATH(){
		return getPermission(PermissionEnum.PAGE, "content/searchRequisition.jsf");
	}
	public static Permission LOG_PATH(){
		return getPermission(PermissionEnum.PAGE, "content/updateLog.jsf");
	}
	public static Permission UPLOAD_REQUISITION_PATH(){
		return getPermission(PermissionEnum.PAGE, "content/uploadRequisition.jsf");
	}
	
	
	private static Permission getPermission(PermissionEnum type, String path){
		Permission permission = new Permission();
		permission.setType(type);
		permission.setPath("/serinse/"+path);
		return permission;
	}
	
}
