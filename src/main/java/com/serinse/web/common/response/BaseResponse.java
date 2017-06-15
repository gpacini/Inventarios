package com.serinse.web.common.response;

public class BaseResponse implements JSONResponse {
	
	public boolean success;
	public String reason;
	
	public boolean redirect;
	public String redirectUrl;
	
	public BaseResponse(boolean s, String r){
		success = s;
		reason = r;
		redirect = false;
		redirectUrl = "";
	}

	public void setRedirectLink(String link){
		redirect = true;
		redirectUrl = link;
	}
	
	public boolean isSuccess( ){
		return success;
	}
	
	public String toString(){
		return "{success: " + success + ", reason: " + reason + "}";
	}
}
