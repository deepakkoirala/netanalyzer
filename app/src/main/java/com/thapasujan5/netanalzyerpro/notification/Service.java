package com.thapasujan5.netanalzyerpro.Notification;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.thapasujan5.netanalyzerpro.R;
import com.thapasujan5.netanalzyerpro.ActionMenu.GetISP;
import com.thapasujan5.netanalzyerpro.ActionMenu.GetWeaather;
import com.thapasujan5.netanalzyerpro.LocationServices.Locate;

/**
 * Created by Suzan on 11/7/2015.
 */
public class Service extends IntentService {


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     * <p/>
     * //     * @param name Used to name the worker thread, important only for debugging.
     */
    public Service() {
        super("ReminderService");


    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     * <p/>
     * //     * @param name Used to name the worker thread, important only for debugging.
     * //
     */


    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sp.edit();
        if (sp.getBoolean(getString(R.string.key_notification_sticky), true)) {
            try {
                Log.i("Service", "To Locator");
                Locate.initLocation(getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("Service", "Exception to init Location.");
            }

            try {
                String intentType = intent.getExtras().getString("receiver");
                if (intentType == null) return;

                if (intentType.contentEquals("rssi_changed")) {
                    Log.i("service rssi_changed", "To NotificationISP.");
                    new NotificationISP(getApplicationContext());
                }
                if (intentType.contentEquals("reboot")) {
                    Log.i("service reboot", "To NotificationISP.");
                    if (new GetISP(this).getInfo() && new GetWeaather(this).getInfo()) {
                        new NotificationISP(getApplicationContext());
                    } else {
                        Log.i("st","some error in getisp or get weather");
                    }

                }
            } catch (Exception e) {
                Log.i("st","exception in service");
            }
        } else {
            // User has disabled notfication service so no need to proced from this point.
        }
    }
}
