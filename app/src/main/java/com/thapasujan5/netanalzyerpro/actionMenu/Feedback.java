package com.thapasujan5.netanalzyerpro.ActionMenu;



import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.thapasujan5.netanalyzerpro.R;

public class Feedback {
	Context context;

	public Feedback(final Context context) {
		this.context = context;

		final Dialog d = new Dialog(context);

		d.requestWindowFeature(Window.FEATURE_NO_TITLE);
		d.setContentView(R.layout.dialog_feedback);
		final EditText etSubject, etMessage;
		etSubject = (EditText) d.findViewById(R.id.etSubject);
		etMessage = (EditText) d.findViewById(R.id.etMessage);
		ImageView icon = (ImageView) d.findViewById(R.id.icon);
		icon.setImageResource(R.drawable.ic_action_about);
		Button btnSendFeedback = (Button) d.findViewById(R.id.btnSendFeedback);
		btnSendFeedback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String sub = etSubject.getText().toString().trim();
				String body = etMessage.getText().toString().trim();
				if (body.length() > 0 && sub.length() > 0) {
					Intent emailIntent = new Intent(Intent.ACTION_SEND);
					emailIntent.setData(Uri.parse("mailto:"));
					emailIntent.setType("text/plain");
					emailIntent.putExtra(Intent.EXTRA_EMAIL,
							new String[] { context.getString(R.string.email) });
					emailIntent.putExtra(Intent.EXTRA_SUBJECT, sub);
					emailIntent.putExtra(Intent.EXTRA_TEXT, body);
					try {
						context.startActivity(Intent.createChooser(emailIntent,
								"Send Feedback via Email:"));
						((Activity) context).overridePendingTransition(
								R.anim.slide_in, R.anim.slide_out);
						Toast.makeText(context, "Thanks for your Support !",
								Toast.LENGTH_SHORT).show();
						Log.i("Finished Sendig Mail...", "");
					} catch (android.content.ActivityNotFoundException ex) {
						Toast.makeText(context,
								"There is no email client installed.",
								Toast.LENGTH_SHORT).show();
						ex.printStackTrace();
					}
					d.dismiss();
				} else {
					Toast.makeText(
							context,
							"Please Write your Subject or Message before Sending !",
							Toast.LENGTH_SHORT).show();

				}
			}
		});
		d.show();

	}
}
