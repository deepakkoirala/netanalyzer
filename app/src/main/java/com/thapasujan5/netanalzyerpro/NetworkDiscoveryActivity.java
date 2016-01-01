package com.thapasujan5.netanalzyerpro;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.thapasujan5.netanalyzerpro.R;
import com.thapasujan5.netanalzyerpro.DataStore.ItemsDiscovered;
import com.thapasujan5.netanalzyerpro.DataStore.ItemsDiscoveredAdapter;
import com.thapasujan5.netanalzyerpro.Fragments.WIFI;
import com.thapasujan5.netanalzyerpro.PingService.FabPing;
import com.thapasujan5.netanalzyerpro.Tools.ConnectionDetector;
import com.thapasujan5.netanalzyerpro.Tools.NetworkUtil;

import org.json.JSONObject;

import java.util.ArrayList;

public class NetworkDiscoveryActivity extends AppCompatActivity {
    ItemsDiscoveredAdapter adapter;
    ArrayList<ItemsDiscovered> itemsDiscovereds;
    ListView listView;
    JSONObject jsonObject;

    SwipeRefreshLayout swipeRefreshLayout;
    TextView tvSsid, tvIp, tvMac, tvRouterip, tvPercent;
    Switch aSwitch;

    WifiManager wifiManager;

    BroadcastReceiver receiver;
    IntentFilter filter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_discovery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Perform Quick Ping from here.", Snackbar.LENGTH_LONG)
                        .setAction("Continue", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new FabPing(NetworkDiscoveryActivity.this);
                            }
                        }).show();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        restoreWifiDetails();

    }

    private void restoreWifiDetails() {
        jsonObject = new JSONObject();
        jsonObject = WIFI.getWifiDetails();
        if (wifiManager.isWifiEnabled()) {
            if (NetworkUtil.getConnectivityStatus(this) == AppConstants.TYPE_WIFI) {
                //Type wifi
                if (new ConnectionDetector(this).isConnectingToInternet()) {
                    //Active Internet
                    if (jsonObject != null) {
                        //PreCheck for debug
                        Log.i("json", jsonObject.toString());
                        //Restore WIFI details to View now
                        tvSsid.setText(jsonObject.optString("ssid", "Internal Error !"));
                        tvPercent.setText(jsonObject.optString("percent", "Internal Error !"));
                        tvRouterip.setText(jsonObject.optString("routerip", "Internal Error !"));
                        tvMac.setText(jsonObject.optString("mac", "Internal Error !"));

                    } else {
                        //When json from WIFI Fragment is null
                        Toast.makeText(getApplicationContext(), "Make sure WIFI is TURNed ON.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //No Active Internet
                    Toast.makeText(getApplicationContext(), "No Internet Access !", Toast.LENGTH_SHORT).show();
                }
            } else {
                //Type not wifi i.e., it's data. We can inform user that Discovery is not available for Data Connection in this app.
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Information");
                alert.setMessage("Discovery unavailable for Cellular Networks !");
                alert.setPositiveButton("Dismiss", null);
                alert.setNegativeButton("TURN ON WIFI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        WifiManager wifi = (WifiManager) NetworkDiscoveryActivity.this.getSystemService(WIFI_SERVICE);
                        wifi.setWifiEnabled(true);

                    }
                });

            }
        } else {
            //When wifi not enabled
            WIFI.alertWifiStatus(this, swipeRefreshLayout);
        }

    }

    private void init() {
        itemsDiscovereds = new ArrayList<ItemsDiscovered>();
        ItemsDiscovered itme = new ItemsDiscovered("GOT", "192.168.1.2", "aa:aa:aa:aa:aa:aa");

        itemsDiscovereds.add(itme);
        itemsDiscovereds.add(itme);
        itemsDiscovereds.add(itme);
        itemsDiscovereds.add(itme);
        itemsDiscovereds.add(itme);
        itemsDiscovereds.add(itme);
        itemsDiscovereds.add(itme);
        itemsDiscovereds.add(itme);
        itemsDiscovereds.add(itme);
        itemsDiscovereds.add(itme);
        itemsDiscovereds.add(itme);


        adapter = new ItemsDiscoveredAdapter(this, R.layout.item_row_dicovered_items, itemsDiscovereds);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        //View Init
        tvSsid = (TextView) findViewById(R.id.tvSsid);
        tvPercent = (TextView) findViewById(R.id.tvPercentage);
        tvRouterip = (TextView) findViewById(R.id.tvRouterIp);
        tvMac = (TextView) findViewById(R.id.tvMac);
        aSwitch = (Switch) findViewById(R.id.tbWifiSwitch);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                restoreWifiDetails();
            }
        };
        filter = new IntentFilter();
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.EXTRA_NETWORK_INFO);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        registerReceiver(receiver, filter);
    }

    @Override
    protected void onStop() {
        try {
            unregisterReceiver(receiver);
            super.onStop();
        } catch (Exception e) {

        }
    }
}
