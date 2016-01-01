package com.thapasujan5.netanalzyerpro.DataStore;

public class ItemsDiscovered {


    public ItemsDiscovered(String dns, String ip, String mac) {
        this.dns = dns;
        this.ip = ip;
        this.mac = mac;
    }

    public String dns;
    public String ip;
    public String mac;


    public String vendor;
    public String type;
    public String firstseen;
    public String status;

}
