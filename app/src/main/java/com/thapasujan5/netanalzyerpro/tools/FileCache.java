package com.thapasujan5.netanalzyerpro.Tools;

import android.content.Context;

import com.thapasujan5.netanalyzerpro.R;

import java.io.File;

public class FileCache {

    private File cacheDir;

    public FileCache(Context context) {
        // Find the dir to save cached images
        if (android.os.Environment.getDataDirectory().equals(
                android.os.Environment.MEDIA_MOUNTED))
            cacheDir = new File(
                    android.os.Environment.getDataDirectory(),
                    context.getResources().getString(R.string.app_name));
        else
            cacheDir = context.getCacheDir();
        if (!cacheDir.exists())
            cacheDir.mkdirs();
    }

    public File getDirecotry() {
        return cacheDir;
    }

    public File getFile(String url) {

        String filename = String.valueOf(url.hashCode());
        // Another possible solution (thanks to grantland)
        // String filename = URLEncoder.encode(url);
        // String filename = url.substring(url.lastIndexOf("/") + 1,
        // url.lastIndexOf("."))
        // + "_" + String.valueOf(url.hashCode())
        // +GetExtension.GetFileExtension(url);
        File f = new File(cacheDir, filename);

        return f;

    }

    public boolean deleteFile(String filename) {

        boolean result = false;
        // Log.d("FileCache", "Inside deletefile");
        File f = getFile(filename);
        if (f.delete()) {
            result = true;
        }
        return result;
    }

    public void clear() {
        File[] files = cacheDir.listFiles();
        if (files == null)
            return;
        for (File f : files)
            f.delete();
    }

    public File[] listallFiles() {
        return cacheDir.listFiles();
    }
}