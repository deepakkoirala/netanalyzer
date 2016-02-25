package com.thapasujan5.netanalzyerpro.Tools;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;

import com.thapasujan5.netanalzyerpro.AppConstants;

/**
 * Created by Sujan Thapa on 4/01/2016.
 */
public class DataUtil {
    TelephonyManager telephonyManager;
    Context context;


    public DataUtil(Context context) {
        this.context = context;
        try {
            telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public String getOperatorName() {
        return telephonyManager.getNetworkOperatorName();
    }


    public int calculateSignalStrengthInPercent(int signalStrength) {
        return (int) ((float) signalStrength / AppConstants.MAX_SIGNAL_DBM_VALUE * 100);
    }


    @SuppressWarnings("deprecation")
    public boolean isAirplaneModeOn() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
        /* API 17 and above */
            return Settings.Global.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
        } else {
        /* below */
            return Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) != 0;
        }
    }

    public String getLine1Number() {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ((Activity) context).requestPermissions(new String[]{Manifest.permission.READ_SMS},
                    1);
        } else {
            return telephonyManager.getLine1Number().toString();
        }
        return "";
    }

}


