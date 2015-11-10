package com.thapasujan5.netanalzyerpro.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Suzan on 11/7/2015.
 */


public class MyRebootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, MeCorpServiceClass.class);
        serviceIntent.putExtra("caller", "RebootReceiver");
        context.startService(serviceIntent);
    }
}

