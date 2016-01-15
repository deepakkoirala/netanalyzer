package com.thapasujan5.netanalzyerpro.Tools;

import android.content.Context;

/**
 * Created by Sujan Thapa on 10/01/2016.
 */
public class GetCode {
    public static int getCode(String seed) {
        return seed.hashCode();
    }

    public static int getCode(Context context) {
        return context.getPackageName().hashCode();
    }
}
