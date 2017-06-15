
		function inventoryCreated(data){
				showDialog("Inventario Creado.", data.reason, blankFunction, true);
				addInventoryToTable(data.object);
			}

		function inventoryCreatedFailed(data){
				showDialog("Inventario no creado", data.reason, function(){
						openCreateInventory();
					});
			}

		function openCreateInventory(){
				$("#id").val("");
				$("#client-select").html("");
				$("#dialog-inventory").removeClass("hidden");
				getRequest(baseUrl+"rest/inventory/availableclients", function(data){
					for( var i = 0; i < data.list.length; i++ ){
						var string = "<option value='" + data.list[i] + "' >" + data.list[i] + "</option>";
						$("#client-select").append(string);
					}
					$("#dialog-inventory").dialog({
						modal: true,
						title: "Crear Inventario",
						buttons: {
						Crear: function(){
								$("#dialog-inventory").dialog('close');
								submitForm("#inventory-create-form", baseUrl + "rest/inventory/create", inventoryCreated, inventoryCreatedFailed, function(){
									showDialog("Error", "No se pudo subir la foto", blankFunction);
								});
							}
						}
					});
					$('#dialog-inventory').dialog('option', 'position', {my:'center top', at:'center top+150', of:window });
				}, function(data){
					
				});
			}

		function inventoryInitialFunction(){
			  showOverlay();
			  showDialog("Cargando Inventarios", "Por favor espere mientras se cargan los inventarios de base de datos.", blankFunction, true);
			  getRequest(baseUrl+"rest/inventory/index", inventoriesLoaded, inventoriesWontLoad, function(){
					showDialog("Error", "No se pudo subir la foto", blankFunction);
				});
			  }

		function inventoriesLoaded(data){
			hideOverlay();
			hideDialog();
				for(var i = 0; i < data.list.length; i++){
					var inventory = data.list[i];
					addInventoryToTable(inventory);
					}
			}

		function inventoriesWontLoad(){
				showDialog("Carga fallida", "No cargan los inventarios. Quiere repetir?", inventoryInitialFunction);
			}
		
		function addInventoryToTable(inventory){
			var string = "<tr>";
			string += "<td class='hidden' id='id-"+inventory.id+"'>" + inventory.id +"</td>";
			string += "<td id='client-"+inventory.id+"'>" + inventory.client +"</td>";
			
			string += "</td>";
			string += "<td><button type='button' class='btn-sm btn-primary' onclick=\"redirectToInventory("+inventory.id+")\">Ingresar</button></td>";
			string += "</tr>";
			$("#inventory-table > tbody:last-child").append(string);
		}
		
		function redirectToInventory(id){
			window.location.href = baseUrl+"content/products.jsf?id=" + id;
		}

		$(function() {
			inventoryInitialFunction();
		});
		