package com.thapasujan5.netanalzyerpro.tools;

import android.content.Intent;
import android.net.Uri;

public class OpenMarket {

	public static Intent openURL(String appPackageName) {
		try {
			return (new Intent(Intent.ACTION_VIEW,
					Uri.parse("market://details?id=" + appPackageName)));
		} catch (android.content.ActivityNotFoundException anfe) {
			return (new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://play.google.com/store/apps/details?id="
							+ appPackageName)));
		}
	}
}