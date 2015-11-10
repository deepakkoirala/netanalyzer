package com.thapasujan5.netanalzyerpro.tools;



import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.Toast;

import com.thapasujan5.netanalyzerpro.R;

public class CheckNet {
	ConnectionDetector con;

	public CheckNet(Context context) {
		con = new ConnectionDetector(context);
		if (!con.isConnectingToInternet()) {
			new ShowToast(context, "No Internet Connection!", Color.WHITE,
					R.drawable.action_bar_bg, 0, Toast.LENGTH_SHORT,
					Gravity.BOTTOM);
		}
	}
}
