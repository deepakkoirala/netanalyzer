package com.thapasujan5.netanalzyerpro.Tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.thapasujan5.netanalyzerpro.R;
import com.thapasujan5.netanalzyerpro.AppConstants;

/**
 * Created by Suzan on 11/13/2015.
 */
public class NetworkUtil {

    public static int TYPE_WIFI = AppConstants.TYPE_WIFI;
    public static int TYPE_MOBILE = AppConstants.TYPE_MOBILE;
    public static int TYPE_NOT_CONNECTED = AppConstants.TYPE_NOT_CONNECTED;


    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }


    public static String getConnectivityStatusString(Context context) {
        int conn = NetworkUtil.getConnectivityStatus(context);
        String status = null;
        if (conn == NetworkUtil.TYPE_WIFI) {
            status = "Wifi enabled";
        } else if (conn == NetworkUtil.TYPE_MOBILE) {
            status = "Mobile data enabled";
        } else if (conn == NetworkUtil.TYPE_NOT_CONNECTED) {
            status = context.getResources().getString(R.string.no_connection);
        }
        return status;
    }
}