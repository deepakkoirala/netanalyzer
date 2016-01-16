package com.thapasujan5.netanalzyerpro.PortScanner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.thapasujan5.netanalyzerpro.R;
import com.thapasujan5.netanalzyerpro.DataStore.ScanResult;
import com.thapasujan5.netanalzyerpro.Tools.Clipboard;
import com.thapasujan5.netanalzyerpro.Tools.NetworkUtil;

import java.util.ArrayList;

/**
 * Created by Suzan on 12/10/2015.
 */
public class PortScanRequest extends AsyncTask<Void, Void, ArrayList<ScanResult>> {
    String ip, pingResult = null;
    Dialog dialog;
    TextView tvTitle;
    ProgressBar pb;
    Button btnHide, btnCancel;
    Context context;

    public PortScanRequest(String ip, Context mContext) {
        this.ip = ip;
        this.context = mContext;
    }

    @Override
    protected void onPreExecute() {

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_pinging);

        pb = (ProgressBar) dialog.findViewById(R.id.pbPinging);
        pb.setIndeterminate(true);

        tvTitle = (TextView) dialog.findViewById(R.id.titleText);
        tvTitle.setText(Html.fromHtml("<b>Scanning " + ip + "</b>"));
        btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cancel(true);
                dialog.dismiss();

            }
        });
        btnHide = (Button) dialog.findViewById(R.id.btnMinimize);
        btnHide.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                cancel(true);
            }
        });
        dialog.show();
        super.onPreExecute();
    }

    @Override
    protected ArrayList<ScanResult> doInBackground(Void... params) {
        return PortScanner.scanHost(NetworkUtil.getIPAddress(true));
    }

    @Override
    protected void onPostExecute(ArrayList<ScanResult> scanResults) {
        dialog.dismiss();
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        if (Build.VERSION.SDK_INT >= 21)
            alert.setIcon(context.getResources().getDrawable(R.mipmap.ic_computer, context.getTheme()));
        else
            alert.setIcon(context.getResources().getDrawable(R.mipmap.ic_computer));

        final ArrayList<String> openPorts = new ArrayList<String>();
        for (ScanResult scanResult : scanResults) {
            openPorts.add(Integer.toString(scanResult.getPort()));
        }
        if (scanResults.size() > 0) {
            alert.setTitle("Open Ports : " + scanResults.size());
            String[] toArray = new String[openPorts.size()];
            toArray = openPorts.toArray(toArray);
            alert.setItems(toArray, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (Clipboard.copyToClipboard(context, openPorts.get(which)))
                        Toast.makeText(context.getApplicationContext(), "Text copied to clipboard.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            alert.setTitle("Port Scan Summary");
            alert.setMessage("No open ports found in targeted host:" + ip);
        }

        Dialog d = alert.create();
        try {
            d.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onPostExecute(scanResults);
    }
}