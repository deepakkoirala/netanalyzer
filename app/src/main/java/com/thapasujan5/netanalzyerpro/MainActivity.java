package com.thapasujan5.netanalzyerpro;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.thapasujan5.netanalyzerpro.R;
import com.thapasujan5.netanalzyerpro.ActionMenu.About;
import com.thapasujan5.netanalzyerpro.ActionMenu.AboutWhatsNew;
import com.thapasujan5.netanalzyerpro.ActionMenu.Feedback;
import com.thapasujan5.netanalzyerpro.ActionMenu.Portal;
import com.thapasujan5.netanalzyerpro.ActionMenu.RateApp;
import com.thapasujan5.netanalzyerpro.ActionMenu.ShareApp;
import com.thapasujan5.netanalzyerpro.ActionMenu.SnapShot;
import com.thapasujan5.netanalzyerpro.DataStore.ViewPagerAdapter;
import com.thapasujan5.netanalzyerpro.PingService.FabPing;
import com.thapasujan5.netanalzyerpro.Tools.CheckDigit;
import com.thapasujan5.netanalzyerpro.Tools.Clipboard;
import com.thapasujan5.netanalzyerpro.Tools.ConnectionDetector;
import com.thapasujan5.netanalzyerpro.Tools.IpMac;
import com.thapasujan5.netanalzyerpro.Tools.ShowToast;
import com.thapasujan5.netanalzyerpro.Tools.UserFunctions;
import com.thapasujan5.netanalzyerpro.Tools.ZoomOutPageTransformer;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextView tvExIpArea;
    ConnectionDetector connectionDetector;
    String result, intIP;
    ProgressBar pbExip;
    BroadcastReceiver networkStateReceiver;
    String extIPAdd;
    SharedPreferences sharedpreferences;
    NotificationManager nm;
    IntentFilter filter;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    View rootView;
    NavigationView navigationView;
    public static TextView tvInfo;
    ImageView navLogo, navSetting;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                                new FabPing(MainActivity.this);
                            }
                        }).show();
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
        new AboutWhatsNew(this); //Run at first install
        initialize();
    }

    private void changeColorSate(NavigationView navigationView) {
        ColorStateList textStateList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{}
                },
                new int[]{
                        getResources().getColor(R.color.background_purple),
                        getResources().getColor(R.color.colorPrimary)
                }
        );
        ColorStateList iconStateList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{}
                },
                new int[]{
                        getResources().getColor(R.color.background_purple),
                        getResources().getColor(R.color.colorPrimary)
                }
        );
        navigationView.setItemIconTintList(iconStateList);
        navigationView.setItemTextColor(textStateList);
    }

    private void initialize() {

        try {
            sharedpreferences = PreferenceManager
                    .getDefaultSharedPreferences(getBaseContext());
            nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            editor = sharedpreferences.edit();

            networkStateReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.w("Network Listener", "Network Type Changed");
                    reValidate();
                }
            };
            filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(networkStateReceiver, filter);
            tvExIpArea = (TextView) findViewById(R.id.extip);
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
            pbExip = (ProgressBar) findViewById(R.id.pbExip);
            connectionDetector = new ConnectionDetector(getApplicationContext());
            viewPager = (ViewPager) findViewById(R.id.pager);
            viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
            PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.pager_title_strip);
            //pagerTabStrip.setDrawFullUnderline(true);
            //pagerTabStrip.setTabIndicatorColor(Color.RED);
            viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
            viewPager.setAdapter(viewPagerAdapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        Log.i(MainActivity.class.getSimpleName(), "onResume");
        reValidate();
        super.onResume();
    }

    public void reValidate() {
        {
            try {
                android.support.v7.app.ActionBar ab = getSupportActionBar();
                //ab.setDisplayHomeAsUpEnabled(true);
                if (connectionDetector.isConnectingToInternet()) {
                    // Get Local IP either from WIFI or Data
                    // WIFI
                    intIP = IpMac.getInternalIP(this);
                    // Set Internal IP
                    if (intIP != null) {
                        if (intIP.length() > 7) {

                            ab.setSubtitle(Html.fromHtml("<fontcolor='#99ff00'><small>" +
                                    intIP + " " + IpMac.getDeviceMacAdd(this).toUpperCase() + "</small></fontcolor>"));
                        }
                    } // Get Ex IP if network is connected
                    new ExternalIPFinder().execute();
                } else {
                    ab.setSubtitle(Html.fromHtml("<fontcolor='#99ff00'><small>"
                            + IpMac.getDeviceMacAdd(this).toUpperCase() + "</small</fontcolor>"));
                    tvExIpArea.setText(R.string.no_connection);
                    tvExIpArea.setVisibility(View.VISIBLE);
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
                if (json.getString("status").contentEquals("success")) {
                    extIPAdd = json.getString("query");
                    org = json.getString("org");
                    city = json.getString("city");
                    country = json.getString("country");
                    data = true;
                } else if (json.getString("status").contentEquals("fail")) {

                }

            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            pbExip.setVisibility(View.INVISIBLE);
            if (data) {
                tvExIpArea.setText("External IP " + extIPAdd + ", " + org + " "
                        + city + ", " + country);
                tvExIpArea.setVisibility(View.VISIBLE);
                editor.putString(getString(R.string.org), org);
                editor.putString(getString(R.string.city), city);
                editor.putString(getString(R.string.country), country);
                editor.putString(getString(R.string.extIpAdd), extIPAdd);
                editor.apply();
                editor.commit();

//                if (sharedpreferences.getBoolean(getString(R.string.key_notification_sticky), true) == true) {
//                    new Notify(MainActivity.this, extIPAdd, IpMac.getInternalIP(MainActivity.this), org, city, country);
//                } else {
//                    nm.cancel(0);
//                }
            } else {
                if (connectionDetector.isConnectingToInternet()) {
                    tvExIpArea.setText("Limited Connectivity Found !");
                } else {
                    tvExIpArea.setText("Disconnected !");
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.nav_exit) {
            finish();
            onDestroy();
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.nav_settings) {

            startActivity(new Intent(this, SettingsActivity.class));
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            return true;
        }
        if (id == R.id.snapshot) {
            new SnapShot(MainActivity.this, getWindow().getDecorView().getRootView());
            return true;

        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        item.setChecked(true);
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_exit) {
            // onStop();
            finish();
            onDestroy();
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
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
