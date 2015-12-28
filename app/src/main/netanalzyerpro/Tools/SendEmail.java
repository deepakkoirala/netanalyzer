package com.thapasujan5.netanalzyerpro.Tools;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import com.thapasujan5.netanalzyerpro.DnsLookupActivity;

import com.thapasujan5.netanalyzerpro.R;

public class SendEmail {
	public static void startEmailActivity(Context context, String subject,
			String[] toEmail) {

		Intent report = new Intent();
		report.setAction(Intent.ACTION_SEND);
		report.setData(Uri.parse("mailto:"));
		report.setType("text/plain");
		report.putExtra(Intent.EXTRA_EMAIL, toEmail);
		report.putExtra(Intent.EXTRA_SUBJECT, subject);
		try {
			context.startActivity(Intent.createChooser(report,
					"Choose Your Mail Client."));
			((DnsLookupActivity) context).overridePendingTransition(R.anim.slide_in,
					R.anim.slide_out);
			Log.d(context.getPackageName(), "Mail Sent");

		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(context, "There is no email client installed.",
					Toast.LENGTH_SHORT).show();
			ex.printStackTrace();
		} catch (Exception e) {
			Log.d(context.getPackageName(), e.toString());
		}

	}

}