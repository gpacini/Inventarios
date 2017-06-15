package com.serinse.web.users.helpers;

import java.util.List;

import com.serinse.web.common.response.BaseResponse;

public class ListResponse<T> extends BaseResponse{

	public List<T> list;
	
	public ListResponse(List<T> u, String message){
		super(true, message);
		list = u;
	}
	
}
