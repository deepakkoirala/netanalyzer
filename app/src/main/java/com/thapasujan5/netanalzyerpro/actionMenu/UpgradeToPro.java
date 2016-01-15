package com.thapasujan5.netanalzyerpro.ActionMenu;


import android.app.Activity;
import android.content.Context;

import com.thapasujan5.netanalyzerpro.R;
import com.thapasujan5.netanalzyerpro.Tools.OpenMarket;

public class UpgradeToPro {
    public UpgradeToPro(Context context) {
        context.startActivity(OpenMarket.openURL(context.getString(R.string.package_name_pro)));
        ((Activity) context).overridePendingTransition(R.anim.slide_in,
                R.anim.slide_out);
    }
}
