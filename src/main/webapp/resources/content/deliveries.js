function deliveryCreated(data) {
	showDialog("Despacho/Entrega Creado.", data.reason, blankFunction, true);
	addDeliveryToTable(data.object);
	if( !r ){
		$(".action-buttons").remove();
	}
}

function deliveryUpdated(data) {
	showDialog("Despacho/Entrega Actualizado.", data.reason, blankFunction, true);
	var delivery = data.object;
	var id = delivery.id;
	$("#id-"+id).html(delivery.id);
	$("#productCode-"+id).html(delivery.productCode);
	$("#productName-"+id).html(delivery.productName);
	$("#productBrand-"+id).html(delivery.productBrand);
	$("#whoAsked-"+id).html(delivery.whoAsked);
	$("#whoReceived-"+id).html(delivery.whoReceived);
	$("#askDate-"+id).html(delivery.askDate);
	$("#deliveryPoint-"+id).html(delivery.deliveryPoint);
	$("#deliveryType-"+id).html(delivery.deliveryType);
	$("#quantity-"+id).html(delivery.quantity);
	$("#status-"+id).html(delivery.status);
	$("#days-"+id).html(delivery.daysInStorehouse);
}

function deliveryCreatedFailed(data) {
	showDialog("Entrega/Despacho no creado", data.reason, function() {
		openCreateDelivery();
	});
}

function deliveryUpdatedFailed(data) {
	showDialog("No se pudo actualizar.", data.reason, function() {
		$("#dialog-delivery").dialog(
				{
					width : ($(window).width() * 0.6),
					modal : true,
					title : "Actualizar Entrega/Despacho",
					position : {
						my : 'center center',
						at : 'center center',
						of : '#wrapper'
					},
					buttons : {
						Actualizar : function() {
							if( prodId == -1 ){
								getRequest(baseUrl+"rest/products/code/" + $("#productCode").val(), function(data){
									$("#dialog-delivery").dialog('close');
									submitForm("#delivery-create-form", baseUrl
											+ "rest/delivery/update/" + data.o.id,
											deliveryUpdated, deliveryUpdatedFailed, function(){
										showDialog("Error", "No se pudo actualizar", blankFunction);
									});
								}, function(data){
									showDialog("Error", data.reason, blankFunction);
								});
							} else {
								$("#dialog-delivery").dialog('close');
								submitForm("#delivery-create-form", baseUrl
										+ "rest/delivery/update/" + prodId,
										deliveryUpdated, deliveryUpdatedFailed, function(){
									showDialog("Error", "No se pudo actualizar", blankFunction);
								});
							}
						}
					}
				});
		$('#dialog-delivery').dialog('option', 'position', {
			my : 'center center',
			at : 'center center',
			of : window
		});
	});
}

function openCreateDelivery(deliveryType) {
	modifyDialog(deliveryType);
	$("#id").val("");
	$("#whoAsked").val("");
	$("#whoReceived").val("");
	$("#askDate").val("");
	$("#leadTime").val("");
	$("#deliveryDate").val("");
	$("#deliveryPoint").val("");
	$("#cities").val("");
	$("#status").val("");
	$("#quantity").val("");
	$("#reason").val("");
	$("#reason_div").addClass("hidden");
	$("#storehouse").removeAttr("disabled");
	if( prodId == -1 ){
		showDialog("Desactivado", "Funcion desactivada temporalmente.", blankFunction);
	} else {
		getRequest(baseUrl + "rest/products/" + prodId, function(data){
			$("#productCode").val(data.o.code);
			$("#productCode").attr("disabled", "disabled");
			$("#productName").val(data.o.material);
			$("#productName").attr("disabled", "disabled");
			$("#productBrand").val(data.o.brand);
			$("#productBrand").attr("disabled", "disabled");
			$("#dialog-delivery").removeClass("hidden");
			$("#dialog-delivery").dialog(
					{
						width : ($(window).width() * 0.6),
						modal : true,
						title : "Crear Despacho/Entrega",
						buttons : {
							Crear : function() {
								$("#dialog-delivery").dialog('close');
								submitForm("#delivery-create-form", baseUrl
										+ "rest/delivery/create/" + prodId,
										deliveryCreated, deliveryCreatedFailed, function(){
									showDialog("Error", "No se pudo crear", blankFunction);
								});
							}
						}
					});
			$('#dialog-delivery').dialog('option', 'position', {
				my : 'center center',
				at : 'center center',
				of : window
			});
		}, function(data){
			showDialog("Error", data.reason, blankFunction);
		}, function(){
			showDialog("Error", "No se puede crear un movimiento", blankReason);
		});
	}
}

function openUpdateDelivery(id) {
	var deliveryType = $("#deliveryType-" + id).html();
	modifyDialog(deliveryType);
	$("#id").val($("#id-" + id).html());
	$("#productCode").val($("#productCode-" + id).html());
	$("#productCode").attr("disabled", "disabled");
	$("#productName").val($("#productName-" + id).html());
	$("#productName").attr("disabled", "disabled");
	$("#productBrand").val($("#productBrand-" + id).html());
	$("#productBrand").attr("disabled", "disabled");
	$("#whoAsked").val($("#whoAsked-" + id).html());
	$("#whoReceived").val($("#whoReceived-" + id).html());
	$("#askDate").val($("#askDate-" + id).html());
	$("#leadTime").val($("#leadTime-" + id).html());
	$("#deliveryDate").val($("#deliveryDate-" + id).html());
	$("#deliveryPoint").val($("#deliveryPoint-" + id).html());
	$("#cities").val($("#cities-" + id).html());
	$("#status").val($("#status-" + id).html());
	$("#deliveryType").val($("#deliveryType-" + id).html());
	$("#quantity").val($("#quantity-" + id).html());
	$("#storehouse").attr("disabled", "disabled");
	$("#storehouse").val($("#storehouse-" + id).html());
	$("#reason").val("");
	$("#reason_div").removeClass("hidden");
	$("#dialog-delivery").removeClass("hidden");
	$("#dialog-delivery").dialog(
			{
				width : ($(window).width() * 0.6),
				modal : true,
				title : "Actualizar Despacho/Entrega",
				buttons : {
					Actualizar : function() {
						if( prodId == -1 ){
							console.log("El producto no esta asignado, se va a buscar su codigo.");
							getRequest(baseUrl+"rest/products/code/" + $("#productCode").val(), function(data){
								$("#dialog-delivery").dialog('close');
								submitForm("#delivery-create-form", baseUrl
										+ "rest/delivery/update/" + data.o.id,
										deliveryUpdated, deliveryUpdatedFailed, function(){
									showDialog("Error", "No se pudo actualizar", blankFunction);
								});
							}, function(data){
								showDialog("Error", data.reason, blankFunction);
							});
						} else {
							$("#dialog-delivery").dialog('close');
							submitForm("#delivery-create-form", baseUrl
									+ "rest/delivery/update/" + prodId,
									deliveryUpdated, deliveryUpdatedFailed, function(){
								showDialog("Error", "No se pudo actualizar", blankFunction);
							});
						}
					}
				}
			});
	$('#dialog-delivery').dialog('option', 'position', {
		my : 'center center',
		at : 'center center',
		of : window
	});
}

var prodId = -1;
var invId = -1;
var product_search = -1;
var date_search = -1;
var limit_search = -1;
var storehouses = [];
function deliveriesInitialFunction() {
	showOverlay();
	if (params['prodId'] == null && params['invId'] == null ) {
		showDialog(
				"No existe codigo de producto o inventario",
				"Error no se pueden cargar los despachos/entregas sin el codigo de producto.",
				blankFunction);
		$("#create-button").remove();
		return;
	}
	getRequest(baseUrl+"rest/storehouses/index", function(data){
		var shString = "";
		var divShString = "";
		for( var i = 0; i < data.list.length ; i++ ){
			shString += "<th>"+data.list[i].name+"</th>"
			$("#storehouse").append("<option value='"+data.list[i].name+"'>"+data.list[i].name+"</option>")
		}
		
		if( params['prodId'] != null ){
			prodId = params['prodId'];
		} 
		if( params['invId'] != null ) {
			invId = params['invId'];
		}
		if( params["search_prod"] != null ){
			product_search = params["search_prod"];
		}
		if( params["search_date"] != null ){
			date_search = params["search_date"];
		}
		if( params['limit'] != null ){
			limit_search = params['limit'];
		}
		if( product_search != -1 ){
			$("#product-search-box").val(product_search);
			showDialog(
					"Cargando Despachos/Entregas",
					"Por favor espere mientras se cargan las entregas/despachos de base de datos.",
					blankFunction, true);
			getRequest(baseUrl+"rest/delivery/search/product/"+product_search+"/"+invId, deliveriesLoaded, function(){
				showDialog("Carga fallida", data.reason, blankFunction);
			});
		} else if( date_search != -1 ){
			$("#date-search-box").val(date_search);
			if( prodId == -1 ){
				showDialog(
						"Cargando Despachos/Entregas",
						"Por favor espere mientras se cargan las entregas/despachos de base de datos.",
						blankFunction, true);
				getRequest(baseUrl+"rest/delivery/search/date_inv/"+date_search+"/"+invId, deliveriesLoaded, function(){
					showDialog("Carga fallida", data.reason, blankFunction);
				});
			} else {
				showDialog(
						"Cargando Despachos/Entregas",
						"Por favor espere mientras se cargan las entregas/despachos de base de datos.",
						blankFunction, true);
				getRequest(baseUrl+"rest/delivery/search/date_prod/"+date_search+"/"+prodId, deliveriesLoaded, function(){
					showDialog("Carga fallida", data.reason, blankFunction);
				});
			}
		} else if( prodId != -1 ){
			showDialog(
					"Cargando Despachos/Entregas",
					"Por favor espere mientras se cargan las entregas/despachos de base de datos.",
					blankFunction, true);
			getRequest(baseUrl + "rest/delivery/index/product/" + prodId, deliveriesLoaded,
					deliveriesWontLoad);
			$("#search_by_product_div").remove();
		} else if(limit_search != -1){
			showDialog(
					"Cargando Despachos/Entregas",
					"Por favor espere mientras se cargan las entregas/despachos de base de datos.",
					blankFunction, true);
			getRequest(baseUrl + "rest/delivery/index/inventory/" + invId +"/" + limit_search, deliveriesLoaded,
					deliveriesWontLoad);
			showLimitPanel(limit_search);
		}
		else {
			showDialog(
					"Cargando Despachos/Entregas",
					"Por favor espere mientras se cargan las entregas/despachos de base de datos.",
					blankFunction, true);
			getRequest(baseUrl + "rest/delivery/index/inventory/" + invId + "/10", deliveriesLoaded,
					deliveriesWontLoad);
			showLimitPanel("10");
		}
	}, function(data){
		showDialog("Error", "Hubo un error cargando las bodegas, por favor recargue la pagina.", blankFunction);
	});
	
}

function showLimitPanel(limit){
	$("#limit-panel").removeClass("hidden");
	$("#limit-value").html(limit);
	$("#limit-search-box").val(limit);
}

function deliveriesLoaded(data) {
	hideOverlay();
	hideDialog();
	for (var i = 0; i < data.list.length; i++) {
		var delivery = data.list[i];
		addDeliveryToTable(delivery);
	}
	if( !r ){
		$(".action-buttons").remove();
	}
}

function deliveriesWontLoad() {
	showDialog("Carga fallida", "No cargan los movimientos. Quiere repetir?",
			deliveriesInitialFunction);
}

function addDeliveryToTable(delivery) {
	
	var deliveryType = delivery.deliveryType;
	if( deliveryType == "Entrega"){
		var string = "<tr id='delivery-" + delivery.id + "'>";
		string += "<td class='hidden' ><span id='id-" + delivery.id + "'>" + delivery.id + "</span><span class='hidden' id='deliveryType-"+delivery.id+"' >"+delivery.deliveryType+"</span></td>";
		string += "<td id='productCode-" + delivery.id + "'>" + delivery.productCode + "</td>";
		string += "<td id='productBrand-" + delivery.id + "'>" + delivery.productBrand + "</td>";
		string += "<td id='productName-" + delivery.id + "'>" + delivery.productName + "</td>";
		string += "<td id='whoAsked-" + delivery.id + "'>" + delivery.whoAsked + "</td>";
		string += "<td id='whoReceived-" + delivery.id + "'>" + delivery.whoReceived + "</td>";
		string += "<td id='askDate-" + delivery.id + "'>" + delivery.askDate + "</td>";
		string += "<td id='storehouse-" + delivery.id + "'>" + delivery.storehouse + "</td>";
		string += "<td id='quantity-" + delivery.id + "'>" + delivery.quantity + "</td>";
		string += "<td id='days-" + delivery.id + "'>" + delivery.daysInStorehouse + "</td>";
		if( update_permission ){
			string += "<td class='action-buttons'><button type='button' class='btn btn-sm btn-primary' onclick='openUpdateDelivery("+delivery.id+")'>Actualizar</button>";
		}
		if( deletePermission ){
			 //string += "| <button type='button' class='btn btn-sm btn-primary' onclick='deleteDelivery("+delivery.id+")' >Eliminar</button></td>";
		}
		string += "</tr>";

		$("#received-table > tbody:last-child").append(string);
	} else if ( deliveryType == "Despacho"){
		var string = "<tr id='delivery-" + delivery.id + "'>";
		string += "<td class='hidden' ><span id='id-" + delivery.id + "'>" + delivery.id + "</span><span class='hidden' id='deliveryType-"+delivery.id+"' >"+delivery.deliveryType+"</span></td>";
		string += "<td id='productCode-" + delivery.id + "'>" + delivery.productCode + "</td>";
		string += "<td id='productBrand-" + delivery.id + "'>" + delivery.productBrand + "</td>";
		string += "<td id='productName-" + delivery.id + "'>" + delivery.productName + "</td>";
		string += "<td id='whoAsked-" + delivery.id + "'>" + delivery.whoAsked + "</td>";
		string += "<td id='whoReceived-" + delivery.id + "'>" + delivery.whoReceived + "</td>";
		string += "<td id='askDate-" + delivery.id + "'>" + delivery.askDate
				+ "</td>";
		string += "<td id='leadTime-" + delivery.id + "'>" + delivery.leadTime
				+ "</td>";
		string += "<td id='deliveryDate-" + delivery.id + "'>" + delivery.deliveryDate + "</td>";
		string += "<td id='deliveryPoint-" + delivery.id + "'>" + delivery.deliveryPoint + "</td>";
		string += "<td id='cities-" + delivery.id + "'>" + delivery.cities
				+ "</td>";
		string += "<td id='status-" + delivery.id + "'>" + delivery.status
				+ "</td>";
		string += "<td id='storehouse-" + delivery.id + "'>" + delivery.storehouse
		+ "</td>";
		string += "<td id='quantity-" + delivery.id + "'>" + delivery.quantity
		+ "</td>";
		if( update_permission ){
			string += "<td class='action-buttons'><button type='button' class='btn btn-sm btn-primary' onclick='openUpdateDelivery("+delivery.id+")'>Actualizar</button>";
		}
		if( deletePermission ){
			 string += "| <button type='button' class='btn btn-sm btn-primary' onclick='deleteDelivery("+delivery.id+")' >Eliminar</button></td>";
		}
		string += "</tr>";

		$("#delivery-table > tbody:last-child").append(string);
	}

}

function deleteDelivery(id){
	showYesNoDialog("Eliminar Despacho/Entrega", "Esta seguro que desea eliminar el movimiento?", function(){
		if( prodId == -1 ){
			getRequest(baseUrl+"rest/products/code/"+$("#productCode-"+id).html(), function(data){
				getRequest(baseUrl+"rest/delivery/delete/" + id + "/" + data.o.id, function(data){
				showDialog("Eliminado", "Movimiento Eliminado Correctamente.", blankFunction, true);
				$("#delivery-"+id).remove();
				}, function(data){
				showDialog("Error", data.reason, blankFunction);
				});
			}, function(data){
				showDialog("Error", data.reason, blankFunction);
			});
		} else {
			getRequest(baseUrl+"rest/delivery/delete/" + id + "/" + prodId, function(data){
				showDialog("Eliminado", "Movimiento Eliminado Correctamente.", blankFunction, true);
				$("#delivery-"+id).remove();
			}, function(data){
				showDialog("No se pudo eliminar", data.reason, blankFunction );
			}, function(){
				showDialog("Error", "No se pudo eliminar", blankFunction);
			});
		}
	});
}

function downloadFile(){
	getRequest(baseUrl+"rest/products/createfile/" + invId, function(data){
		window.location = baseUrl+"rest/products/download/"+invId;
	}, function( data ){
		showDialog("Error", data.reason, blankFunction, blankFunction, true);
	}, function(){
		showDialog("Error", "No se pudo crear el archivo." , blankFunction, false);
	});
}

function openInventory(){
	if( prodId == -1 ){
		window.location.href = baseUrl + "content/products.jsf?id=" + invId;
	} else {
		window.location.href = baseUrl + "content/products.jsf?id=" + invId;
	}
	
}

function modifyDialog(deliveryType){
	if( deliveryType == "Entrega"){
		$("#deliveryType").val("Entrega");

		$("#leadTime-div").addClass("hidden");
		$("#leadTime").val("1");
		$("#deliveryDate-div").addClass("hidden");
		$("#deliveryDate").val("10/10/2010");
		$("#cities-div").addClass("hidden");
		$("#cities").val("1");
		$("#status-div").addClass("hidden");
		$("#status").val("1");

		$("#whoAsked-label").html("Quien Envio?");
		$("#whoReceived-label").html("Quien Recibio?");
		$("#askDate-label").html("Fecha de Recibo");
		$("#deliveryPoint-label").html("Punto de Recibo");
		
	} else if( deliveryType == "Despacho"){
		$("#deliveryType").val("Despacho");
		
		$("#leadTime-div").removeClass("hidden");
		$("#leadTime").val("");
		$("#deliveryDate-div").removeClass("hidden");
		$("#deliveryDate").val("");
		$("#cities-div").removeClass("hidden");
		$("#cities").val("");
		$("#status-div").removeClass("hidden");
		$("#status").val("");
		
		$("#whoAsked-label").html("Quien Solicito?");
		$("#whoReceived-label").html("Quien Despacho?");
		$("#askDate-label").html("Fecha de Solicitud");
		$("#deliveryPoint-label").html("Punto de Entrega");
	}
}

function searchByProduct(){
	var product = $("#product-search-box").val();
	if( product.length > 0 ){
		window.location.href = baseUrl + "content/deliveries.jsf?invId="+invId+"&search_prod="+product;
	} else {
		window.location.href = baseUrl + "content/deliveries.jsf?invId="+invId;
	}
}


function searchByLimit(){
	var limit = $("#limit-search-box").val();
	window.location.href = baseUrl + "content/deliveries.jsf?invId="+invId+"&limit="+limit;
}

function searchByDate(){
	var date = $("#date-search-box").val();
	if( date.length > 0 ){
		if( prodId != -1 ){
			window.location.href = baseUrl + "content/deliveries.jsf?prodId="+prodId+"&invId="+invId+"&search_date="+date;
		} else {
			window.location.href = baseUrl + "content/deliveries.jsf?invId="+invId+"&search_date="+date;
		}
	} else {
		if( prodId != -1 ){
			window.location.href = baseUrl + "content/deliveries.jsf?prodId="+prodId+"&invId="+invId;
		} else {
			window.location.href = baseUrl + "content/deliveries.jsf?invId="+invId;
		}
	}
}


$(function() {
	if (!r) {
		$(".acciones-th").remove();
		$("#create-button").remove();
		$("#action-buttons").remove();
		$(".action-buttons").remove();
	}
	if( !update_permission ){
		$(".acciones-th").remove();
	}
	deliveriesInitialFunction();
	$(".datepicker").datepicker({
		dateFormat: "dd/mm/yy"
	});
});
