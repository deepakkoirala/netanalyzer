package com.thapasujan5.netanalzyerpro;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.thapasujan5.netanalyzerpro.R;
import com.thapasujan5.netanalzyerpro.ActionMenu.SnapShot;
import com.thapasujan5.netanalzyerpro.DataStore.ItemsDiscovered;
import com.thapasujan5.netanalzyerpro.DataStore.ItemsDiscoveredAdapter;
import com.thapasujan5.netanalzyerpro.Fragments.WIFI;
import com.thapasujan5.netanalzyerpro.PingService.FabPing;
import com.thapasujan5.netanalzyerpro.PingService.PingRequest;
import com.thapasujan5.netanalzyerpro.Tools.Clipboard;
import com.thapasujan5.netanalzyerpro.Tools.ConnectionDetector;
import com.thapasujan5.netanalzyerpro.Tools.DateTimeFormatted;
import com.thapasujan5.netanalzyerpro.Tools.IpMac;
import com.thapasujan5.netanalzyerpro.Tools.NetworkUtil;

import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class NetworkDiscoveryActivity extends AppCompatActivity {
    ItemsDiscoveredAdapter adapter;
    ArrayList<ItemsDiscovered> items;
    ListView listView;
    JSONObject jsonObject;

    SwipeRefreshLayout swipeRefreshLayout;
    TextView tvSsid, tvIp, tvMac, tvRouterip, tvPercent, tvInfo;
    Switch aSwitch;
    ProgressBar progressBar;
    ImageView ivCancel;
    LinearLayout llPb;

    WifiManager wifiManager;
    BroadcastReceiver receiver;
    IntentFilter filter;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    boolean once = false;
    int ttl = 200, timeout = 2000;


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
                Snackbar.make(view, "Perform Quick Ping from here.", Snackbar.LENGTH_SHORT)
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
        setUpSubTitle();

    }

    private void init() {
        items = new ArrayList<ItemsDiscovered>();
        adapter = new ItemsDiscoveredAdapter(this, R.layout.item_row_dicovered_items, items);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(ItemLongClicked);
        //View Init
        tvSsid = (TextView) findViewById(R.id.tvSsid);
        tvPercent = (TextView) findViewById(R.id.tvPercentage);
        tvRouterip = (TextView) findViewById(R.id.tvRouterIp);
        tvMac = (TextView) findViewById(R.id.tvMac);
        aSwitch = (Switch) findViewById(R.id.tbWifiSwitch);
        aSwitch.setOnCheckedChangeListener(WifiSwitchChanged);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(SwipeListener);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        llPb = (LinearLayout) findViewById(R.id.llPb);
        llPb.setVisibility(View.GONE);
        ivCancel = (ImageView) findViewById(R.id.ivCancel);
        ivCancel.setOnClickListener(ScanCancelled);
        tvInfo = (TextView) findViewById(R.id.tvInfo);
        if (items.size() > 1) {
            tvInfo.setVisibility(View.GONE);
        } else {
            tvInfo.setVisibility(View.VISIBLE);
        }

        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                restoreWifiDetails();
                setUpSubTitle();
                if (wifiManager.isWifiEnabled())
                    aSwitch.setChecked(true);
                else
                    aSwitch.setChecked(false);
            }
        };
        filter = new IntentFilter();
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.EXTRA_NETWORK_INFO);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        registerReceiver(receiver, filter);
    }

    ListView.OnItemLongClickListener ItemLongClicked = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
            final ItemsDiscovered item = items.get(position);
            AlertDialog.Builder alert = new AlertDialog.Builder(NetworkDiscoveryActivity.this);

            String[] ContextMenuItems = {"Copy IP", "Copy Name", "Delete", "Ping"};
            alert.setItems(ContextMenuItems, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String result = null;
                    switch (which) {
                        case 0:
                            if (Clipboard.copyToClipboard(NetworkDiscoveryActivity.this, item.ip))
                                result = "Text Copied to Clipboard";
                            break;
                        case 1:
                            if (Clipboard.copyToClipboard(NetworkDiscoveryActivity.this, item.dns))
                                result = "Text Copied to Clipboard";
                            break;
                        case 2:
                            items.remove(position);
                            result = "Deleted";
                            adapter.notifyDataSetChanged();
                            break;
                        case 3:
                            new PingRequest(item.ip, NetworkDiscoveryActivity.this).execute();
                            break;
                    }
                    if (result != null)
                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                }
            });
            Dialog d = alert.create();
            d.setTitle(item.dns);
            d.show();
            return false;
        }
    };

    Switch.OnCheckedChangeListener WifiSwitchChanged = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            final WifiManager wifi = (WifiManager) NetworkDiscoveryActivity.this.getSystemService(Context.WIFI_SERVICE);
            if (isChecked) {
                wifi.setWifiEnabled(true);

            } else {
                wifi.setWifiEnabled(false);
            }
        }
    };
    SwipeRefreshLayout.OnRefreshListener SwipeListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (wifiManager.isWifiEnabled()) {
                new NetworkDiscovery(NetworkDiscoveryActivity.this).execute();
            } else {
                WIFI.alertWifiStatus(NetworkDiscoveryActivity.this, swipeRefreshLayout);
            }
        }
    };

    private void restoreWifiDetails() {
        jsonObject = new JSONObject();
        jsonObject = WIFI.getConnectionDetails();
        if (wifiManager.isWifiEnabled()) {
            if (NetworkUtil.getConnectivityStatus(this) == AppConstants.TYPE_WIFI) {
                //Type wifi
                if (new ConnectionDetector(this).isConnectingToInternet()) {
                    //Active Internet
                    if (jsonObject != null) {
                        //Restore WIFI details to View now
                        tvSsid.setText(jsonObject.optString(getString(R.string.ssid), "Internal Error !"));
                        tvPercent.setText(jsonObject.optString(getString(R.string.percent), "Internal Error !"));
                        if (Build.VERSION.SDK_INT < 23) {
                            tvPercent.setTextAppearance(this, android.R.style.TextAppearance_Large);
                        } else {
                            tvPercent.setTextAppearance(android.R.style.TextAppearance_Large);
                        }
                        tvPercent.setTextColor(getResources().getColor(R.color.app_theme_background));
                        tvRouterip.setText(jsonObject.optString(getString(R.string.routerip), "Internal Error !"));
                        tvMac.setText(jsonObject.optString(getString(R.string.mac), "Internal Error !"));


                        //Save this json values for lastTimeValues()
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(jsonObject.optString(getString(R.string.ssid)), "Internal Error !");
                        editor.putString(jsonObject.optString(getString(R.string.routerip)), "Internal Error !");
                        editor.putString(jsonObject.optString(getString(R.string.mac)), "Internal Error !");
                        editor.putString(getString(R.string.time), System.currentTimeMillis() + "");
                        editor.apply();
                        editor.commit();

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
                alert.setMessage("Discovery unavailable for Cellular Networks.");
                alert.setPositiveButton("Dismiss", null);
                alert.setNegativeButton("TURN ON WIFI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        WifiManager wifi = (WifiManager) NetworkDiscoveryActivity.this.getSystemService(WIFI_SERVICE);
                        wifi.setWifiEnabled(true);

                    }
                });
                if (!once && NetworkUtil.getConnectivityStatus(this) == AppConstants.TYPE_MOBILE) {
                    once = true;
                    alert.show();
                }
                restoreLastValues(jsonObject);


            }
        } else {
            //When wifi not enabled
            restoreLastValues(jsonObject);
            if (!once) {
                once = true;
                WIFI.alertWifiStatus(this, swipeRefreshLayout);
            }
        }

    }

    private void restoreLastValues(JSONObject jsonObject) {
        tvSsid.setText(sharedPreferences.getString(getString(R.string.ssid), "WIFI Disconnected !"));
        tvPercent.setText(DateTimeFormatted.getDate(Long.parseLong(sharedPreferences.getString(getString(R.string.time), (Long.parseLong(System.currentTimeMillis() + "") + ""))), "EEE MMM dd yyyy HH:mm"));
        if (Build.VERSION.SDK_INT < 23) {
            tvPercent.setTextAppearance(this, android.R.style.TextAppearance_Small);
        } else {
            tvPercent.setTextAppearance(android.R.style.TextAppearance_Small);
        }

        tvPercent.setTextColor(this.getResources().getColor(R.color.app_theme_background));
        tvMac.setText(sharedPreferences.getString(getString(R.string.mac), "n/a"));
        tvRouterip.setText(sharedPreferences.getString(getString(R.string.routerip), "n/a"));


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        if (id == R.id.scan) {
            NetworkDiscovery ns = new NetworkDiscovery(this);
            ns.execute();
        }

        if (id == R.id.snapshot) {
            new SnapShot(this, getWindow().getDecorView().getRootView());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    ImageView.OnClickListener ScanCancelled = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            llPb.setVisibility(View.GONE);
            ttl = 1;
            timeout = 1;
            NetworkDiscovery ns = new NetworkDiscovery(NetworkDiscoveryActivity.this);
            ns.cancelTask();
        }
    };

    private class NetworkDiscovery extends AsyncTask<Void, Integer, ItemsDiscovered> {
        Context context;


        public NetworkDiscovery(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            llPb.setVisibility(View.VISIBLE);

            setProgressBarIndeterminateVisibility(true);
            items.clear();
            if (items.size() > 1) {
                tvInfo.setVisibility(View.GONE);
            } else {
                tvInfo.setVisibility(View.VISIBLE);
            }
            progressBar.setMax(100);
            super.onPreExecute();
        }

        public void cancelTask() {
            cancel(true);
            llPb.setVisibility(View.GONE);
        }

        @Override
        protected void onCancelled() {
            cancel(true);
            llPb.setVisibility(View.GONE);
            super.onCancelled();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            double percent = (double) values[0] / (double) 255.0 * 100.0;
            progressBar.setProgress((int) percent);
            if (items.size() > 1) {
                tvInfo.setVisibility(View.GONE);
            } else {
                tvInfo.setVisibility(View.VISIBLE);
            }
            adapter.notifyDataSetChanged();
            super.onProgressUpdate(values);
        }

        @Override
        protected ItemsDiscovered doInBackground(Void... params) {

            try {
                NetworkInterface iFace = NetworkInterface
                        .getByInetAddress(InetAddress.getByName(jsonObject.optString(getString(R.string.routerip), "192.168.1.1")));
                String addr = jsonObject.optString(getString(R.string.routerip), "192.168.1.1");
                for (int i = 1; i <= 255; i++) {
                    // build the next IP address
                    addr = addr.substring(0, addr.lastIndexOf('.') + 1) + i;
                    InetAddress pingAddr = InetAddress.getByName(addr);

                    // set timeout and ttl for ping
                    Log.i("target", addr);
                    if (isCancelled()) return null;
                    if (i > 25) {
                        ttl = 64;
                        timeout = 50;
                    }
                    if (pingAddr.isReachable(iFace, ttl, timeout)) {
                        ItemsDiscovered item = new ItemsDiscovered(pingAddr.getHostName(), pingAddr.getHostAddress(), "");
                        items.add(item);
                    }
                    publishProgress(i);
                }
            } catch (UnknownHostException ex) {
            } catch (IOException ex) {
            }

            return null;
        }

        @Override
        protected void onPostExecute(ItemsDiscovered itemsDiscovered) {
            if (items.size() > 1 == false) {
                tvInfo.setVisibility(View.VISIBLE);
            } else {
                tvInfo.setVisibility(View.GONE);
            }


            llPb.setVisibility(View.GONE);
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
            Toast.makeText(getApplicationContext(), "Scan Completed.", Toast.LENGTH_SHORT).show();
            super.onPostExecute(itemsDiscovered);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_network_discovery_menu, menu);
        return true;
    }

    private void setUpSubTitle() {
        try {
            android.support.v7.app.ActionBar ab = getSupportActionBar();
            ab.setDisplayHomeAsUpEnabled(true);
            if (new ConnectionDetector(this).isConnectingToInternet()) {
                // Get Local IP either from WIFI or Data
                // WIFI
                String intIP = IpMac.getInternalIP(this);
                // Set Internal IP
                if (intIP != null) {
                    if (intIP.length() > 7) {
                        ab.setSubtitle(Html.fromHtml("<fontcolor='#99ff00'><small>" +
                                intIP + " " + IpMac.getDeviceMacAdd(this).toUpperCase() + "</small></fontcolor>"));
                    }
                } // Get Ex IP if network is connected

            } else {
                ab.setSubtitle(Html.fromHtml("<fontcolor='#99ff00'><small>"
                        + IpMac.getDeviceMacAdd(this).toUpperCase() + "</small</fontcolor>"));
            }
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
