package com.serinse.web.controllers;

import java.io.Serializable;
import java.util.HashSet;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuModel;

import com.serinse.common.Constants;
import com.serinse.ejb.impl.inventory.InventoryBean;
import com.serinse.pers.entity.adm.Role;
import com.serinse.web.session.UserSessionBean;

@Named("controllerMenu")
@SessionScoped
public class ControllerMenu implements Serializable {

	private static final long serialVersionUID = -8269005589867918675L;

	public static final int USERS = 0;
	public static final int CLIENTS = 1;
	public static final int INVENTORY = 2;
	public static final int PRODUCTS = 3;
	public static final int LOG = 4;
	public static final int REQUISITIONS = 5;
	public static final int MULTIPLECLIENTINVENTORY = 6;
	public static final int MAIL = 7;

	@Inject
	UserSessionBean userSessionBean;
	@Inject
	InventoryBean inventoryBean;

	public String getLink(int type) {
		if (!userSessionBean.isUserInSession())
			return "";
		switch (type) {
		case USERS:
			if (!getRenderAdminMenu())
				return "";
			return Constants.BASE_URL + "admin/users.jsf";
		case CLIENTS:
			if (!getRenderAdminMenu())
				return "";
			return Constants.BASE_URL + "admin/clients.jsf";
		case INVENTORY:
			if (!getRenderInventoryMenu())
				return "";
			return Constants.BASE_URL + "content/inventory.jsf";
		case LOG:
			return Constants.BASE_URL + "content/updatelog.jsf";
		case REQUISITIONS:
			return Constants.BASE_URL + "content/searchRequisition.jsf";
		case MULTIPLECLIENTINVENTORY:
			return Constants.BASE_URL + "content/multipleClientInventory.jsf";
		}
		return "";
	}

	public boolean getRenderAdminMenu() {
		if (!userSessionBean.isUserInSession())
			return false;
		HashSet<String> roles = new HashSet<>();
		roles.add(Role.PROGRAMMING_ROLE);
		roles.add(Role.ADMIN_ROLE);
		return userSessionBean.hasAnyRole(roles);
	}

	public boolean getRenderInventoryMenu() {
		if (!userSessionBean.isUserInSession())
			return false;
		HashSet<String> roles = new HashSet<>();
		roles.add(Role.PROGRAMMING_ROLE);
		roles.add(Role.ADMIN_ROLE);
		roles.add(Role.CHIEF_ROLE);
		roles.add(Role.DIGITATOR_ROLE);
		roles.add(Role.CLIENT_ROLE);
		return userSessionBean.hasAnyRole(roles);
	}
	
	public boolean getRenderInventoryAdminMenu(){
		if (!userSessionBean.isUserInSession())
			return false;
		HashSet<String> roles = new HashSet<>();
		roles.add(Role.PROGRAMMING_ROLE);
		roles.add(Role.ADMIN_ROLE);
		roles.add(Role.CHIEF_ROLE);
		roles.add(Role.DIGITATOR_ROLE);
		return userSessionBean.hasAnyRole(roles);
	}

	public boolean getRenderClientInventoryMenu() {
		if (!userSessionBean.isUserInSession())
			return false;
		return (userSessionBean.hasRole(Role.CLIENT_ROLE));
	}

	public MenuModel getLeftMenuModel() {
		HashSet<String> roles = new HashSet<>();
		roles.add(Role.PROGRAMMING_ROLE);
		roles.add(Role.ADMIN_ROLE);
		if (userSessionBean.hasAnyRole(roles)) {

			MenuModel mm = new DefaultMenuModel();
			mm.addElement(getMenuItem(USERS));
			mm.addElement(getMenuItem(CLIENTS));
			mm.addElement(getMenuItem(LOG));
			mm.addElement(getMenuItem(MAIL));

			return mm;
		} else {
			return new DefaultMenuModel();
		}
	}

	private DefaultMenuItem getMenuItem(int menu) {
		DefaultMenuItem menuItem = new DefaultMenuItem();
		switch (menu) {
		case USERS:
			menuItem.setValue("Usuarios");
			menuItem.setUrl("/admin/users.jsf");
			break;
		case CLIENTS:
			menuItem.setValue("Clientes");
			menuItem.setUrl("/admin/clients.jsf");
			break;
		case LOG:
			menuItem.setValue("Registro");
			menuItem.setUrl("/content/updatelog.jsf");
			break;
		case MAIL:
			menuItem.setValue("Correo");
			menuItem.setUrl("/admin/emailAdmin.jsf");
			break;
		default:
			menuItem = null;
		}
		return menuItem;
	}

	public int getUsers() {
		return USERS;
	}

	public int getClients() {
		return CLIENTS;
	}

	public int getInventory() {
		return INVENTORY;
	}

	public int getProducts() {
		return PRODUCTS;
	}

	public int getLog() {
		return LOG;
	}

	public int getRequisitions() {
		return REQUISITIONS;
	}
	
	public int getMultipleClientInventory(){
		return MULTIPLECLIENTINVENTORY;
	}

	public int getMail() {
		return MAIL;
	}
	

}
