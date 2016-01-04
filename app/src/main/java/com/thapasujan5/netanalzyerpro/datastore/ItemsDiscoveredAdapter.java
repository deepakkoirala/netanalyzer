package com.thapasujan5.netanalzyerpro.DataStore;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.thapasujan5.netanalyzerpro.R;

import java.util.ArrayList;

public class ItemsDiscoveredAdapter extends ArrayAdapter<ItemsDiscovered> {

    Context context;
    int layoutResourceId;
    ArrayList<ItemsDiscovered> data = null;

    public ItemsDiscoveredAdapter(Context context, int layoutResourceId,
                                  ArrayList<ItemsDiscovered> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        row = null;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
        }
        try {
            final ItemsDiscovered item = data.get(position);
            ((TextView) row.findViewById(R.id.count)).setText((Integer.toString(position + 1)));
            ((TextView) row.findViewById(R.id.tvDns)).setText(item.dns);
            ((TextView) row.findViewById(R.id.tvIp)).setText(item.ip);


            ((TextView) row.findViewById(R.id.tvMac))
                    .setText(item.mac);


        } catch (Exception e) {

        }
        return row;
    }

}
