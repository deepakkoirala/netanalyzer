package com.thapasujan5.netanalzyerpro;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.thapasujan5.netanalyzerpro.R;
import com.thapasujan5.netanalzyerpro.ActionMenu.SnackBarActions;
import com.thapasujan5.netanalzyerpro.ActionMenu.SnapShot;
import com.thapasujan5.netanalzyerpro.ActionMenu.UpgradeToPro;
import com.thapasujan5.netanalzyerpro.DataStore.ItemsDiscovered;
import com.thapasujan5.netanalzyerpro.DataStore.ItemsDiscoveredAdapter;
import com.thapasujan5.netanalzyerpro.Fragments.WIFI;
import com.thapasujan5.netanalzyerpro.PingService.PingRequest;
import com.thapasujan5.netanalzyerpro.PortScanner.PortScanRequest;
import com.thapasujan5.netanalzyerpro.Tools.Clipboard;
import com.thapasujan5.netanalzyerpro.Tools.ConnectionDetector;
import com.thapasujan5.netanalzyerpro.Tools.DateTimeFormatted;
import com.thapasujan5.netanalzyerpro.Tools.NetworkUtil;
import com.thapasujan5.netanalzyerpro.Tools.WifiUtil;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

public class NetworkDiscoveryActivity extends AppCompatActivity {
    ItemsDiscoveredAdapter adapter;
    ArrayList<ItemsDiscovered> items;
    ListView listView;
    JSONObject jsonObject;

    SwipeRefreshLayout swipeRefreshLayout;
    TextView tvSsid, tvMac, tvRouterip, tvPercent, tvInfo, tvChannel;
    Switch aSwitch;
    ProgressBar progressBar;
    TextView tvScanPercent;
    ImageView ivCancel;
    RelativeLayout llPb;

    WifiManager wifiManager;
    BroadcastReceiver receiver;
    IntentFilter filter;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    boolean once = false;
    NetworkDiscovery ns;
    int[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.DKGRAY, Color.MAGENTA, Color.BLACK};
    Random r;
    //AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_discovery);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SnackBarActions(NetworkDiscoveryActivity.this, view);
            }
        });
        r = new Random();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        restoreWifiDetails();
        setUpSubTitle();
        if (sharedPreferences.getBoolean(getString(R.string.startup_scan), false) == true) {
            ns = new NetworkDiscovery(this);
            ns.execute();
        }
        //new ShowBannerAd(this, adView);
    }

    private void init() {
        items = new ArrayList<ItemsDiscovered>();
        adapter = new ItemsDiscoveredAdapter(this, R.layout.item_row_dicovered_items, items);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(ItemLongClicked);
        //View Locate
        //adView = (AdView) findViewById(R.id.adView);
        tvSsid = (TextView) findViewById(R.id.tvEssidWig);
        tvPercent = (TextView) findViewById(R.id.tvPercentage);
        tvRouterip = (TextView) findViewById(R.id.tvIpWig);
        tvMac = (TextView) findViewById(R.id.tvBSSIdWig);
        aSwitch = (Switch) findViewById(R.id.tbWifiSwitch);
        aSwitch.setOnCheckedChangeListener(WifiSwitchChanged);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(SwipeListener);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        llPb = (RelativeLayout) findViewById(R.id.llPb);
        llPb.setVisibility(View.GONE);
        tvScanPercent = (TextView) findViewById(R.id.tvScanPercent);
        tvScanPercent.setText("");
        ivCancel = (ImageView) findViewById(R.id.ivCancel);
        ivCancel.setOnClickListener(ScanCancelled);
        tvInfo = (TextView) findViewById(R.id.tvInfo);
        tvChannel = (TextView) findViewById(R.id.tvChannel);
        if (items.size() > 1) {
            tvInfo.setVisibility(View.GONE);
        } else {
            tvInfo.setVisibility(View.VISIBLE);
        }

        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        editor = sharedPreferences.edit();
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
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onResume() {
        try {
            //new ShowBannerAd(this, adView);
            restoreWifiDetails();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onResume();
    }

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
                        tvSsid.setText(jsonObject.optString(getString(R.string.ssid), ""));
                        tvPercent.setText(jsonObject.optString(getString(R.string.percent), ""));

                        tvPercent.setTextColor(getResources().getColor(R.color.app_theme_background));
                        tvRouterip.setText(jsonObject.optString(getString(R.string.routerip), ""));
                        tvMac.setText(jsonObject.optString(getString(R.string.mac), ""));
                        tvChannel.setText(jsonObject.optString(getString(R.string.channel), ""));
                        //Save this json values for lastTimeValues()
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(jsonObject.optString(getString(R.string.ssid)), "Check Connection");
                        editor.putString(jsonObject.optString(getString(R.string.routerip)), "");
                        editor.putString(jsonObject.optString(getString(R.string.mac)), "");
                        editor.putString(jsonObject.optString(getString(R.string.channel)), "");
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
                //Type not wifi i.e., it's data. We can inform user that Discovery is not available for DataOld Connection in this app.
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
        tvSsid.setText(sharedPreferences.getString(getString(R.string.ssid), "WIFI Disconnected"));

        if (sharedPreferences.getString(getString(R.string.time), "null").contains("null")) {
            tvPercent.setText("Last Scan: Not Scanned yet.");
        } else {
            String time = DateTimeFormatted.getDate(Long.parseLong(sharedPreferences.getString(getString(R.string.time), null)), "EEE MMM dd yyyy HH:mm");
            tvPercent.setText("Last Scan:" + time);
        }
        tvPercent.setTextColor(this.getResources().getColor(R.color.app_theme_background));
        tvMac.setText(sharedPreferences.getString(getString(R.string.mac), ""));
        tvChannel.setText(sharedPreferences.getString(getString(R.string.channel), ""));
        tvRouterip.setText(sharedPreferences.getString(getString(R.string.routerip), ""));
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
            newTask(id);
            return true;

        }

        if (id == R.id.snapshot) {
            new SnapShot(this, findViewById(android.R.id.content).getRootView());
            return true;
        }
        return true;
    }

    ImageView.OnClickListener ScanCancelled = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            llPb.setVisibility(View.GONE);
            ns.cancel(true);
        }
    };

    private class NetworkDiscovery extends AsyncTask<Void, Integer, ItemsDiscovered> {
        Context context;
        Process process;

        public NetworkDiscovery(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            if (NetworkUtil.getConnectivityStatus(context) == AppConstants.TYPE_WIFI) {
                swipeRefreshLayout.setRefreshing(true);
                llPb.setVisibility(View.VISIBLE);
                items.clear();
                if (items.size() > 1) {
                    tvInfo.setVisibility(View.GONE);
                } else {
                    tvInfo.setVisibility(View.VISIBLE);
                }
                progressBar.setMax(100);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.time), System.currentTimeMillis() + "");
                editor.apply();
                editor.commit();
            } else {
                WIFI.alertWifiStatus(NetworkDiscoveryActivity.this, swipeRefreshLayout);
                return;
            }
            super.onPreExecute();
        }


        @Override
        protected void onCancelled() {
            adapter.notifyDataSetChanged();
            Toast.makeText(getApplicationContext(), "Scan Aborted.", Toast.LENGTH_SHORT).show();
            llPb.setVisibility(View.GONE);
            if (swipeRefreshLayout.isRefreshing())
                swipeRefreshLayout.setRefreshing(false);
            super.onCancelled();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            adapter.notifyDataSetChanged();
            double percent = (double) values[0] / (double) 255.0 * 100.0;
            tvScanPercent.setText((new DecimalFormat("#.#").format(percent)) + "%");
            tvScanPercent.setTextColor(colors[r.nextInt(colors.length)]);
            progressBar.setProgress((int) percent);

            if (items.size() > 0) {
                tvInfo.setVisibility(View.GONE);
            }

            super.onProgressUpdate(values);
        }

        @Override
        protected ItemsDiscovered doInBackground(Void... params) {

            try {
                String gateway = new WifiUtil(NetworkDiscoveryActivity.this).getGateway();
//                NetworkInterface iFace = NetworkInterface
//                        .getByInetAddress(InetAddress.getByName(gateway));

                for (int i = 1; i <= 255; i++) {
                    if (isCancelled()) {
                        Log.i("NDA", "cancelled");
                        ns = null;
                        break;
                    }
                    if (gateway != null) {
                        // build the next IP address
                        gateway = gateway.substring(0, gateway.lastIndexOf('.') + 1) + i;
                        InetAddress pingAddr = InetAddress.getByName(gateway);

                        // set timeout and ttl for ping
                        // Log.i("target", gateway);
                        String str = "";
                        process = Runtime.getRuntime().exec(
                                "/system/bin/ping -c 1 " + gateway);
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(process.getInputStream()));
                        int j;
                        char[] buffer = new char[4096];
                        StringBuffer output = new StringBuffer();
                        while ((j = reader.read(buffer)) > 0) {
                            if (isCancelled()) {
                                Log.i("NDA", "cancelled");
                                break;
                            }
                            output.append(buffer, 0, j);
                        }
                        reader.close();
                        str = output.toString();
                        if (str.contains("100%") == false) {
                            ItemsDiscovered item = new ItemsDiscovered(pingAddr.getHostName(), pingAddr.getHostAddress(), "");
                            items.add(item);

                        }
                        publishProgress(i);
                        try {
                            process.destroy();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        break;
                    }
                }
            } catch (UnknownHostException ex) {
            } catch (IOException ex) {
            }

            return null;
        }

        @Override
        protected void onPostExecute(ItemsDiscovered itemsDiscovered) {
            adapter.notifyDataSetChanged();
            restoreWifiDetails();
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
                // Get Local IP either from WIFI or DataOld
                // WIFI
                String intIP = NetworkUtil.getIPAddress(true);
                // Set Internal IP
                if (intIP != null) {
                    if (intIP.length() > 7) {
                        ab.setSubtitle(Html.fromHtml("<fontcolor='#99ff00'><small>" +
                                intIP + " " + NetworkUtil.getMACAddress(getString(R.string.wlan0)).toUpperCase() + "</small></fontcolor>"));
                    }
                } // Get Ex IP if network is connected

            } else {
                ab.setSubtitle(Html.fromHtml("<fontcolor='#99ff00'><small>"
                        + NetworkUtil.getMACAddress(getString(R.string.wlan0)).toUpperCase() + "</small</fontcolor>"));
            }
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ListView.OnItemLongClickListener ItemLongClicked = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
            final ItemsDiscovered item = items.get(position);
            AlertDialog.Builder alert = new AlertDialog.Builder(NetworkDiscoveryActivity.this);

            String[] ContextMenuItems = {"Copy IP", "Copy Name", "Delete", "Ping", "Scan Ports"};
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
                        case 4:
                            if (NetworkDiscoveryActivity.this.getPackageName().contentEquals("com.thapasujan5.serversearch") == false) {
                                new PortScanRequest(item.ip, NetworkDiscoveryActivity.this).execute();
                            } else {
                                new android.support.v7.app.AlertDialog.Builder(NetworkDiscoveryActivity.this).setTitle("Net Analyzer Lite").
                                        setMessage("This feature requires Full Version of Net Analyzer.").
                                        setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                new UpgradeToPro(NetworkDiscoveryActivity.this);
                                            }
                                        }).setNegativeButton("Cancel", null).show();

                            }

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

    private void newTask(int source) {
        if (wifiManager.isWifiEnabled() && NetworkUtil.getConnectivityStatus(NetworkDiscoveryActivity.this) == AppConstants.TYPE_WIFI) {
            if (ns != null && ns.getStatus() != AsyncTask.Status.FINISHED) {
                new AlertDialog.Builder(NetworkDiscoveryActivity.this).
                        setMessage("Cancel current scan?").setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ns.cancel(true);
                            }
                        }).setNegativeButton("Cancel", null).show();
            } else {
                ns = new NetworkDiscovery(NetworkDiscoveryActivity.this);
                ns.execute();
            }

        } else {
            WIFI.alertWifiStatus(NetworkDiscoveryActivity.this, swipeRefreshLayout);
        }
    }

    SwipeRefreshLayout.OnRefreshListener SwipeListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            newTask(R.id.swipeRefreshLayout);
        }
    };

    @Override
    protected void onStop() {
        try {
            unregisterReceiver(receiver);
            if (ns != null && ns.isCancelled() == false) {
                ns.cancel(true);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        super.onStop();
    }
}