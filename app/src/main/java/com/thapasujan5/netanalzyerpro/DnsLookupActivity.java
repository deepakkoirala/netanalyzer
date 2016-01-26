package com.thapasujan5.netanalzyerpro;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.thapasujan5.netanalyzerpro.R;
import com.thapasujan5.netanalzyerpro.ActionMenu.SetISP;
import com.thapasujan5.netanalzyerpro.ActionMenu.ShowBannerAd;
import com.thapasujan5.netanalzyerpro.ActionMenu.SnackBarActions;
import com.thapasujan5.netanalzyerpro.ActionMenu.SnapShot;
import com.thapasujan5.netanalzyerpro.ActionMenu.UpgradeToPro;
import com.thapasujan5.netanalzyerpro.DataStore.Items;
import com.thapasujan5.netanalzyerpro.DataStore.ItemsAdapter;
import com.thapasujan5.netanalzyerpro.DataStore.ReportChoices;
import com.thapasujan5.netanalzyerpro.DataStore.ReportChoicesAdapter;
import com.thapasujan5.netanalzyerpro.Database.DAO;
import com.thapasujan5.netanalzyerpro.Notification.NotificationISP;
import com.thapasujan5.netanalzyerpro.PingService.PingRequest;
import com.thapasujan5.netanalzyerpro.PortScanner.PortScanRequest;
import com.thapasujan5.netanalzyerpro.Tools.CheckDigit;
import com.thapasujan5.netanalzyerpro.Tools.CheckNet;
import com.thapasujan5.netanalzyerpro.Tools.Clipboard;
import com.thapasujan5.netanalzyerpro.Tools.ConnectionDetector;
import com.thapasujan5.netanalzyerpro.Tools.DateTimeFormatted;
import com.thapasujan5.netanalzyerpro.Tools.NetworkUtil;
import com.thapasujan5.netanalzyerpro.Tools.ShowToast;
import com.thapasujan5.netanalzyerpro.Tools.UserFunctions;

import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;

public class DnsLookupActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener, TextView.OnEditorActionListener {

    EditText etSearchBox;
    ImageView ivSearch;
    RelativeLayout rlProgressbarContainer, rlInfo;
    TextView tvEntriesCountArea, tvExIpArea;
    ConnectionDetector connectionDetector;
    ImageView ivClearList, ivShare, ivCopy;
    String result, intIP;
    ListView listview;
    ProgressBar pbExip, pbMain;
    ItemsAdapter adapterMain;
    ArrayList<Items> currentItems;
    ArrayList<Items> dbItems;
    DAO dao;
    BroadcastReceiver networkStateReceiver;
    String extIPAdd;
    boolean intentServiceResult;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    NotificationManager nm;
    IntentFilter filter;
    protected SwipeRefreshLayout swipeRefreshLayout;

    AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dnslookup);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        adView = (AdView) findViewById(R.id.adView);
        new ShowBannerAd(this, adView);
        Bundle fields = getIntent().getExtras();
        if (fields != null) {
            intentServiceResult = fields.getBoolean("isr");
        }
        new ShowBannerAd(this, (AdView) findViewById(R.id.adView));
        initialize();
    }

    private void initialize() {
        try {
            sharedpreferences = PreferenceManager
                    .getDefaultSharedPreferences(getBaseContext());
            nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            editor = sharedpreferences.edit();
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(this);

            networkStateReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.w("Network Listener", "Network Type Changed");
                    String status = NetworkUtil.getConnectivityStatusString(context);
                    reValidate();
                }
            };
            filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(networkStateReceiver, filter);
            etSearchBox = (EditText) findViewById(R.id.input);
            etSearchBox.setOnEditorActionListener(this);
            // Check if no view has focus:
            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            ivSearch = (ImageView) findViewById(R.id.find);
            ivSearch.setOnClickListener(this);

            ivShare = (ImageView) findViewById(R.id.share);
            ivShare.setOnClickListener(this);

            ivCopy = (ImageView) findViewById(R.id.copy);
            ivCopy.setOnClickListener(this);

            ivClearList = (ImageView) findViewById(R.id.clearlist);
            ivClearList.setOnClickListener(this);

            tvEntriesCountArea = (TextView) findViewById(R.id.count);

            tvExIpArea = (TextView) findViewById(R.id.extip);
            tvExIpArea.setOnClickListener(this);

            rlProgressbarContainer = (RelativeLayout) findViewById(R.id.rvPB);
            rlProgressbarContainer.setVisibility(View.GONE);

            rlInfo = (RelativeLayout) findViewById(R.id.rlInfo);
            rlInfo.setVisibility(View.GONE);

            currentItems = new ArrayList<Items>();
            dbItems = new ArrayList<Items>();

            adapterMain = new ItemsAdapter(this, R.layout.item_row_list_dnslookup, dbItems);

            listview = (ListView) findViewById(R.id.listview);
            listview.setOnItemClickListener(this);
            listview.setOnItemLongClickListener(this);
            listview.setAdapter(adapterMain);

            pbExip = (ProgressBar) findViewById(R.id.pbExip);
            pbMain = (ProgressBar) findViewById(R.id.progressBar);

            swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
            swipeRefreshLayout.setColorSchemeColors(R.color.swipeRefresh1, R.color.swipeRefresh2, R.color.swipeRefresh3, R.color.swipeRefresh4);

            connectionDetector = new ConnectionDetector(getApplicationContext());
            dao = new DAO(getApplicationContext());
            new DBAsync().execute();
        } catch (Exception e) {
            finish();
            e.printStackTrace();
        }
    }

    protected SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (connectionDetector.isConnectingToInternet() == false) {
                new CheckNet(getApplicationContext());
            } else {
                reValidate();
            }
        }
    };


    private class ServerAsync extends AsyncTask<Void, Void, Void> {
        String ips = "";
        boolean error = false, value = false;
        String inputString;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            rlProgressbarContainer.setVisibility(View.VISIBLE);
        }

        ServerAsync(String inputString) {
            this.inputString = inputString;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                Items item;
                Date d = new Date();
                long time = d.getTime();

                InetAddress[] add = null;
                UserFunctions userFunctions = new UserFunctions();
                JSONObject json;
                try {
                    add = InetAddress.getAllByName(inputString.trim());

                    if (add.length > 0) {

                        dao.open();
                        currentItems.clear();
                        for (InetAddress inet : add) {

                            try {
                                json = userFunctions.getIPInfo(inet.getHostAddress());
                                if (json != null)
                                    if (json.getString("status").contentEquals(
                                            "success")) {
                                        String isp = json.optString("isp"), city = json
                                                .optString("city"), country = json
                                                .optString("country");

                                        item = new Items(inet.getHostName(),
                                                inet.getHostAddress(), isp, city
                                                + " " + country,
                                                Long.toString(time), json.optString("lat"), json.optString("lon"), json.optString("region"), json.optString("regionName"), json.optString("zip"), json.optString("timezone"));
                                        dao.addItems(item);
                                        currentItems.add(new Items(inet
                                                .getCanonicalHostName(), inet
                                                .getHostAddress(), isp, city + " "
                                                + country, Long.toString(time), json.optString("lat"), json.optString("lon"), json.optString("region"), json.optString("regionName"), json.optString("zip"), json.optString("timezone")));
                                    } else if (json.getString("message")
                                            .contentEquals("private range")) {
                                        InetAddress ad = InetAddress
                                                .getByName(inputString);
                                        item = new Items(ad.getCanonicalHostName(),
                                                ad.getHostAddress(), "", "",
                                                Long.toString(time), "", "", "", "", "", "");
                                        dao.addItems(item);
                                        currentItems.add(item);

                                    }
                            } catch (Exception e) {
                                InetAddress ad = InetAddress.getByName(inputString);
                                item = new Items(ad.getCanonicalHostName(),
                                        ad.getHostAddress(), "", "",
                                        Long.toString(time), "", "", "", "", "", "");
                                dao.open();
                                dao.addItems(item);
                                currentItems.add(item);
                            }
                        }
                        dao.close();
                        value = true;
                    }
                } catch (UnknownHostException e) {
                    error = true;
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result1) {
            try {


                rlProgressbarContainer.setVisibility(View.GONE);
                ivSearch.setEnabled(true);
                if (error) {
                    Toast.makeText(getApplicationContext(),
                            R.string.oops_something_went_wrong_try_in_a_while_,
                            Toast.LENGTH_LONG).show();

                } else if (value) {
                    new DBAsync().execute();
                    result = ips;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.onPostExecute(result1);
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
                    SetISP.setISP(editor, DnsLookupActivity.this, extIPAdd, org, city, country);
                } else if (json.getString("status").contentEquals("fail")) {
                    SetISP.setISP(editor, DnsLookupActivity.this, extIPAdd, org, city, country);
                }

            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            pbExip.setVisibility(View.INVISIBLE);
            if (data) {
                new ServerAsync(extIPAdd).execute();
                tvExIpArea.setText("External IP " + extIPAdd + ", " + org + " "
                        + city + ", " + country);
                tvExIpArea.setVisibility(View.VISIBLE);
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
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   int position, long id) {
        //clicked Item

        view.setBackgroundColor(Color.parseColor("#00ffffff"));
        final Items myItem = dbItems.get(position);

        String[] choices = {"Open " + myItem.ip,
                "Copy " + myItem.ip, "Export data... ", "Remove from list",
                "Ping " + myItem.ip, "View on Map", "Details...", "Scan Ports"};
        //Finalized MenuItmes
        ArrayList<ReportChoices> choice = new ArrayList<ReportChoices>();
        choice.add(new ReportChoices(choices[0], R.mipmap.open));
        choice.add(new ReportChoices(choices[1],
                R.mipmap.copy_content));
        choice.add(new ReportChoices(choices[2],
                R.mipmap.importexport));
        choice.add(new ReportChoices(choices[3],
                R.mipmap.delete));
        choice.add(new ReportChoices(choices[4], R.mipmap.merge));
        choice.add(new ReportChoices(choices[5], R.mipmap.place));
        choice.add(new ReportChoices(choices[6],
                R.mipmap.details));
        choice.add(new ReportChoices(choices[7],
                R.mipmap.ic_computer));


        ReportChoicesAdapter adapter = new ReportChoicesAdapter(this, R.layout.item_row_context_menu,
                choice);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    // open value.ip
                    String url = "http://" + myItem.ip;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(Intent.createChooser(i, "Open " + myItem.ip
                            + " using"));
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    return;
                }
                if (which == 1) {
                    // copy ip to clipboard
                    boolean status = Clipboard.copyToClipboard(
                            DnsLookupActivity.this, myItem.ip);
                    if (status) {
                        new ShowToast(DnsLookupActivity.this, "Copied " + myItem.ip
                                + " to Clipboard", Color.WHITE,
                                R.drawable.action_bar_bg, 0,
                                Toast.LENGTH_SHORT, Gravity.BOTTOM);
                    }
                    return;
                }
                if (which == 2) {
                    // Share
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Website: "
                            + myItem.name + "\n" + "IP: " + myItem.ip + "\n"
                            + "Server Location: " + myItem.location + ","
                            + myItem.date);
                    sendIntent.setType("text/plain");
                    startActivity(Intent.createChooser(sendIntent,
                            "Share using"));
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    return;
                }

                if (which == 3) {
                    // Remove
                    dao.open();
                    boolean res = dao.deleteItem(myItem);

                    if (res) {
                        new DBAsync().execute();
                        Toast.makeText(DnsLookupActivity.this,
                                "Removed " + myItem.name + ":" + myItem.ip,
                                Toast.LENGTH_SHORT).show();


                        if (connectionDetector.isConnectingToInternet()) {
                            new ExternalIPFinder().execute();
                        }
                    } else {
                        Toast.makeText(DnsLookupActivity.this, "Error deleteing !",
                                Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
                if (which == 4) {
                    //Ping
                    new PingRequest(myItem.ip, DnsLookupActivity.this).execute();
                }
                if (which == 5) {
                    //map
                    if (DnsLookupActivity.this.getPackageName().contentEquals("com.thapasujan5.serversearch") == false) {
                        if (myItem.lon.length() > 0 && myItem.lat.length() > 0) {
                            Intent openMap = new Intent(DnsLookupActivity.this, MapsActivity.class);
                            openMap.putExtra("location", myItem.location);
                            openMap.putExtra("lat", Double.parseDouble(myItem.lat));
                            openMap.putExtra("lon", Double.parseDouble(myItem.lon));
                            startActivity(openMap);
                            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        } else {
                            new ShowToast(getApplicationContext(), "No geo-location data found !", Color.WHITE, 0, Color.RED, Toast.LENGTH_SHORT, Gravity.BOTTOM);
                        }
                    } else {
                        new android.support.v7.app.AlertDialog.Builder(DnsLookupActivity.this).setTitle("Net Analyzer Lite").
                                setMessage("This feature requires Full Version of Net Analyzer.").
                                setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        new UpgradeToPro(DnsLookupActivity.this);
                                    }
                                }).setNegativeButton("Cancel", null).show();

                    }


                }
                if (which == 7) {
                    //Port Scan
                    if (getPackageName().contentEquals("com.thapasujan5.serversearch") == false) {
                        new PortScanRequest(myItem.ip, DnsLookupActivity.this).execute();
                    } else {
                        new android.support.v7.app.AlertDialog.Builder(DnsLookupActivity.this).setTitle("Net Analyzer Lite").
                                setMessage("This feature requires Full Version of Net Analyzer.").
                                setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        new UpgradeToPro(DnsLookupActivity.this);
                                    }
                                }).setNegativeButton("Cancel", null).show();

                    }


                }
                if (which == 6) {
                    //Details
                    Context context = DnsLookupActivity.this;

                    final Dialog d = new Dialog(context);
                    d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    d.setContentView(R.layout.dialog_newfeatures);

                    TextView title, message;
                    title = (TextView) d.findViewById(R.id.titleText);
                    title.setText("Details:" + myItem.ip);

                    message = (TextView) d.findViewById(R.id.message);
                    final String messageDetails = getFormattedDetails(myItem);
                    message.setText(messageDetails);
                    message.setGravity(Gravity.LEFT);


                    Button ok = (Button) d.findViewById(R.id.ok);
                    ok.setText("Copy");
                    ok.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // copy ip to clipboard
                            boolean status = Clipboard.copyToClipboard(
                                    DnsLookupActivity.this, messageDetails);
                            if (status) {
                                new ShowToast(DnsLookupActivity.this,
                                        "Copied to Clipboard", Color.WHITE,
                                        R.drawable.action_bar_bg, 0,
                                        Toast.LENGTH_LONG, Gravity.BOTTOM);
                            }
                            d.dismiss();
                        }
                    });
                    d.show();
                }
            }
        });
        Dialog d = alert.create();
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.show();
        return true;
    }

    private String getFormattedDetails(Items item) {
        return "DNS: " + item.name + "\nIP: "
                + item.ip + "\nISP: " + item.isp + "\nLocation: "
                + item.location + "\nRegion: " + item.region
                + "\nRegion Name: " + item.region_name
                + "\nTime Zone: " + item.time_zone
                + "\nZip: " + item.zip
                + "\nLast Saved: " + DateTimeFormatted.getDate(
                Long.parseLong(item.date), "EEE MMM dd yyyy HH:mm z")
                + "\nCoordinate: " + item.lon
                + ", " + item.lat
                + "\n";
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        view.setBackgroundColor(Color.parseColor("#00ffffff"));
        if (connectionDetector.isConnectingToInternet()) {
            new ExternalIPFinder().execute();
        } else {
            tvExIpArea.setVisibility(View.INVISIBLE);
        }
        Items value = dbItems.get(position);
        String url = "http://" + value.name;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(Intent.createChooser(i, "Open " + value.name + " using"));
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    private String getAllData() {
        String allData = "";
        dao.open();
        ArrayList<Items> allDbItems = new ArrayList<>();
        allDbItems.addAll(dao.getItems());
        for (int i = 0; i < allDbItems.size(); i++) {
            allData += allData + (i + 1) + ".\nDNS: " + allDbItems.get(i).name + "\nIP: "
                    + allDbItems.get(i).ip + "\nISP: " + allDbItems.get(i).isp + "\nLocation: "
                    + allDbItems.get(i).location + "\nRegion: " + allDbItems.get(i).region
                    + "\nRegion Name: " + allDbItems.get(i).region_name
                    + "\nTime Zone: " + allDbItems.get(i).time_zone
                    + "\nZip: " + allDbItems.get(i).zip
                    + "\nLast Saved: " + DateTimeFormatted.getDate(
                    Long.parseLong(allDbItems.get(i).date), "EEE MMM dd yyyy HH:mm z")
                    + "\nCoordinate: " + allDbItems.get(i).lon
                    + ", " + allDbItems.get(i).lat

                    + "\nRegion: " + allDbItems.get(i).region + "\n\n";
        }
        return allData;

    }


    //Global onClick Handler
    public void onClick(View arg0) {

        //Get the id of clicked item and decide function accordingly
        int id = arg0.getId();
        //Functions starts now

        if (id == R.id.fab) {
            new SnackBarActions(this, arg0);
        }

        if (id == R.id.extip) {
            String value = tvExIpArea.getText().toString().trim();
            if (CheckDigit.containsDigit(value)) {
                // copy ip to clipboard
                boolean status = Clipboard.copyToClipboard(DnsLookupActivity.this,
                        value);
                if (status) {
                    new ShowToast(DnsLookupActivity.this, "Copied " + value
                            + " to Clipboard", Color.WHITE,
                            R.drawable.action_bar_bg, 0, Toast.LENGTH_LONG,
                            Gravity.BOTTOM);
                }
            }

        }

        if (id == R.id.clearlist) {
            ArrayList<Items> tempItems = new ArrayList<Items>();
            dao.open();
            tempItems.addAll(dao.getItems());
            if (tempItems.size() > 0) {
                final Dialog d = new Dialog(DnsLookupActivity.this);
                d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                d.setContentView(R.layout.dialog_common);
                Button ok = (Button) d.findViewById(R.id.ok);
                ok.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        dao.open();
                        dao.deleteAllItems();
                        currentItems.clear();
                        dbItems.clear();
                        adapterMain.notifyDataSetChanged();
                        tvEntriesCountArea.setText(dbItems.size() + " Entries");
                        d.dismiss();
                        onResume();

                    }

                });
                Button cancel = (Button) d.findViewById(R.id.cancel);
                cancel.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        d.dismiss();
                    }
                });
                d.show();
            } else {
                new ShowToast(getApplicationContext(), "Nothing to clear...",
                        Color.WHITE, R.drawable.action_bar_bg, 0,
                        Toast.LENGTH_SHORT, Gravity.BOTTOM);
            }
        }

        if (id == R.id.share) {
            if (dbItems.size() > 0) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getAllData());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Share using"));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            } else {
                new ShowToast(getApplicationContext(), "Nothing to share...",
                        Color.WHITE, R.drawable.action_bar_bg, 0,
                        Toast.LENGTH_SHORT, Gravity.BOTTOM);
            }

        }
        if (id == R.id.copy) {
            if (dbItems.size() > 0) {
                boolean status = Clipboard.copyToClipboard(DnsLookupActivity.this,
                        getAllData());
                if (status) {
                    new ShowToast(DnsLookupActivity.this, "Copied all to Clipboard",
                            Color.WHITE, R.drawable.action_bar_bg, 0,
                            Toast.LENGTH_SHORT, Gravity.BOTTOM);
                }
            } else {
                new ShowToast(getApplicationContext(), "Nothing to copy...",
                        Color.WHITE, R.drawable.action_bar_bg, 0,
                        Toast.LENGTH_SHORT, Gravity.BOTTOM);
            }
        }
        if (id == R.id.find) {
            initFinding();
        }
    }

    private void initFinding() {
        try {
            if (etSearchBox.getText().toString().contentEquals("")) {
                Toast.makeText(getApplicationContext(), "Nothing  to search...",
                        Toast.LENGTH_SHORT).show();
            } else {
                if (connectionDetector.isConnectingToInternet()) {
                    ivSearch.setEnabled(false);
                    new ServerAsync(etSearchBox.getText().toString()).execute();
                } else {
                    new CheckNet(getApplication());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class DBAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            dbItems.clear();
            dao.open();
            dbItems.addAll(dao.getItems());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            adapterMain.notifyDataSetChanged();
            if (dbItems.size() > 0) {
                tvEntriesCountArea.setVisibility(View.VISIBLE);
                tvEntriesCountArea.setText(dbItems.size() + " Entries");
                rlInfo.setVisibility(View.GONE);
            } else {
                tvEntriesCountArea.setVisibility(View.GONE);
                rlInfo.setVisibility(View.VISIBLE);
            }
            super.onPostExecute(result);
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        adapterMain.notifyDataSetChanged();
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if ((actionId == EditorInfo.IME_ACTION_DONE)) {
            InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(etSearchBox.getApplicationWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            initFinding();
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        reValidate();
        new NotificationISP(this);
        new ShowBannerAd(this, adView);
        super.onResume();
    }

    private void reValidate() {
        try {
            new DBAsync().execute();
            android.support.v7.app.ActionBar ab = getSupportActionBar();
            ab.setDisplayHomeAsUpEnabled(true);
            if (connectionDetector.isConnectingToInternet()) {
                // Get Local IP either from WIFI or Data
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
                        + NetworkUtil.getMACAddress(getString(R.string.wlan0)).toUpperCase() + "</small</fontcolor>"));
                tvExIpArea.setText(R.string.no_connection);
                tvExIpArea.setVisibility(View.VISIBLE);
            }
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        if (id == R.id.snapshot) {
            new SnapShot(this, getWindow().getDecorView().getRootView());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activiy_dnslookup_menu, menu);
        return true;
    }
    @Override
    protected void onDestroy() {
        Log.i(DnsLookupActivity.class.getSimpleName(), "onDestroy");
        try {
            unregisterReceiver(networkStateReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}

