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

import com.thapasujan5.netanalyzerpro.BuildConfig;
import com.thapasujan5.netanalyzerpro.R;

public class SplashActivity extends Activity {
    public static final String MyPREFERENCES = "MyPrefs";

    ImageView iv;
    Image icon;
    TextView versionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //   overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        setContentView(R.layout.activity_splash);
        versionName = (TextView) findViewById(R.id.projectversion);
        //versionName.setText(getString(R.string.version));
        versionName.setText(BuildConfig.VERSION_NAME);
        iv = (ImageView) findViewById(R.id.icon);
        Animation an2 = AnimationUtils.loadAnimation(this, R.anim.splash_anim1);
        iv.startAnimation(an2);
        startMainActivity();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        finish();
    }

    public void startMainActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(getApplicationContext(),
                        MainActivity.class);
                startActivity(i);
                //  overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        }, AppConstants.SPLASH_TIME);
    }
}
