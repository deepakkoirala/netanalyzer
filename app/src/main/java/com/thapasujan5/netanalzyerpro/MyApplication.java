package com.thapasujan5.netanalzyerpro;

import com.parse.Parse;
import com.parse.ParseInstallation;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;


/**
 * Created by Sujan Thapa on 6/01/2016.
 */
@ReportsCrashes(
        formUri = "https://collector.tracepot.com/b2c2fec6"
)
public class MyApplication extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "DSqX7ySU4sYBE23ZRFFFghIdmxI2SOlkEh4K67Cy", "wlZPVal6UEG6vrJnTESYFJ8rTy2WLYMHAc2OdeXZ");
        ParseInstallation.getCurrentInstallation().saveInBackground();
        // The following line triggers the initialization of ACRA
        ACRA.init(this);
    }
}

