package com.thapasujan5.netanalzyerpro.Notification;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.thapasujan5.netanalyzerpro.R;
import com.thapasujan5.netanalzyerpro.LocationServices.Locate;
import com.thapasujan5.netanalzyerpro.Tools.UserFunctions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.logging.Handler;

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
                    Log.i("service rssi_changed", "To Notify.");
                    new Notify(getApplicationContext());
                }

                if (intentType.contentEquals("reboot")) {
                    UserFunctions f = new UserFunctions();
                    SharedPreferences.Editor editor = sp.edit();
                    Log.i("service reboot", "To Notify.");
                    String org = "", city = "", country = "", extIPAdd = "";
                    try {
                        JSONObject json = f.getOwnInfo();
                        if (json.getString("status").contentEquals("success")) {
                            extIPAdd = json.getString("query");
                            org = json.getString("org");
                            city = json.getString("city");
                            country = json.getString("country");

                            editor.putString(getString(R.string.org), org);
                            editor.putString(getString(R.string.city), city);
                            editor.putString(getString(R.string.country), country);
                            editor.putString(getString(R.string.extIpAdd), extIPAdd);
                            editor.apply();
                            editor.commit();

                        } else if (json.getString("status").contentEquals("fail")) {

                        }
                        JSONObject jsonObject;
                        if (sp.getString("lat", "").length() > 0 || sp.getString("lon", "").length() > 0) {
                            Log.i("Service", "Get Weather using lon/lat");
                            editor.putString(getString(R.string.weather_source), getString(R.string.geolocation));
                            editor.apply();
                            editor.commit();
                            jsonObject = f.getWeatherInfoLatLon(sp.getString(getString(R.string.lat), city), sp.getString(getString(R.string.lon), country));

                        } else {
                            Log.i("Service", "Get Weather Using ISP Info");
                            editor.putString(getString(R.string.weather_source), getString(R.string.isp));
                            editor.apply();
                            editor.commit();
                            jsonObject = f.getWeatherInfo(city, country);


                        }

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

                    } catch (Exception e) {
                        new Notify(getApplicationContext());
                    }
                    new Notify(getApplicationContext());
                }
            } catch (Exception e) {
                new Notify(getApplicationContext());
            }
        } else {
            // User has disabled notfication service so no need to proced from this point.
        }
    }
}
