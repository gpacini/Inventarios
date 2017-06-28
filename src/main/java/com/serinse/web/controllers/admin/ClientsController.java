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
import org.primefaces.model.DualListModel;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.serinse.ejb.impl.administration.UserBean;
import com.serinse.ejb.impl.client.ClientBean;
import com.serinse.ejb.impl.inventory.InventoryBean;
import com.serinse.pers.entity.adm.Role;
import com.serinse.pers.entity.adm.User;
import com.serinse.pers.entity.client.Client;
import com.serinse.pers.entity.client.ClientByUser;
import com.serinse.pers.entity.inventory.Inventory;

@Named
@ViewScoped
public class ClientsController implements Serializable{

	private static final long serialVersionUID = 7264192318923918L;
	
	@Inject ClientBean clientBean;
	@Inject InventoryBean inventoryBean;
	@Inject UserBean userBean;
	
	private LazyDataModel<Client> clients;
	private DualListModel<String> users;
	
	private Client clientToEdit;
	private Inventory inventoryToEdit;
	
	private Boolean showClientDetails;
	private Boolean newClient;
	
	@PostConstruct
	public void init(){
		initClientsModel();
	}
	
	private void initClientsModel(){
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

			@Override
			public Client getRowData(String rowKey){
				@SuppressWarnings("unchecked")
				List<Client> clients = (List<Client>) getWrappedData();
			    Long value = Long.valueOf(rowKey);

			    for (Client client : clients) {
			        if (client.getId().equals(value)) {
			            return client;
			        }
			    }
			    return null;
			}
			
			@Override
			public Object getRowKey(Client client) {
			    return client != null ? client.getId() : null;
			}
		};
		
		clients.setRowCount(clientBean.count(new HashMap<String, String>()));
	}
	
	public void reset(){
		clientToEdit = null;
		inventoryToEdit = null;
		users = null;
		showClientDetails = false;
		newClient = false;
	}
	
	public void clientSelected(){
		List<User> allUsers = userBean.findAllByRole(Role.CLIENT_ROLE);
		List<User> selectedUsers = userBean.getUsersByClient(clientToEdit.getName());
		List<String> selected = new ArrayList<>();
		List<String> source = new ArrayList<>();
		for( User user : allUsers ){
			if( selectedUsers.contains(user) ){
				selected.add(user.getUsername());
			} else {
				source.add(user.getUsername());
			}
		}
		users = new DualListModel<>(source, selected);
		
		inventoryToEdit = inventoryBean.findByClientId(clientToEdit.getId());
		newClient = false;
		showDetailsDialog();
	}
	
	public void addClient(){
		clientToEdit = new Client();
		inventoryToEdit = new Inventory();
		List<User> allUsers = userBean.findAllByRole(Role.CLIENT_ROLE);
		List<String> source = new ArrayList<>();
		for( User user : allUsers ){
			source.add(user.getUsername());
		}
		users = new DualListModel<>(source, new ArrayList<>());
		newClient = true;
		
		showDetailsDialog();
	}
	
	public void saveClient(){
		if( newClient ){
			Client old = clientBean.findByName(clientToEdit.getName());
			if( old != null ){
				showErrorMessage("Ya existe un cliente con ese nombre");
				return;
			}
			clientBean.save(clientToEdit);
			inventoryToEdit.setClient(clientToEdit);
			inventoryBean.save(inventoryToEdit);
		} else {
			Client old = clientBean.findByName(clientToEdit.getName());
			if( old != null && !old.getId().equals(clientToEdit.getId()) ){
				showErrorMessage("No puede cambiar el nombre, ya existe un cliente con ese nombre");
				return;
			}
			clientBean.update(clientToEdit);
			inventoryToEdit.setClient(clientToEdit);
			inventoryBean.update(inventoryToEdit);
		}
		
		List<ClientByUser> usersForClient = new ArrayList<>();
		for(String username : users.getTarget()){
			User user = userBean.findUserByUsername(username);
			ClientByUser cbu = new ClientByUser(user, clientToEdit);
			usersForClient.add(cbu);
		}
		clientToEdit.setUsers(usersForClient);
		clientBean.update(clientToEdit);
		
		RequestContext.getCurrentInstance().execute("PF('clientDetailsDialog').hide();");
	}
	
	public void showDetailsDialog(){
		showClientDetails = true;
	}
	
	public static void showErrorMessage(String message){
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, ""));
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

	public DualListModel<String> getUsers() {
		return users;
	}

	public void setUsers(DualListModel<String> users) {
		this.users = users;
	}

	public Boolean getShowClientDetails() {
		return showClientDetails;
	}

	public void setShowClientDetails(Boolean showClientDetails) {
		this.showClientDetails = showClientDetails;
	}

	public Inventory getInventoryToEdit() {
		return inventoryToEdit;
	}

	public void setInventoryToEdit(Inventory inventoryToEdit) {
		this.inventoryToEdit = inventoryToEdit;
	}
	
}
