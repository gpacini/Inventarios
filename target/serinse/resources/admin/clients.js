
		function clientCreated(data){
				showDialog("Cliente Creado.", data.reason, blankFunction, true);
				addClientToTable(data.object);
			}

		function clientUpdated(data){
				showDialog("Cliente Actualizado.", data.reason, blankFunction, true);
				var client = data.object;
				$("#id-"+client.id).html(client.id);
				$("#name-"+client.id).html(client.name);
				$("#description-"+client.id).html(client.description);
			}

		function clientCreatedFailed(data){
				showDialog("Cliente no creado", data.reason, function(){
						openCreateClient();
					});
			}

		function clientUpdatedFailed(data){
				showDialog("No se pudo actualizar.", data.reason, function(){
					$("#dialog-client").dialog({
						modal: true,
						title: "Actualizar Cliente",
		    			position: { my: 'center center', at: 'center center', of: '#wrapper' },
						buttons: {
						Actualizar: function(){
								$("#dialog-client").dialog('close');
								submitForm("#client-create-form", baseUrl + "rest/admin/clients/update", clientUpdated, clientUpdatedFailed, function(){
									showDialog("Error", "No se pudo subir la foto", blankFunction);
								});
							}
						}
					});
					$('#dialog-client').dialog('option', 'position', {my:'center top', at:'center top+150', of:window });
					});
			}

		function openCreateClient(){
				$("#id").val("");
				$("#name").val("");
				$("#description").val("");
				$("#dialog-client").removeClass("hidden");
				$("#dialog-client").dialog({
					modal: true,
					title: "Crear Cliente",
					buttons: {
					Crear: function(){
							$("#dialog-client").dialog('close');
							submitForm("#client-create-form", baseUrl + "rest/admin/clients/create", clientCreated, clientCreatedFailed, function(){
								showDialog("Error", "No se pudo subir la foto", blankFunction);
							});
						}
					}
				});
				$('#dialog-client').dialog('option', 'position', {my:'center top', at:'center top+150', of:window });
			}

		function openUpdateClient(id){
				$("#id").val($("#id-"+id).html());
				$("#name").val($("#name-"+id).html());
				$("#description").val($("#description-"+id).html());
				$("#dialog-client").removeClass("hidden");
				$("#dialog-client").dialog({
					modal: true,
					title: "Actualizar Cliente",
					buttons: {
					Actualizar: function(){
							$("#dialog-client").dialog('close');
							submitForm("#client-create-form", baseUrl + "rest/admin/clients/update", clientUpdated, clientUpdatedFailed, function(){
								showDialog("Error", "No se pudo subir la foto", blankFunction);
							});
						}
					}
				});
				$('#dialog-client').dialog('option', 'position', {my:'center top', at:'center top+150', of:window });
			}

		function clientsInitialFunction(){
			  showOverlay();
			  showDialog("Cargando Clientes", "Por favor espere mientras se cargan los clientes de base de datos.", blankFunction, true);
			  getRequest(baseUrl+"rest/admin/clients/index", clientsLoaded, clientsWontLoad, function(){
					showDialog("Error", "No se pudo subir la foto", blankFunction);
				});
			  }

		function clientsLoaded(data){
			hideOverlay();
			hideDialog();
				for(var i = 0; i < data.list.length; i++){
					var client = data.list[i];
					addClientToTable(client);
					}
			}

		function clientsWontLoad(){
				showDialog("Carga fallida", "No cargan los clientes. Quiere repetir?", clientsInitialFunction);
			}
		
		function addClientToTable(client){
			var string = "<tr>";
			string += "<td id='id-"+client.id+"'>" + client.id +"</td>";
			string += "<td id='name-"+client.id+"'>" + client.name +"</td>";
			string += "<td id='description-"+client.id+"'>" + client.description +"</td>";
			string += "<td id='users-"+client.id+"'>";
			
			for( var j = 0; j < client.users.length; j++ ){
				string += "<span id='user-for-"+client.id+"-"+client.users[j].username+"' >";
				string += client.users[j].username;
				string += "<a type='button' onclick=\"confirmDeleteUser("+client.id+", '"+client.users[j].username+"');\" >";
				string += "<span class='glyphicon glyphicon-trash' aria-hidden='true'></span></a></span> | "; 
			}
			
			string += "</td>";
			string += "<td><button type='button' class='btn-sm btn-primary' onclick='openUpdateClient("+client.id+")'>Actualizar</button> | ";
			string += "<button type='button' class='btn-sm btn-primary' onclick=\"openAddUser("+client.id+", '"+client.name+"')\">Agregar Usuario</button></td>";
			string += "</tr>";
			$("#user-table > tbody:last-child").append(string);
		}
		
		function openAddUser(clientid, username){
			showOverlay();
			showDialog("Cargando Usuarios.", "Espere mientras se cargan los roles", blankFunction);
			getRequest(baseUrl+"rest/admin/clients/getavailableusers/"+clientid, function(data){
				hideDialog();
				console.log(data.list);
				$("#user-select").find("option").remove();
				for( var i = 0; i < data.list.length; i++ ){
					$("#user-select").append("<option value='"+data.list[i]+"'>"+data.list[i]+"</option>");
				}
				hideOverlay();
				$("#dialog-user").removeClass("hidden");
				$("#dialog-user").dialog({
					modal: true,
					title: "Agregar Usuario al Cliente " + username,
					buttons: {
					Agregar: function(){
						showOverlay();
						var username = $("#user-select").find(":selected").text();
						if( username != "blank" ){
						getRequest(baseUrl+"rest/admin/clients/adduser/"+clientid+"/"+username, function(data){
							hideOverlay();
							$("#dialog-user").dialog('close');
							if( !data.success ){
								showDialog("No se pudo agregar", data.reason, blankFunction);
							} else {
								showDialog("Exito", "Se ha agregado el usuario correctamente", blankFunction, true);
								var string = "<span id='user-for-"+clientid+"-"+username+"' >" + username;
								string += "<a type='button' onclick=\"confirmDeleteUser("+clientid+", '"+username+"'); \" >";
								string += "<span class='glyphicon glyphicon-trash' aria-hidden='true'></span></a></span> | "; 
								$("#users-"+clientid).append(string);
							}
						}
						, function(){
								hideOverlay();
								$("#dialog-user").dialog('close');
								showDialog("Error", "No se pudo agregar el usuario", blankFunction);
							}, function(){
								showDialog("Error", "No se pudo subir la foto", blankFunction);
							});
						}
					}
				}});
				$('#dialog-user').dialog('option', 'position', {my:'center top', at:'center top+150', of:window});
				
			}, function(){
				hideDialog();
				hideOverlay();
				showDialog("Error", "No se pudieron cargar los usuarios", blankFunction );
			} );
		}
		
		function confirmDeleteUser(clientid, username){
			showYesNoDialog("Eliminando Usuario", "Esta seguro que desea eliminar el usuario " + username +" del cliente?", function(){
				showOverlay();
				getRequest(baseUrl+"rest/admin/clients/deleteuser/" + clientid + "/" + username, function(data){
					hideOverlay();
					if( data.success ){
						showDialog("Exito", "El usuario fue eliminado correctamente", blankFunction, true);
						$("#users-for-" + clientid + "-" + username).remove();
					} else {
						showDialog("No se pudo eliminar", data.reason, blankFunction);
					}
				}, function(){
					showDialog("Error", "No se pudo subir la foto", blankFunction);
				});
			} );
		}


		$(function() {
			  clientsInitialFunction();
		});
		