package com.thapasujan5.netanalzyerpro.Weather;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.thapasujan5.netanalzyerpro.Notification.Service;

import java.util.TimerTask;

/**
 * Created by Sujan Thapa on 5/01/2016.
 */
public class WeatherUpdater extends TimerTask {
    Context context;

    public WeatherUpdater(Context context) {
        this.context = context;
        run();
    }

    @Override
    public void run() {
      //  Log.d("WeatherUpdater", "Scheduled Update.");
        Intent serviceIntent = new Intent(context, Service.class);
        serviceIntent.putExtra("receiver", "reboot");
        context.startService(serviceIntent);
    }
}
