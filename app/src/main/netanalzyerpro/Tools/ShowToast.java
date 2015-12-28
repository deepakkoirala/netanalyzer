package com.thapasujan5.netanalzyerpro.Tools;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class ShowToast {
    private String tag = ShowToast.class.getSimpleName();

    @SuppressWarnings("deprecation")
    public ShowToast(Context context, String message, int textColor,
                     int backgroundResource, int backgroundcolor, int duration,
                     int position) {
        Toast toast = Toast.makeText(context, message, duration);
        toast.getView().setPadding(30, 30, 30, 30);

        if (position != 0) {
            toast.setGravity(position, 0, 0);
            Log.d(tag, "Gravity=" + position);
        }
        if (textColor != 0) {
            TextView v = (TextView) toast.getView().findViewById(
                    android.R.id.message);
            v.setTextColor(textColor);
            Log.d(tag, "TextColor=" + textColor);
        }
        if (backgroundResource != 0) {
            toast.getView().setBackgroundDrawable(
                    context.getResources().getDrawable(backgroundResource));
            Log.d(tag, "BackgroundDrawable=" + backgroundcolor);
        }
        if (backgroundcolor != 0) {
            toast.getView().setBackgroundColor(backgroundcolor);
            Log.d(tag, "BackgroundColor=" + backgroundcolor);
        }
        toast.show();
    }
}
