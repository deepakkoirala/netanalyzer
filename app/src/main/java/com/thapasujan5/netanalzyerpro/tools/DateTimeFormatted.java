package com.thapasujan5.netanalzyerpro.Tools;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Sujan Thapa on 2/01/2016.
 */
public class DateTimeFormatted {
    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified
        // format.
        String defaultFormat = "EEE MMM dd yyyy HH:mm z";
        SimpleDateFormat formatter;
        if (dateFormat != null) {
            formatter = new SimpleDateFormat(dateFormat, Locale.US);
        } else {
            formatter = new SimpleDateFormat(defaultFormat, Locale.US);
        }


        // Create a calendar object that will convert the date and time value in
        // milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}
