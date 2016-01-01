package com.thapasujan5.netanalzyerpro.ActionMenu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.widget.Toast;

import com.thapasujan5.netanalyzerpro.R;

/**
 * Created by Suzan on 12/23/2015.
 */
public class ShareApp {
    public ShareApp(final Context context) {
        Toast.makeText(context.getApplicationContext(), "Please wait !", Toast.LENGTH_LONG).show();
        new CountDownTimer(Toast.LENGTH_LONG, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out this new app " + context.getResources().getString(R.string.bitlyurl) + " I found to be useful.");
                sendIntent.setType("text/plain");
                context.startActivity(Intent.createChooser(sendIntent,
                        "How would you like to spread your message?"));
                ((Activity) context).overridePendingTransition(R.anim.slide_in,
                        R.anim.slide_out);

            }
        }.start();

    }
}
