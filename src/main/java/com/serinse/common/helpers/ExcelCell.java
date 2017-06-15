package com.serinse.common.helpers;

public class ExcelCell{
	
	public static final int STRING = 0;
	public static final int BOOLEAN = 1;
	public static final int DOUBLE = 2;
	public static final int DATE = 3;
	
	public ExcelCell(){}
	
	public int type;
	public String value;
	
	public String dateFormat;
}