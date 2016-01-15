package com.thapasujan5.netanalzyerpro.Tools;

import android.content.Context;
import android.net.wifi.WifiManager;

/**
 * Created by Suzan on 11/30/2015.
 */
public class IpMac {
    public static String getInternalIP(Context context) {
        String intIP = "";

        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        @SuppressWarnings("deprecation")
        String wifiIP = InttoIp.intToIp(wm.getConnectionInfo().getIpAddress());
        if (wifiIP.length() > 7) {
            intIP = wifiIP;
        } else {
            // Data
            intIP = NetworkUtil.getIPAddress(true);
        }
        return intIP;
    }

    public static String getDeviceMacAdd(Context context) {
        String mac = "";
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        @SuppressWarnings("deprecation")
        String macAddress = wm.getConnectionInfo().getMacAddress();
        return macAddress;
    }
}
