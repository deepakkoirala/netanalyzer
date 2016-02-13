package com.thapasujan5.netanalzyerpro;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.thapasujan5.netanalyzerpro.BuildConfig;
import com.thapasujan5.netanalyzerpro.R;
import com.thapasujan5.netanalzyerpro.ActionMenu.About;
import com.thapasujan5.netanalzyerpro.ActionMenu.AboutWhatsNew;
import com.thapasujan5.netanalzyerpro.ActionMenu.Feedback;
import com.thapasujan5.netanalzyerpro.ActionMenu.Portal;
import com.thapasujan5.netanalzyerpro.ActionMenu.RateApp;
import com.thapasujan5.netanalzyerpro.ActionMenu.SetISP;
import com.thapasujan5.netanalzyerpro.ActionMenu.ShareApp;
import com.thapasujan5.netanalzyerpro.ActionMenu.SnackBarActions;
import com.thapasujan5.netanalzyerpro.ActionMenu.SnapShot;
import com.thapasujan5.netanalzyerpro.ActionMenu.UpgradeToPro;
import com.thapasujan5.netanalzyerpro.DataStore.ViewPagerAdapter;
import com.thapasujan5.netanalzyerpro.Notification.NotificationISP;
import com.thapasujan5.netanalzyerpro.PingService.FabPing;
import com.thapasujan5.netanalzyerpro.PortScanner.FabPortScan;
import com.thapasujan5.netanalzyerpro.Tools.CheckDigit;
import com.thapasujan5.netanalzyerpro.Tools.Clipboard;
import com.thapasujan5.netanalzyerpro.Tools.ConnectionDetector;
import com.thapasujan5.netanalzyerpro.Tools.NetworkUtil;
import com.thapasujan5.netanalzyerpro.Tools.ShowToast;
import com.thapasujan5.netanalzyerpro.Tools.UserFunctions;
import com.thapasujan5.netanalzyerpro.Tools.ZoomOutPageTransformer;

import org.json.JSONObject;

import java.io.File;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ActivityCompat.OnRequestPermissionsResultCallback {
    TextView tvExIpArea, navUpgrade, tvAppName;
    ConnectionDetector connectionDetector;
    String intIP;
    ProgressBar pbExip;
    BroadcastReceiver networkStateReceiver;
    String extIPAdd;
    SharedPreferences sharedpreferences;
    IntentFilter filter;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    NavigationView navigationView;
    ImageView navSetting;
    SharedPreferences.Editor editor;
    //  AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (BuildConfig.FLAVOR.contentEquals("free")) {
            this.setTitle("Net Analyzer Lite");
            new AlertDialog.Builder(this).setTitle("Net Analyzer Lite").
                    setMessage("You are one click away to get the full version...").
                    setPositiveButton("Get It Now !", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new UpgradeToPro(MainActivity.this);
                        }
                    }).setNegativeButton("Later", null).show();
        } else {

        }
        if (this.getPackageName().contentEquals("com.thapasujan5.serversearch"))
            this.setTitle("Net Analyzer Lite");
        //   adView = (AdView) findViewById(R.id.adView);
        getPermission(Manifest.permission.ACCESS_FINE_LOCATION, AppConstants.ACCESS_FINE_LOCATION);
        getPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, AppConstants.WRITE_EXTERNAL_STORAGE);
//        requestPermissions.getPermission(Manifest.permission.SYSTEM_ALERT_WINDOW, AppConstants.SYSTEM_ALERT_WINDOW);
        getPermission(Manifest.permission.CHANGE_NETWORK_STATE, AppConstants.CHANGE_NETWORK_STATE);
        try {
            initView();
            //  new ShowBannerAd(this, adView);
            initialize();
            new AboutWhatsNew(this); //Run at first install
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getPermission(String permissions, int code) {

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(permissions)
                    != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{permissions},
                        code);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case AppConstants.ACCESS_FINE_LOCATION: {
                Log.d("MainActivity", permissions[0] + " granted");
                break;
            }
            case AppConstants.WRITE_EXTERNAL_STORAGE: {
                Log.d("MainActivity", permissions[0] + " granted");
                break;
            }
            case AppConstants.READ_PHONE_STATE: {
                Log.d("MainActivity", permissions[0] + " granted");
                try {
                    viewPager.setAdapter(viewPagerAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    @Override
    protected void onResume() {
        try {
            reValidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    private void initialize() throws Exception {
        try {
            networkStateReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.w("Network Listener", "Network Type Changed");
                    reValidate();
                }
            };
            filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(networkStateReceiver, filter);

            tvExIpArea.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    String value = tvExIpArea.getText().toString().trim();
                    if (CheckDigit.containsDigit(value)) {
                        // copy ip to clipboard
                        boolean status = Clipboard.copyToClipboard(MainActivity.this,
                                value);
                        if (status) {
                            new ShowToast(MainActivity.this, "Copied " + value
                                    + " to Clipboard", Color.WHITE,
                                    R.drawable.action_bar_bg, 0, Toast.LENGTH_LONG,
                                    Gravity.BOTTOM);
                        }
                    }
                    return true;
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SnackBarActions(MainActivity.this, view);
            }
        });


        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.inflateHeaderView(R.layout.nav_header_main2);
        navSetting = (ImageView) header.findViewById(R.id.nav_settings);
        navSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                drawer.closeDrawers();
            }
        });
        navigationView.getMenu().getItem(1).setChecked(true);
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        // PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.pager_title_strip);
        //pagerTabStrip.setDrawFullUnderline(true);
        //pagerTabStrip.setTabIndicatorColor(Color.RED);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(1);
        sharedpreferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        editor = sharedpreferences.edit();
        pbExip = (ProgressBar) findViewById(R.id.pbExip);
        connectionDetector = new ConnectionDetector(getApplicationContext());
        tvExIpArea = (TextView) findViewById(R.id.extip);
        if (BuildConfig.FLAVOR.contentEquals("free") || (this.getPackageName().contentEquals("com.thapasujan5.serversearch"))) {
            navUpgrade = (TextView) header.findViewById(R.id.upgrade);
            tvAppName = (TextView) header.findViewById(R.id.tvAppName);
            tvAppName.setText("Net Analyzer Lite");
            navUpgrade.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new UpgradeToPro(MainActivity.this);
                    drawer.closeDrawers();
                }
            });
            navUpgrade.setVisibility(View.VISIBLE);
        }
    }


    public void reValidate() {
        {
            try {
                // new ShowBannerAd(this, adView);
                android.support.v7.app.ActionBar ab = getSupportActionBar();
                //ab.setDisplayHomeAsUpEnabled(true);
                if (connectionDetector.isConnectingToInternet()) {
                    // Get Local IP either from WIFI or DataOld
                    // WIFI
                    intIP = NetworkUtil.getIPAddress(true);
                    // Set Internal IP
                    if (intIP != null) {
                        if (intIP.length() > 7) {

                            ab.setSubtitle(Html.fromHtml("<fontcolor='#99ff00'><small>" +
                                    intIP + " " + NetworkUtil.getMACAddress(getString(R.string.wlan0)).toUpperCase() + "</small></fontcolor>"));
                        }
                    } // Get Ex IP if network is connected
                    new ExternalIPFinder().execute();
                } else {
                    ab.setSubtitle(Html.fromHtml("<fontcolor='#99ff00'><small>"
                            + NetworkUtil.getMACAddress(getString(R.string.wlan0)) + "</small</fontcolor>"));
                    tvExIpArea.setText(R.string.no_connection);
                    tvExIpArea.setVisibility(View.VISIBLE);
                    pbExip.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private class ExternalIPFinder extends AsyncTask<Void, Void, Void> {
        String org, city, country;
        boolean data = false;

        @Override
        protected void onPreExecute() {
            pbExip.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            UserFunctions f = new UserFunctions();
            try {
                JSONObject json = f.getOwnInfo();
                if (isCancelled())
                    return null;
                if (json.getString("status").contentEquals("success")) {
                    extIPAdd = json.getString("query");
                    org = json.getString("org");
                    city = json.getString("city");
                    country = json.getString("country");
                    data = true;
                    if (isCancelled())
                        return null;
                    SetISP.setISP(editor, MainActivity.this, extIPAdd, org, city, country);
                } else if (json.getString("status").contentEquals("fail")) {
                    SetISP.setISP(editor, MainActivity.this, "", "", "", "");
                }

            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onCancelled() {
            pbExip.setVisibility(View.INVISIBLE);
            tvExIpArea.setText("Operation Interrupted !");
            tvExIpArea.setVisibility(View.VISIBLE);
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Void result) {
            pbExip.setVisibility(View.INVISIBLE);
            if (data) {
                tvExIpArea.setText("External IP " + extIPAdd + ", " + org + " "
                        + city + ", " + country);
                tvExIpArea.setVisibility(View.VISIBLE);


            } else {
                if (connectionDetector.isConnectingToInternet()) {
                    tvExIpArea.setText(R.string.network_error);
                } else {
                    tvExIpArea.setText(R.string.disconnected);
                }
                tvExIpArea.setVisibility(View.VISIBLE);
            }
            super.onPostExecute(result);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (viewPager.getCurrentItem() != 0) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        if (this.getPackageName().contentEquals("com.thapasujan5.serversearch")) {
            MenuItem item = menu.findItem(R.id.snapshot);
            item.setTitle("Get Pro");
            item.setIcon(null);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.nav_exit) {
            finish();
            onDestroy();
        }

        if (id == R.id.nav_settings) {

            startActivity(new Intent(this, SettingsActivity.class));
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            return true;
        }
        if (id == R.id.snapshot) {
            if (getPackageName().contentEquals("com.thapasujan5.serversearch") == false)
                new SnapShot(this, findViewById(android.R.id.content).getRootView());
            else {
                new UpgradeToPro(this);
            }
            return true;

        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        item.setChecked(true);
        int id = item.getItemId();
        if (id == R.id.nav_exit) {
            finish();
            onDestroy();
        }
        if (id == R.id.nav_portscanner) {
            if (this.getPackageName().contentEquals("com.thapasujan5.serversearch") == false) {
                new FabPortScan(MainActivity.this);
            } else {
                new AlertDialog.Builder(this).setTitle("Net Analyzer Lite").
                        setMessage("This feature requires Full Version of Net Analyzer.").
                        setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new UpgradeToPro(MainActivity.this);
                            }
                        }).setNegativeButton("Cancel", null).show();

            }
        }
        if (id == R.id.nav_connectiondetails) {
            viewPager.setCurrentItem(0);
        } else if (id == R.id.nav_dnsip) {
            startActivity(new Intent(this, DnsLookupActivity.class));
        } else if (id == R.id.nav_networkdiscovery) {
            startActivity(new Intent(this, NetworkDiscoveryActivity.class));
        } else if (id == R.id.nav_ping) {
            new FabPing(MainActivity.this);

        } else if (id == R.id.nav_share) {
            new ShareApp(this);

        } else if (id == R.id.nav_rate) {
            new RateApp(this);
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        } else if (id == R.id.nav_report) {
            new Feedback(this);
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        } else if (id == R.id.nav_about) {
            new About(this);
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        } else if (id == R.id.nav_likeus) {
            new Portal(this);
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        } else if (id == R.id.nav_clear_cache) {
            new AlertDialog.Builder(this).setTitle("Confirm Action").setMessage("This will delete all your saved DNS cache, preferences and screenshots.").setIcon(R.mipmap.ic_clear).setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    File cache = getCacheDir();
                    File appDir = new File(cache.getParent());
                    if (appDir.exists()) {
                        String[] children = appDir.list();
                        for (String s : children) {
                            if (!s.equals("lib")) {
                                deleteDir(new File(appDir, s));
                                new NotificationISP(MainActivity.this);
                                Toast.makeText(getApplicationContext(), "Cache Cleared", Toast.LENGTH_SHORT).show();
                                Log.i("TAG", "**************** File /data/data/APP_PACKAGE/" + s + " DELETED *******************");

                            }
                        }
                    }

                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(networkStateReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }
}
