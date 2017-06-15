function submitForm(formId, url, successFunction, failureFunction, errorFunction){
	var jsonForm = new Object();
	$(formId + " :input").each(function(){
		var name = $(this).attr("name");
		var value = $(this).val();
		if( name != null ){
			if( jsonForm[name] == null ){
				jsonForm[name] = value;
			} else {
				if( jsonForm[name].constructor === Array ){
					jsonForm[name][jsonForm[name].length] = value;
				} else {
					jsonForm[name] = [jsonForm[name], value];
				}
			}
		}
	});

	showOverlay();
	$.ajax({
		  type: "POST",
		  url: url,
		  data: JSON.stringify(jsonForm),
	      contentType: 'application/json',
		  success: function(data){
			  hideOverlay();
				if( data.success ){
					successFunction(data);
				} else {
					failureFunction(data);
				}
			},
	        error: function(){
				hideOverlay();
	        	if( errorFunction ){
	        		errorFunction();
	        	} else {
	        		showDialog("Error", "No se pudo ejecutar la accion", blankFunction);
	        	}
	        },
		  dataType: "json"
		});
}

function getRequest(url, successFunction, failureFunction, errorFunction){
	showOverlay();
	$.ajax({
		  type: "GET",
		  url: url,
		  success: function(data){
			  hideOverlay();
				if( data.success ){
					successFunction(data);
				} else {
					failureFunction(data);
				}
			},
	        error: function(){
				hideOverlay();
	        	if( errorFunction ){
	        		errorFunction();
	        	} else {
	        		showDialog("Error", "No se pudo ejecutar la accion", blankFunction);
	        	}
	        },
		  dataType: "json"
		});
}

function blankFunction(){}

function showDialog(title, message, closingFunction, useTimeout){
	$("#dialog").removeClass("hidden");
	$("#dialog .message").html(message);
	$("#dialog").dialog({
		modal: true,
		title: title,
	    position: { my: 'center top', at: 'center top+50', of: window },
		buttons: {
			Ok: function(){
				closingFunction();
				$( this ).dialog( "close" );
			}
		},
		show: {
			effect: "drop",
			duration: 400,
			easing:"easeOutExpo",
			direction:"up", 
			distance:300, 
		},
		hide: {
			effect: "drop",
			duration: 200,
			easing:"easeInExpo",
			direction:"up", 
			distance:300, 
		}
	});
	if( useTimeout !== null && useTimeout ){
		setTimeout(hideDialog, 3000);
	}
}

function showYesNoDialog(title, message, acceptFunction){
	$("#dialog-YesNo").removeClass("hidden");
	$("#dialog-YesNo .message").html(message);
	$("#dialog-YesNo").dialog({
		modal: true,
		title: title,
	    position: { my: 'center top', at: 'center top+50', of: window },
		buttons: {
			Si: function(){
				acceptFunction();
				$( this ).dialog( "close" );
			},
			No: function(){
				$(this).dialog("close");
			}
		},
		show: {
			effect: "drop",
			duration: 300,
			easing:"easeOutExpo",
			direction:"up", 
			distance:300, 
		},
		hide: {
			effect: "drop",
			duration: 100,
			easing:"easeInExpo",
			direction:"up", 
			distance:300, 
		}
	});
}

function hideDialog(){
	$("#dialog").dialog("close");
}

var overlayImagePositioned = false;
function showOverlay(){
	if( !overlayImagePositioned ){
		$("#overlay").removeClass("hidden");
    	$(".overlay-image" ).position({my:'center center', at:'center center', of:'#overlay'});
    	overlayImagePositioned = true;
	}
	$("#wrapper").find('*').attr('disabled',true).css('opacity', '0.7');
	$("#overlay").fadeOut(1).fadeIn(100);
}

function hideOverlay(){
	$("#wrapper").find('*').attr('disabled',false).css('opacity', '1');
	$("#overlay").fadeOut(100);
}

function logout(){
	getRequest(baseUrl + "rest/authentication/logout", function(data){
		window.location.href = data.redirectUrl;
	}, function(data){
		showDialog("Logout fallido", data.reason, blankFunction);
	});
}

function htmlDateToMySqlDate(date){
	var askDate = date.split("/");
	var newDate = askDate[2] + '-' + askDate[1] + '-' + askDate[0];
	return newDate;
}

function mySqlDateToHtmlDate(date){
	var askDate = date.split("-");
	var newDate = askDate[1] + '/' + askDate[2] + '/' + askDate[0];
	return newDate;
}

function submitFileForm(id, url, successFunction, failureFunction, errorFunction){
    var formData = new FormData($(id)[0]);
    $.ajax({
        url: url,
        type: 'POST',
        data: formData,
        cache: false,
        contentType: false,
        processData: false,
        dataType: 'json',
        success: function(data) {
            if(data.success){
            	successFunction(data);
            } else {
            	failureFunction(data);
            }
        },
        error: function(){
        	if( errorFunction ){
        		errorFunction();
        	} else {
        		showDialog("Error", "No se pudo ejecutar la accion", blankFunction);
        	}
        }
    });
}

function openUploadPhoto(id, table, directory, successFunction){
	$("#photo-fkId").val(id);
	$("#photo-table").val(table);
	$("#photo-directory").val(directory);
	$("#photo-description").val("");
	$("#photo-title").val("");
	$("#photo-photo").val("");
	$("#upload-photo-dialog").removeClass("hidden");
	$("#upload-photo-dialog").dialog(
			{
				width : ($(window).width() * 0.6),
				modal : true,
				title : "Subir Imagen",
				buttons : {
					Subir : function() {
						$("#upload-photo-dialog").dialog('close');
						submitFileForm("#photo-upload-form", baseUrl+"rest/photos/upload", function(data){
							showDialog("Exito", "Imagen Subida Correctamente", blankFunction, blankFunction);
							successFunction(data);
						}, 
							function(data){
								showDialog("Error subiendo foto", data.reason, blankFunction, blankFunction);
							}, function(){
								showDialog("Error", "Error subiendo la foto", blankFunction, blankFunction);
							});
					}
				}
			});
	$('upload-photo-dialog').dialog('option', 'position', {
		my : 'center center',
		at : 'center center',
		of : window
	});
}

function openPhotoDialog(photoId){
	getRequest(baseUrl+"rest/photos/get/"+photoId, function(data){
		$("#show-photo-dialog").removeClass("hidden");
		$("#show-photo-dialog .message").html(data.o.description);
		$("#show-photo-dialog .image").attr("src", baseUrl + data.o.webDir);
		$("#show-photo-dialog").dialog({
			modal: true,
			title: data.o.title,
		    position: { my: 'center top', at: 'center top+50', of: window },
			buttons: {
				Ok: function(){
					$( this ).dialog( "close" );
				}
			},
			show: {
				effect: "drop",
				duration: 400,
				easing:"easeOutExpo",
				direction:"up", 
				distance:300, 
			},
			hide: {
				effect: "drop",
				duration: 200,
				easing:"easeInExpo",
				direction:"up", 
				distance:300, 
			}
		});
	}, function(data){
		showDialog("Error abriendo foto", data.reason, blankFunction);
	});
}


params={};location.search.replace(/[?&]+([^=&]+)=([^&]*)/gi,function(s,k,v){params[k]=v})
