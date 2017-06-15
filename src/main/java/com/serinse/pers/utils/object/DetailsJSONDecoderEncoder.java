package com.serinse.pers.utils.object;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DetailsJSONDecoderEncoder {


	public static JSONArray paymentDetailsToJsonArray(String details){
		JSONParser parser = new JSONParser();
		JSONObject detalles = null;
		try {
			detalles = (JSONObject) parser.parse(details);
		} catch (ParseException e) {
			System.out.println("####Error al hacer parse de los detalles de pago");
			e.printStackTrace();
		}
		return (JSONArray) detalles.get("details");
	}


	@SuppressWarnings("unchecked")
	public static String detailsJsonArrayToString(JSONArray details){
		JSONObject paymentDetails = new JSONObject( );
		paymentDetails.put("details", details);
		return paymentDetails.toJSONString();
	}
	
}
