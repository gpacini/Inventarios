package com.serinse.web.user.authentication;

import com.serinse.web.common.response.BaseResponse;

public class LoginResponse extends BaseResponse{

	public String username;
	public String sessionKey;
	
	public LoginResponse(String username, String sessionKey){
		super(true, "Se ingreso correctamente");
		this.username = username;
		this.sessionKey = sessionKey;
	}
	
}
