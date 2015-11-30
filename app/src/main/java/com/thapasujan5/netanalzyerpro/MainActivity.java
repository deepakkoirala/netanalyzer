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
import android.support.design.widget.Snackbar;
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

import com.thapasujan5.netanalyzerpro.R;
import com.thapasujan5.netanalzyerpro.actionMenu.About;
import com.thapasujan5.netanalzyerpro.actionMenu.AboutWhatsNew;
import com.thapasujan5.netanalzyerpro.actionMenu.Feedback;
import com.thapasujan5.netanalzyerpro.actionMenu.Portal;
import com.thapasujan5.netanalzyerpro.actionMenu.RateApp;
import com.thapasujan5.netanalzyerpro.actionMenu.ScreenShot;
import com.thapasujan5.netanalzyerpro.datastore.Items;
import com.thapasujan5.netanalzyerpro.datastore.ItemsAdapter;
import com.thapasujan5.netanalzyerpro.datastore.ReportChoices;
import com.thapasujan5.netanalzyerpro.datastore.ReportChoicesAdapter;
import com.thapasujan5.netanalzyerpro.db.DAO;
import com.thapasujan5.netanalzyerpro.notification.Notify;
import com.thapasujan5.netanalzyerpro.tools.CheckNet;
import com.thapasujan5.netanalzyerpro.tools.Clipboard;
import com.thapasujan5.netanalzyerpro.tools.ConnectionDetector;
import com.thapasujan5.netanalzyerpro.tools.GetIntIP;
import com.thapasujan5.netanalzyerpro.tools.NetworkUtil;
import com.thapasujan5.netanalzyerpro.tools.ShowToast;
import com.thapasujan5.netanalzyerpro.tools.UserFunctions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener, TextView.OnEditorActionListener {

    EditText etSearchBox;
    ImageView ivSearch;
    RelativeLayout rlProgressbarContainer;
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
    NotificationManager nm;
    IntentFilter filter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle fields = getIntent().getExtras();
        if (fields != null) {
            intentServiceResult = fields.getBoolean("isr");
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Perform Quick Ping from here.", Snackbar.LENGTH_LONG)
                        .setAction("Continue", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder d = new AlertDialog.Builder(MainActivity.this);
                                d.setTitle("Quick Ping Service");

                                final EditText editText = new EditText(MainActivity.this);
                                editText.setHint("Enter DNS or IP ");
                                editText.setSingleLine();
                                editText.setGravity(Gravity.CENTER);
                                d.setView(editText);
                                d.setPositiveButton("Ping", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (editText.getText().toString().trim().length() > 0) {
                                            new PingRequest(editText.getText().toString().trim()).execute();
                                            dialog.dismiss();
                                        } else {
                                            Toast.makeText(MainActivity.this, "Address required !", Toast.LENGTH_SHORT).show();

                                            editText.requestFocus();
                                        }
                                    }
                                });
                                d.setNegativeButton("Close", null);


                                editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                                    @Override
                                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                        if ((actionId == EditorInfo.IME_ACTION_DONE)) {
                                            InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            in.hideSoftInputFromWindow(etSearchBox.getApplicationWindowToken(),
                                                    InputMethodManager.HIDE_NOT_ALWAYS);
                                            if (editText.getText().toString().trim().length() > 0) {
                                                new PingRequest(editText.getText().toString().trim()).execute();
                                            } else {
                                                Toast.makeText(MainActivity.this, "Address required !", Toast.LENGTH_SHORT).show();
                                                editText.requestFocus();
                                            }
                                            return true;
                                        }
                                        return false;
                                    }
                                });
                                final Dialog dialog = d.create();

                                dialog.show();

                            }
                        }).show();
            }
        });
        //fab.setVisibility(View.GONE);

        initialize();
        new AboutWhatsNew(this);

        networkStateReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                Log.w("Network Listener", "Network Type Changed");
                String status = NetworkUtil.getConnectivityStatusString(context);
                Toast.makeText(context, status, Toast.LENGTH_LONG).show();
                reValidate();
            }
        };

        filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkStateReceiver, filter);


    }

    private void initialize() {
        Log.i("f", "initialize");
        try {
            sharedpreferences = PreferenceManager
                    .getDefaultSharedPreferences(getBaseContext());
            nm = (NotificationManager) MainActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);
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

            currentItems = new ArrayList<Items>();
            dbItems = new ArrayList<Items>();

            adapterMain = new ItemsAdapter(this, R.layout.item_row_list, dbItems);

            listview = (ListView) findViewById(R.id.listview);
            listview.setOnItemClickListener(this);
            listview.setOnItemLongClickListener(this);
            listview.setAdapter(adapterMain);

            pbExip = (ProgressBar) findViewById(R.id.pbExip);
            pbMain = (ProgressBar) findViewById(R.id.progressBar);

            connectionDetector = new ConnectionDetector(getApplicationContext());

            dao = new DAO(getApplicationContext());

            new DBAsync().execute();
            new CheckNet(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


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
                    ivCopy.setVisibility(View.GONE);
                    ivShare.setVisibility(View.GONE);

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

    public class ExternalIPFinder extends AsyncTask<Void, Void, Void> {
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
            pbExip.setVisibility(View.GONE);
            if (data) {
                new ServerAsync(extIPAdd).execute();
                tvExIpArea.setText("External IP: " + extIPAdd + ", " + org + " "
                        + city + ", " + country);
                tvExIpArea.setVisibility(View.VISIBLE);
                if (sharedpreferences.getBoolean(getString(R.string.key_notification_sticky), true) == true) {

                    new Notify(MainActivity.this, extIPAdd, GetIntIP.getInternalIP(MainActivity.this), org, city, country);
                } else {
                    nm.cancel(0);
                    Log.i("notification", "notification cancelled from main");
                }
            } else {
                if (connectionDetector.isConnectingToInternet()) {
                    tvExIpArea.setText("Check Internet Access !");
                } else {
                    tvExIpArea.setText("Disconnected !");
                }
                tvExIpArea.setVisibility(View.VISIBLE);

            }
            super.onPostExecute(result);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.rate) {
            new RateApp(MainActivity.this);
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        }
        if (id == R.id.about) {
            new About(MainActivity.this);
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        }
        if (id == R.id.report) {
            new Feedback(MainActivity.this);
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        }

        if (id == R.id.screenShot) {
            new ScreenShot(MainActivity.this);
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        }
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        }
        if (id == R.id.portal) {
            new Portal(MainActivity.this);
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   int position, long id) {
        //clicked Item
        final Items item = dbItems.get(position);

        //Possible Menus
        String[] choices = {"Open " + item.ip + " in browser...",
                "Copy " + item.ip, "Export data... ", "Remove from list",
                "Ping " + " Server", "View on Map", "Details..."};
        //Finalized MenuItmes
        ArrayList<ReportChoices> choice = new ArrayList<ReportChoices>();
        choice.add(new ReportChoices(choices[0], android.R.drawable.ic_menu_send));
        choice.add(new ReportChoices(choices[1],
                android.R.drawable.ic_menu_edit));
        choice.add(new ReportChoices(choices[2],
                android.R.drawable.ic_menu_add));
        choice.add(new ReportChoices(choices[3],
                android.R.drawable.ic_menu_delete));
        choice.add(new ReportChoices(choices[4], android.R.drawable.ic_menu_revert));
        choice.add(new ReportChoices(choices[5], android.R.drawable.ic_menu_mapmode));
        choice.add(new ReportChoices(choices[6],
                android.R.drawable.ic_menu_more));

        //Link model(adapter) with datasource (choice)
        ReportChoicesAdapter adapter = new ReportChoicesAdapter(this, R.layout.item_row_context_menu,
                choice);

        @SuppressWarnings("deprecation")

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    // open value.ip
                    String url = "http://" + item.ip;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(Intent.createChooser(i, "Open " + item.ip
                            + " using"));
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    return;
                }
                if (which == 1) {
                    // copy ip to clipboard
                    boolean status = Clipboard.copyToClipboard(
                            MainActivity.this, item.ip);
                    if (status) {
                        new ShowToast(MainActivity.this, "Copied " + item.ip
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
                            + item.name + "\n" + "IP: " + item.ip + "\n"
                            + "Server Location: " + item.location + ","
                            + item.date);
                    sendIntent.setType("text/plain");
                    startActivity(Intent.createChooser(sendIntent,
                            "Share using"));
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    return;
                }

                if (which == 3) {
                    // Remove
                    dao.open();
                    boolean res = dao.deleteItem(item);
                    dao.close();
                    if (res) {
                        new ExternalIPFinder().execute();

                        Toast.makeText(MainActivity.this,
                                "Removed " + item.name + ":" + item.ip,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Error deleteing !",
                                Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
                if (which == 4) {
                    //Ping
                    new PingRequest(item.ip).execute();
                }
                if (which == 5) {
                    Intent openMap = new Intent(MainActivity.this, MapsActivity.class);
                    openMap.putExtra("location", item.location);
                    openMap.putExtra("lat", Double.parseDouble(item.lat));
                    openMap.putExtra("lon", Double.parseDouble(item.lon));
                    startActivity(openMap);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                }
                if (which == 6) {
                    //Details
                    Context context = MainActivity.this;

                    final Dialog d = new Dialog(context);
                    d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    d.setContentView(R.layout.dialog_newfeatures);

                    TextView title, message;
                    title = (TextView) d.findViewById(R.id.titleText);
                    title.setText("Details:" + item.ip);

                    message = (TextView) d.findViewById(R.id.message);
                    final String messageDetails = getFormattedDetails(item);
                    message.setText(messageDetails);
                    message.setGravity(Gravity.LEFT);


                    Button ok = (Button) d.findViewById(R.id.ok);
                    ok.setText("Copy");
                    ok.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // copy ip to clipboard
                            boolean status = Clipboard.copyToClipboard(
                                    MainActivity.this, messageDetails);
                            if (status) {
                                new ShowToast(MainActivity.this,
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
                + "\nLast Saved: " + ItemsAdapter.getDate(
                Long.parseLong(item.date), "EEE MMM dd yyyy HH:mm z")
                + "\nCoordinate: " + item.lon
                + ", " + item.lat
                + "\n";
    }

    private class PingRequest extends AsyncTask<Void, Void, Void> {
        String ip, pingResult = null;
        Dialog dialog;
        TextView tvTitle;
        ProgressBar pb;
        Button btnHide, btnCancel;

        PingRequest(String ip) {
            this.ip = ip;
        }

        @Override
        protected void onPreExecute() {

            dialog = new Dialog(MainActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_pinging);

            pb = (ProgressBar) dialog.findViewById(R.id.pbPinging);
            pb.setIndeterminate(true);
            tvTitle = (TextView) dialog.findViewById(R.id.titleText);
            tvTitle.setText("Pinging : " + ip);
            btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    cancel(true);
                    dialog.dismiss();

                }
            });
            btnHide = (Button) dialog.findViewById(R.id.btnMinimize);
            btnHide.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    cancel(true);
                }
            });

            dialog.show();

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            String str = "";
            try {
                Process process = Runtime.getRuntime().exec(
                        "/system/bin/ping -c 8 " + ip);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));
                int i;
                char[] buffer = new char[4096];
                StringBuffer output = new StringBuffer();
                while ((i = reader.read(buffer)) > 0) {

                    output.append(buffer, 0, i);
                }
                reader.close();
                // body.append(output.toString()+"\n");
                str = output.toString();
                pingResult = str;

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            dialog.dismiss();
            if (pingResult.length() > 0) {
                final Dialog d = new Dialog(MainActivity.this);
                d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                d.setContentView(R.layout.dialog_ping_result);
                final TextView tvPingResult = (TextView) d
                        .findViewById(R.id.tvPingResut);
                tvPingResult.setText(pingResult);
                final TextView tvTitleArea = (TextView) d
                        .findViewById(R.id.tvtitleArea);
                tvTitleArea.setText("Ping Statistics for " + ip);
                Button btnShare = (Button) d.findViewById(R.id.btnShare);
                btnShare.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, pingResult);
                        sendIntent.setType("text/plain");
                        startActivity(Intent.createChooser(sendIntent,
                                "Share using"));
                        overridePendingTransition(R.anim.slide_in,
                                R.anim.slide_out);
                    }
                });
                Button btnCopy = (Button) d.findViewById(R.id.btnCopy);
                btnCopy.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // copy ip to clipboard
                        boolean status = Clipboard.copyToClipboard(
                                MainActivity.this, pingResult);
                        if (status) {
                            new ShowToast(MainActivity.this,
                                    "Copied to Clipboard", Color.WHITE,
                                    R.drawable.action_bar_bg, 0,
                                    Toast.LENGTH_LONG, Gravity.BOTTOM);
                        }
                        d.dismiss();
                    }

                });
                Button btnClose = (Button) d.findViewById(R.id.btnClose);
                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.dismiss();
                    }
                });
                d.setOnCancelListener(null);
                d.setCancelable(false);
                d.show();
            } else {
                new ShowToast(getApplicationContext(), "Server unreachable.",
                        Color.YELLOW, R.drawable.action_bar_bg, 0, 2000,
                        Gravity.BOTTOM);
            }
            super.onPostExecute(result);
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
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
                    + "\nLast Saved: " + ItemsAdapter.getDate(
                    Long.parseLong(allDbItems.get(i).date), "EEE MMM dd yyyy HH:mm z")
                    + "\nCoordinate: " + allDbItems.get(i).lon
                    + ", " + allDbItems.get(i).lat

                    + "\nRegion: " + allDbItems.get(i).region + "\n\n";
        }
        return allData;

    }

    public void onClick(View arg0) {
        int id = arg0.getId();
        if (id == R.id.extip) {
            String value = tvExIpArea.getText().toString().trim();
            if (value.contains(":")) {
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

        }
        if (id == R.id.clearlist) {
            ArrayList<Items> tempItems = new ArrayList<Items>();
            dao.open();
            tempItems.addAll(dao.getItems());
            if (tempItems.size() > 0) {
                final Dialog d = new Dialog(MainActivity.this);
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
                boolean status = Clipboard.copyToClipboard(MainActivity.this,
                        getAllData());
                if (status) {
                    new ShowToast(MainActivity.this, "Copied all to Clipboard",
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
                    Toast.makeText(getApplicationContext(), "Disconnected !",
                            Toast.LENGTH_SHORT).show();
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
                tvEntriesCountArea.setText(dbItems.size() + " Entries");
            }
            super.onPostExecute(result);
        }
    }


    @Override
    protected void onResume() {
        Log.i("s", "onResume");
        reValidate();
        super.onResume();
    }

    private void reValidate() {
        try {
            android.support.v7.app.ActionBar ab = getSupportActionBar();
            if (connectionDetector.isConnectingToInternet()) {

                // Get Local IP either from WIFI or Data
                // WIFI
                intIP = GetIntIP.getInternalIP(MainActivity.this);

                // Set Internal IP

                if (intIP != null) {
                    if (intIP.length() > 7) {
                        intIP = ", " + intIP;
                        ab.setSubtitle(Html.fromHtml("<fontcolor='#99ff00'><small>Device:"
                                + android.os.Build.MODEL + intIP + "</small></fontcolor>"));
                    }
                } // Get Ex IP if network is connected
                new ExternalIPFinder().execute();

            } else {
                ab.setSubtitle(Html.fromHtml("<fontcolor='#99ff00'><small>"
                        + android.os.Build.MODEL + "</small</fontcolor>"));
                tvExIpArea.setText("Check your Connectivity !");
                tvExIpArea.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(networkStateReceiver);
        } catch (Exception e) {
            e.printStackTrace();
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
}
