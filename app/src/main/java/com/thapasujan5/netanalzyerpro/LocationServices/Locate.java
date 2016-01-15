package com.thapasujan5.netanalzyerpro.LocationServices;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;

import com.thapasujan5.netanalyzerpro.R;
import com.thapasujan5.netanalzyerpro.Notification.ReceiverReboot;

/**
 * Created by Sujan Thapa on 8/01/2016.
 */
public class Locate {
    public static void initLocation(final Context context) throws Exception {
        final Locator locator = new Locator(context);
        locator.getLocation(Locator.Method.NETWORK_THEN_GPS, new Locator.Listener() {
            @Override
            public void onLocationFound(Location location) {
                double lat = location.getLatitude();
                double lon = location.getLongitude();
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                if (sp.getString(context.getString(R.string.lon), "").contentEquals(Double.toString(lon)) == false || sp.getString(context.getString(R.string.lat), "").contentEquals(Double.toString(lat)) == false) {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(context.getString(R.string.lat), Double.toString(lat));
                    editor.putString(context.getString(R.string.lon), Double.toString(lon));
                    editor.apply();
                    editor.commit();
                    Log.i("New Location Found", lat + " " + lon);
                    new ReceiverReboot();
                } else {
                    Log.i("Same Location", "Lat:" + lat + "|" + sp.getString(context.getString(R.string.lat), "") + " Lon:" + lon + "|" + sp.getString(context.getString(R.string.lon), ""));
                }
            }

            @Override
            public void onLocationNotFound() {
                Log.i("Locator", "No location");
            }
        });
    }
}
