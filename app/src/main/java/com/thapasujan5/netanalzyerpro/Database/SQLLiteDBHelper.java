package com.thapasujan5.netanalzyerpro.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.thapasujan5.netanalzyerpro.AppConstants;

public class SQLLiteDBHelper extends SQLiteOpenHelper {
    Context context;

    public SQLLiteDBHelper(Context c) {
        super(c, AppConstants.DB_NAME, null, AppConstants.DB_VERSION);
        this.context = c;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + AppConstants.TBL_Items + " ("
                + AppConstants.col_item_id
                + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + AppConstants.col_item_name + " TEXT,"
                + AppConstants.col_item_ip + " TEXT,"
                + AppConstants.col_item_isp + " TEXT,"
                + AppConstants.col_item_location + " TEXT,"
                + AppConstants.col_item_date + " TEXT,"

                + AppConstants.col_item_lat + " TEXT,"
                + AppConstants.col_item_lon + " TEXT,"
                + AppConstants.col_item_region + " TEXT,"
                + AppConstants.col_item_region_name + " TEXT,"
                + AppConstants.col_item_zip + " TEXT,"
                + AppConstants.col_item_time_zone + " TEXT );");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + AppConstants.TBL_Items);

        onCreate(db);
    }
}
