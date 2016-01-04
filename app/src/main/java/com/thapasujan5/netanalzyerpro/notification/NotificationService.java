package com.thapasujan5.netanalzyerpro.Notification;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.thapasujan5.netanalyzerpro.R;
import com.thapasujan5.netanalzyerpro.AppConstants;
import com.thapasujan5.netanalzyerpro.Tools.NetworkUtil;
import com.thapasujan5.netanalzyerpro.Tools.UserFunctions;

import org.json.JSONObject;

/**
 * Created by Suzan on 11/7/2015.
 */
public class NotificationService extends IntentService {


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public NotificationService() {
        super("ReminderService");


    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */


    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            String intentType = intent.getExtras().getString("caller");
            if (intentType == null) return;
            if (intentType.contentEquals("RebootReceiver")) {
                //Do reboot stuff
                //handle other types of callers, like a notification_main.
                String org, city, country, extIPAdd;
                boolean data = false;
                UserFunctions f = new UserFunctions();
                try {
                    JSONObject json = f.getOwnInfo();
                    if (json.getString("status").contentEquals("success")) {
                        extIPAdd = json.getString("query");
                        org = json.getString("org");
                        city = json.getString("city");
                        country = json.getString("country");
                        data = true;
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = sp.edit();

                        editor.putString(getString(R.string.org), org);
                        editor.putString(getString(R.string.city), city);
                        editor.putString(getString(R.string.country), country);
                        editor.putString(getString(R.string.extIpAdd), extIPAdd);
                        editor.apply();
                        editor.commit();


                        int source = -1;
                        if (NetworkUtil.getConnectivityStatus(getApplicationContext()) == AppConstants.TYPE_WIFI) {
                            source = AppConstants.TYPE_WIFI;
                        } else {
                            source = AppConstants.TYPE_MOBILE;
                        }
                        if (source != -1) {
                            Log.i("NotificationService", "To Notify");
                            new Notify(getApplicationContext(), source);
                        } else {
                            Log.i("NotificationService", "Error with Connections");
                        }

                    } else if (json.getString("status").contentEquals("fail")) {

                    }

                } catch (Exception e) {
                    // new Notify(getApplicationContext());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
