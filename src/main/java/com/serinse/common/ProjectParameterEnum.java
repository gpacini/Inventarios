package com.serinse.common;


public enum ProjectParameterEnum {
	REQUISITION_EGRESS_CONSECUTIVE("requisition_egress_consecutive"), REQUISITION_INGRESS_CONSECUTIVE("requisition_ingress_consecutive"),
	LOTS_CREATED("Lots creaTed"),
	
	//Datos de correo electronico de envio de notificaciones
	EMAIL_USER("email_user"), EMAIL_PASS("email_password"), EMAIL_SERVER("email_server"), EMAIL_PORT("email_port"),
	EMAIL_SSL("email_ssl_on"), EMAIL_ACTIVE("email_active");

	private String name;

	private ProjectParameterEnum(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
