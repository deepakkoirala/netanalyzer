package com.thapasujan5.netanalzyerpro.Tools;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

/**
 * Created by Suzan on 12/17/2015.
 */
public class DayHourMinSec {

    public static JSONObject getDHMS(int seconds) {
        JSONObject jsonObject = new JSONObject();
        try {
            int day = (int) TimeUnit.SECONDS.toDays(seconds);
            long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24);
            long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60);
            long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) * 60);

            if (day != 0) {
                jsonObject.put("days", Integer.toString(day) + " Day ");
            } else if (day == 1) {
                jsonObject.put("days", Integer.toString(day) + " Days ");
            } else {
                jsonObject.put("days", "");
            }
            if (hours != 0) {
                jsonObject.put("hours", Long.toString(hours) + " Hours ");
            } else {
                jsonObject.put("hours", "");
            }
            if (minute != 0) {
                jsonObject.put("minutes", Long.toString(minute) + " Minutes ");
            } else {
                jsonObject.put("minutes", "");
            }
            if (second != 0) {
                jsonObject.put("seconds", Long.toString(second) + " Seconds ");
            } else {
                jsonObject.put("seconds", "");
            }

            jsonObject.put("result", "success");
            return jsonObject;
        } catch (Exception e) {
            return jsonObject;
        }
    }
}
