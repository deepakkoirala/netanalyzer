package com.thapasujan5.netanalzyerpro.ActionMenu;



import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.thapasujan5.netanalzyerpro.Tools.OpenMarket;

import com.thapasujan5.netanalyzerpro.R;

public class RateApp {
	Context context;

	public RateApp(Context context) {
		Toast.makeText(context.getApplicationContext(), "Please wait !", Toast.LENGTH_LONG).show();
		context.startActivity(OpenMarket.openURL(context.getPackageName()));
		((Activity) context).overridePendingTransition(R.anim.slide_in,
				R.anim.slide_out);
	}
}
