package com.thapasujan5.netanalzyerpro.Tools;

import android.content.Context;
import android.net.wifi.WifiManager;

/**
 * Created by Sujan Thapa on 4/01/2016.
 */
public class WifiUtil {
    WifiManager wifiManager;

    public WifiUtil(Context context) {
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    public boolean isWifiEnabled() {
        return wifiManager.isWifiEnabled();
    }

    public String getDns1() {
        return InttoIp.intToIp(wifiManager.getDhcpInfo().dns1);
    }

    public String getDns2() {
        return InttoIp.intToIp(wifiManager.getDhcpInfo().dns2);
    }

    public String getNetMask() {
        return InttoIp.intToIp(wifiManager.getDhcpInfo().netmask);
    }

    public int getLeaseDuration() {
        return wifiManager.getDhcpInfo().leaseDuration;
    }

    public String getRssi() {
        return Integer.toString(wifiManager.getConnectionInfo().getRssi());
    }

    public String getLinkSpeed() {
        return Integer.toString(wifiManager.getConnectionInfo().getLinkSpeed());
    }

    public String getFreq() {
        return Integer.toString(wifiManager.getConnectionInfo().getFrequency());
    }

    public String getSsid() {
        return wifiManager.getConnectionInfo().getSSID().replace("\"", "");
    }

    public String getIpAddress() {
        String ip = InttoIp.intToIp(wifiManager.getConnectionInfo()
                .getIpAddress());
        return ip;
    }

    public String getGateway() {
        return InttoIp.intToIp(wifiManager.getDhcpInfo().gateway);
    }

    public String getMacAddress() {
        return wifiManager.getConnectionInfo().getBSSID().toUpperCase();
    }

    public String getPercentSignal() {
        return (int) (DbToPercent.getPercentfromDb(wifiManager.getConnectionInfo().getRssi())) + "%";
    }
}
