package com.serinse.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateHelpers {

	public static boolean isDateInFormat(String date, String format){
		SimpleDateFormat d1 = new SimpleDateFormat(format);
		try {
			d1.parse(date);
		} catch (ParseException e) {
			return false;
		}
		return true;
	}
	
	public static String formatDate(String date, String oldFormat, String newFormat){
		SimpleDateFormat d1 = new SimpleDateFormat(oldFormat);
		SimpleDateFormat d2 = new SimpleDateFormat(newFormat);
		try {
			return d2.format(d1.parse(date));
		} catch (ParseException e) {
			return null;
		}
	}
}
