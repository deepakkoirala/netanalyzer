package com.thapasujan5.netanalzyerpro.DataStore;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.thapasujan5.netanalzyerpro.Fragments.Data;
import com.thapasujan5.netanalzyerpro.Fragments.WIFI;

/**
 * Created by Suzan on 12/14/2015.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
    private static final int NUM_PAGES = 2;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new WIFI();
                return fragment;

            case 1:
                fragment = new Data();
                return fragment;
            default:
                fragment = new WIFI();
        }
        return fragment;

    }

//    @Override
//    public float getPageWidth(int position) {
//        return 0.93f;
//    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position) {
            case 0:
                title = "WiFi";
                break;
            case 1:
                title = "Cellular";
                break;
        }
        return title;
    }
}
