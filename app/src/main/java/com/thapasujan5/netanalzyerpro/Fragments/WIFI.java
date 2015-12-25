package com.thapasujan5.netanalzyerpro.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.thapasujan5.netanalyzerpro.R;
import com.thapasujan5.netanalzyerpro.Tools.Clipboard;
import com.thapasujan5.netanalzyerpro.Tools.ConnectionDetector;
import com.thapasujan5.netanalzyerpro.Tools.DayHourMinSec;
import com.thapasujan5.netanalzyerpro.Tools.DbToPercent;
import com.thapasujan5.netanalzyerpro.Tools.InttoIp;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by Suzan on 12/14/2015.
 */
public class WIFI extends Fragment implements View.OnClickListener {
    TextView tvSSID, tvIp, tvGateway, tvSubnet, tvDns1, tvDns2, tvLease, tvStatus, tvStrength, tvSpeed, tvFrequency, tvSecurity;
    TextView tvPercent;
    TextView tvRouterMac, tvRouterIP;
    WifiManager wifi;
    WifiInfo wifiInfo;
    DhcpInfo dhcpInfo;
    View rootView;
    protected SwipeRefreshLayout swipeRefreshLayout;
    ImageView ivWifi;

    public WIFI() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_wifi, container, false);
        try {
            alertWifiStatus(getContext());
            initialize(rootView);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rootView;
    }


    private void initialize(View rootView) throws Exception {
        wifi = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(onSwipeListener);
        tvSSID = (TextView) rootView.findViewById(R.id.tvSSIDValue);
        tvSSID.setOnClickListener(this);
        tvIp = (TextView) rootView.findViewById(R.id.tvIPValue);
        tvIp.setOnClickListener(this);
        tvGateway = (TextView) rootView.findViewById(R.id.tvGatewayValue);
        tvGateway.setOnClickListener(this);
        tvSubnet = (TextView) rootView.findViewById(R.id.tvSubnetValue);
        tvSubnet.setOnClickListener(this);
        tvDns1 = (TextView) rootView.findViewById(R.id.tvDns1Value);
        tvDns1.setOnClickListener(this);
        tvDns2 = (TextView) rootView.findViewById(R.id.tvDns2Value);
        tvDns2.setOnClickListener(this);
        tvLease = (TextView) rootView.findViewById(R.id.tvLeaseValue);
        tvLease.setOnClickListener(this);
        tvStatus = (TextView) rootView.findViewById(R.id.tvStatusValue);
        tvStatus.setOnClickListener(this);
        tvStrength = (TextView) rootView.findViewById(R.id.tvSignalStrengthValue);
        tvStatus.setOnClickListener(this);
        tvSpeed = (TextView) rootView.findViewById(R.id.tvLinkSpeedValue);
        tvSpeed.setOnClickListener(this);
        tvFrequency = (TextView) rootView.findViewById(R.id.tvFrequencyValue);
        tvFrequency.setOnClickListener(this);
        tvSecurity = (TextView) rootView.findViewById(R.id.tvSecurityValue);
        tvSecurity.setOnClickListener(this);


        tvRouterMac = (TextView) rootView.findViewById(R.id.tvRouterMac);
        tvRouterMac.setOnClickListener(this);
        tvRouterIP = (TextView) rootView.findViewById(R.id.tvRouterIP);
        tvRouterIP.setOnClickListener(this);

        ivWifi = (ImageView) rootView.findViewById(R.id.ivWifi);
        tvPercent = (TextView) rootView.findViewById(R.id.tvPercentage);


//        if (wifi.isWifiEnabled() && new ConnectionDetector(getContext()).isConnectingToInternet()) {
//            setupValues();
//        }
        //Network State Change event
        getActivity().getApplicationContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                try {
                    setupValues();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
        //RSSI Change Event
        getActivity().getApplicationContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                try {
                    setupValues();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new IntentFilter(WifiManager.RSSI_CHANGED_ACTION));
    }

    private void setupValues() throws Exception {
        wifi = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled() && new ConnectionDetector(getContext()).isConnectingToInternet()) {
            Log.i("mac", "Setting up Values");
            //Getting Base Classes

            dhcpInfo = wifi.getDhcpInfo();
            wifiInfo = wifi.getConnectionInfo();

            //Fill DHCP Config Info

            if (wifiInfo.getSSID().contains("\"")) {
                tvSSID.setText(wifiInfo.getSSID().replace("\"", ""));

            } else {
                tvSSID.setText(wifiInfo.getSSID().toString());
            }
            if (Build.VERSION.SDK_INT < 23) {
                tvSSID.setTextAppearance(getContext(), android.R.style.TextAppearance_Large);
            } else {
                tvSSID.setTextAppearance(android.R.style.TextAppearance_Large);
            }
            //tvSSID.setTextColor(getContext().getResources().getColor(R.color.colorAccent));

            tvIp.setText(InttoIp.intToIp(wifi.getConnectionInfo()
                    .getIpAddress()));
            tvGateway.setText(InttoIp.intToIp(dhcpInfo.gateway));
            tvSubnet.setText(InttoIp.intToIp(dhcpInfo.netmask));
            tvDns1.setText(InttoIp.intToIp(dhcpInfo.dns1));
            tvDns2.setText(InttoIp.intToIp(dhcpInfo.dns2));

            JSONObject leaseJson = new JSONObject();
            leaseJson = DayHourMinSec.getDHMS(dhcpInfo.leaseDuration);
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

            tvSpeed.setText(wifiInfo.getLinkSpeed() + "Mbps");
            tvStrength.setText(wifiInfo.getRssi() + "db");
            tvPercent.setText((int) (DbToPercent.getPercentfromDb(wifiInfo.getRssi())) + "%");
            tvPercent.setVisibility(View.VISIBLE);

            if (Build.VERSION.SDK_INT >= 21) {
                int freq = wifiInfo.getFrequency();
                Double inGhz = Double.parseDouble(Integer.toString(freq)) / 1000;
                tvFrequency.setText(inGhz + "GHz");
            } else {
                tvFrequency.setVisibility(View.GONE);
//                TextView freqLable = (TextView) rootView.findViewById(R.id.tvFrequency);
//                freqLable.setVisibility(View.GONE);
                TableRow trf = (TableRow) rootView.findViewById(R.id.trFrequency);
                trf.setVisibility(View.GONE);

            }
            if (new ConnectionDetector(getContext()).isConnectingToInternet()) {
                tvStatus.setText("Connected");
                tvStatus.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
                updateSecurity();
            } else {
                tvStatus.setText("Disconnected");
                tvStatus.setTextColor(Color.RED);
                tvSecurity.setText("n/a");
            }

            //Fill Router Info
            tvRouterMac.setText(wifiInfo.getBSSID().toUpperCase());
            tvRouterMac.setVisibility(View.VISIBLE);

            tvRouterIP.setText(InttoIp.intToIp(dhcpInfo.gateway));
            tvRouterIP.setVisibility(View.VISIBLE);

            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        } else {
            tvSSID.setText("Slide down to turn WIFI ON.");
            tvIp.setText("n/a");
            tvGateway.setText("n/a");
            tvDns1.setText("n/a");
            tvDns2.setText("n/a");
            tvStatus.setText("n/a");
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
        }
    }

    private void updateSecurity() throws Exception {
        WifiManager wifi = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> networkList = wifi.getScanResults();

//get current connected SSID for comparison to ScanResult
        WifiInfo wi = wifi.getConnectionInfo();
        String currentSSID = wi.getSSID();
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
            if (new ConnectionDetector(getContext()).isConnectingToInternet()) {
                try {
                    setupValues();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                alertWifiStatus(getContext());
            }
        }
    };

    public void alertWifiStatus(final Context context) {
        final WifiManager wifi = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
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
        }
    }

    @Override
    public void onClick(View v) {
        String value = null;
        switch (v.getId()) {
            case R.id.tvSSIDValue:
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
            case R.id.tvRouterMac:
                value = tvRouterMac.getText().toString().trim();
                break;
            case R.id.tvRouterIP:
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

}

