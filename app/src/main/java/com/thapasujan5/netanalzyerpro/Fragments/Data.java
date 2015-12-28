package com.thapasujan5.netanalzyerpro.Fragments;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.thapasujan5.netanalyzerpro.R;
import com.thapasujan5.netanalzyerpro.AppConstants;
import com.thapasujan5.netanalzyerpro.MainActivity;
import com.thapasujan5.netanalzyerpro.Tools.Clipboard;
import com.thapasujan5.netanalzyerpro.Tools.ConnectionDetector;
import com.thapasujan5.netanalzyerpro.Tools.GetDeviceIP;
import com.thapasujan5.netanalzyerpro.Tools.NetworkUtil;

import java.lang.reflect.Method;

/**
 * Created by Suzan on 12/14/2015.
 */
public class Data extends Fragment implements View.OnLongClickListener {
    TextView tvNtName, tvNtType, tvCellularGateway, tvIp, tvPercent, tvGateway, tvDns1, tvDns2, tvPhone, tvImei, tvImeisv, tvSignalStrength, tvStatus;
    TextView tvSimState, tvSimSerialNo, tvSimType;
    BroadcastReceiver receiver;
    IntentFilter filter;
    View rootView;
    WifiManager wifi;
    protected SwipeRefreshLayout swipeRefreshLayout;
    ConnectivityManager cm;
    TelephonyManager telephonyManager;
    public int MAX_SIGNAL_DBM_VALUE = 31;
    public static final int UNKNOW_CODE = 99;
    DhcpInfo dhcp;
    SharedPreferences sharedPreferences;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_data, container, false);
        try {
            if (isAirplaneModeOn(getContext())) {
                alertAirplaneMode(getContext());
            }
            initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rootView;
    }


    private void initialize() throws Exception {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());

        tvSimState = (TextView) rootView.findViewById(R.id.tvSimState);
        tvSimState.setOnLongClickListener(this);

        tvSimSerialNo = (TextView) rootView.findViewById(R.id.tvSimSerialNo);
        tvSimSerialNo.setOnLongClickListener(this);

        tvSimType = (TextView) rootView.findViewById(R.id.tvSimType);
        tvSimType.setOnLongClickListener(this);

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
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(onSwipeListener);
        swipeRefreshLayout.setColorSchemeColors(R.color.swipeRefresh1, R.color.swipeRefresh2, R.color.swipeRefresh3, R.color.swipeRefresh4);

        wifi = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        cm = (ConnectivityManager) getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        dhcp = new DhcpInfo();
        telephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
        registerNetworkLister();
    }

    @SuppressWarnings("deprecation")
    private static boolean isAirplaneModeOn(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
        /* API 17 and above */
            return Settings.Global.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
        } else {
        /* below */
            return Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) != 0;
        }
    }

    public class AndroidPhoneStateListener extends PhoneStateListener {


        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            if (null != signalStrength && signalStrength.getGsmSignalStrength() != UNKNOW_CODE) {


                int SignalStrength_ASU = signalStrength.getGsmSignalStrength(); // -> asu
                int SignalStrength_dBm = (2 * SignalStrength_ASU) - 113; // -> dBm

                int signalStrengthPercent = calculateSignalStrengthInPercent(signalStrength.getGsmSignalStrength());
                tvPercent.setText(signalStrengthPercent + "%");

                tvSignalStrength.setText(SignalStrength_dBm + "dB " + SignalStrength_ASU + "asu");
                try {
                    setupValues();
                } catch (Exception e) {
                }

            }
        }
    }

    private int calculateSignalStrengthInPercent(int signalStrength) {
        return (int) ((float) signalStrength / MAX_SIGNAL_DBM_VALUE * 100);
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
            setupValues();
        }
    }


    private void setupValues() throws Exception {
        if (isAirplaneModeOn(getContext()) == false) {
            //setup values now
            tvNtName.setText(telephonyManager.getNetworkOperatorName());
            if (Build.VERSION.SDK_INT < 23) {
                tvNtName.setTextAppearance(getContext(), android.R.style.TextAppearance_Medium);
            } else {
                tvNtName.setTextAppearance(android.R.style.TextAppearance_Medium);
            }
            tvNtType.setText(networkType());

            tvNtName.setVisibility(View.VISIBLE);
            tvNtType.setVisibility(View.VISIBLE);
            tvCellularGateway.setVisibility(View.VISIBLE);
            tvPercent.setVisibility(View.VISIBLE);

            if (getDataConnectionStatus()) {
                if (NetworkUtil.getConnectivityStatus(getContext()) == AppConstants.TYPE_MOBILE) {
                    tvIp.setText(GetDeviceIP.getDeviceIP());
                    tvCellularGateway.setText(GetDeviceIP.getDeviceIP());
                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.putString("cellip", tvIp.getText().toString().trim());
                    edit.apply();
                    edit.commit();
                }
            } else {
                tvIp.setText("n/a");
            }
        } else {
            tvNtName.setText("Slide down to TURN OFF Airplane-Mode.");

            tvNtType.setVisibility(View.GONE);
            tvPercent.setVisibility(View.GONE);
            tvCellularGateway.setVisibility(View.GONE);

            tvIp.setText("n/a");
            tvGateway.setText("n/a");
            tvDns1.setText("n/a");
        }
        setSIMDetails();

    }

    private void setSIMDetails() {
        TelephonyManager telMgr = telephonyManager;
        int simState = telMgr.getSimState();
        tvNtName.setText("SIM Service Unavailable");
        switch (simState) {
            case TelephonyManager.SIM_STATE_ABSENT:
                // do something
                tvSimState.setText("No SIM");
                tvNtName.setText("No SIM Card");
                Toast.makeText(getContext().getApplicationContext(), "SIM State Absent.", Toast.LENGTH_SHORT).show();
                break;
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                // do something
                tvSimState.setText("Netowrk Locked");

                Toast.makeText(getContext().getApplicationContext(), "SIM State Network Locked.", Toast.LENGTH_SHORT).show();
                break;
            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                // do something
                tvSimState.setText("PIN Required");
                Toast.makeText(getContext().getApplicationContext(), "SIM State PIN Required.", Toast.LENGTH_SHORT).show();
                break;
            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                // do something
                tvSimState.setText("PUK Required");
                Toast.makeText(getContext().getApplicationContext(), "SIM State PUK Required.", Toast.LENGTH_SHORT).show();
                break;
            case TelephonyManager.SIM_STATE_READY:
                tvSimState.setText("Ready");
                tvSimState.setTextColor(Color.parseColor("#006400"));
                getSimData();
                break;
            case TelephonyManager.SIM_STATE_UNKNOWN:
                // do something
                tvSimState.setText("Unknown");
                Toast.makeText(getContext().getApplicationContext(), "SIM State Unknown.", Toast.LENGTH_SHORT).show();
                break;
        }

        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
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
        tvNtName.setText(telephonyManager.getNetworkOperatorName());
        tvSimType.setText(SimType);
        tvPhone.setText(telephonyManager.getLine1Number() + "");
        tvImei.setText(telephonyManager.getDeviceId() + "");
        tvImeisv.setText(telephonyManager.getDeviceSoftwareVersion());
        tvSimSerialNo.setText(telephonyManager.getSimSerialNumber().toString());
        if (getDataConnectionStatus()) {
            tvStatus.setText("Connected");
            tvStatus.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));

        } else {
            tvStatus.setText("Disconnected");
            tvStatus.setTextColor(Color.RED);
        }
    }

    private boolean getDataConnectionStatus() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean mobileDataEnabled = false; // Assume disabled
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            mobileDataEnabled = (Boolean) method.invoke(cm);
        } catch (Exception e) {
        }
        return mobileDataEnabled;
    }

    private String networkType() {
        TelephonyManager teleMan = (TelephonyManager)
                getContext().getSystemService(Context.TELEPHONY_SERVICE);
        int networkType = teleMan.getNetworkType();
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return "1xRTT";
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return "CDMA";
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return "EDGE";
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return "eHRPD";
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return "EVDO rev. 0";
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return "EVDO rev. A";
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return "EVDO rev. B";
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return "GPRS";
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return "HSDPA";
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return "HSPA";
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "HSPA+";
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return "HSUPA";
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "iDen";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "LTE";
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return "UMTS";
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return "Unknown";
        }
        throw new RuntimeException("New type of network");
    }

    @Override
    public boolean onLongClick(View v) {
        final WifiManager wifi = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);

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
            case R.id.tvSimSerialNo:
                value = tvSimSerialNo.getText().toString().trim();
                break;
            case R.id.tvSimType:
                value = tvSimType.getText().toString().trim();
                break;
            case R.id.tvSimState:
                value = tvSimState.getText().toString().trim();
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
        return true;
    }

    protected SwipeRefreshLayout.OnRefreshListener onSwipeListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (isAirplaneModeOn(getContext()) == false) {
                try {
                    if (new ConnectionDetector(getContext()).isConnectingToInternet()) {
                        ((MainActivity) getContext()).reValidate();
                    }
                    setupValues();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                alertAirplaneMode(getContext());
            }
        }
    };

    private void alertAirplaneMode(final Context context) {


        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Network Change Alert (Experimental: Root Level)");
        alert.setMessage("Airplane mode active. TURN OFF now?");
        alert.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                boolean isEnabled = isAirplaneModeOn(context);
                if (isEnabled == true) {
                    setSettings(context, isEnabled ? 1 : 0);
                    Settings.System.putInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0);
                    Intent newIntent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
                    newIntent.putExtra("state", false);
                    try {
                        context.sendBroadcast(newIntent);
                    } catch (SecurityException e) {
                        Toast.makeText(context.getApplicationContext(), "This feature requires rooted device.", Toast.LENGTH_SHORT).show();
                        if (swipeRefreshLayout.isRefreshing())
                            swipeRefreshLayout.setRefreshing(false);
                    } catch (Exception e) {
                        Toast.makeText(context.getApplicationContext(), "Unable to execute.", Toast.LENGTH_SHORT).show();
                        if (swipeRefreshLayout.isRefreshing())
                            swipeRefreshLayout.setRefreshing(false);
                    }
                } else {
                    Toast.makeText(context.getApplicationContext(), "Flight mode off.", Toast.LENGTH_LONG).show();
                    setSettings(context, isEnabled ? 1 : 0);
                }
                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
            }

        });
        alert.setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
            }
        });
        alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
            }
        });
        alert.show();

    }

    @SuppressLint("NewApi")
    private void setSettings(Context context, int value) {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                Settings.System.putInt(
                        context.getContentResolver(),
                        Settings.System.AIRPLANE_MODE_ON, value);
            } else {
                Settings.Global.putInt(
                        context.getContentResolver(),
                        Settings.Global.AIRPLANE_MODE_ON, value);
            }
        } catch (SecurityException e) {
            Toast.makeText(context.getApplicationContext(), "This feature requires rooted device.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context.getApplicationContext(), "Unable to execute.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        getContext().unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        try {
            initialize();
        } catch (Exception e) {
        }
        super.onResume();
    }
}
