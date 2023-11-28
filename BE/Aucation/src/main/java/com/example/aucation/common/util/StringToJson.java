package com.example.aucation.common.util;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class StringToJson {

	public static JSONObject stringToJson(String jsonString) throws ParseException {
		JSONParser parser = new JSONParser();
		Object obj;
		obj = parser.parse(jsonString);
		return (JSONObject) obj;
	}

}
