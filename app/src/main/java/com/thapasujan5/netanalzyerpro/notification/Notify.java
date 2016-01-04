package com.thapasujan5.netanalzyerpro.Notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.thapasujan5.netanalyzerpro.R;
import com.thapasujan5.netanalzyerpro.AppConstants;
import com.thapasujan5.netanalzyerpro.MainActivity;
import com.thapasujan5.netanalzyerpro.Tools.WifiUtil;

/**
 * Created by Suzan on 11/7/2015.
 */
public class Notify {
    SharedPreferences sp;

    String org, city, country, extIpAdd, intIp;
    String name, gateway, ip, mac, percent;
    String weather;

    int source;
    Context context;

    public Notify(Context context) {

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder n = new NotificationCompat.Builder(context);
        n.setContentIntent(pendingIntent).setTicker("Ticker").setSmallIcon(R.mipmap.about).setContentTitle("Network Alert.").setContentText("Review your network connection.")
                .setAutoCancel(true);
        notificationManager.notify(0, n.build());

    }


    private void NotifyWifi() {
        gatherWifiData();

        int icon = R.mipmap.ic_launcher;
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, context.getResources().getString(R.string.app_name), when);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.custom_notification);
        contentView.setImageViewResource(R.id.ivWifi, R.mipmap.ic_launcher);
        contentView.setTextViewText(R.id.tvISP, org + ", " + city + ", " + country);
        contentView.setTextViewText(R.id.tvExip, extIpAdd);
        contentView.setTextViewText(R.id.tvIntIp, ip);

        contentView.setTextViewText(R.id.tvNetworkName, name);
        contentView.setTextViewText(R.id.tvPercent, percent);
        contentView.setTextViewText(R.id.tvGateway, gateway);
        contentView.setTextViewText(R.id.tvMac, mac);
        contentView.setTextViewText(R.id.tvWeather, weather);


        notification.contentView = contentView;

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.contentIntent = contentIntent;


        if (sp.getBoolean(context.getString(R.string.key_notification_sticky), true) == true) {
            if (sp.getBoolean(context.getString(R.string.key_notification_ongoing), true) == true) {
                notification.flags |= Notification.FLAG_NO_CLEAR; //Do not clear the notification
            }
            notification.defaults |= Notification.DEFAULT_LIGHTS; // LED
            notification.defaults |= Notification.DEFAULT_VIBRATE; //Vibration
            notification.defaults |= Notification.DEFAULT_SOUND; // Sound
            mNotificationManager.notify(0, notification);
        }
    }

    private void NotifyData() {

    }


    private void gatherWifiData() {

        name = new WifiUtil(context).getSsid();
        gateway = new WifiUtil(context).getGateway();
        ip = new WifiUtil(context).getIpAddress();
        mac = new WifiUtil(context).getMacAddress();
        percent = new WifiUtil(context).getPercentSignal();

        org = sp.getString(context.getString(R.string.org), "");
        city = sp.getString(context.getString(R.string.city), "");
        country = sp.getString(context.getString(R.string.country), "");
        extIpAdd = sp.getString(context.getString(R.string.extIpAdd), "");

        //ToDo Weather API Works to be done.
        weather = "Penshurst Clouds 20 C";
    }

    public Notify(Context context, int source) {
        this.source = source;
        this.context = context;
        sp = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        if (source == AppConstants.TYPE_WIFI) {
            Log.i("Notify", "type wifi");
            NotifyWifi();
        } else if (source == AppConstants.TYPE_MOBILE) {
            Log.i("Notify", "type mobile");
            NotifyData();
        }


//        sp = PreferenceManager.getDefaultSharedPreferences(context);
//        // Prepare intent which is triggered if the
//        // notification is selected
//        Intent intent = new Intent(context, MainActivity.class);
//        PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);
//        //Older Devices
//        NotificationCompat.Builder n = new NotificationCompat.Builder(context);
//        //For api>15
//        Notification noti = null;
//        if (extIpAdd != null && org != null && city != null && country != null) {
//            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                noti = new Notification.Builder(context).setTicker("ISP Information").setSmallIcon(R.mipmap.ic_launcher)
//                        .setContentIntent(pIntent).setColor(context.getResources().getColor(R.color.colorPrimary))
//                        .setContentTitle(org + " "
//                                + city + ", " + country)
//                        .setContentText("IntIP: " + intIp + "  ExIP: " + extIpAdd).build();
//            } else {
//                n.setSmallIcon(R.mipmap.ic_launcher)
//                        .setContentIntent(pIntent)
//                        .setContentTitle(org + " "
//                                + city + ", " + country)
//                        .setContentText("IP: " + intIp + "  /  " + extIpAdd);
//            }
//
//
//        } else {
//            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                noti = new Notification.Builder(context).setTicker("ISP Information").setSmallIcon(R.mipmap.ic_launcher)
//                        .setContentIntent(pIntent).setColor(context.getResources().getColor(R.color.colorPrimary))
//                        .setContentTitle(context.getString(R.string.limited_access))
//                        .setContentText(context.getString(R.string.intranet_env)).build();
//            } else {
//                n.setSmallIcon(R.mipmap.ic_launcher)
//                        .setContentIntent(pIntent).setContentTitle(context.getString(R.string.limited_access))
//                        .setContentText(context.getString(R.string.intranet_env));
//            }
//        }
//
//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//
    }
}