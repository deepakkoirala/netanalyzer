package com.thapasujan5.netanalzyerpro.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.thapasujan5.netanalzyerpro.AppConstants;
import com.thapasujan5.netanalzyerpro.Tools.NetworkUtil;

/**
 * Created by Suzan on 11/7/2015.
 */


public class Receiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {


        String status = NetworkUtil.getConnectivityStatusString(context);
        if (NetworkUtil.getConnectivityStatus(context) == AppConstants.TYPE_NOT_CONNECTED) {
            Log.i("Receiver", "To Notify: Not Connected.");
            new Notify(context);
        } else {
            Log.i("Receiver", "To Notification Service");
            Intent serviceIntent = new Intent(context, NotificationService.class);
            serviceIntent.putExtra("caller", "RebootReceiver");
            context.startService(serviceIntent);
        }
    }
}
