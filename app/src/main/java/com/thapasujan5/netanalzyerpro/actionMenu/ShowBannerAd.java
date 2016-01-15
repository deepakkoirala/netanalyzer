package com.thapasujan5.netanalzyerpro.ActionMenu;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.thapasujan5.netanalyzerpro.R;

/**
 * Created by Sujan Thapa on 13/01/2016.
 */
public class ShowBannerAd {
    AdView adView;
    AdRequest adRequest;
    Handler mHandler = new Handler();

    public ShowBannerAd(Context context, final AdView adView) {
        this.adView = adView;
        boolean choice;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        if (context.getPackageName().contentEquals("com.thapasujan5.serversearch")) {
            Log.i("Ads", "FreeVersion");
            choice = true;
        } else {
            choice = false;
        }
        if (choice) {
           // Log.i("Ads", "GetAds");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    while (true) {
                        try {
                            //waits 10 sec. to load the admob, enough time
                            //to load the activity
                            Thread.sleep(5000);
                            mHandler.post(new Runnable() {

                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    // Write your code here to the admob
                                    adRequest = new AdRequest.Builder()
                                            //.addTestDevice("BCF7DE10DC5E699BCC5185E5B89929C8")
                                            .build();
                                    adView.loadAd(adRequest);
                                }
                            });
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                    }
                }
            }).start();


            adView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    adView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdClosed() {
                    adView.setVisibility(View.GONE);
                    super.onAdClosed();
                }
            });
        } else {
            adView.setVisibility(View.GONE);
            adView.removeView(adView);

        }
    }
}
