package com.thapasujan5.netanalzyerpro.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.thapasujan5.netanalzyerpro.AppConstants;
import com.thapasujan5.netanalzyerpro.DataStore.Items;

import java.util.ArrayList;

public class DAO {

    Context context;
    private SQLLiteDBHelper dbHeper;
    private SQLiteDatabase db;

    public DAO(Context context) {
        this.context = context;
        try {
            dbHeper = new SQLLiteDBHelper(context);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void open() {
        try {
            db = dbHeper.getWritableDatabase();
        } catch (SQLException e) {

        }
    }

    public void close() {
        dbHeper.close();
    }

    public void deleteAllItems() {
        db.delete(AppConstants.TBL_Items, null, null);

    }

    public boolean deleteItem(Items item) {

        return db.delete(AppConstants.TBL_Items, AppConstants.col_item_name
                + "='" + item.name + "' and " + AppConstants.col_item_ip + "='"
                + item.ip + "' and " + AppConstants.col_item_isp + "='"
                + item.isp + "' and " + AppConstants.col_item_location + "='"
                + item.location + "' and " + AppConstants.col_item_date + "='"
                + item.date + "'", null) > 0;
    }

    public void addItems(Items item) {

        ContentValues values = new ContentValues();

        Cursor c = db.rawQuery("select * from " + AppConstants.TBL_Items
                + " where " + AppConstants.col_item_ip + " = '" + item.ip
                + "' and " + AppConstants.col_item_name + " = '" + item.name
                + "' and " + AppConstants.col_item_isp + " = '" + item.isp
                + "' and " + AppConstants.col_item_location + " = '"
                + item.location + "';", null);

        if (c.moveToFirst() == false) {
            values.put(AppConstants.col_item_name, item.name);
            values.put(AppConstants.col_item_ip, item.ip);
            values.put(AppConstants.col_item_isp, item.isp);
            values.put(AppConstants.col_item_location, item.location);
            values.put(AppConstants.col_item_date, item.date);

            values.put(AppConstants.col_item_lat, item.lat);
            values.put(AppConstants.col_item_lon, item.lon);
            values.put(AppConstants.col_item_region, item.region);
            values.put(AppConstants.col_item_region_name, item.region_name);
            values.put(AppConstants.col_item_zip, item.zip);
            values.put(AppConstants.col_item_time_zone, item.time_zone);

            db.insert(AppConstants.TBL_Items, null, values);

        } else {
            Cursor c1 = db.rawQuery("UPDATE " + AppConstants.TBL_Items
                    + " SET " + AppConstants.col_item_date + " = '" + item.date
                    + "' where " + AppConstants.col_item_ip + " = '" + item.ip
                    + "';", null);
        }
    }

    public ArrayList<Items> getItems() {
        ArrayList<Items> items = new ArrayList<Items>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + AppConstants.TBL_Items
                + " order by " + AppConstants.col_item_date + " desc;", null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Items mContent = new Items(cursor.getString(cursor
                    .getColumnIndex(AppConstants.col_item_name)),
                    cursor.getString(cursor.getColumnIndex(AppConstants.col_item_ip)),
                    cursor.getString(cursor.getColumnIndex(AppConstants.col_item_isp)),
                    cursor.getString(cursor.getColumnIndex(AppConstants.col_item_location)),
                    cursor.getString(cursor.getColumnIndex(AppConstants.col_item_date)),
                    cursor.getString(cursor.getColumnIndex(AppConstants.col_item_lat)),
                    cursor.getString(cursor.getColumnIndex(AppConstants.col_item_lon)),
                    cursor.getString(cursor.getColumnIndex(AppConstants.col_item_region)),
                    cursor.getString(cursor.getColumnIndex(AppConstants.col_item_region_name)),
                    cursor.getString(cursor.getColumnIndex(AppConstants.col_item_zip)),
                    cursor.getString(cursor.getColumnIndex(AppConstants.col_item_time_zone)));
            items.add(mContent);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return items;
    }

    public int getItemsCount() {
        int rowsCount = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) from "
                + AppConstants.TBL_Items, null);
        cursor.moveToFirst();
        rowsCount = Integer.parseInt(cursor.getString(0));

        return rowsCount;
    }
}