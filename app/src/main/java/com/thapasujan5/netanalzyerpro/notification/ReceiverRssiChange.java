package com.thapasujan5.netanalzyerpro.Notification;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.thapasujan5.netanalzyerpro.AppConstants;
import com.thapasujan5.netanalzyerpro.Tools.NetworkUtil;

/**
 * Created by Suzan on 11/7/2015.
 */


public class ReceiverRssiChange extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {


        String status = NetworkUtil.getConnectivityStatusString(context);
        if (NetworkUtil.getConnectivityStatus(context) == AppConstants.TYPE_NOT_CONNECTED) {
            new Notify(context);
        } else if (NetworkUtil.getConnectivityStatus(context) == AppConstants.TYPE_WIFI) {
            Intent serviceIntent = new Intent(context, Service.class);
            serviceIntent.putExtra("receiver", "rssi_changed");
            context.startService(serviceIntent);
        } else if (NetworkUtil.getConnectivityStatus(context) == AppConstants.TYPE_MOBILE) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(0);
        } else {

        }
    }
}
