package com.thapasujan5.netanalzyerpro;

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

        // The following line triggers the initialization of ACRA
        ACRA.init(this);
    }
}

