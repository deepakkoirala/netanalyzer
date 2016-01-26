package com.thapasujan5.netanalzyerpro.ActionMenu;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.thapasujan5.netanalyzerpro.R;
import com.thapasujan5.netanalzyerpro.Tools.UserFunctions;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Sujan Thapa on 21/01/2016.
 */
public class GetWeaather {
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    Context context;
    UserFunctions f;

    public GetWeaather(Context context) {
        this.context = context;
        sp = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        editor = sp.edit();
        f = new UserFunctions();
    }

    public boolean getInfo() {
        JSONObject jsonObject;
        if (sp.getString("lat", "").length() > 0 || sp.getString("lon", "").length() > 0) {
            Log.i("GetWeather", "Get Weather using lon/lat");
            editor.putString(context.getString(R.string.weather_source), context.getString(R.string.geolocation));
            editor.apply();
            editor.commit();
            jsonObject = f.getWeatherInfoLatLon(sp.getString(context.getString(R.string.lat), ""),
                    sp.getString(context.getString(R.string.lon), ""));

        } else {
            Log.i("GetWeather", "Get Weather Using ISP Info");
            editor.putString(context.getString(R.string.weather_source), context.getString(R.string.isp));
            editor.apply();
            editor.commit();
            jsonObject = f.getWeatherInfo(sp.getString(context.getString(R.string.city), ""), sp.getString(context.getString(R.string.country), ""));
        }
        try {
            if (jsonObject != null) {
                JSONArray array = jsonObject.getJSONArray("weather");
                JSONObject weather = array.getJSONObject(0);
                String mainWeather = weather.optString("main");
                String iconWeather = weather.optString("icon");

                JSONObject main = jsonObject.getJSONObject("main");
                String tempWeather = main.optString("temp");

                editor.putString(context.getString(R.string.name_weather), jsonObject.optString("name"));
                editor.putString(context.getString(R.string.main_weather), mainWeather);
                editor.putString(context.getString(R.string.icon_weather), iconWeather + ".png");
                editor.putString(context.getString(R.string.temp_weather), tempWeather);
                editor.apply();
                editor.commit();
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
