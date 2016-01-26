package com.thapasujan5.netanalzyerpro.Tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;
import android.view.View;

public class TakeScreenShot {

	public static Bitmap getBitmapFromScreen(View view, String path) {
		View v = view.getRootView();
		Bitmap bmp = Bitmap.createBitmap(v.getDrawingCache());
		try {
			v.setDrawingCacheEnabled(true);
			v.setDrawingCacheEnabled(false);
			FileOutputStream fos = new FileOutputStream(new File(path));
			bmp.compress(CompressFormat.PNG, 100, fos);
			Log.i("TakeScreenShotClass", "Compressed");
			fos.flush();
			fos.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bmp;
	}
}
