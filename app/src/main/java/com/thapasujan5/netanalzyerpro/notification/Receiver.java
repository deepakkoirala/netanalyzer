package com.thapasujan5.netanalzyerpro.notification;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.thapasujan5.netanalzyerpro.tools.NetworkUtil;

/**
 * Created by Suzan on 11/7/2015.
 */


public class Receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String status = NetworkUtil.getConnectivityStatusString(context);
        if (NetworkUtil.getConnectivityStatus(context) == 0) {
            Toast.makeText(context, status, Toast.LENGTH_LONG).show();
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(0);
        } else {

            Toast.makeText(context, status, Toast.LENGTH_LONG).show();
            Intent serviceIntent = new Intent(context, NotificationService.class);
            serviceIntent.putExtra("caller", "RebootReceiver");
            context.startService(serviceIntent);

        }
    }

}