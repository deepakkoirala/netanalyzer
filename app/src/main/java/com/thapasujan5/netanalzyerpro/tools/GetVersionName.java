package com.thapasujan5.netanalzyerpro.Tools;

import android.content.Context;
import android.content.res.Resources;

import com.thapasujan5.netanalyzerpro.BuildConfig;

/**
 * Created by Sujan Thapa on 14/01/2016.
 */
public class GetVersionName {
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            versionName = BuildConfig.VERSION_NAME;
        } catch (Exception e) {
            try {
                versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            } catch (Resources.NotFoundException e1) {

            } catch (Exception e2) {

            }
        }
        return versionName;
    }
}
