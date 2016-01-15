package com.thapasujan5.netanalzyerpro.Tools;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;

/**
 * Created by Sujan Thapa on 4/01/2016.
 */
public class WifiUtil {
    WifiManager wifiManager;
    Context context;

    public WifiUtil(Context context) {
        this.context = context;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    public String getWifiChannel() {
        if (Build.VERSION.SDK_INT >= 21) {
            int freq = wifiManager.getConnectionInfo().getFrequency();
            {
                if (freq == 2484)
                    return Integer.toString(14);

                if (freq < 2484)
                    return Integer.toString((freq - 2407) / 5);

                return Integer.toString(freq / 5 - 1000);
            }
        } else {
            return "";
        }
    }

    public boolean isWifiEnabled() {
        try {
            return wifiManager.isWifiEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public String getDns1() {
        try {
            return InttoIp.intToIp(wifiManager.getDhcpInfo().dns1);
        } catch (Exception e) {
            return "";
        }
    }

    public String getDns2() {
        try {
            return InttoIp.intToIp(wifiManager.getDhcpInfo().dns2);
        } catch (Exception e) {
            return "";
        }
    }

    public String getNetMask() {
        try {
            return InttoIp.intToIp(wifiManager.getDhcpInfo().netmask);
        } catch (Exception e) {
            return "";
        }
    }

    public int getLeaseDuration() {
        try {
            return wifiManager.getDhcpInfo().leaseDuration;
        } catch (Exception e) {
            return 0;
        }
    }

    public String getRssi() {
        try {
            return Integer.toString(wifiManager.getConnectionInfo().getRssi());
        } catch (Exception e) {
            return "";
        }
    }

    public String getLinkSpeed() {
        try {
            return Integer.toString(wifiManager.getConnectionInfo().getLinkSpeed());
        } catch (Exception e) {
            return "";
        }
    }

    public String getFreq() {
        try {
            if (Build.VERSION.SDK_INT >= 21)
                return Integer.toString(wifiManager.getConnectionInfo().getFrequency());
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    public String getSsid() {
        try {
            return wifiManager.getConnectionInfo().getSSID().replace("\"", "");
        } catch (Exception e) {
            return "";
        }
    }

    public String getIpAddress() {
        String ip = InttoIp.intToIp(wifiManager.getConnectionInfo()
                .getIpAddress());
        try {
            return ip;
        } catch (Exception e) {
            return "";
        }
    }

    public String getGateway() {
        try {
            return InttoIp.intToIp(wifiManager.getDhcpInfo().gateway);
        } catch (Exception e) {
            return "";
        }
    }

    public String getMacAddress() {
        try {
            return wifiManager.getConnectionInfo().getBSSID().toUpperCase();
        } catch (Exception e) {
            return "n/a";
        }
    }

    public String getPercentSignal() {
        try {
            return (int) (DbToPercent.getPercentfromDb(wifiManager.getConnectionInfo().getRssi())) + "%";
        } catch (Exception e) {
            return "";
        }
    }
}
