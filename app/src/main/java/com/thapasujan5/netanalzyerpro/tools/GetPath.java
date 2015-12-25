package com.thapasujan5.netanalzyerpro.Tools;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.thapasujan5.netanalyzerpro.R;
import com.thapasujan5.netanalzyerpro.MainActivity;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Suzan on 12/21/2015.
 */
public class GetPath {
    static String path;
    Context context;
    static JSONObject json;

    static File folder;

    public GetPath(Context context) {
        this.context = context;
        json = new JSONObject();
        folder = new File(Environment
                .getExternalStorageDirectory().toString(), context.getResources().getString(R.string.app_name));
        compute();
    }

    public static String getPathOnly() {
        return path;
    }

    public static File getFolder() {
        return folder;
    }

    public static JSONObject getPathAndName() {
        return json;
    }

    public void compute() {

        if (!folder.exists()) {
            folder.mkdir();
            Log.i(MainActivity.class.getSimpleName(),
                    "Folder Created.");
        }
        path = folder.getAbsolutePath() + "/";
        SimpleDateFormat df = new SimpleDateFormat("MM_dd_yyyy", Locale.US);
        String now = df.format(new Date()) + "_"
                + System.currentTimeMillis();
        String name = "IMG_" + now;
        try {
            json.put("path", path);
            json.put("name", name);

        } catch (Exception e) {

        }
    }
}
