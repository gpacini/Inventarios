
function logInitialFunction() {
	showOverlay();
	showDialog("Cargando Registros",
			"Por favor espere mientras se cargan los registros de base de datos.",
			blankFunction, true);
	getRequest(baseUrl + "rest/updatelog/index", logLoaded,
			logWontLoad);
}

function logLoaded(data) {
	hideOverlay();
	hideDialog();
	for (var i = 0; i < data.list.length; i++) {
		var item = data.list[i];
		addLogToTable(item);
	}
}

function logWontLoad() {
	showDialog("Carga fallida", "No cargan los registros. Quiere repetir?",
			logInitialFunction);
}

function addLogToTable(item) {
	var string = "<tr>";
	string += "<td>"+item.action+"</td>";
	string += "<td>"+item.date+"</td>";
	string += "<td>"+item.user+"</td>";
	string += "<td>"+item.table+"</td>";
	string += "<td>"+item.code+"</td>";
	string += "<td>"+item.name+"</td>";
	string += "<td>"+item.client+"</td>";
	string += "<td>"+item.initialValue+"</td>";
	string += "<td>"+item.finalValue+"</td>";
	string += "<td>"+item.reason+"</td>";
	string += "</tr>";

	$("#log-table > tbody:last-child").append(string);
}

$(function() {
	logInitialFunction();
});
