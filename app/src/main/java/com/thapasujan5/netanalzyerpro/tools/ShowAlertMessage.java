package com.thapasujan5.netanalzyerpro.tools;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;

public class ShowAlertMessage {

	public ShowAlertMessage(Context context, String title, String message) {

		AlertDialog.Builder alert = new AlertDialog.Builder(context,
				AlertDialog.THEME_HOLO_DARK);
		TextView myMsg = new TextView(context);
		myMsg.setText(message);
		myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
		myMsg.setTextColor(Color.WHITE);
		myMsg.setTextSize(20);
		myMsg.setPadding(0, 15, 0, 15);
		alert.setIcon(android.R.drawable.ic_dialog_info).setTitle(title)
				.setView(myMsg).setPositiveButton("Ok", null);
		Dialog d = alert.create();
		d.show();
	}
}
