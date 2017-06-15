package com.serinse.pers.utils.interfaces;

public interface Downloadable {

	byte[] getFileInBytes();
	String getContentType();
	String getName();
	String getExtension();

}
