package com.thapasujan5.netanalzyerpro.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.NotificationCompat;

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


        //Older Devices
        NotificationCompat.Builder n = new NotificationCompat.Builder(context);
        //For api>15
        Notification noti = null;
        if (extIPAdd != null && org != null && city != null && country != null) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                noti = new Notification.Builder(context).setTicker("ISP Information").setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(pIntent).setColor(context.getResources().getColor(R.color.colorPrimary))
                        .setContentTitle("External IP: " + extIPAdd)
                        .setContentText(org + " "
                                + city + ", " + country).build();
            } else {
                n.setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(pIntent)
                        .setContentTitle("External IP: " + extIPAdd)
                        .setContentText(org + " "
                                + city + ", " + country);
            }


        } else {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                noti = new Notification.Builder(context).setTicker("ISP Information").setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(pIntent).setColor(context.getResources().getColor(R.color.colorPrimary))
                        .setContentTitle("Check Network Access !")
                        .setContentText("Touch to reload.").build();
            } else {
                n.setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(pIntent).setContentTitle("Check Network Access !")
                        .setContentText("Touch to reload.");
            }
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // hide the notification after its selected
        if (noti != null) {
            noti.flags |= Notification.FLAG_ONGOING_EVENT;
            notificationManager.notify(0, noti);
        } else {
            n.setAutoCancel(false);
            n.setOngoing(true);
            notificationManager.notify(0, n.build());
        }
    }
}
