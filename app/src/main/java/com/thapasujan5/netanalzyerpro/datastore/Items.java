package com.thapasujan5.netanalzyerpro.DataStore;

public class Items {


    public Items(String name, String ip, String isp, String location, String date, String lat, String lon, String region, String region_name, String zip, String time_zone) {
        this.name = name;
        this.ip = ip;
        this.isp = isp;
        this.location = location;
        this.date = date;

        this.lat = lat;
        this.lon = lon;
        this.region = region;
        this.region_name = region_name;
        this.zip = zip;
        this.time_zone = time_zone;
    }

    public String name;
    public String ip;
    public String isp;
    public String location;
    public String date;

    public String lat;
    public String lon;
    public String region;
    public String region_name;
    public String zip;
    public String time_zone;
}
