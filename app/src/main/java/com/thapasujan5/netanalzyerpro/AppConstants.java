package com.thapasujan5.netanalzyerpro;

public class AppConstants {

    public static final long SPLASH_TIME = 1300;

    public static final int DB_VERSION = 7;
    public static final String DB_NAME = "servers";

    public static final String TBL_Items = "items";

    public static String col_item_id = "id";
    public static String col_item_ip = "ip";
    public static String col_item_name = "name";
    public static String col_item_isp = "isp";
    public static String col_item_location = "location";
    public static String col_item_date = "date";
    public static String col_item_lat = "lat";
    public static String col_item_lon = "lon";
    public static String col_item_region = "region";
    public static String col_item_region_name = "region_name";
    public static String col_item_zip = "zip";
    public static String col_item_time_zone = "time_zone";

    public static String ip_ip = "http://209.58.180.196/json/";
    public static String url_weather_api = "http://162.243.58.21/data/2.5/weather?units=metric&cnt=7&lang=en&appid=68fddab87d63335009484f1a9f788026";
    public static String url_weather_icon = "http://162.243.58.21/img/w/";

    public static final int TYPE_NOT_CONNECTED = 0;
    public static final int TYPE_WIFI = 1;
    public static final int TYPE_MOBILE = 2;


    public static final int MAX_SIGNAL_DBM_VALUE = 31;
    public static final int UNKNOW_CODE = 99;
    public static final long WEATHER_UPDATE_DURATION = 30 * 60000; //1hour because api is renewed every hour.

    public static final int READ_CONTACTS = 0;
    public static final int READ_SMS = 1;
    public static final int ACCESS_FINE_LOCATION = 2;
    public static final int ACCESS_CORASE_LOCATION = 3;
    public static final int READ_PHONE_STATE = 4;
    public static final int READ_EXTERNAL_STORAGE = 5;
    public static final int WRITE_EXTERNAL_STORAGE = 6;
    public static final int SYSTEM_ALERT_WINDOW = 7;
    public static final int CHANGE_NETWORK_STATE = 8;

    public enum MonetizationType {
        PAID, FREE
    }
}
