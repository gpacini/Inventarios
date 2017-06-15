package com.serinse.web.controllers;

import javax.enterprise.context.ApplicationScoped;

import javax.inject.Named;
import com.serinse.common.Constants;


@Named("controllerGlobal")
@ApplicationScoped
public class ControllerGlobal {

	private String baseUrl = Constants.BASE_URL;


	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
}
