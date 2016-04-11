package com.thapasujan5.netanalzyerpro.PortScanner;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.thapasujan5.netanalzyerpro.ActionMenu.UpgradeToPro;

/**
 * Created by Suzan on 11/04/2016.
 */
public class RunPortScanner {
    public RunPortScanner(final Context context) {
        if (context.getPackageName().contentEquals("com.thapasujan5.serversearch") == false) {
            new FabPortScan(context);
        } else {
            new AlertDialog.Builder(context).setTitle("Net Analyzer Lite").
                    setMessage("This feature requires Full Version of Net Analyzer.").
                    setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new UpgradeToPro(context);
                        }
                    }).setNegativeButton("Cancel", null).show();

        }
    }
}
