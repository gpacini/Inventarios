package com.serinse.web.controllers.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.serinse.ejb.impl.administration.UserBean;
import com.serinse.ejb.impl.client.ClientBean;
import com.serinse.ejb.impl.inventory.CustomRowBean;
import com.serinse.ejb.impl.inventory.InventoryBean;
import com.serinse.pers.entity.adm.Role;
import com.serinse.pers.entity.adm.User;
import com.serinse.pers.entity.client.Client;
import com.serinse.pers.entity.inventory.Inventory;
import com.serinse.pers.entity.inventory.customRow.CustomRowByClient;

@Named
@ViewScoped
public class ClientsController implements Serializable{

	private static final long serialVersionUID = 7264192318923918L;
	
	@Inject ClientBean clientBean;
	@Inject InventoryBean inventoryBean;
	@Inject UserBean userBean;
	@Inject CustomRowBean customRowBean;
	
	private LazyDataModel<Client> clients;
	
	private Client clientToEdit;
	private Inventory inventoryToEdit;
	
	private List<String> selectedUsers;
	private List<User> posibleUsers;
	
	private CustomRowByClient row;

	private int action;
	private static final int ADD_ACTION = 1;
	private static final int EDIT_ACTION = 2;
	
	@PostConstruct
	public void init(){
		action = ADD_ACTION;
		clientToEdit = new Client();
		inventoryToEdit = new Inventory(); 
		
		posibleUsers = userBean.findAllByRole(Role.CLIENT_ROLE);

		clients = new LazyDataModel<Client>(){
			private static final long serialVersionUID = 175674123124345L;

			@Override
            public List<Client> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
				Map<String,String> newMap = new HashMap<String,String>();
				for (Map.Entry<String, Object> entry : filters.entrySet()) {
				       if(entry.getValue() instanceof String){
				            newMap.put(entry.getKey(), (String) entry.getValue());
				          }
				 }
				clients.setRowCount(clientBean.count(newMap));
                return clientBean.getResultList(first, pageSize, sortField, sortOrder, newMap);
            }
		};
		
		clients.setRowCount(clientBean.count(new HashMap<String, String>()));
		row = new CustomRowByClient();
	}
	
	public void saveClient(){
		clientToEdit.setDescription(clientToEdit.getDescription().trim());
		clientToEdit.setName(clientToEdit.getName().trim());
		
		if(clientToEdit.getName().equals("") ){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "El nombre no puede estar en blanco", ""));
			return;
		}
		
		if( action == ADD_ACTION ){
			saveNewClient();
		} else if( action == EDIT_ACTION ){
			editCurrentClient();
		}
	}
	
	public void editCurrentClient(){
		Client oldClient = clientBean.findByName(clientToEdit.getName());
		if( oldClient != null && !oldClient.getId().equals(clientToEdit.getId()) ){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ya existe un cliente con ese nombre", ""));
			return;
		}

		clientBean.update(clientToEdit);
		RequestContext.getCurrentInstance().execute("PF('clientEditionDialog').hide()");
		RequestContext.getCurrentInstance().update("clientsForm:clientsDataTable");
	}
	
	public void saveNewClient(){
		Client oldClient = clientBean.findByName(clientToEdit.getName());
		if( oldClient != null ){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ya existe un cliente con ese nombre", ""));
			return;
		}
		clientBean.save(clientToEdit);
		inventoryToEdit.setClient(clientBean.findByName(clientToEdit.getName()));
		inventoryBean.save(inventoryToEdit);
		
		RequestContext.getCurrentInstance().execute("PF('clientEditionDialog').hide()");
		RequestContext.getCurrentInstance().update("clientsForm:clientsDataTable");
	}
	
	public void addClient(){
		clientToEdit = new Client();
		inventoryToEdit = new Inventory();
		action = ADD_ACTION;
		
		RequestContext.getCurrentInstance().update("clientsForm:clientEditionDialog");
		RequestContext.getCurrentInstance().execute("PF('clientEditionDialog').show()");
	}
	
	public void editClient(Client client){
		clientToEdit = client;
		inventoryToEdit = inventoryBean.findByClientId(client.getId());
		action = EDIT_ACTION;

		RequestContext.getCurrentInstance().update("clientsForm:clientEditionDialog");
		RequestContext.getCurrentInstance().execute("PF('clientEditionDialog').show()");
	}
	
	public void addUsers(Client client){
		clientToEdit = client;
		List<User> addedUsers = userBean.getUsersByClient(clientToEdit.getName());
		selectedUsers = new ArrayList<>();
		for( User user : addedUsers ){
			selectedUsers.add(user.getUsername());
		}
		
		RequestContext.getCurrentInstance().update("clientsForm:userSelectionDialog");
		RequestContext.getCurrentInstance().execute("PF('userSelectionDialog').show()");
	}
	
	public void saveSelectedUsers(){
		userBean.deleteClientsUsers(clientToEdit.getName());
		
		for( String sUser : selectedUsers ){
			System.out.println("Cliente" + clientToEdit.getName() + " Usuario: " + sUser);
			User user = userBean.findUserByUsername(sUser);
			userBean.saveClientByUser(clientToEdit, user);
		}

		RequestContext.getCurrentInstance().update("clientsForm:clientsDataTable");
		RequestContext.getCurrentInstance().execute("PF('userSelectionDialog').hide()");
	}
	
	public void activateCosts(Client client){
		Inventory inv = inventoryBean.findByClientId(client.getId());
		inv.setUnitCost(true);
		inventoryBean.update(inv);
	}
	
	public void deactivateCosts(Client client){
		Inventory inv = inventoryBean.findByClientId(client.getId());
		inv.setUnitCost(false);
		inventoryBean.update(inv);
	}
	
	public Boolean getCostsActivated(Client client){
		Inventory inventory = inventoryBean.findByClientId(client.getId());
		if( inventory.getUnitCost() == null ) return false;
		return inventory.getUnitCost();
	}
	
	public void addCustomRow(Client client){
		clientToEdit = client;
		if( customRowBean.exists(clientToEdit, 1) && customRowBean.exists(clientToEdit, 2) && customRowBean.exists(clientToEdit, 3)){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "No se puede agregar la columna", "Ya existen muchas columnas para el cliente"));
		}
	}
	
	public List<CustomRowByClient> getClientsRows(Client c){
		customRowBean.findAllById();
		return null; //TODO
	}
	
	public void saveCustomRowByClient(){
		row.setClient(clientToEdit);
		if( !customRowBean.exists(clientToEdit, 1)){
			row.setRowId(1);
			customRowBean.save(row);
		} else if( !customRowBean.exists(clientToEdit, 2)){
			row.setRowId(2);
			customRowBean.save(row);
		} else if( !customRowBean.exists(clientToEdit, 3)){
			row.setRowId(3);
			customRowBean.save(row);
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "No se puede agregar la columna", "Ya existen muchas columnas para el cliente"));
		}
		
		RequestContext.getCurrentInstance().update("clientsForm:clientsDataTable");
		RequestContext.getCurrentInstance().execute("PF('addCustomRowDialog').hide()");
	}
	
	public List<User> getClientsUsers(Client client){
		return userBean.getUsersByClient(client.getName());
	}

	public LazyDataModel<Client> getClients() {
		return clients;
	}

	public void setClients(LazyDataModel<Client> clients) {
		this.clients = clients;
	}

	public Client getClientToEdit() {
		return clientToEdit;
	}

	public void setClientToEdit(Client clientToEdit) {
		this.clientToEdit = clientToEdit;
	}

	public List<String> getSelectedUsers() {
		return selectedUsers;
	}

	public void setSelectedUsers(List<String> selectedUsers) {
		this.selectedUsers = selectedUsers;
	}

	public List<User> getPosibleUsers() {
		return posibleUsers;
	}

	public void setPosibleUsers(List<User> posibleUsers) {
		this.posibleUsers = posibleUsers;
	}

	public CustomRowByClient getRow() {
		return row;
	}

	public void setRow(CustomRowByClient row) {
		this.row = row;
	}
	
}
