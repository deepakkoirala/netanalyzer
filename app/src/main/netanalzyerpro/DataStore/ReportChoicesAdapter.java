package com.thapasujan5.netanalzyerpro.DataStore;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.thapasujan5.netanalyzerpro.R;

import java.util.ArrayList;

public class ReportChoicesAdapter extends ArrayAdapter<ReportChoices> {

	Context context;
	int layoutResourceId;
	ArrayList<ReportChoices> data = null;

	public ReportChoicesAdapter(Context context, int layoutResourceId,
			ArrayList<ReportChoices> data) {

		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
		}

		ReportChoices i = data.get(position);

		((ImageView) row.findViewById(R.id.icon)).setImageResource(i.icon);
		((TextView) row.findViewById(R.id.name)).setText(i.name);
		return row;
	}
}
