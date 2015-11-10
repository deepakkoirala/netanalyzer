package com.thapasujan5.netanalzyerpro;


import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.thapasujan5.netanalyzerpro.R;

public class SplashScreen extends Activity {
	public static final String MyPREFERENCES = "MyPrefs";

	ImageView iv;
	Image icon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);

		iv = (ImageView) findViewById(R.id.icon);

		Animation an2 = AnimationUtils.loadAnimation(this, R.anim.splash);
		iv.startAnimation(an2);

		startMainActivity();
		overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
	}


	@Override
	protected void onPause() {
		super.onPause();
		finish();
		overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
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
