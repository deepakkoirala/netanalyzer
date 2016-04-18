package com.thapasujan5.netanalzyerpro.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.thapasujan5.netanalyzerpro.R;
import com.thapasujan5.netanalzyerpro.AppConstants;
import com.thapasujan5.netanalzyerpro.Tools.Clipboard;
import com.thapasujan5.netanalzyerpro.Tools.DataUtil;
import com.thapasujan5.netanalzyerpro.Tools.GetNetworkType;
import com.thapasujan5.netanalzyerpro.Tools.NetworkUtil;

import org.json.JSONObject;

import java.lang.reflect.Method;

/**
 * Created by Sujan Thapa on 8/02/2016.
 */
public class Data extends Fragment implements View.OnLongClickListener, ActivityCompat.OnRequestPermissionsResultCallback {
    TextView tvNtName, tvNtType, tvCellularGateway, tvIp, tvPercent, tvGateway, tvDns1, tvDns2, tvNumber, tvImei, tvImeisv, tvSignalStrength, tvStatus;
    TextView tvSimState, tvSimSerialNo, tvSimType;
    BroadcastReceiver receiver;
    IntentFilter filter;
    View rootView;
    WifiManager wifi;
    //    protected SwipeRefreshLayout swipeRefreshLayout;
    ConnectivityManager cm;
    TelephonyManager telephonyManager;

    DhcpInfo dhcp;
    SharedPreferences sharedPreferences;
    boolean once = false;

    static JSONObject jsonObject;

    public Data() {
        jsonObject = new JSONObject();
    }

    public static JSONObject getConnectionDetails() {
        return jsonObject;
    }

    @Override
    public void onDestroy() {
        try {
            getContext().unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

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
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        wifi = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        cm = (ConnectivityManager) getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        dhcp = new DhcpInfo();
        telephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);


        tvSimState = (TextView) rootView.findViewById(R.id.tvSimState);
        tvSimState.setOnLongClickListener(this);

        tvSimSerialNo = (TextView) rootView.findViewById(R.id.tvSimSerialNo);
        tvSimSerialNo.setOnLongClickListener(this);

        tvSimType = (TextView) rootView.findViewById(R.id.tvSimType);
        tvSimType.setOnLongClickListener(this);

        tvNtName = (TextView) rootView.findViewById(R.id.tvEssidWig);
        tvNtName.setOnLongClickListener(this);

        tvCellularGateway = (TextView) rootView.findViewById(R.id.tvCellularGateway);
        tvCellularGateway.setOnLongClickListener(this);

        tvNtType = (TextView) rootView.findViewById(R.id.tvBSSIdWig);
        tvNtType.setOnLongClickListener(this);

        tvIp = (TextView) rootView.findViewById(R.id.tvIPValue);
        tvIp.setOnLongClickListener(this);

        tvPercent = (TextView) rootView.findViewById(R.id.tvPercentage);

        tvGateway = (TextView) rootView.findViewById(R.id.tvGatewayValue);
        tvGateway.setOnLongClickListener(this);

        tvDns1 = (TextView) rootView.findViewById(R.id.tvDns1Value);
        tvDns1.setOnLongClickListener(this);

        tvDns2 = (TextView) rootView.findViewById(R.id.tvDns2Value);

        tvNumber = (TextView) rootView.findViewById(R.id.tvNumber);
        tvNumber.setOnLongClickListener(this);

        tvImei = (TextView) rootView.findViewById(R.id.tvImei);
        tvImei.setOnLongClickListener(this);

        tvImeisv = (TextView) rootView.findViewById(R.id.tvImeisv);
        tvImeisv.setOnLongClickListener(this);

        tvSignalStrength = (TextView) rootView.findViewById(R.id.tvSignalStrengthValue);
        tvStatus = (TextView) rootView.findViewById(R.id.tvStatusValue);
//        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
//        swipeRefreshLayout.setOnRefreshListener(onSwipeListener);
//        swipeRefreshLayout.setColorSchemeColors(Color.RED, R.color.swipeRefresh2, R.color.swipeRefresh3, R.color.swipeRefresh4);
        setupValues();
        registerNetworkLister();
    }

    private void setSIMDetails() {
        TelephonyManager telMgr = telephonyManager;
        int simState = telMgr.getSimState();
        tvNtName.setText("SIM Service Unavailable");
        String statusString = null;
        switch (simState) {
            case TelephonyManager.SIM_STATE_ABSENT:
                // do something
                tvSimState.setText("No SIM");
                tvNtName.setText("No SIM Card");
                statusString = "SIM Absent.";
                break;
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                // do something
                tvSimState.setText("Netowrk Locked");

                statusString = "SIM State Network Locked.";
                break;
            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                // do something
                tvSimState.setText("PIN Required");
                statusString = "SIM State PIN Required.";
                break;
            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                // do something
                tvSimState.setText("PUK Required");
                statusString = "SIM State PUK Required.";
                break;
            case TelephonyManager.SIM_STATE_READY:
                tvSimState.setText("Ready");
                tvSimState.setTextColor(getContext().getResources().getColor(R.color.green_dark));
                getSimData();
                break;
            case TelephonyManager.SIM_STATE_UNKNOWN:
                // do something
                tvSimState.setText("Unknown");
                statusString = "SIM State Unknown.";
                break;
        }
        if (statusString != null && once == false) {
            once = true;
            Toast.makeText(getContext().getApplicationContext(), statusString, Toast.LENGTH_SHORT).show();
        }

//        if (swipeRefreshLayout.isRefreshing()) {
//            swipeRefreshLayout.setRefreshing(false);
//        }
    }

    private void getSimData() {
        String SimType = null;
        if (telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
            SimType = "GSM";
        } else if (telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
            SimType = "CDMA";
        } else if (telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_SIP) {
            SimType = "SIP";
        } else if (telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
            SimType = "None";
        }
        tvNtName.setText(new DataUtil(getContext()).getOperatorName());
        tvSimType.setText(SimType);
        tvNumber.setText(new DataUtil(getContext()).getLine1Number());
        tvImei.setText(telephonyManager.getDeviceId() + "");
        tvImeisv.setText(telephonyManager.getDeviceSoftwareVersion());
        tvSimSerialNo.setText(telephonyManager.getSimSerialNumber().toString());
        if (getDataConnectionStatus()) {
            tvStatus.setText("DATA ON");
            tvStatus.setTextColor(getContext().getResources().getColor(R.color.app_theme_foreground));

        } else {
            tvStatus.setText("DATA OFF");
            tvStatus.setTextColor(Color.RED);
        }
    }

    private boolean getDataConnectionStatus() {
        // ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean mobileDataEnabled = false; // Assume disabled
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            mobileDataEnabled = (Boolean) method.invoke(cm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mobileDataEnabled;
    }

    public void getPermission(String permissions, int code) {
        if (Build.VERSION.SDK_INT >= 23) {
            Log.d("datanew", "requesting");
            ((Activity) getContext()).requestPermissions(new String[]{permissions},
                    code);
        }
    }

    public void setupValues() throws Exception {
        setSIMDetails();
        if (new DataUtil(getContext()).isAirplaneModeOn() == false) {

            //setup values now
            tvNtName.setText(telephonyManager.getNetworkOperatorName());
            tvNtType.setText(GetNetworkType.networkType(getContext()));


            tvNtType.setVisibility(View.VISIBLE);
            tvCellularGateway.setVisibility(View.VISIBLE);
            tvPercent.setVisibility(View.VISIBLE);

            if (getDataConnectionStatus()) {
                if (NetworkUtil.getConnectivityStatus(getContext()) == AppConstants.TYPE_MOBILE) {

                    tvIp.setText(NetworkUtil.getIPAddress(true));
                    tvCellularGateway.setVisibility(View.VISIBLE);
                    tvCellularGateway.setText(NetworkUtil.getIPAddress(true));
                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.putString("cellip", tvIp.getText().toString().trim());
                    edit.apply();
                    edit.commit();
                } else {
                    tvIp.setText("n/a");
                    tvCellularGateway.setVisibility(View.GONE);
                }
            } else {
                tvIp.setText("n/a");
                tvCellularGateway.setVisibility(View.GONE);
            }
        } else {
            updateAirplaneModeValues();
            tvStatus.setText("n/a");
            tvSignalStrength.setText("n/a");
        }
    }

    private void updateAirplaneModeValues() {
        Toast.makeText(getContext().getApplicationContext(), "Airplane mode active.", Toast.LENGTH_SHORT).show();
        tvNtName.setText("Airplane Mode ON");
        tvNtName.setVisibility(View.VISIBLE);
        tvNtType.setVisibility(View.INVISIBLE);
        tvPercent.setVisibility(View.INVISIBLE);
        tvCellularGateway.setVisibility(View.INVISIBLE);
        tvIp.setText("n/a");
        tvGateway.setText("n/a");
        tvDns1.setText("n/a");

    }

    private void registerNetworkLister() throws Exception {
        if (receiver == null) {
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    try {
                        setupValues();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            filter = new IntentFilter();
            filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            filter.addAction(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);
            filter.addAction(ConnectivityManager.EXTRA_NETWORK);
            filter.addAction("android.intent.action.AIRPLANE_MODE");
            filter.addAction(TelephonyManager.EXTRA_STATE);
            filter.addAction(TelephonyManager.EXTRA_STATE_IDLE);

            getContext().registerReceiver(receiver, filter);
            AndroidPhoneStateListener phoneStateListener = new AndroidPhoneStateListener();
            telephonyManager.listen(phoneStateListener,
                    PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        }
    }


    private class AndroidPhoneStateListener extends PhoneStateListener {


        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            if (null != signalStrength && signalStrength.getGsmSignalStrength() != AppConstants.UNKNOW_CODE) {


                int SignalStrength_ASU = signalStrength.getGsmSignalStrength(); // -> asu
                int SignalStrength_dBm = (2 * SignalStrength_ASU) - 113; // -> dBm

                int signalStrengthPercent = new DataUtil(getContext()).calculateSignalStrengthInPercent(signalStrength.getGsmSignalStrength());
                tvPercent.setText(signalStrengthPercent + "%");
                tvSignalStrength.setText(SignalStrength_dBm + "dB " + SignalStrength_ASU + "asu");
                try {
                    //setupValues();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public boolean onLongClick(View v) {
        String value = null;
        int id = v.getId();
        switch (id) {
            case R.id.tvSimSerialNo:
                value = tvSimSerialNo.getText().toString().trim();
                break;
            case R.id.tvImei:
                value = tvImei.getText().toString().trim();
                break;
            case R.id.tvEssidWig:
                value = tvNtName.getText().toString().trim();
                break;
            case R.id.tvNumber:
                value = tvNumber.getText().toString().trim();
                break;
            default:
                return false;
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
            return true;
        } else {
            Toast.makeText(getContext().getApplicationContext(), "Something went wrong! Try again.", Toast.LENGTH_SHORT).show();
            return false;
        }

    }
}
