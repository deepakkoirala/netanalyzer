package com.thapasujan5.netanalzyerpro.ActionMenu;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.thapasujan5.netanalzyerpro.DnsLookupActivity;
import com.thapasujan5.netanalzyerpro.Tools.TakeScreenShot;

import com.thapasujan5.netanalyzerpro.R;

public class ScreenShot {

    public ScreenShot(Context context) {

        String extStorageDirectory = Environment.getExternalStorageDirectory()
                .toString();
        File folder = new File(extStorageDirectory,
                context.getString(R.string.app_name));

        if (!folder.exists()) {
            folder.mkdir();
            Log.i(DnsLookupActivity.class.getSimpleName(), "Folder Created");
        }
        String path = folder.getAbsolutePath() + "/";

        SimpleDateFormat df = new SimpleDateFormat("MM_dd_yyyy", Locale.US);

        String now = df.format(new Date()) + "_" + System.currentTimeMillis();

        String name = "IMG_" + now;
        ((Activity) context).getWindow().getDecorView()
                .setDrawingCacheEnabled(true);
        try {
            Bitmap bitmap = TakeScreenShot.getBitmapFromScreen(
                    ((Activity) context).getWindow().getDecorView()
                            .getRootView(), path + name + ".png");
            bitmap.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }

        File file = new File(path + name + ".png");

        try {
            file.createNewFile();
            // String filepath = file.getAbsolutePath();

            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_STREAM,
                    Uri.parse("file://" + file.getAbsolutePath()));

            context.startActivity(Intent.createChooser(intent,
                    "Share Screenshot with:"));

            ((Activity) context).getWindow().getDecorView()
                    .setDrawingCacheEnabled(true);
            Log.i(DnsLookupActivity.class.getSimpleName(), "Sharing");
        } catch (Exception e) {
            Log.i(DnsLookupActivity.class.getSimpleName(), e.toString());
            file.delete();
        }

    }
}
