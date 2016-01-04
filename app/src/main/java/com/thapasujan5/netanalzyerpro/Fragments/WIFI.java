package com.thapasujan5.netanalzyerpro.Fragments;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.thapasujan5.netanalyzerpro.R;
import com.thapasujan5.netanalzyerpro.AppConstants;
import com.thapasujan5.netanalzyerpro.MainActivity;
import com.thapasujan5.netanalzyerpro.Tools.Clipboard;
import com.thapasujan5.netanalzyerpro.Tools.ConnectionDetector;
import com.thapasujan5.netanalzyerpro.Tools.DayHourMinSec;
import com.thapasujan5.netanalzyerpro.Tools.NetworkUtil;
import com.thapasujan5.netanalzyerpro.Tools.WifiUtil;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by Suzan on 12/14/2015.
 */
public class WIFI extends Fragment implements View.OnLongClickListener {
    TextView tvSSID, tvIp, tvGateway, tvSubnet, tvDns1, tvDns2, tvLease, tvStatus, tvStrength, tvSpeed, tvFrequency, tvSecurity;
    TextView tvPercent;
    TextView tvRouterMac, tvRouterIP;

    View rootView;
    protected SwipeRefreshLayout swipeRefreshLayout;
    ImageView ivWifi;
    BroadcastReceiver receiver;
    BroadcastReceiver receiverSwitch;
    IntentFilter filter;
    Switch tbWifiSwitch;
    SharedPreferences sharedpreferences;
    static JSONObject jsonObject;


    NotificationManager notificationManager;

    public WIFI() {
        jsonObject = new JSONObject();
    }

    public static JSONObject getConnectionDetails() {
        return jsonObject;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_wifi, container, false);
        try {
            initialize(rootView);
            alertWifiStatus(getContext(), swipeRefreshLayout);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rootView;
    }

    private void initialize(View rootView) throws Exception {
        notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        sharedpreferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity().getBaseContext());


        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(onSwipeListener);
        swipeRefreshLayout.setColorSchemeColors(R.color.swipeRefresh1, R.color.swipeRefresh2, R.color.swipeRefresh3, R.color.swipeRefresh4);
        tvSSID = (TextView) rootView.findViewById(R.id.tvNetworkName);
        tvSSID.setOnLongClickListener(this);
        tvIp = (TextView) rootView.findViewById(R.id.tvIPValue);
        tvIp.setOnLongClickListener(this);
        tvGateway = (TextView) rootView.findViewById(R.id.tvGatewayValue);
        tvGateway.setOnLongClickListener(this);
        tvSubnet = (TextView) rootView.findViewById(R.id.tvSubnetValue);
        tvSubnet.setOnLongClickListener(this);
        tvDns1 = (TextView) rootView.findViewById(R.id.tvDns1Value);
        tvDns1.setOnLongClickListener(this);
        tvDns2 = (TextView) rootView.findViewById(R.id.tvDns2Value);
        tvDns2.setOnLongClickListener(this);
        tvLease = (TextView) rootView.findViewById(R.id.tvLeaseValue);
        tvLease.setOnLongClickListener(this);
        tvStatus = (TextView) rootView.findViewById(R.id.tvStatusValue);
        tvStatus.setOnLongClickListener(this);
        tvStrength = (TextView) rootView.findViewById(R.id.tvSignalStrengthValue);
        tvStatus.setOnLongClickListener(this);
        tvSpeed = (TextView) rootView.findViewById(R.id.tvLinkSpeedValue);
        tvSpeed.setOnLongClickListener(this);
        tvFrequency = (TextView) rootView.findViewById(R.id.tvFrequencyValue);
        tvFrequency.setOnLongClickListener(this);
        tvSecurity = (TextView) rootView.findViewById(R.id.tvSecurityValue);
        tvSecurity.setOnLongClickListener(this);


        tvRouterMac = (TextView) rootView.findViewById(R.id.tvNetworkType);
        tvRouterMac.setOnLongClickListener(this);
        tvRouterIP = (TextView) rootView.findViewById(R.id.tvIp);
        tvRouterIP.setOnLongClickListener(this);

        ivWifi = (ImageView) rootView.findViewById(R.id.ivWifi);
        tvPercent = (TextView) rootView.findViewById(R.id.tvPercentage);

        tbWifiSwitch = (Switch) rootView.findViewById(R.id.tbWifiSwitch);
        tbWifiSwitch.setOnCheckedChangeListener(wifiToggled);
        ColorStateList buttonStates = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{-android.R.attr.state_enabled},
                        new int[]{}
                },
                new int[]{
                        Color.RED,
                        Color.BLUE,
                        Color.GREEN
                }
        );
        if (Build.VERSION.SDK_INT >= 21) {
            tbWifiSwitch.setButtonTintList(buttonStates);
        }
        setupValues();
        receiverSwitch = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (new WifiUtil(getContext()).isWifiEnabled()) {
                    tbWifiSwitch.setChecked(true);

                } else {
                    tbWifiSwitch.setChecked(false);
                }
                try {
                    jsonObject.put("switch", tbWifiSwitch.isChecked());
                } catch (Exception e) {
                }
            }
        };
        getContext().registerReceiver(receiverSwitch, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
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
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);
        getContext().registerReceiver(receiver, filter);
    }

    Switch.OnCheckedChangeListener wifiToggled = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            final WifiManager wifi = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
            if (isChecked) {
                wifi.setWifiEnabled(true);
            } else {
                wifi.setWifiEnabled(false);
            }
        }
    };

    private void setupValues() throws Exception {
        if (new WifiUtil(getContext()).isWifiEnabled() && NetworkUtil.getConnectivityStatus(getContext()) == AppConstants.TYPE_WIFI) {
            //Fill DHCP Config Info
            String ssid = new WifiUtil(getContext()).getSsid();
            tvSSID.setText(ssid);
            jsonObject.put(getString(R.string.ssid), ssid);

            String ip = new WifiUtil(getContext()).getIpAddress();
            tvIp.setText(ip);
            jsonObject.put(getString(R.string.ip), ip);

            tvGateway.setText(new WifiUtil(getContext()).getGateway());
            tvSubnet.setText(new WifiUtil(getContext()).getNetMask());
            tvDns1.setText(new WifiUtil(getContext()).getDns1());
            tvDns2.setText(new WifiUtil(getContext()).getDns2());

            JSONObject leaseJson = new JSONObject();
            leaseJson = DayHourMinSec.getDHMS(new WifiUtil(getContext()).getLeaseDuration());
            try {
                if (leaseJson.getString("result").contentEquals("success")) {
                    tvLease.setText(leaseJson.getString("days") + leaseJson.getString("hours") + leaseJson.getString("minutes") + leaseJson.getString("seconds"));
                } else {
                    tvLease.setText("Error Obtaining Lease Time! Refresh Once.");
                }
            } catch (Exception e) {
                tvLease.setText("Error Obtaining Lease Time! Refresh Once.");
                e.printStackTrace();
            }

            tvSpeed.setText(new WifiUtil(getContext()).getLinkSpeed() + "Mbps");
            tvStrength.setText(new WifiUtil(getContext()).getRssi() + "dB");
            String percent = new WifiUtil(getContext()).getPercentSignal();

            tvPercent.setText(percent);
            jsonObject.put(getString(R.string.percent), percent);
            tvPercent.setVisibility(View.VISIBLE);

            if (Build.VERSION.SDK_INT >= 21) {

                Double inGhz = Double.parseDouble(new WifiUtil(getContext()).getFreq()) / 1000;
                tvFrequency.setText(inGhz + "GHz");
            } else {
                tvFrequency.setVisibility(View.GONE);
                TableRow trf = (TableRow) rootView.findViewById(R.id.trFrequency);
                trf.setVisibility(View.GONE);

            }
            if (new ConnectionDetector(getContext()).isConnectingToInternet()) {
                tvStatus.setText("Connected");
                tvStatus.setTextColor(getContext().getResources().getColor(R.color.colorPrimaryold));
                updateSecurity();
            } else {
                tvStatus.setText("Disconnected");
                tvStatus.setTextColor(Color.RED);
                tvSecurity.setText("n/a");
            }

            //Fill Router Info
            String mac = tvSSID.getText().toString();
            tvRouterMac.setText(mac);
            tvRouterMac.setVisibility(View.VISIBLE);
            jsonObject.put(getString(R.string.mac), mac);

            String routerip = tvGateway.getText().toString();
            tvRouterIP.setText(routerip);
            tvRouterIP.setVisibility(View.VISIBLE);
            jsonObject.put(getString(R.string.routerip), routerip);
        } else {
            if (new WifiUtil(getContext()).isWifiEnabled()) {
                tvSSID.setText("Not Connected.");
            } else {
                tvSSID.setText(R.string.desc_ssid);

            }
            tvIp.setText("n/a");
            tvGateway.setText("n/a");
            tvDns1.setText("n/a");
            tvDns2.setText("n/a");
            tvStatus.setText("Disconnected.");
            tvSpeed.setText("n/a");
            tvSpeed.setTypeface(null, Typeface.NORMAL);
            tvFrequency.setText("n/a");
            tvFrequency.setTypeface(null, Typeface.NORMAL);
            tvSecurity.setText("n/a");
            tvStrength.setText("n/a");
            tvLease.setText("n/a");
            tvRouterMac.setVisibility(View.GONE);
            tvRouterIP.setVisibility(View.GONE);
            tvPercent.setVisibility(View.GONE);

            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    private void updateSecurity() throws Exception {
        WifiManager wifi = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> networkList = wifi.getScanResults();

//get current connected SSID for comparison to ScanResult
        WifiInfo wi = wifi.getConnectionInfo();
        String currentSSID = wi.getSSID();
        if (currentSSID.contains("\"")) ;
        currentSSID = currentSSID.replace("\"", "");


        if (networkList != null) {
            for (ScanResult network : networkList) {
                //check if current connected SSID

                if (currentSSID.contentEquals(network.SSID)) {
                    //get capabilities of current connection
                    String Capabilities = network.capabilities;

                    if (Capabilities.contains("WPA2")) {
                        tvSecurity.setText("WPA2");
                    } else if (Capabilities.contains("WPA")) {
                        tvSecurity.setText("WPA");
                    } else if (Capabilities.contains("WEP")) {
                        tvSecurity.setText("WEP");
                    } else {
                        tvSecurity.setText("Free WIFI");
                    }
                }
            }
        } else {
            Toast.makeText(getContext().getApplicationContext(), "Scanning Error", Toast.LENGTH_SHORT).show();
        }
    }

    protected SwipeRefreshLayout.OnRefreshListener onSwipeListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (new WifiUtil(getContext()).isWifiEnabled()) {
                try {
                    if (new ConnectionDetector(getContext()).isConnectingToInternet()) {
                        ((MainActivity) getContext()).reValidate();
                    }
                    setupValues();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {

                alertWifiStatus(getContext(), swipeRefreshLayout);
            }
        }
    };

    public static void alertWifiStatus(final Context context, final SwipeRefreshLayout swipeRefreshLayout) {
        final WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled() == false) {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle("Network Change Alert");
            alert.setMessage("WIFI is off. TURN ON now ?");
            alert.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(context.getApplicationContext(), "Enabling WIFI...", Toast.LENGTH_LONG).show();
                    wifi.setWifiEnabled(true);
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
        } else {

        }
    }


    @Override
    public void onDestroy() {
        getContext().unregisterReceiver(receiver);
        getContext().unregisterReceiver(receiverSwitch);
        super.onDestroy();
    }

    @Override
    public boolean onLongClick(View v) {
        if (new WifiUtil(getContext()).isWifiEnabled() && NetworkUtil.getConnectivityStatus(getContext()) == AppConstants.TYPE_WIFI) {
            String value = null;
            switch (v.getId()) {
                case R.id.tvNetworkName:
                    value = tvSSID.getText().toString().trim();
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
                case R.id.tvLeaseValue:
                    value = tvLease.getText().toString().trim();
                    break;

                case R.id.tvStatusValue:
                    value = tvStatus.getText().toString().trim();
                    break;
                case R.id.tvSignalStrengthValue:
                    value = tvStrength.getText().toString().trim();
                    break;
                case R.id.tvFrequencyValue:
                    value = tvFrequency.getText().toString().trim();
                    break;
                case R.id.tvSecurityValue:
                    value = tvSecurity.getText().toString().trim();
                    break;
                case R.id.tvNetworkType:
                    value = tvRouterMac.getText().toString().trim();
                    break;
                case R.id.tvIp:
                    value = tvRouterIP.getText().toString().trim();
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

