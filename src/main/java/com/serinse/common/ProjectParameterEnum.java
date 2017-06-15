package com.serinse.common;


public enum ProjectParameterEnum {
	REQUISITION_EGRESS_CONSECUTIVE("requisition_egress_consecutive"), REQUISITION_INGRESS_CONSECUTIVE("requisition_ingress_consecutive");

	private String name;

	private ProjectParameterEnum(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
