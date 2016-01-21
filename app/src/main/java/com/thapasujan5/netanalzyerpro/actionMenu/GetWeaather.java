package com.thapasujan5.netanalzyerpro.ActionMenu;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.thapasujan5.netanalyzerpro.R;
import com.thapasujan5.netanalzyerpro.Tools.UserFunctions;

import org.json.JSONObject;

/**
 * Created by Sujan Thapa on 21/01/2016.
 */
public class GetWeaather {
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    Context context;

    public GetWeaather(Context context) {
        this.context = context;
        sp = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        editor = sp.edit();
    }

    private boolean getInfo() {
        UserFunctions f = new UserFunctions();

        Log.i("service reboot", "To NotificationISP.");
        String org, city , country , extIPAdd;
        try {
            JSONObject json = f.getOwnInfo();
            if (json.getString("status").contentEquals("success")) {
                extIPAdd = json.getString("query");
                org = json.getString("org");
                city = json.getString("city");
                country = json.getString("country");

                editor.putString(context.getString(R.string.org), org);
                editor.putString(context.getString(R.string.city), city);
                editor.putString(context.getString(R.string.country), country);
                editor.putString(context.getString(R.string.extIpAdd), extIPAdd);
                editor.apply();
                editor.commit();
                return true;

            } else if (json.getString("status").contentEquals("fail")) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
