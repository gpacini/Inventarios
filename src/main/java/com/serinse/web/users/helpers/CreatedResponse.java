package com.serinse.web.users.helpers;

import com.serinse.web.common.response.BaseResponse;

public class CreatedResponse<T> extends BaseResponse {

	public T object;
	
	public CreatedResponse(T o, String message){
		super(true, message);
		this.object = o;
	}
}
