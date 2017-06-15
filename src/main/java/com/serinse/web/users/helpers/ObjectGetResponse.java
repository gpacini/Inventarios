package com.serinse.web.users.helpers;

import com.serinse.web.common.response.BaseResponse;

public class ObjectGetResponse<T> extends BaseResponse{

	public T o;
	
	public ObjectGetResponse(T o, String message){
		super(true, message);
		this.o = o;
	}
}
