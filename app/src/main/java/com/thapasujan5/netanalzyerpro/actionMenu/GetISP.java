package com.thapasujan5.netanalzyerpro.ActionMenu;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.thapasujan5.netanalyzerpro.R;
import com.thapasujan5.netanalzyerpro.Tools.NetworkUtil;
import com.thapasujan5.netanalzyerpro.Tools.UserFunctions;

import org.json.JSONObject;

/**
 * Created by Sujan Thapa on 21/01/2016.
 */
public class GetISP {
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    Context context;

    public GetISP(Context context) {
        this.context = context;
        sp = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        editor = sp.edit();
    }

    public boolean getInfo() {
        UserFunctions f = new UserFunctions();


        String org, city, country, extIPAdd;
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
                editor.putString(context.getString(R.string.source_isp_info), Integer.toString(NetworkUtil.getConnectivityStatus(context)));
                editor.apply();
                editor.commit();
                Log.i("GetISP", "Got isp.");
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
