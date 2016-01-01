package com.thapasujan5.netanalzyerpro;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.thapasujan5.netanalyzerpro.R;
import com.thapasujan5.netanalzyerpro.DataStore.ItemsDiscovered;
import com.thapasujan5.netanalzyerpro.DataStore.ItemsDiscoveredAdapter;

import java.util.ArrayList;

public class NetworkDiscoveryActivity extends AppCompatActivity {
    ItemsDiscoveredAdapter adapter;
    ArrayList<ItemsDiscovered> itemsDiscovereds;
    ListView listView;

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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
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

    }


}
