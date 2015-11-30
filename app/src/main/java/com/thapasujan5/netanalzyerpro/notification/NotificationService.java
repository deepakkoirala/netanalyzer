package com.thapasujan5.netanalzyerpro.notification;

import android.app.IntentService;
import android.content.Intent;

import com.thapasujan5.netanalzyerpro.tools.GetIntIP;
import com.thapasujan5.netanalzyerpro.tools.UserFunctions;

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
                //handle other types of callers, like a notification.
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
                        new Notify(getApplicationContext(), extIPAdd, GetIntIP.getInternalIP(getApplicationContext()), org, city, country);

                    } else if (json.getString("status").contentEquals("fail")) {

                    }

                } catch (Exception e) {
                    new Notify(getApplicationContext(), "Network Alert !","You've Limited Access ", null, null, null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
