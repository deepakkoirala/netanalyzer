package com.thapasujan5.netanalzyerpro.Notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.thapasujan5.netanalyzerpro.R;
import com.thapasujan5.netanalzyerpro.AppConstants;
import com.thapasujan5.netanalzyerpro.MainActivity;
import com.thapasujan5.netanalzyerpro.Tools.DataUtil;
import com.thapasujan5.netanalzyerpro.Tools.DownloadFileFromUrl;
import com.thapasujan5.netanalzyerpro.Tools.FileCache;
import com.thapasujan5.netanalzyerpro.Tools.GetDeviceIP;
import com.thapasujan5.netanalzyerpro.Tools.ImageLoader;
import com.thapasujan5.netanalzyerpro.Tools.WifiUtil;

import java.io.File;

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
    ImageLoader imageLoader;

    public Notify(Context context) {

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder n = new NotificationCompat.Builder(context);
        n.setContentIntent(pendingIntent).setTicker("Ticker").setSmallIcon(R.mipmap.about).setContentTitle("Network Alert.").setContentText("Review your network connection.")
                .setAutoCancel(true);
        notificationManager.notify(0, n.build());

    }

    public Notify(Context context, int source) {
        this.source = source;
        this.context = context;
        imageLoader = new ImageLoader(context);
        sp = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        if (source == AppConstants.TYPE_WIFI) {
            //    Log.i("Notify", "type wifi");
            NotifyWifi();
        } else if (source == AppConstants.TYPE_MOBILE) {
            //   Log.i("Notify", "type mobile");
            NotifyData();
        }
    }


    private void NotifyWifi() {
        getWifiData();
        int icon = R.mipmap.ic_wifi_logo;
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, context.getResources().getString(R.string.app_name), when);


        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification_wifi);
        contentView.setImageViewResource(R.id.ivWifi, R.mipmap.ic_launcher);
        FileCache fc = new FileCache(context);
        File file = fc.getFile(AppConstants.url_weather_icon + sp.getString(context.getString(R.string.icon_weather), ""));
        Bitmap bitmap = null;
        if (file.exists() == false) {
           // Log.d("Notify", "To Download: " + AppConstants.url_weather_icon + sp.getString(context.getString(R.string.icon_weather), ""));
            new DownloadFileFromUrl(context, AppConstants.url_weather_icon + sp.getString(context.getString(R.string.icon_weather), ""));
        } else {
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        }
        if (bitmap == null) {
            contentView.setImageViewResource(R.id.ivWeather, R.mipmap.ic_launcher);
        } else {
            contentView.setImageViewBitmap(R.id.ivWeather, bitmap);
        }
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
                notification.flags |= Notification.FLAG_ONGOING_EVENT; //Do not clear the notification
            }

//            notification.defaults |= Notification.DEFAULT_LIGHTS; // LED
//            notification.defaults |= Notification.DEFAULT_VIBRATE; //Vibration
//            notification.defaults |= Notification.DEFAULT_SOUND; // Sound
            mNotificationManager.notify(0, notification);
        }
    }

    private void NotifyData() {
        getMobileData();
        int icon = R.mipmap.ic_data;
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, context.getResources().getString(R.string.app_name), when);


        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification_data);
        contentView.setImageViewResource(R.id.ivData, R.mipmap.ic_data);
        FileCache fc = new FileCache(context);
        File file = fc.getFile(AppConstants.url_weather_icon + sp.getString(context.getString(R.string.icon_weather), ""));
        Bitmap bitmap = null;
        if (file.exists() == false) {
          //  Log.d("Notify", "To Download: " + AppConstants.url_weather_icon + sp.getString(context.getString(R.string.icon_weather), ""));
            new DownloadFileFromUrl(context, AppConstants.url_weather_icon + sp.getString(context.getString(R.string.icon_weather), ""));
        } else {
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        }
        if (bitmap == null) {
            contentView.setImageViewResource(R.id.ivWeather, R.mipmap.ic_launcher);
        } else {
            contentView.setImageViewBitmap(R.id.ivWeather, bitmap);
        }
        contentView.setTextViewText(R.id.tvISP, org + ", " + city + ", " + country);
        contentView.setTextViewText(R.id.tvExip, extIpAdd);
        contentView.setTextViewText(R.id.tvIntIp, ip);

        contentView.setTextViewText(R.id.tvNetworkName, name);
        contentView.setTextViewText(R.id.tvPercent, "");
        //contentView.setTextViewText(R.id.tvGateway, gateway);
        //contentView.setTextViewText(R.id.tvMac, mac);
        contentView.setTextViewText(R.id.tvWeather, weather);

        notification.contentView = contentView;

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.contentIntent = contentIntent;


        if (sp.getBoolean(context.getString(R.string.key_notification_sticky), true) == true) {
            if (sp.getBoolean(context.getString(R.string.key_notification_ongoing), true) == true) {
                notification.flags |= Notification.FLAG_ONGOING_EVENT; //Do not clear the notification
            }

//            notification.defaults |= Notification.DEFAULT_LIGHTS; // LED
//            notification.defaults |= Notification.DEFAULT_VIBRATE; //Vibration
//            notification.defaults |= Notification.DEFAULT_SOUND; // Sound
            mNotificationManager.notify(0, notification);
        }
    }


    private void getWifiData() {
        name = new WifiUtil(context).getSsid();
        gateway = new WifiUtil(context).getGateway();
        ip = new WifiUtil(context).getIpAddress();
        mac = new WifiUtil(context).getMacAddress();
        percent = new WifiUtil(context).getPercentSignal();

        org = sp.getString(context.getString(R.string.org), "");
        city = sp.getString(context.getString(R.string.city), "");
        country = sp.getString(context.getString(R.string.country), "");
        extIpAdd = sp.getString(context.getString(R.string.extIpAdd), "");


        weather = sp.getString(context.getString(R.string.name_weather), "s") + " " + sp.getString(context.getString(R.string.main_weather), "") + " " + sp.getString(context.getString(R.string.temp_weather), "") + "\u2103";
    }

    private void getMobileData() {
        name = new DataUtil(context).getOperatorName();
        //  gateway = new WifiUtil(context).getGateway();
        ip = GetDeviceIP.getDeviceIP();
        //   mac = new WifiUtil(context).getMacAddress();
        //percent = new DataUtil(context).ge

        org = sp.getString(context.getString(R.string.org), "");
        city = sp.getString(context.getString(R.string.city), "");
        country = sp.getString(context.getString(R.string.country), "");
        extIpAdd = sp.getString(context.getString(R.string.extIpAdd), "");


        weather = sp.getString(context.getString(R.string.name_weather), "s") + " " + sp.getString(context.getString(R.string.main_weather), "") + " " + sp.getString(context.getString(R.string.temp_weather), "") + "\u2103";
    }


}