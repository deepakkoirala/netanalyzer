package com.thapasujan5.netanalzyerpro.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.thapasujan5.netanalzyerpro.AppConstants;
import com.thapasujan5.netanalzyerpro.Tools.Clipboard;
import com.thapasujan5.netanalzyerpro.Tools.ConnectionDetector;
import com.thapasujan5.netanalzyerpro.Tools.NetworkUtil;

/**
 * Created by Suzan on 12/14/2015.
 */
public class Data extends Fragment implements View.OnLongClickListener {
    TextView tvNtName, tvNtType, tvCellularGateway, tvIp, tvPercent, tvGateway, tvDns1, tvDns2, tvPhone, tvImei, tvImeisv, tvSignalStrength, tvStatus;
    BroadcastReceiver receiver;
    IntentFilter filter;
    View rootView;
    WifiManager wifi;
    protected SwipeRefreshLayout swipeRefreshLayout;
    ConnectivityManager cm;
    NetworkInfo activeNetwork;
    //  TelephonyManager telephonyManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_data, container, false);
        try {
            initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rootView;
    }

    private void initialize() throws Exception {
        wifi = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);

        tvNtName = (TextView) rootView.findViewById(R.id.tvNetworkName);
        tvNtName.setOnLongClickListener(this);

        tvCellularGateway = (TextView) rootView.findViewById(R.id.tvCellularGateway);
        tvCellularGateway.setOnLongClickListener(this);

        tvNtType = (TextView) rootView.findViewById(R.id.tvNetworkType);
        tvNtType.setOnLongClickListener(this);

        tvIp = (TextView) rootView.findViewById(R.id.tvIPValue);
        tvIp.setOnLongClickListener(this);

        tvPercent = (TextView) rootView.findViewById(R.id.tvPercentage);

        tvGateway = (TextView) rootView.findViewById(R.id.tvGatewayValue);
        tvGateway.setOnLongClickListener(this);

        tvDns1 = (TextView) rootView.findViewById(R.id.tvDns1Value);
        tvDns1.setOnLongClickListener(this);

        tvDns2 = (TextView) rootView.findViewById(R.id.tvDns2Value);

        tvPhone = (TextView) rootView.findViewById(R.id.tvMobileNo);
        tvPhone.setOnLongClickListener(this);

        tvImei = (TextView) rootView.findViewById(R.id.tvImei);
        tvImei.setOnLongClickListener(this);

        tvImeisv = (TextView) rootView.findViewById(R.id.tvImeisv);
        tvImeisv.setOnLongClickListener(this);

        tvSignalStrength = (TextView) rootView.findViewById(R.id.tvSignalStrengthValue);
        tvStatus = (TextView) rootView.findViewById(R.id.tvStatusValue);

        registerNetworkLister();
    }

    private void registerNetworkLister() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.w("Receiver:Cellular", intent.getAction().toString());
                try {
                    setupValues();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        filter = new IntentFilter();

        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

        getContext().registerReceiver(receiver, filter);
        setupValues();
    }

    private void setupValues() {
        //base classes
        cm = (ConnectivityManager) getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = cm.getActiveNetworkInfo();
        //       telephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);

        //setup values now

        //     tvPhone.setText(telephonyManager.getLine1Number() + "");


    }

    @Override
    public boolean onLongClick(View v) {
        final WifiManager wifi = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled() == false && new ConnectionDetector(getContext()).isConnectingToInternet() && NetworkUtil.getConnectivityStatus(getContext()) == AppConstants.TYPE_MOBILE) {
            String value = null;
            switch (v.getId()) {
                case R.id.tvNetworkName:
                    value = tvNtName.getText().toString().trim();
                    break;
                case R.id.tvCellularGateway:
                    value = tvCellularGateway.getText().toString().trim();
                    break;
                case R.id.tvIPValue:
                    value = tvIp.getText().toString().trim();
                    break;
                case R.id.tvGatewayValue:
                    value = tvGateway.getText().toString().trim();
                    break;
                case R.id.tvDns1Value:
                    value = tvDns1.getText().toString().trim();
                    break;
                case R.id.tvDns2Value:
                    value = tvDns2.getText().toString().trim();
                    break;
                case R.id.tvNetworkType:
                    value = tvNtType.getText().toString().trim();
                    break;

                case R.id.tvStatusValue:
                    value = tvStatus.getText().toString().trim();
                    break;
                case R.id.tvSignalStrengthValue:
                    value = tvSignalStrength.getText().toString().trim();
                    break;

                case R.id.tvMobileNo:
                    value = tvPhone.getText().toString().trim();
                    break;
                case R.id.tvImei:
                    value = tvImei.getText().toString().trim();
                    break;
                case R.id.tvImeisv:
                    value = tvImeisv.getText().toString().trim();
                    break;


            }
            if (value != null) {

                boolean status = Clipboard.copyToClipboard(getContext(),
                        value);
                value += " to Clipboard.";
                if (status) {
                    Toast.makeText(getContext().getApplicationContext(), "Copied " + value, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext().getApplicationContext(), "Try Again ! ", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext().getApplicationContext(), "Something went wrong! Try again.", Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }
}
