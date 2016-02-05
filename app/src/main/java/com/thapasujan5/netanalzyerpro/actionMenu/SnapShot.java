package com.thapasujan5.netanalzyerpro.ActionMenu;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.thapasujan5.netanalzyerpro.Tools.GetPath;
import com.thapasujan5.netanalzyerpro.Tools.TakeScreenShot;

import org.json.JSONObject;

import java.io.File;

/**
 * Created by Suzan on 12/23/2015.
 */
public class SnapShot {
    public SnapShot(final Context context, final View view)

    {

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Confirm");
        alert.setMessage("Ready to take screenshot to share further.");
        alert.setPositiveButton("Take Screenshot", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Please wait !", Toast.LENGTH_SHORT
                ).show();
                final JSONObject location = new GetPath(context).getPathAndName();
                if (location != null) {
                    try {

                        final Bitmap mBitmap = TakeScreenShot.getBitmapFromScreen(
                                view,
                                location.getString("path") + location.getString("name") + ".png");

                        AlertDialog.Builder alert = new AlertDialog.Builder(context);
                        alert.setTitle("Preview");
                        ImageView view = new ImageView(context);
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                        view.setImageBitmap(mBitmap);
                        alert.setView(view);
                        alert.setPositiveButton("Share", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    File file = new File(location.getString("path") + location.getString("name") + ".png");
                                    Intent intent = new Intent();
                                    intent.setType("image/*");
                                    intent.setAction(Intent.ACTION_SEND);
                                    intent.putExtra(Intent.EXTRA_STREAM,
                                            Uri.parse("file://" + file.getAbsolutePath()));

                                    context.startActivity(Intent.createChooser(intent,
                                            "Share Image with:"));
                                    if (mBitmap != null && !mBitmap.isRecycled()) {
                                        mBitmap.recycle();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();

                                }

                            }
                        });
                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    File file = new File(location.getString("path") + location.getString("name") + ".png");
                                    file.delete();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                        alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                try {
                                    File file = new File(location.getString("path") + location.getString("name") + ".png");
                                    file.delete();
                                } catch (Exception e) {

                                }
                            }
                        });
                        alert.show();


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d("snapshot", "location null");
                }
            }
        });
        alert.setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });
        alert.show();

    }
}
