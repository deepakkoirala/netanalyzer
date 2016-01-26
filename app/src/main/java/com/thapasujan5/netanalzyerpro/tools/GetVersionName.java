package com.thapasujan5.netanalzyerpro.Tools;

import android.content.Context;
import android.content.res.Resources;

import com.thapasujan5.netanalyzerpro.BuildConfig;
import com.thapasujan5.netanalyzerpro.R;

/**
 * Created by Sujan Thapa on 14/01/2016.
 */
public class GetVersionName {
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            return BuildConfig.VERSION_NAME;
        } catch (Exception e) {
            try {
                return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            } catch (Resources.NotFoundException e1) {
                return context.getResources().getString(R.string.version);
            } catch (Exception e2) {
                return context.getResources().getString(R.string.version);
            }
        }
    }
}
