package com.thapasujan5.netanalzyerpro.Notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.thapasujan5.netanalyzerpro.R;
import com.thapasujan5.netanalzyerpro.ActionMenu.GetISP;
import com.thapasujan5.netanalzyerpro.ActionMenu.GetWeaather;
import com.thapasujan5.netanalzyerpro.AppConstants;
import com.thapasujan5.netanalzyerpro.MainActivity;
import com.thapasujan5.netanalzyerpro.Tools.DataUtil;
import com.thapasujan5.netanalzyerpro.Tools.DownloadFileFromUrl;
import com.thapasujan5.netanalzyerpro.Tools.FileCache;
import com.thapasujan5.netanalzyerpro.Tools.GetCode;
import com.thapasujan5.netanalzyerpro.Tools.ImageLoader;
import com.thapasujan5.netanalzyerpro.Tools.NetworkUtil;
import com.thapasujan5.netanalzyerpro.Tools.WifiUtil;

import java.io.File;
import java.text.DecimalFormat;

/**
 * Created by Suzan on 11/7/2015.
 */
public class NotificationISP {
    SharedPreferences sp;

    String org, city, country, extIpAdd, intIp;
    String name, gateway, ip, mac, percent, channel;
    String weather;
    Bitmap bitmap = null;

    Context context;
    int source;
    ImageLoader imageLoader;
    FileCache fc;

    NotificationManager notificationManager;
    NotificationCompat.Builder defaultNotify;
    android.app.Notification notification;
    Intent notificationIntent;
    PendingIntent pendingIntent;
    RemoteViews contentView;
    boolean weatherInfoAvailable = false, ispInfoAvailable = false;


    public NotificationISP(Context context) {
        this.context = context;
        this.source = NetworkUtil.getConnectivityStatus(context);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationIntent = new Intent(context, MainActivity.class);
        pendingIntent = PendingIntent.getActivity(context, GetCode.getCode(context), notificationIntent, 0);
        defaultNotify = new NotificationCompat.Builder(context);
        fc = new FileCache(context);
        imageLoader = new ImageLoader(context);
        sp = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        NotifyConnection();
    }

    public void cancelNotification() {
        notificationManager.cancel(GetCode.getCode(context));
    }

    private void NotifyConnection() {
        if (source == AppConstants.TYPE_NOT_CONNECTED) {
            NotifyDefault();
        } else {

            if (NetworkUtil.getConnectivityStatus(context) == AppConstants.TYPE_WIFI || NetworkUtil.getConnectivityStatus(context) == AppConstants.TYPE_MOBILE) {
                getWeather();
                getISPInfo();
                if (source == AppConstants.TYPE_WIFI) {
                    NotifyWifi();
                } else if (source == AppConstants.TYPE_MOBILE) {
                    NotifyData();
                }
            }
        }
    }

    private void NotifyDefault() {
        defaultNotify.setContentIntent(pendingIntent).setTicker("Net Analyzer Alert").setSmallIcon(R.drawable.ic_notifications_black_24dp).setContentTitle("Net Analyzer.").setContentText("You are not connected to the Internet.")
                .setAutoCancel(true);
        defaultNotify.setAutoCancel(true);
        notificationManager.notify(GetCode.getCode(context), defaultNotify.build());
    }

    private void NotifyWifi() {
        getWifiData();
        int icon = R.drawable.iclauncher_notify;
        long when = System.currentTimeMillis();
        notification = new android.app.Notification(icon, context.getResources().getString(R.string.app_name), when);

        contentView = new RemoteViews(context.getPackageName(), R.layout.notification_wifi);
        contentView.setImageViewResource(R.id.ivWifi, R.mipmap.ic_launcher);

        if (bitmap == null) {
            contentView.setImageViewResource(R.id.ivWeather, R.mipmap.ic_launcher);
        } else {
            contentView.setImageViewBitmap(R.id.ivWeather, bitmap);
        }
        contentView.setTextViewText(R.id.tvISP, org + " " + city + " " + country);
        contentView.setTextViewText(R.id.tvExip, extIpAdd);
        contentView.setTextViewText(R.id.tvIntIp, ip);

        contentView.setTextViewText(R.id.tvNetworkName, name);
        contentView.setTextViewText(R.id.tvPercent, percent);
        contentView.setTextViewText(R.id.tvGateway, gateway);
        contentView.setTextViewText(R.id.tvMac, mac);
        contentView.setTextViewText(R.id.tvWeather, weather);
        contentView.setTextViewText(R.id.tvChanel, channel);

        notification.contentView = contentView;
        notification.contentIntent = pendingIntent;
        if (weatherInfoAvailable && ispInfoAvailable) {
            fireNotify();
        }
    }

    private void fireNotify() {
        if (sp.getBoolean(context.getString(R.string.key_notification_sticky), true) == true) {
            if (sp.getBoolean(context.getString(R.string.key_notification_ongoing), true) == true) {
                if (notification != null)
                    notification.flags |= android.app.Notification.FLAG_ONGOING_EVENT; //Do not clear the notification
            }

//            notification.defaults |= NotificationISP.DEFAULT_LIGHTS; // LED
//            notification.defaults |= NotificationISP.DEFAULT_VIBRATE; //Vibration
//            notification.defaults |= NotificationISP.DEFAULT_SOUND; // Sound
            if (notification != null)
                notificationManager.notify(GetCode.getCode(context), notification);
        }
    }

    private void NotifyData() {
        getMobileData();
        int icon = R.drawable.iclauncher_notify;
        long when = System.currentTimeMillis();
        notification = new android.app.Notification(icon, context.getResources().getString(R.string.app_name), when);


        contentView = new RemoteViews(context.getPackageName(), R.layout.notification_data);
        contentView.setImageViewResource(R.id.ivData, R.mipmap.ic_data);

        if (bitmap == null) {
            contentView.setImageViewResource(R.id.ivWeather, R.mipmap.ic_launcher);
        } else {
            contentView.setImageViewBitmap(R.id.ivWeather, bitmap);
        }
        contentView.setTextViewText(R.id.tvISP, org + " " + city + " " + country);
        contentView.setTextViewText(R.id.tvExip, extIpAdd);
        contentView.setTextViewText(R.id.tvIntIp, ip);

        contentView.setTextViewText(R.id.tvNetworkName, name);
        contentView.setTextViewText(R.id.tvPercent, "");
        //contentView.setTextViewText(R.id.tvGateway, gateway);
        //contentView.setTextViewText(R.id.tvMac, mac);
        contentView.setTextViewText(R.id.tvWeather, weather);
        notification.contentView = contentView;
        notification.contentIntent = pendingIntent;
        fireNotify();
    }

    private void getWifiData() {
        name = new WifiUtil(context).getSsid();
        gateway = new WifiUtil(context).getGateway();
        ip = new WifiUtil(context).getIpAddress();
        mac = new WifiUtil(context).getMacAddress();
        percent = new WifiUtil(context).getPercentSignal();
        if (Build.VERSION.SDK_INT >= 21)
            channel = (new WifiUtil(context).getWifiChannel());
        else
            channel = "";
        getISPInfo();

    }

    private void getMobileData() {
        name = new DataUtil(context).getOperatorName();
        //  gateway = new WifiUtil(context).getGateway();
        ip = NetworkUtil.getIPAddress(true);
    }

    private void getISPInfo() {

        if (Integer.toString(NetworkUtil.getConnectivityStatus(context)).contentEquals(sp.getString(context.getString(R.string.source_isp_info), ""))) {
            org = sp.getString(context.getString(R.string.org), "");
            city = sp.getString(context.getString(R.string.city), "");
            country = sp.getString(context.getString(R.string.country), "");
            extIpAdd = sp.getString(context.getString(R.string.extIpAdd), "");
            ispInfoAvailable = true;
        } else {
            new GetISP(context).getInfo();
        }
    }

    private String getWeather() {
        if (sp.getString(context.getString(R.string.weather_source), "isp").contains(context.getString(R.string.isp))) {
            if (sp.getString("lat", "").length() > 0 == false || sp.getString("lon", "").length() > 0 == false) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        new GetWeaather(context).getInfo();
                        try {
                            Thread.sleep(3000);                 //1000 milliseconds is one second.
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                    }
                });

                return "";
            }
        }
        File file = fc.getFile(AppConstants.url_weather_icon + sp.getString(context.getString(R.string.icon_weather), ""));

        if (file.exists() == false) {
            String url = AppConstants.url_weather_icon + sp.getString(context.getString(R.string.icon_weather), "");
            if (url.contains("png") || url.contains("PNG") || url.contains("jpg") || url.contains("jpeg") || url.contains("JPG") || url.contains("JPEG")) {// Log.i("NotificationISP", "To Download: " + url);
                new DownloadFileFromUrl(context, AppConstants.url_weather_icon + sp.getString(context.getString(R.string.icon_weather), "")).execute();
            } else {
                new GetWeaather(context).getInfo();
                return "";
            }
        } else {
            Log.i("NotificationISP", "Using: " + AppConstants.url_weather_icon + sp.getString(context.getString(R.string.icon_weather), ""));
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            weatherInfoAvailable = true;
        }

        String value = sp.getString(context.getString(R.string.temp_weather), "");
        if (value.length() > 0) {
            double rounded;
            try {
                Double d = Double.parseDouble(value);
                DecimalFormat df = new DecimalFormat("#");
                if (sp.getString(context.getString(R.string.key_temperature), "0").contains("1")) {
                    d = (double) convertCelciusToFahrenheit(d.floatValue());
                    weather = sp.getString(context.getString(R.string.name_weather), "") + " " + sp.getString(context.getString(R.string.main_weather), "") + " " +
                            df.format(Math.round(d)) + "\u2109";
                    return weather;
                } else {
                    rounded = Math.round(d);
                    weather = sp.getString(context.getString(R.string.name_weather), "") + " " + sp.getString(context.getString(R.string.main_weather), "") + " " +
                            df.format(rounded) + "\u2103";
                    return weather;
                }
            } catch (Exception e) {
                weather = sp.getString(context.getString(R.string.name_weather), "") + " " + sp.getString(context.getString(R.string.main_weather), "") + " " + sp.getString(context.getString(R.string.temp_weather), "") + "\u2103";
                return weather;
            }
        } else {
            return "";
        }
    }

    // Converts to celcius
    private float convertFahrenheitToCelcius(float fahrenheit) {
        return ((fahrenheit - 32) * 5 / 9);
    }

    // Converts to fahrenheit
    private float convertCelciusToFahrenheit(float celsius) {
        return ((celsius * 9) / 5) + 32;
    }
}