
		function userCreated(data){
				showDialog("Usuario Creado.", data.reason, blankFunction, true);
				addUserToTable(data.object);
			}

		function userUpdated(data){
				showDialog("Usuario Actualizado.", data.reason, blankFunction, true);
				var user = data.object;
				$("#id-"+user.id).html(user.id);
				$("#username-"+user.id).html(user.username);
				$("#email-"+user.id).html(user.email);
			}

		function userCreatedFailed(data){
				showDialog("Usuario no creado", data.reason, function(){
						openCreateUser();
					});
			}

		function userUpdatedFailed(data){
				showDialog("No se pudo actualizar.", data.reason, function(){
					$("#dialog-user").dialog({
						modal: true,
						title: "Actualizar Usuario",
						buttons: {
						Actualizar: function(){
								$("#dialog-user").dialog('close');
								submitForm("#user-create-form", baseUrl + "rest/admin/users/update", userUpdated, userUpdatedFailed, function(){
									showDialog("Error", "No se pudo subir la foto", blankFunction);
								});
							}
						}
					});
					$('#dialog-user').dialog('option', 'position', {my:'center top', at:'center top+150', of:window });
					});
			}

		function openCreateUser(){
				$("#id").val("");
				$("#username").val("");
				$("#email").val("");
				$("#password").val("");
				$("#dialog-user").removeClass("hidden");
				$("#dialog-user").dialog({
					modal: true,
					title: "Crear Usuario",
					buttons: {
					Crear: function(){
							$("#dialog-user").dialog('close');
							submitForm("#user-create-form", baseUrl + "rest/admin/users/create", userCreated, userCreatedFailed, function(){
								showDialog("Error", "No se pudo subir la foto", blankFunction);
							});
						}
					}
				});
				$('#dialog-user').dialog('option', 'position', {my:'center top', at:'center top+150', of:window });
			}

		function openUpdateUser(id){
				$("#id").val($("#id-"+id).html());
				$("#username").val($("#username-"+id).html());
				$("#email").val($("#email-"+id).html());
				$("#password").val("");
				$("#dialog-user").removeClass("hidden");
				$("#dialog-user").dialog({
					modal: true,
					title: "Actualizar Usuario",
					buttons: {
					Actualizar: function(){
							$("#dialog-user").dialog('close');
							submitForm("#user-create-form", baseUrl + "rest/admin/users/update", userUpdated, userUpdatedFailed, function(){
								showDialog("Error", "No se pudo subir la foto", blankFunction);
							});
						}
					}
				});
				$('#dialog-user').dialog('option', 'position', {my:'center top', at:'center top+150', of:window });
			}

		function usersInitialFunction(){
			  showOverlay();
			  showDialog("Cargando Usuarios", "Por favor espere mientras se cargan los usuarios de base de datos.", blankFunction, true);
			  getRequest(baseUrl+"rest/admin/users/index", usersLoaded, usersWontLoad);
			  }

		function usersLoaded(data){
			hideOverlay();
			hideDialog();
				for(var i = 0; i < data.list.length; i++){
					var user = data.list[i];
					addUserToTable(user);
					}
			}

		function usersWontLoad(){
				showDialog("Carga fallida", "No cargan los usuarios. Quiere repetir?", usersInitialFunction);
			}
		
		function addUserToTable(user){
			var string = "<tr>";
			string += "<td id='id-"+user.id+"'>" + user.id +"</td>";
			string += "<td id='username-"+user.id+"'>" + user.username +"</td>";
			string += "<td id='email-"+user.id+"'>" + user.email +"</td>";
			string += "<td id='roles-"+user.id+"'>";
			
			for( var j = 0; j < user.userByRoles.length; j++ ){
				string += "<span id='role-for-"+user.id+"-"+user.userByRoles[j].role.roleName+"' >";
				string += user.userByRoles[j].role.roleName;
				string += "<a type='button' onclick=\"confirmDeleteRole("+user.id+", '"+user.userByRoles[j].role.roleName+"');\" >";
				string += "<span class='glyphicon glyphicon-trash' aria-hidden='true'></span></a></span> | "; 
			}
			
			string += "</td>";
			string += "<td><button type='button' class='btn-sm btn-primary' onclick='openUpdateUser("+user.id+")'>Actualizar</button> | ";
			string += "<button type='button' class='btn-sm btn-primary' onclick=\"openAddRole("+user.id+", '"+user.username+"')\">Agregar Rol</button></td>";
			string += "</tr>";
			$("#user-table > tbody:last-child").append(string);
		}
		
		function openAddRole(userid, username){
			showOverlay();
			showDialog("Cargando Roles.", "Espere mientras se cargan los usuarios", blankFunction);
			getRequest(baseUrl+"rest/admin/users/getavailableroles/"+userid, function(data){
				hideDialog();
				console.log(data.list);
				$("#role-select").find("option").remove();
				for( var i = 0; i < data.list.length; i++ ){
					$("#role-select").append("<option value='"+data.list[i]+"'>"+data.list[i]+"</option>");
				}
				hideOverlay();
				$("#dialog-role").removeClass("hidden");
				$("#dialog-role").dialog({
					modal: true,
					title: "Agregar Rol a usuario " + username,
					buttons: {
					Agregar: function(){
						showOverlay();
						var rolename = $("#role-select").find(":selected").text();
						if( rolename != "blank" ){
						getRequest(baseUrl+"rest/admin/users/addrole/"+userid+"/"+rolename, function(data){
							hideOverlay();
							$("#dialog-role").dialog('close');
							if( !data.success ){
								showDialog("No se pudo agregar", data.reason, blankFunction);
							} else {
								showDialog("Exito", "Se ha agregado el rol correctamente", blankFunction, true);
								var string = "<span id='role-for-"+userid+"-"+rolename+"' >" + rolename;
								string += "<a type='button' onclick=\"confirmDeleteRole("+userid+", '"+rolename+"'); \" >";
								string += "<span class='glyphicon glyphicon-trash' aria-hidden='true'></span></button></a> | "; 
								$("#roles-"+userid).append(string);
							}
						}
						, function(){
								hideOverlay();
								$("#dialog-role").dialog('close');
								showDialog("Error", "No se pudo agregar el rol", blankFunction);
							}, function(){
								showDialog("Error", "No se pudo subir la foto", blankFunction);
							});
						}
					}
				}});
				$('#dialog-role').dialog('option', 'position', {my:'center top', at:'center top+150', of:window });
				
			}, function(){
				hideDialog();
				hideOverlay();
				showDialog("Error", "No se pudieron cargar los roles", blankFunction );
			} );
		}
		
		function confirmDeleteRole(userid, rolename){
			showYesNoDialog("Eliminando Rol", "Esta seguro que desea eliminar el rol " + rolename +" del usuario?", function(){
				showOverlay();
				getRequest(baseUrl+"rest/admin/users/deleterole/" + userid + "/" + rolename, function(data){
					hideOverlay();
					if( data.success ){
						showDialog("Exito", "El rol fue eliminado correctamente", blankFunction, true);
						$("#role-for-" + userid + "-" + rolename).remove();
					} else {
						showDialog("No se pudo eliminar", data.reason, blankFunction);
					}
				}, function(){
					showDialog("Error", "No se pudo subir la foto", blankFunction);
				});
			} );
		}


		$(function() {
			  usersInitialFunction();
		});
		