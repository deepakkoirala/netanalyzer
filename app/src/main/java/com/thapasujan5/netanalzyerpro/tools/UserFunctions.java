package com.thapasujan5.netanalzyerpro.tools;

import org.json.JSONObject;

import com.thapasujan5.netanalzyerpro.AppConstants;

public class UserFunctions {

	private final JSONParser jsonParser;

	private static String URL = AppConstants.ip_ip;

	static String units = "metric";
	private static String cnt = "7";
	private static String lang = "en";

	// constructor
	public UserFunctions() {
		jsonParser = new JSONParser();
	}

	public JSONObject getIPInfo(String ip) {
		JSONObject json = jsonParser.getJSONFromUrl(URL + ip);
		return json;
	}

	public JSONObject getOwnInfo() {
		JSONObject json = jsonParser.getJSONFromUrl(URL);
		return json;
	}
}
