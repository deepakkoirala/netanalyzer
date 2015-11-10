package com.thapasujan5.netanalzyerpro.tools;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class SocialIntentProvider {
	public static Intent getOpenFacebookIntent(Context context, String fbid,
			String fbname) {

		try {
			context.getPackageManager()
					.getPackageInfo("com.facebook.katana", 0);
			return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/"
					+ fbid));
		} catch (Exception e) {
			return new Intent(Intent.ACTION_VIEW,
					Uri.parse("https://www.facebook.com/" + fbname));
		}
	}

	public static Intent getOpenTwitterIntent(String twitterid,
			String twittername) {
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("twitter://user?screen_name=" + twittername));
			return intent;

		} catch (Exception e) {
			return (new Intent(Intent.ACTION_VIEW,
					Uri.parse("https://twitter.com/#!/" + twittername)));
		}
	}
}
