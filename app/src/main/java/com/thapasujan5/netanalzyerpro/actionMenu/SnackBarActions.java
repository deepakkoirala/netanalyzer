package com.thapasujan5.netanalzyerpro.ActionMenu;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.thapasujan5.netanalyzerpro.R;
import com.thapasujan5.netanalzyerpro.DataStore.SnackBarAdapter;
import com.thapasujan5.netanalzyerpro.DataStore.SnackBarItems;
import com.thapasujan5.netanalzyerpro.PingService.FabPing;
import com.thapasujan5.netanalzyerpro.PortScanner.FabPortScan;

import java.util.ArrayList;

/**
 * Created by Sujan Thapa on 9/01/2016.
 */
public class SnackBarActions {
    Context context;

    public SnackBarActions(Context context, View view) {
        this.context = context;
        Snackbar snackbar = Snackbar.make(view, "", Snackbar.LENGTH_LONG);

        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();

        TextView textView = (TextView) layout.findViewById(android.support.design.R.id.snackbar_text);
        textView.setVisibility(View.INVISIBLE);
        View snackView = ((Activity) context).getLayoutInflater().inflate(R.layout.snackbar, null);
        ArrayList<SnackBarItems> choice = new ArrayList<SnackBarItems>();
        choice.add(new SnackBarItems("Quick Ping", R.mipmap.merge));
        choice.add(new SnackBarItems("Port Scanner",
                R.mipmap.ic_computer));
        SnackBarAdapter adapter = new SnackBarAdapter(context, R.layout.item_row_snackbar,
                choice);
        ListView listView = (ListView) snackView.findViewById(R.id.listView);
        listView.setOnItemClickListener(itemClicked);
        listView.setAdapter(adapter);// Configure the view
        ViewGroup group = (ViewGroup) snackbar.getView();
        group.setBackgroundColor(ContextCompat.getColor(context, R.color.app_theme_background));
        layout.addView(snackView, 0);

        snackbar.show();
    }

    ListView.OnItemClickListener itemClicked = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    new FabPing(context);
                    break;
                case 1:
                    new FabPortScan(context);
                    break;
            }
        }
    };
}
