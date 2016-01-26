package com.thapasujan5.netanalzyerpro;


import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.thapasujan5.netanalyzerpro.R;
import com.thapasujan5.netanalzyerpro.Tools.GetVersionName;

public class SplashActivity extends Activity {
    public static final String MyPREFERENCES = "MyPrefs";

    ImageView iv;
    Image icon;
    TextView versionName, appName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        setContentView(R.layout.activity_splash);
        versionName = (TextView) findViewById(R.id.projectversion);
        versionName.setText(GetVersionName.getAppVersionName(this));
        appName = (TextView) findViewById(R.id.appName);
        if (this.getPackageName().contentEquals("com.thapasujan5.serversearch")) {
            appName.setText(this.getString(R.string.app_name)+" Lite");
        } else {
            appName.setText(this.getString(R.string.app_name));
        }
        iv = (ImageView) findViewById(R.id.icon);
        Animation an2 = AnimationUtils.loadAnimation(this, R.anim.splash);
        iv.startAnimation(an2);
        startMainActivity();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        finish();
    }

    public void startMainActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(getApplicationContext(),
                        MainActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        }, AppConstants.SPLASH_TIME);
    }
}
