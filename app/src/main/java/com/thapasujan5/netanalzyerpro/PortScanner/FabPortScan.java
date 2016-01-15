package com.thapasujan5.netanalzyerpro.PortScanner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.thapasujan5.netanalzyerpro.PingService.PingRequest;

/**
 * Created by Suzan on 12/10/2015.
 */
public class FabPortScan {
    public FabPortScan(final Context context) {
        AlertDialog.Builder d = new AlertDialog.Builder(context);
        d.setTitle("Quick Port Scan Service");

        final EditText editText = new EditText(context);
        editText.setHint("Enter DNS or IP ");
        editText.setSingleLine();
        editText.setGravity(Gravity.CENTER);
        d.setView(editText);
        d.setPositiveButton("Scan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (editText.getText().toString().trim().length() > 0) {
                    new PortScanRequest(editText.getText().toString().trim(), context).execute();
                    //new FabPortScan(editText.getText().toString().trim(), context).execute();
                    dialog.dismiss();
                } else {
                    Toast.makeText(context, "Address required !", Toast.LENGTH_SHORT).show();

                    editText.requestFocus();
                }
            }
        });
        d.setNegativeButton("Close", null);


        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_DONE)) {
                    InputMethodManager in = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(editText.getApplicationWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    if (editText.getText().toString().trim().length() > 0) {
                        new PingRequest(editText.getText().toString().trim(), context).execute();
                    } else {
                        Toast.makeText(context, "Address required !", Toast.LENGTH_SHORT).show();
                        editText.requestFocus();
                    }
                    return true;
                }
                return false;
            }
        });
        final Dialog dialog = d.create();
        dialog.show();
    }
}

