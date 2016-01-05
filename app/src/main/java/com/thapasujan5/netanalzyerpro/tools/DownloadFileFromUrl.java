package com.thapasujan5.netanalzyerpro.Tools;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Sujan Thapa on 5/01/2016.
 */
public class DownloadFileFromUrl implements Runnable {
    Context context;
    FileCache fileCache;
    String url;

    MemoryCache memoryCache;
    ImageLoader imageLoader;

    public DownloadFileFromUrl(Context context, String url) {
        this.context = context;
        fileCache = new FileCache(context);
        imageLoader = new ImageLoader(context);
        this.url = url;
        memoryCache = new MemoryCache();
        run();
    }

    @Override
    public void run() {

        File f = fileCache.getFile(url);

        try {
            Log.d("Dowloadfile", "Downloading:" + url);

            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imageUrl
                    .openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is = conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            UtilsForImageLoader.CopyStream(is, os);
            os.close();
            conn.disconnect();
        } catch (Throwable ex) {
            Log.d("DownloadFile", "Exception occured for :" + f.getName());
            ex.printStackTrace();
            if (ex instanceof OutOfMemoryError)
                memoryCache.clear();
        }
    }
}
