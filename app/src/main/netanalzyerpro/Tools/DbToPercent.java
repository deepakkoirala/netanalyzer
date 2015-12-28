package com.thapasujan5.netanalzyerpro.Tools;

import android.util.Log;

/**
 * Created by Suzan on 12/21/2015.
 */
public class DbToPercent {
    public static double getPercentfromDb(double dBm) {
        double quality = 0.0;
        if (dBm <= -100)
            quality = 0;
        else if (dBm >= -50)
            quality = 100;
        else
            quality = 2 * (dBm + 100);
        return quality;

    }
}
