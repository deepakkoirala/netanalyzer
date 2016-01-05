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

import org.json.JSONArray;
import org.json.JSONObject;

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

        try {
            String intentType = intent.getExtras().getString("receiver");
            if (intentType == null) return;
            if (intentType.contentEquals("rssi_changed")) {
              //  Log.d("service", "rssi_changed");
                int source = -1;
                if (NetworkUtil.getConnectivityStatus(getApplicationContext()) == AppConstants.TYPE_WIFI) {
                    source = AppConstants.TYPE_WIFI;
                } else {
                    source = AppConstants.TYPE_MOBILE;
                }
                if (source != -1) {
                    //  Log.i("Service", "To Notify");
                    new Notify(getApplicationContext(), source);
                } else {
                    //    Log.i("Service", "Error with Connections");
                    new Notify(getApplicationContext());
                }

            }
            if (intentType.contentEquals("reboot")) {
             //   Log.d("service", "reboot");
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

//                        Calendar cal = Calendar.getInstance();
//
//                        int millisecond = cal.get(Calendar.MILLISECOND);
//                        int second = cal.get(Calendar.SECOND);
//                        int minute = cal.get(Calendar.MINUTE);
//                        //12 hour format
//                        int hour = cal.get(Calendar.HOUR);
//                        //24 hour format
//                        int hourofday = cal.get(Calendar.HOUR_OF_DAY);
                        //Get Weather Info.
                        JSONObject jsonObject = f.getWeatherInfo(city, country);

                        JSONArray array = jsonObject.getJSONArray("weather");
                        JSONObject weather = array.getJSONObject(0);
                        String mainWeather = weather.optString("main");
                        String iconWeather = weather.optString("icon");

                        JSONObject main = jsonObject.getJSONObject("main");
                        String tempWeather = main.optString("temp");

                        editor.putString(getString(R.string.name_weather), jsonObject.optString("name"));
                        editor.putString(getString(R.string.main_weather), mainWeather);
                        editor.putString(getString(R.string.icon_weather), iconWeather + ".png");
                        editor.putString(getString(R.string.temp_weather), tempWeather);
                        editor.apply();
                        editor.commit();
                        //      MyLocation.getMyLoation(getApplicationContext());
                        int source = -1;
                        if (NetworkUtil.getConnectivityStatus(getApplicationContext()) == AppConstants.TYPE_WIFI) {
                            source = AppConstants.TYPE_WIFI;
                        } else {
                            source = AppConstants.TYPE_MOBILE;
                        }
                        if (source != -1) {
                            //  Log.i("Service", "To Notify");
                            new Notify(getApplicationContext(), source);
                        } else {
                            //    Log.i("Service", "Error with Connections");
                            new Notify(getApplicationContext());
                        }
                    } else if (json.getString("status").contentEquals("fail")) {

                    }

                } catch (Exception e) {
                    //sLog.d("NS", "err");
                    new Notify(getApplicationContext());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
