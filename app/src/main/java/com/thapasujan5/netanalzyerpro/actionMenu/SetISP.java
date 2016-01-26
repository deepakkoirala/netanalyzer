package com.thapasujan5.netanalzyerpro.ActionMenu;

import android.content.Context;
import android.content.SharedPreferences;

import com.thapasujan5.netanalyzerpro.R;
import com.thapasujan5.netanalzyerpro.Notification.NotificationISP;

/**
 * Created by Sujan Thapa on 10/01/2016.
 */
public class SetISP {
    public static void setISP(SharedPreferences.Editor editor, Context context, String extIPAdd, String org, String city, String country) {
        editor.putString(context.getString(R.string.org), org);
        editor.putString(context.getString(R.string.city), city);
        editor.putString(context.getString(R.string.country), country);
        editor.putString(context.getString(R.string.extIpAdd), extIPAdd);
        editor.apply();
        editor.commit();
        new NotificationISP(context);
    }
}
