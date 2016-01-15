package com.thapasujan5.netanalzyerpro.Tools;

import com.thapasujan5.netanalzyerpro.AppConstants;

import org.json.JSONObject;

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

    public JSONObject getWeatherInfo(String city, String country) {
        JSONObject json = jsonParser.getJSONFromUrl(AppConstants.url_weather_api + "&q=" + city + "," + country);
        return json;
    }

    public JSONObject getWeatherInfoLatLon(String lat, String lon) {
        JSONObject json = jsonParser.getJSONFromUrl(AppConstants.url_weather_api + "&lat=" + lat + "&lon=" + lon);
        return json;
    }
}
