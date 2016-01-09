package com.thapasujan5.netanalzyerpro.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.thapasujan5.netanalzyerpro.AppConstants;
import com.thapasujan5.netanalzyerpro.Tools.NetworkUtil;
import com.thapasujan5.netanalzyerpro.Weather.WeatherUpdater;

import java.util.Date;
import java.util.Timer;

/**
 * Created by Suzan on 11/7/2015.
 */


public class ReceiverLocationUpdate extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        Intent serviceIntent = new Intent(context, Service.class);
        serviceIntent.putExtra("receiver", "reboot");
        if (NetworkUtil.getConnectivityStatus(context) == AppConstants.TYPE_NOT_CONNECTED) {
            new Notify(context);
        } else if (NetworkUtil.getConnectivityStatus(context) == AppConstants.TYPE_WIFI || NetworkUtil.getConnectivityStatus(context) == AppConstants.TYPE_MOBILE) {
            context.startService(serviceIntent);
            new Timer().scheduleAtFixedRate(new WeatherUpdater(context), new Date(), AppConstants.WEATHER_UPDATE_DURATION); //1Hour
        }
    }
}
