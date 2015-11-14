package com.thapasujan5.netanalzyerpro.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.thapasujan5.netanalyzerpro.R;
import com.thapasujan5.netanalzyerpro.MainActivity;

/**
 * Created by Suzan on 11/7/2015.
 */
public class Notify {
    public Notify(Context context, String extIPAdd, String org, String city, String country) {
        // Prepare intent which is triggered if the
        // notification is selected
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);

        // Build notification
        // Actions are just fake
        Notification noti;

        if (extIPAdd != null && org != null && city != null && country != null) {
            noti = new Notification.Builder(context).setTicker("ISP Information").setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pIntent).setColor(context.getResources().getColor(R.color.colorPrimary))
                    .setContentTitle("External IP: " + extIPAdd)
                    .setContentText(org + " "
                            + city + ", " + country).build();

            ;
        } else {
            noti = new Notification.Builder(context).setTicker("ISP Information").setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pIntent).setColor(context.getResources().getColor(R.color.colorPrimary))
                    .setContentTitle("Check Network Access !")
                    .setContentText("Touch to reload.").build();
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_ONGOING_EVENT;

        notificationManager.notify(0, noti);

    }
}
