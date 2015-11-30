package com.thapasujan5.netanalzyerpro.tools;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

/**
 * Created by Suzan on 11/30/2015.
 */
public class GetIntIP {
    public static String getInternalIP(Context context) {
        String intIP = "";

        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        @SuppressWarnings("deprecation")
        String wifiIP = Formatter.formatIpAddress(wm.getConnectionInfo()
                .getIpAddress());

        if (wifiIP.length() > 7) {
            intIP = wifiIP;
        } else {
            // Data
            intIP = GetDeviceIP.getDeviceIP();
        }
        return intIP;
    }
}
