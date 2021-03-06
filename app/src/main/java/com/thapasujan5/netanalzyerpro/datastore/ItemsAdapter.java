package com.thapasujan5.netanalzyerpro.DataStore;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.thapasujan5.netanalyzerpro.R;
import com.thapasujan5.netanalzyerpro.ActionMenu.UpgradeToPro;
import com.thapasujan5.netanalzyerpro.MapsActivity;
import com.thapasujan5.netanalzyerpro.Tools.DateTimeFormatted;

import java.util.ArrayList;

public class ItemsAdapter extends ArrayAdapter<Items> {

    Context context;
    int layoutResourceId;
    ArrayList<Items> data = null;

    public ItemsAdapter(Context context, int layoutResourceId,
                        ArrayList<Items> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;

    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        View row = convertView;
        row = null;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
        }
        try {
            final Items item = data.get(position);
            ((TextView) row.findViewById(R.id.count)).setText(position + 1
                    + ".");
            ((TextView) row.findViewById(R.id.name)).setText(item.name);
            ((TextView) row.findViewById(R.id.ip)).setText(item.ip);
            if (item.isp.length() > 0) {
                ((TextView) row.findViewById(R.id.isp)).setText(item.isp);
            } else {
                row.findViewById(R.id.isp)
                        .setVisibility(View.GONE);
            }

            ((TextView) row.findViewById(R.id.date)).setText(DateTimeFormatted.getDate(
                    Long.parseLong(item.date), "EEE MMM dd yyyy HH:mm z")); // dd/MM/yyyy
            // hh:mm:ss.SSS
            if (item.location.length() > 0) {
                ((TextView) row.findViewById(R.id.location))
                        .setText(item.location);
            } else {
                row.findViewById(R.id.location)
                        .setVisibility(View.GONE);
            }
            ImageView ivLocation = (ImageView) row.findViewById(R.id.ivLocation);
            if (item.lat.length() > 0 && item.lon.length() > 0) {

                ivLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            Intent openMap = new Intent(context, MapsActivity.class);
                            openMap.putExtra("location", item.location);
                            openMap.putExtra("lat", Double.parseDouble(item.lat));
                            openMap.putExtra("lon", Double.parseDouble(item.lon));
                            context.startActivity(openMap);
                            ((Activity) context).overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                    }
                });
            } else {
                ivLocation.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {

        }
        return row;
    }
}
