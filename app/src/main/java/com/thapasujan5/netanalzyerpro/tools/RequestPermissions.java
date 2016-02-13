package com.thapasujan5.netanalzyerpro.Tools;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;

import com.thapasujan5.netanalyzerpro.R;
import com.thapasujan5.netanalzyerpro.AppConstants;

/**
 * Created by Sujan Thapa on 8/01/2016.
 */
public class RequestPermissions implements ActivityCompat.OnRequestPermissionsResultCallback {
    private Context context;
    private int code;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    boolean result = false;

    public RequestPermissions(Context context) {
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        editor = sharedPreferences.edit();
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case AppConstants.ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    editor.putBoolean(context.getString(R.string.access_fine_location), true);
                    editor.apply();
                    editor.commit();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    editor.putBoolean(context.getString(R.string.access_fine_location), false);
                    editor.apply();
                    editor.commit();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
            }
            case AppConstants.READ_PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    editor.putBoolean(context.getString(R.string.read_phone_state), true);
                    editor.apply();
                    editor.commit();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    editor.putBoolean(context.getString(R.string.read_phone_state), false);
                    editor.apply();
                    editor.commit();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
            }
            case AppConstants.READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    editor.putBoolean(context.getString(R.string.read_external_storage), true);
                    editor.apply();
                    editor.commit();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    editor.putBoolean(context.getString(R.string.read_external_storage), false);
                    editor.apply();
                    editor.commit();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
            }
            case AppConstants.WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    editor.putBoolean(context.getString(R.string.write_external_storage), true);
                    editor.apply();
                    editor.commit();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    editor.putBoolean(context.getString(R.string.write_external_storage), false);
                    editor.apply();
                    editor.commit();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
            }
            case AppConstants.ACCESS_CORASE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    editor.putBoolean(context.getString(R.string.access_corase_location), true);
                    editor.apply();
                    editor.commit();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    editor.putBoolean(context.getString(R.string.access_corase_location), false);
                    editor.apply();
                    editor.commit();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
            }
            case AppConstants.SYSTEM_ALERT_WINDOW: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    result = true;
                } else {
                    editor.putBoolean(context.getString(R.string.access_corase_location), false);
                    editor.apply();
                    editor.commit();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
            }


        }
    }
}
